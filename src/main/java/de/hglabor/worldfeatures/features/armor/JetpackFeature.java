package de.hglabor.worldfeatures.features.armor;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Random;

public class JetpackFeature extends Feature {
    public JetpackFeature() {
        super("Jetpack");
    }

    @Override
    public void onEnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ItemStack chestplate = Objects.requireNonNull(player.getEquipment()).getChestplate();
                    if(chestplate != null) {
                        if(chestplate.hasItemMeta()) {
                            if(chestplate.getItemMeta().hasCustomModelData()) {
                                if(chestplate.getItemMeta().getCustomModelData() == 2) {
                                    if(!player.isOnGround()) {
                                        if(!player.isJumping()) {
                                            if(hasFuel(player)) {
                                                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 15, 1, false,false));
                                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20, 1, false,false));
                                                player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 0.1f, 1);
                                                for (int i = 0; i < 7; i++) {
                                                    player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 0);
                                                }
                                                if(new Random().nextBoolean() && new Random().nextBoolean()) {
                                                    decreaseFuel(player);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 0, 20L);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ItemStack chestplate = Objects.requireNonNull(player.getEquipment()).getChestplate();
                    if(chestplate != null) {
                        if(chestplate.hasItemMeta()) {
                            if(chestplate.getItemMeta().hasCustomModelData()) {
                                if(chestplate.getItemMeta().getCustomModelData() == 2) {
                                    if(!player.isOnGround()) {
                                        if(!player.isJumping()) {
                                            for (int i = 0; i < 7; i++) {
                                                if(hasFuel(player)) {
                                                    player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 0);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 0, 2);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(!isEnabled) {
            return;
        }
        ItemStack chestplate = player.getEquipment().getChestplate();
        if(chestplate == null) {
            return;
        }
        if(!chestplate.hasItemMeta()) {
            return;
        }
        if(!chestplate.getItemMeta().hasCustomModelData()) {
            return;
        }
        if(chestplate.getItemMeta().getCustomModelData() != 2) {
            return;
        }
        if(player.isJumping()) {
            return;
        }
        if(!hasFuel(player)) {
            return;
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20, 3, false,false));
        if(!player.isOnGround()) {
            if(player.isSneaking()) {
                player.removePotionEffect(PotionEffectType.LEVITATION);
            }
            if(player.isSprinting()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 1, false,false));
                if(new Random().nextInt(120) < 40) {
                    decreaseFuel(player);
                }
            }
        }
    }

    @Override
    public void onServerStart(Plugin plugin) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "hglabor_jetpack"), new ItemBuilder(Material.NETHERITE_CHESTPLATE).withCustomModelData(2).setName(ChatColor.YELLOW + "Jetpack").withDescription(ChatColor.GRAY + "Mit diesem Jetpack kannst du fliegen.", ChatColor.GRAY + "Das Jetpack steuerst du so" + "   ", ChatColor.DARK_GRAY + "- "+ ChatColor.WHITE + "Sneaken: Sinken", ChatColor.DARK_GRAY + "- "+ ChatColor.WHITE + "Sprinten: Steigen").setUnbreakable(true).build());
        recipe.shape("E E", "INI", "RBR");
        recipe.setIngredient('E', Material.END_ROD);
        recipe.setIngredient('I', Material.IRON_BLOCK);
        recipe.setIngredient('N', Material.NETHERITE_CHESTPLATE);
        recipe.setIngredient('R', Material.REDSTONE);
        recipe.setIngredient('B', Material.BLAZE_POWDER);
        Bukkit.addRecipe(recipe);
    }

    private void decreaseFuel(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if(itemStack != null) {
                if(itemStack.getType() == Material.BLAZE_ROD) {
                    itemStack.setAmount(itemStack.getAmount()-1);
                    break;
                }
            }
        }
    }

    private boolean hasFuel(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if(itemStack != null) {
                if(itemStack.getType() == Material.BLAZE_ROD) {
                    return true;
                }
            }
        }
        return false;
    }
}
