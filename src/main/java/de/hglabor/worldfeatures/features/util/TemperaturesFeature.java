package de.hglabor.worldfeatures.features.util;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Random;

public class TemperaturesFeature extends Feature {

    public TemperaturesFeature() {
        super("Temperatures");
    }

    private static HashMap<Player, Integer> temperatureLevels = new HashMap<>();

    private int getTemperatureLevel(Player player) {
        return temperatureLevels.getOrDefault(player,0);
    }

    private void increaseTemperatureLevel(Player player) {
        int temperatureLevel = getTemperatureLevel(player);
        temperatureLevel+=1;
        temperatureLevels.remove(player);
        temperatureLevels.put(player, temperatureLevel);
    }

    private void decreaseTemperatureLevel(Player player) {
        int temperatureLevel = getTemperatureLevel(player);
        if(temperatureLevel-1 > 0) {
            temperatureLevel-=1;
            temperatureLevels.remove(player);
            temperatureLevels.put(player, temperatureLevel);
        }
    }

    @EventHandler
    public void onFood(PlayerItemConsumeEvent event) {
        if(!isEnabled) {
            return;
        }
        if(event.getItem().getType().name().contains("POTION")) {
            for (int i = 0; i < 7; i++) {
                decreaseTemperatureLevel(event.getPlayer());
            }
        }
    }

