package de.hglabor.worldfeatures.features.entity.implementation;

import de.hglabor.worldfeatures.features.entity.LaborEntity;
import de.hglabor.worldfeatures.features.entity.animation.AnimationBuilder;
import de.hglabor.worldfeatures.features.entity.animation.IAnimateable;
import de.hglabor.worldfeatures.utils.Identifier;
import de.hglabor.worldfeatures.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class RaptorEntityFeature extends LaborEntity<ArmorStand> implements IAnimateable<ArmorStand> {

    public RaptorEntityFeature() {
        super(new Identifier("raptor"), 4, it -> it.getWorld().getName().equalsIgnoreCase("world"));
        makeShootable();
    }

    @Override
    public LaborEntity<ArmorStand> getNewInstance() {
        return new RaptorEntityFeature();
    }

    private ArmorStand stolenEntity = null;

    @Override
    public void prepareSpawn(Object obj) {
        withYOffset(15.8);
    }

    @Override
    public void afterSpawn(ArmorStand entity) {
        entity.setVisible(false);
        entity.setArms(true);
        entity.getEquipment().setHelmet(new ItemBuilder(Material.FEATHER).withCustomModelData(2).build());
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            entity.addEquipmentLock(equipmentSlot, ArmorStand.LockType.ADDING_OR_CHANGING);
            entity.addEquipmentLock(equipmentSlot, ArmorStand.LockType.REMOVING_OR_CHANGING);
        }
        entity.setGravity(false);
    }

    @Override
    public void setLivingAnimations(ArmorStand entity, float tickDelta) {
        for (Entity targetPossibility : entity.getNearbyEntities(15, 150, 15)) {
            AnimationBuilder animationBuilder = new AnimationBuilder(this);
            animationBuilder.withRemoveOnBlock(false);
            animationBuilder.withDeathAnimation(it -> it.getWorld().playSound(it.getLocation(), Sound.ENTITY_PHANTOM_DEATH, 1, 10));
            animationBuilder.withWalkingAnimation(it -> {
                ItemStack itemStack = entity.getEquipment().getHelmet();
                ItemMeta itemMeta = itemStack.getItemMeta();
                if(stolenEntity != null) {
                    itemMeta.setCustomModelData(3);
                    if(entity.getTicksLived() > 180*20) {
                        stolenEntity = null;
                    }
                    stolenEntity.teleport(entity.getLocation().clone().subtract(0,0.7,0));
                }
                itemMeta.setCustomModelData(2);
                itemStack.setItemMeta(itemMeta);
            });
            animationBuilder.shouldAttackWhen(it -> {
                if(stolenEntity == it) {
                    return false;
                }
                if(stolenEntity == null || stolenEntity.isDead()) {
                    return it.getScoreboardTags().contains("isBody:true");
                }
                return false;
            });
            if(targetPossibility instanceof ArmorStand) {
                if(!targetPossibility.isDead()) {
                    if(targetPossibility != stolenEntity && stolenEntity == null || stolenEntity.isDead()) {
                        animationBuilder.withPathfinderGoal((LivingEntity) targetPossibility, 0.1);
                    } else {
                        continue;
                    }
                    animationBuilder.withAttackAnimation(it -> {
                        stolenEntity = (ArmorStand) it;
                        it.getWorld().playSound(it.getLocation(), Sound.ENTITY_PHANTOM_BITE, 1, 10);
                        int randomY = new Random().nextInt(200);
                        animationBuilder.withPathfinderGoal(entity.getLocation().clone().subtract(0,entity.getLocation().getY(),0).add(new Random().nextBoolean() ? 15 : -15, randomY > 80 ? randomY : 87,new Random().nextBoolean() ? 15 : -15));
                    });
                }
            } else {
                int randomY = new Random().nextInt(200);
                animationBuilder.withPathfinderGoal(entity.getLocation().clone().subtract(0,entity.getLocation().getY(),0).add(new Random().nextBoolean() ? 15 : -15, randomY > 80 ? randomY : 87,new Random().nextBoolean() ? 15 : -15));
            }
            animationBuilder.apply();
            break;
        }
    }
}
