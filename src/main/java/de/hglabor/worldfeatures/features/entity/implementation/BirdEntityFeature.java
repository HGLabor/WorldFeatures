package de.hglabor.worldfeatures.features.entity.implementation;

import de.hglabor.utils.noriskutils.ChanceUtils;
import de.hglabor.worldfeatures.features.entity.LaborEntity;
import de.hglabor.worldfeatures.features.entity.animation.AnimationBuilder;
import de.hglabor.worldfeatures.features.entity.animation.IAnimateable;
import de.hglabor.worldfeatures.utils.Identifier;
import de.hglabor.worldfeatures.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.Random;

public class BirdEntityFeature extends LaborEntity<ArmorStand> implements IAnimateable<ArmorStand> {

    public BirdEntityFeature() {
        super(new Identifier("bird"), 15, it -> it.getWorld().getName().equalsIgnoreCase("world"));
    }

    @Override
    public LaborEntity<ArmorStand> getNewInstance() {
        return new BirdEntityFeature();
    }

    @Override
    public void setLivingAnimations(ArmorStand entity, float tickDelta) {
        AnimationBuilder animationBuilder = new AnimationBuilder(this);
        animationBuilder.withDeathAnimation(it -> it.getWorld().playSound(it.getLocation(), Sound.ENTITY_PHANTOM_DEATH, 1, 10));
        animationBuilder.withPathfinderGoal(entity.getLocation().clone().add(new Random().nextBoolean() ? -15 : 15, 0,new Random().nextBoolean() ? -15 : 15));
        animationBuilder.withWalkingAnimation(it -> {
            for (Entity possibleTarget : it.getNearbyEntities(30, 30, 30)) {
                if(possibleTarget instanceof Player) {
                    animationBuilder.withPathfinderGoal(possibleTarget.getLocation().clone().add(0,20,0));
                }
            }
            for (Entity reachableTarget : it.getNearbyEntities(2, 40, 20)) {
                if(reachableTarget instanceof Player) {
                    if(ChanceUtils.roll(50)) {
                        Snowball snowball = it.getLocation().getWorld().spawn(it.getLocation(), Snowball.class);
                        snowball.setVelocity(new Vector(0,-1.5,0));
                        ((LivingEntity)reachableTarget).damage(2, it);
                        reachableTarget.getWorld().playSound(reachableTarget.getLocation(), Sound.BLOCK_SLIME_BLOCK_FALL,1, 1);
                    }
                }
            }
        });
        animationBuilder.apply();
    }

    @Override
    public void afterSpawn(ArmorStand entity) {
        entity.setVisible(false);
        entity.setArms(true);
        entity.getEquipment().setHelmet(new ItemBuilder(Material.PLAYER_HEAD).setPlayerSkull("Septumbre").build());
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            entity.addEquipmentLock(equipmentSlot, ArmorStand.LockType.ADDING_OR_CHANGING);
            entity.addEquipmentLock(equipmentSlot, ArmorStand.LockType.REMOVING_OR_CHANGING);
        }
        entity.setSmall(new Random().nextBoolean() && new Random().nextBoolean());
        entity.setCustomName("Bird");
        entity.setGravity(false);
    }

    @Override
    public void prepareSpawn(Object obj) {
        withYOffset(15.8);
    }
}
