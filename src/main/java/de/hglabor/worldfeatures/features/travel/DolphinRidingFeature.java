package de.hglabor.worldfeatures.features.travel;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import org.bukkit.*;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DolphinRidingFeature extends Feature {

    public DolphinRidingFeature() {
        super("dolphin_riding");
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if(!(event.getRightClicked() instanceof Dolphin)) {
            return;
        }
        if(!isEnabled) {
            return;
        }
        Dolphin dolphin = (Dolphin) event.getRightClicked();
        Player player = event.getPlayer();
        if(player.getInventory().getItemInMainHand().getType() == Material.SADDLE) {
            if(dolphin.getScoreboardTags().contains("saddled")) {
                return;
            }
            dolphin.addScoreboardTag("saddled");
            player.getInventory().getItemInMainHand().setAmount(0);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PIG_SADDLE, 1f, 1f);
            event.setCancelled(true);
        } else {
            if(dolphin.getScoreboardTags().contains("saddled")) {
                if(!dolphin.getPassengers().contains(player)) {
                    dolphin.addPassenger(player);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(!dolphin.getPassengers().contains(player)) {
                                cancel();
                            } else {
                                World world = player.getWorld();
                                Location location = player.getLocation();
                                for (int i = 0; i < 30; i++) {
                                    world.spawnParticle(Particle.WATER_SPLASH, location, 3);
                                }
                                world.playSound(location, Sound.ENTITY_DOLPHIN_SPLASH, 0.4f, 1f);
                                //world.playSound(location, Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED, 0.3f, 1f);
                                dolphin.setRotation(location.getYaw(), location.getPitch());
                                dolphin.setVelocity(player.getLocation().getDirection().multiply(0.9).setY(0.07));
                                Bukkit.getScheduler().runTaskLater(WorldFeatures.getPlugin(), new Runnable() {
                                    @Override
                                    public void run() {
                                        dolphin.setVelocity(dolphin.getVelocity().setY(-0.09));
                                    }
                                }, 3L);
                            }
                        }
                    }.runTaskTimer(WorldFeatures.getPlugin(), 0, 5L);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof Dolphin) {
            Dolphin dolphin = (Dolphin) event.getEntity();
            if(dolphin.getScoreboardTags().contains("saddled")) {
                event.getDrops().clear();
                dolphin.getWorld().dropItemNaturally(dolphin.getLocation(), new ItemStack(Material.SADDLE));
            }
        }
    }
}
