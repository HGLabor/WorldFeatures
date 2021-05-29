@file:UseSerializers(ItemStackSerializer::class)
package de.hglabor.worldfeatures.kotlin.features

import de.hglabor.worldfeatures.features.Feature
import de.hglabor.worldfeatures.kotlin.data.MongoManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.axay.kspigot.runnables.task
import net.axay.kspigot.serialization.ItemStackSerializer
import org.bukkit.*
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.EulerAngle
import org.litote.kmongo.bson
import org.litote.kmongo.findOne
import java.util.*
import kotlin.collections.ArrayList

@Serializable
data class LootableBody(val owner: String, val uuid: String, var loot: ArrayList<ItemStack>)

class LootableBodiesFeature : Feature("LootableBodies") {

    @EventHandler
    fun onPlayerDeath(it: PlayerDeathEvent) {
        if(!isEnabled) {
            return
        }
        if(!it.entity.world.getGameRuleValue(GameRule.KEEP_INVENTORY)!!) {
            val armorStand = it.entity.world.spawnEntity(it.entity.location.clone().subtract(0.0,1.0,0.0), EntityType.ARMOR_STAND) as ArmorStand
            armorStand.isSwimming = true
            armorStand.setBasePlate(false)
            armorStand.setArms(true)
            armorStand.setRotation(42f,0f)
            for (equipmentSlot in EquipmentSlot.values()) {
                for (lockType in ArmorStand.LockType.values()) {
                    armorStand.addEquipmentLock(equipmentSlot,lockType)
                }
            }
            armorStand.bodyPose = EulerAngle(80.0, 0.0, 0.0)
            armorStand.headPose = EulerAngle(68.0, 39.0, 10.0)
            armorStand.rightLegPose = EulerAngle(360.0, 0.0, 0.0)
            armorStand.leftArmPose = EulerAngle(240.0, 329.0, 41.0)
            armorStand.rightArmPose = EulerAngle(261.0, 0.0, 265.0)
            armorStand.isCustomNameVisible = true
            armorStand.customName = it.entity.name
            armorStand.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, Int.MAX_VALUE, 0, false,false))
            armorStand.addScoreboardTag("isBody:true")
            armorStand.addScoreboardTag("hasLoot:true")
            val stack = ItemStack(Material.PLAYER_HEAD)
            var meta = stack.itemMeta as SkullMeta
            meta.owningPlayer = it.entity
            stack.itemMeta = meta
            armorStand.equipment!!.helmet = stack
            val arrayList = arrayListOf<ItemStack>()
            for (itemStack in it.drops) {
                arrayList.add(itemStack)
            }
            arrayList.add(ItemStack(Material.ROTTEN_FLESH, 3))
            arrayList.add(ItemStack(Material.BONE, 3))
            it.drops.clear()
            MongoManager.BODIES.insertOne(LootableBody(
                it.entity.uniqueId.toString(),
                armorStand.uniqueId.toString(),
                arrayList
            ))
            stack.type = Material.LEATHER_CHESTPLATE
            val leatherMeta = stack.itemMeta as LeatherArmorMeta
            leatherMeta.setColor(Color.OLIVE)
            stack.itemMeta = leatherMeta
            armorStand.equipment!!.chestplate = stack
            stack.type = Material.LEATHER_LEGGINGS
            stack.itemMeta = leatherMeta
            armorStand.equipment!!.leggings = stack
            it.droppedExp = it.entity.expToLevel
            var i = 0
            val event = it
            task(
                period = 3,
                howOften = 60
            ) {
                event.entity.world.spawnParticle(Particle.ASH, event.entity.location.clone().add(0.0,1.0,0.0),0)
            }
            task(
                period = 15*20
            ) {
                if(!armorStand.isDead) {
                    if(Random().nextBoolean()) {
                        armorStand.world.playSound(armorStand.location, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.6f, 0f)
                    } else {
                        armorStand.world.playSound(armorStand.location, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.6f, 10f)
                    }
                    i += 1
                    if(i == 15) {
                        armorStand.equipment!!.helmet = ItemStack(Material.SKELETON_SKULL)
                    }
                } else {
                    it.cancel()
                }
            }
            task(
                delay = 30*60*20
            ) {
                if(event.entity.isDead) {
                    it.cancel()
                } else {
                    event.entity.remove()
                }
            }
        }
    }

    @EventHandler
    fun onInteractAtEntity(it: PlayerInteractAtEntityEvent) {
        if(!isEnabled) {
            return
        }
        if(it.rightClicked is ArmorStand) {
            if (it.rightClicked.scoreboardTags.contains("hasLoot:true")) {
                it.player.world.playSound(it.player.location, Sound.ENTITY_LEASH_KNOT_BREAK, 1f,1f)
                val lootableBody = MongoManager.BODIES.findOne("{\"uuid\":\"${it.rightClicked.uniqueId}\"}")!!
                for (itemStack in lootableBody.loot) {
                    it.rightClicked.world.dropItem(it.rightClicked.location, itemStack)
                }
                it.rightClicked.removeScoreboardTag("hasLoot:true")
                MongoManager.BODIES.deleteOne("{\"uuid\":\"${it.rightClicked.uniqueId}\"}".bson)
            }
        }
    }

    @EventHandler
    fun onDamageEntity(it: EntityDamageByEntityEvent) {
        if (!isEnabled) {
            return
        }
        if(it.entity is ArmorStand) {
            if(it.entity.scoreboardTags.contains("isBody:true")) {
                if(it.damager is Player) {
                    if((it.damager as Player).inventory.itemInMainHand.type.name.contains("SHOVEL")) {
                        if(it.entity.scoreboardTags.contains("hasLoot:true")) {
                            it.damager.world.playSound(it.damager.location, Sound.ENTITY_LEASH_KNOT_BREAK, 1f,1f)
                            val lootableBody = MongoManager.BODIES.findOne("{\"uuid\":\"${it.entity.uniqueId}\"}")!!
                            for (itemStack in lootableBody.loot) {
                                it.entity.world.dropItem(it.entity.location, itemStack)
                            }
                            it.entity.removeScoreboardTag("hasLoot:true")
                            MongoManager.BODIES.deleteOne("{\"uuid\":\"${it.entity.uniqueId}\"}".bson)
                        }
                        it.entity.remove()
                    } else {
                        it.isCancelled = true
                    }
                    val event = it
                    task(
                        period = 3,
                        howOften = 30
                    ) {
                        event.entity.world.spawnParticle(Particle.ASH, event.entity.location,0)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onBodyDeath(it: EntityDeathEvent) {
        if(!isEnabled) {
            return
        }
        if(it.entity is ArmorStand) {
            if (it.entity.scoreboardTags.contains("isBody:true")) {
                if(it.entity.scoreboardTags.contains("hasLoot:true")) {
                    it.entity.world.playSound(it.entity.location, Sound.ENTITY_LEASH_KNOT_BREAK, 1f,1f)
                    val lootableBody = MongoManager.BODIES.findOne("{\"uuid\":\"${it.entity.uniqueId}\"}")!!
                    for (itemStack in lootableBody.loot) {
                        it.entity.world.dropItem(it.entity.location, itemStack)
                    }
                    it.entity.removeScoreboardTag("hasLoot:true")
                    val event = it
                    task(
                        period = 3,
                        howOften = 30
                    ) {
                        event.entity.world.spawnParticle(Particle.ASH, event.entity.location,0)
                    }
                    MongoManager.BODIES.deleteOne("{\"uuid\":\"${it.entity.uniqueId}\"}".bson)
                }
            }
        }
    }

}