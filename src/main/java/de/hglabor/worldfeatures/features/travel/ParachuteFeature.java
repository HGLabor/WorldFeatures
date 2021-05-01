package de.hglabor.worldfeatures.features.travel;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ParachuteFeature extends Feature {
    public ParachuteFeature() {
        super("Parachute");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(!isEnabled) {
            return;
        }
        Player player = event.getPlayer();
        if(hasParachute(player)) {
            if(!player.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
                if(player.getFallDistance() > 32) {
                    activateParachute(player);
                }
            }
        }
    }

    private void activateParachute(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 10*20, 22, false,false));
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1f, 1f);
        player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(), 0);
        ArrayList<ArmorStand> armorStands = new ArrayList<>();
        HashMap<ArmorStand, Double> locationMapX = new HashMap<>();
        HashMap<ArmorStand, Double> locationMapZ = new HashMap<>();
        double x = -0.5;
        for (int i = 0; i < 10; i++) {
            x+=0.3;
            ArmorStand armorStand = player.getWorld().spawn(player.getLocation().clone().add(x, 1.2,0), ArmorStand.class);
            armorStand.setVisible(false);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.WHITE_WOOL));
            armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
            armorStand.setInvulnerable(true);
            armorStand.setLeashHolder(player);
            armorStands.add(armorStand);
            locationMapX.put(armorStand, x);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!player.hasPotionEffect(PotionEffectType.SLOW_FALLING) || player.isOnGround()) {
                        locationMapX.remove(armorStand);
                        armorStands.remove(armorStand);
                        armorStand.remove();
                        player.stopSound(Sound.ITEM_ELYTRA_FLYING);
                    }
                }
            }.runTaskTimer(WorldFeatures.getPlugin(), 0,1);
        }
        double z = -0.5;
        for (int i = 0; i < 10; i++) {
            z+=0.3;
            ArmorStand armorStand = player.getWorld().spawn(player.getLocation().clone().add(0, 1.2,z), ArmorStand.class);
            armorStand.setVisible(false);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.WHITE_WOOL));
            armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
            armorStand.setInvulnerable(true);
            armorStand.setLeashHolder(player);
            armorStands.add(armorStand);
            locationMapZ.put(armorStand, z);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!player.hasPotionEffect(PotionEffectType.SLOW_FALLING) || player.isOnGround()) {
                        locationMapZ.remove(armorStand);
                        armorStands.remove(armorStand);
                        armorStand.remove();
                        player.stopSound(Sound.ITEM_ELYTRA_FLYING);
                    }
                }
            }.runTaskTimer(WorldFeatures.getPlugin(), 0,1);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!player.hasPotionEffect(PotionEffectType.SLOW_FALLING) || player.isOnGround()) {
                    cancel();
                } else {
                    player.playSound(player.getLocation(), Sound.ITEM_ELYTRA_FLYING, 0.08f, 1f);
                    for (ArmorStand armorStand : armorStands) {
                        armorStand.teleport(player.getLocation().clone().add(locationMapX.getOrDefault(armorStand, 0D),1.2,locationMapZ.getOrDefault(armorStand, 0D)));
                    }

                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 0, 2);
    }

    @Override
    public void onServerStart(Plugin plugin) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "hglabor_parachute"), new ItemBuilder(Material.FEATHER).withCustomModelData(1).setName(ChatColor.YELLOW + "Parachute").build());
        recipe.shape("FWF", " S ", " S ");
        recipe.setIngredient('W', Material.WHITE_WOOL);
        recipe.setIngredient('F', Material.FEATHER);
        recipe.setIngredient('S', Material.STICK);
        Bukkit.addRecipe(recipe);
    }

    private boolean hasParachute(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if(itemStack != null) {
                if(itemStack.getType() == Material.FEATHER) {
                    if(itemStack.hasItemMeta()) {
                        if(itemStack.getItemMeta().hasCustomModelData()) {
                            if(itemStack.getItemMeta().getCustomModelData() == 1) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