    @Override
    public void onServerStart(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) continue;
                    player.sendActionBar(Component.text(ChatColor.WHITE + "Temperature Level: " + colorTemperatureLevel(player)));
                    Block block = player.getLocation().getBlock();
                    Biome biome = player.getWorld().getBiome(block.getX(), block.getY(), block.getZ());
                    if(block.getType().name().contains("WATER")) {
                        decreaseTemperatureLevel(player);
                    }
                    if(player.getLocation().clone().subtract(0,1,0).getBlock().getType().name().contains("CLAY") && new Random().nextInt(9) < 6) {
                        for (int i = 0; i < 9; i++) {
                            decreaseTemperatureLevel(player);
                        }
                    }
                    if(player.getLocation().clone().subtract(0,1,0).getBlock().getType().name().contains("ICE") && new Random().nextInt(9) < 6) {
                        for (int i = 0; i < 9; i++) {
                            decreaseTemperatureLevel(player);
                        }
                    }
                    if(player.getLocation().clone().subtract(0,1,0).getBlock().getType().name().contains("MAGMA") && new Random().nextInt(9) < 6) {
                        for (int i = 0; i < 6; i++) {
                            increaseTemperatureLevel(player);
                        }
                    }
                    if(player.getLocation().clone().subtract(0,1,0).getBlock().getType().name().contains("LAVA") && new Random().nextInt(9) < 6) {
                        for (int i = 0; i < 6; i++) {
                            increaseTemperatureLevel(player);
                        }
                    }
                    if(player.getLocation().clone().subtract(0,1,0).getBlock().getType().name().contains("SAND") && new Random().nextInt(52) < 27) {
                        increaseTemperatureLevel(player);
                        if(getTemperatureLevel(player) > 7) {
                            player.damage(new Random().nextInt(4));
                        }
                        if(getTemperatureLevel(player) > 9) {
                            player.damage(new Random().nextInt(4));
                            player.setFireTicks(30);
                        }
                        if(getTemperatureLevel(player) > 6) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 50, 1, true,true));
                        }
                        if(getTemperatureLevel(player) > 12) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1, true,true));
                        }
                        if(getTemperatureLevel(player) > 15) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1, true,true));
                        }
                        if(getTemperatureLevel(player) > 22) {
                            player.damage(new Random().nextInt(10));
                        }
                        if(getTemperatureLevel(player) > 42) {
                            player.getWorld().strikeLightning(player.getLocation());
                            player.damage(134023);
                        }
                    }
                    if(biome != null) {
                        if(isHotBiome(biome)) {
                            if(new Random().nextInt(150) < 27) {
                                increaseTemperatureLevel(player);
                                if(getTemperatureLevel(player) > 7) {
                                    player.damage(new Random().nextInt(4));
                                }
                                if(getTemperatureLevel(player) > 9) {
                                    player.damage(new Random().nextInt(4));
                                    player.setFireTicks(30);
                                }
                                if(getTemperatureLevel(player) > 6) {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 50, 1, true,true));
                                }
                                if(getTemperatureLevel(player) > 12) {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1, true,true));
                                }
                                if(getTemperatureLevel(player) > 15) {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1, true,true));
                                }
                                if(getTemperatureLevel(player) > 22) {
                                    player.damage(new Random().nextInt(10));
                                }
                                if(getTemperatureLevel(player) > 42) {
                                    player.getWorld().strikeLightning(player.getLocation());
                                    player.damage(134023);
                                }
                                if(getTemperatureLevel(player) < -22) {
                                    player.getLocation().clone().add(0,1,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(1,1,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,1,1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(1,1,1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,-1,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(-1,1,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,1,-1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(-1,1,-1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(1,1,-1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(-1,1,1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,0,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(1,0,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,0,1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(1,0,1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,-0,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(-1,0,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,0,-1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(-1,0,-1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(1,0,-1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(-1,0,1).getBlock().setType(Material.BLUE_ICE);
                                }
                                if(getTemperatureLevel(player) < -29) {
                                    player.getLocation().clone().add(0,1,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(1,1,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,1,1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(1,1,1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,-1,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(-1,1,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,1,-1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(-1,1,-1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(1,1,-1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(-1,1,1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,0,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(1,0,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,0,1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(1,0,1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,-0,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(-1,0,0).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(0,0,-1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(-1,0,-1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(1,0,-1).getBlock().setType(Material.BLUE_ICE);
                                    player.getLocation().clone().add(-1,0,1).getBlock().setType(Material.BLUE_ICE);
                                    player.damage(new Random().nextInt(40));
                                }
                            }
                        } else {
                            if(new Random().nextInt(41) < 27) {
                                decreaseTemperatureLevel(player);
                            }
                        }
                    } else {
                        if(new Random().nextInt(41) < 27) {
                            decreaseTemperatureLevel(player);
                        }
                    }
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(),0,50);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if(getTemperatureLevel(event.getEntity()) > 10 && event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            event.setDeathMessage(event.getEntity().getDisplayName() + " §fwas too hot");
        }
        for (int i = 0; i < 50; i++) {
            decreaseTemperatureLevel(event.getEntity());
        }
    }

    private String colorTemperatureLevel(Player player) {
        int temperatureLevel = getTemperatureLevel(player);
        if(temperatureLevel == 5 || temperatureLevel == 4) {
            return ChatColor.YELLOW.toString() + temperatureLevel;
        } else if(temperatureLevel == 6 || temperatureLevel == 7) {
            return ChatColor.GOLD.toString() + temperatureLevel;
        } else if(temperatureLevel == 1 || temperatureLevel == 2) {
            return ChatColor.GREEN.toString() + temperatureLevel;
        } else if(temperatureLevel == 3) {
            return ChatColor.DARK_GREEN.toString() + temperatureLevel;
        } else if(temperatureLevel == 0) {
            return ChatColor.BLUE.toString() + temperatureLevel;
        } else if(temperatureLevel == 8 || temperatureLevel == 9) {
            return ChatColor.RED.toString() + temperatureLevel;
        } else if(temperatureLevel < -5  && temperatureLevel > -10) {
            return ChatColor.GREEN.toString() + temperatureLevel;
        } else if(temperatureLevel < -10 && temperatureLevel > -16) {
            return ChatColor.DARK_GREEN.toString() + temperatureLevel;
        } else if(temperatureLevel < -16 && temperatureLevel > -22) {
            return ChatColor.YELLOW.toString() + temperatureLevel;
        } else if(temperatureLevel < -22 && temperatureLevel > -29) {
            return ChatColor.GOLD.toString() + temperatureLevel;
        } else if(temperatureLevel < -29 && temperatureLevel > -36) {
            return ChatColor.RED.toString() + temperatureLevel;
        } else {
            return ChatColor.DARK_RED.toString() + temperatureLevel;
        }
    }

    private boolean isHotBiome(Biome biome) {
        String biomeName = biome.toString();
        return biomeName.contains("DESERT") || biomeName.contains("BADLANDS") || biomeName.contains("SAVANNA") || biome.getKey().getKey().toUpperCase().contains("VOLCANO") || biomeName.contains("BASALT") || biomeName.contains("CRIMSON");
    }
}
