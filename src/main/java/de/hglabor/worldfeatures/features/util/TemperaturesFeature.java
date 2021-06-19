package de.hglabor.worldfeatures.features.util;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.features.armor.GasFeature;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.BiomeBase;
import net.minecraft.server.v1_16_R3.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TemperaturesFeature extends Feature {

    public TemperaturesFeature() {
        super("Temperatures");
    }

    private static final HashMap<Player, Float> temperatureLevels = new HashMap<>();

    private final HashMap<Player, Biome> lastBiomes = new HashMap<>();

    private Float getTemperatureLevel(Player player) {
        return temperatureLevels.getOrDefault(player, 0f);
    }

    private Biome getLastBiom(Player player) {
        return lastBiomes.getOrDefault(player, player.getLocation().getBlock().getBiome());
    }

    private void setDefaultTemperatureLevel(Player player) {
        float temperatureLevel = getTemperatureLevel(player);
        temperatureLevel = 0;
        temperatureLevels.remove(player);
        temperatureLevels.put(player, temperatureLevel);
    }

    private void increaseTemperatureLevel(Player player, float toIncrease, long delay, long period) {
        new BukkitRunnable() {
            float temperatureLevel = getTemperatureLevel(player);
            final float defaultValue = temperatureLevel;

            @Override
            public void run() {
                temperatureLevel += 0.1;
                temperatureLevels.remove(player);
                temperatureLevels.put(player, temperatureLevel);
                if (temperatureLevel - defaultValue >= toIncrease) {
                    cancel();
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), delay, period);
    }

    private void increaseTemperatureLevel(Player player, float toIncrease, long period) {
        new BukkitRunnable() {
            float temperatureLevel = getTemperatureLevel(player);
            final float defaultValue = temperatureLevel;

            @Override
            public void run() {
                temperatureLevel += 0.1;
                temperatureLevels.remove(player);
                temperatureLevels.put(player, temperatureLevel);
                if (temperatureLevel - defaultValue >= toIncrease) {
                    cancel();
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 40, period);
    }

    private void decreaseTemperatureLevel(Player player, float toDecrease, long delay, long period) {
        new BukkitRunnable() {
            float temperatureLevel = getTemperatureLevel(player);
            final float defaultValue = temperatureLevel;

            @Override
            public void run() {
                temperatureLevel -= 0.1;
                temperatureLevels.remove(player);
                temperatureLevels.put(player, temperatureLevel);
                if (Math.abs(temperatureLevel - defaultValue) >= toDecrease) {
                    cancel();
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), delay, period);
    }

    private void decreaseTemperatureLevel(Player player, float toDecrease, long period) {
        new BukkitRunnable() {
            float temperatureLevel = getTemperatureLevel(player);
            final float defaultValue = temperatureLevel;

            @Override
            public void run() {
                temperatureLevel -= 0.1;
                temperatureLevels.remove(player);
                temperatureLevels.put(player, temperatureLevel);
                if (Math.abs(temperatureLevel - defaultValue) >= toDecrease) {
                    cancel();
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 40, period);
    }

    @EventHandler
    public void onFood(PlayerItemConsumeEvent event) {
        if (!isEnabled) {
            return;
        }
        if (event.getItem().getType().name().contains("POTION") || event.getItem().getType().name().contains("BOTTLE")) {
            if (event.getItem().getType().name().contains("HONEY")) return;
            decreaseTemperatureLevel(event.getPlayer(), 2.4f, 13);
        }
        if (event.getItem().getType().name().contains("MILK")) {
            decreaseTemperatureLevel(event.getPlayer(), 1f, 15);
        }
        if (event.getItem().getType().name().contains("COOKED") || event.getItem().getType().name().contains("BAKED")) {
            increaseTemperatureLevel(event.getPlayer(), 0.7f, 28);
        }
    }

    @Override
    public void onServerStart(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.sendActionBar(Component.text(ChatColor.WHITE + "Temperature Level: " + colorTemperatureLevel(player)));
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 0, 30);
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Block block = player.getLocation().getBlock();
                    Biome biome = player.getWorld().getBiome(block.getX(), block.getY(), block.getZ());
                    String blockName = player.getLocation().clone().subtract(0, 1, 0).getBlock().getType().name();

                    if(player.getGameMode() == GameMode.CREATIVE)
                        continue;
                    //biome
                    double biomeTemp = getBiomeTemperature(player.getLocation());
                    biomeTemp =  biomeTemp * 10;
                    if(biomeTemp < 7) {
                        if(getTemperatureLevel(player) > biomeTemp - 10)
                            decreaseTemperatureLevel(player, 0.2f, 30, 20);
                    } else {
                        if(getTemperatureLevel(player) < biomeTemp)
                            increaseTemperatureLevel(player, 0.2f, 30, 20);
                    }
                    //biome

                    //--armor
                    ItemStack[] armorContents = player.getInventory().getArmorContents();
                    int armorPiecesOn = armorContents.length;
                    for(ItemStack item : armorContents) {
                        if (item == null) {
                            armorPiecesOn--;
                        }
                    }
                    if (armorPiecesOn > 0 && new Random().nextInt(130) < 2 + armorPiecesOn) {
                        if(getTemperatureLevel(player) < 14)
                            increaseTemperatureLevel(player, 0.8f, 0, 8);
                    }
                    //--armor

                    //-----height
                    if (player.getLocation().getY() >= 90 && player.getLocation().getY() <= 130 && new Random().nextInt(34) < 5) {
                        if (!(getTemperatureLevel(player) < -14))
                            decreaseTemperatureLevel(player, 0.4f, 30);
                    }
                    if (player.getLocation().getY() > 130 && new Random().nextInt(27) < 4) {
                        if (!(getTemperatureLevel(player) < -30))
                            decreaseTemperatureLevel(player, 0.9f, 25);
                    }
                    if (player.getLocation().getY() < 55 && player.getLocation().getY() >= 25 && new Random().nextInt(6) < 1) {
                        if (!(getTemperatureLevel(player) > 15))
                            increaseTemperatureLevel(player, 0.5f, 30);
                    }
                    if (player.getLocation().getY() < 25 && new Random().nextInt(7) < 1) {
                        if (!(getTemperatureLevel(player) > 26))
                            increaseTemperatureLevel(player, 0.8f, 15);
                    }
                    //-----height

                    //--------block
                    if (block.getType().name().contains("WATER") && new Random().nextInt(19) < 17) {
                        decreaseTemperatureLevel(player, 0.4f, 15, 25);
                    }
                    if (player.getLocation().clone().subtract(0, 1, 0).getBlock().getType().name().contains("CLAY") && new Random().nextInt(9) < 7) {
                        decreaseTemperatureLevel(player, 0.6f, 15, 20);
                    }
                    if (player.getLocation().clone().subtract(0, 1, 0).getBlock().getType().name().contains("ICE")) {
                        decreaseTemperatureLevel(player, 1.7f, 25, 13);
                    }
                    if (player.getLocation().clone().subtract(0, 1, 0).getBlock().getType().name().contains("SNOW") && new Random().nextInt(9) < 8) {
                        decreaseTemperatureLevel(player, 1.3f, 30, 20);
                    }
                    //--------block

                    if (!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                        //--------in der nähe von kalt/heiß
                        for(Block block1 : new GasFeature().getNearbyBlocks(player.getLocation(), 10)) {
                            if (block1.getType().name().contains("LAVA") && new Random().nextInt(7) == 0) {
                                if(getTemperatureLevel(player) < 15)
                                    increaseTemperatureLevel(player, 0.3f, 30, 30);
                            }
                        }
                        for(Block block1 : new GasFeature().getNearbyBlocks(player.getLocation(), 6)) {
                            if (block1.getType().name().contains("LAVA") && new Random().nextInt(10) == 0) {
                                if(getTemperatureLevel(player) < 21)
                                    increaseTemperatureLevel(player, 0.6f, 25, 20);
                            }
                        }
                        for(Block block1 : new GasFeature().getNearbyBlocks(player.getLocation(), 4)) {
                            if (block1.getType().name().contains("LAVA") && new Random().nextInt(12) == 0) {
                                if(getTemperatureLevel(player) < 30)
                                    increaseTemperatureLevel(player, 0.8f, 20, 15);
                            }
                        }
                        for(Block block1 : new GasFeature().getNearbyBlocks(player.getLocation(), 2)) {
                            if (block1.getType().name().contains("LAVA") && new Random().nextInt(5) == 0) {
                                increaseTemperatureLevel(player, 2f, 8, 12);
                            }
                            if (block1.getType().name().contains("FIRE") && new Random().nextInt(4) == 0) {
                                if(getTemperatureLevel(player) < 26)
                                    increaseTemperatureLevel(player, 1.5f, 15);
                            }
                        }
                        for(Block block1 : new GasFeature().getNearbyBlocks(player.getLocation(), 3)) {
                            if (block1.getType().name().contains("TORCH") && new Random().nextInt(9) < 6) {
                                if(getTemperatureLevel(player) < 8)
                                    increaseTemperatureLevel(player, 0.9f, 10, 15);
                            }
                            if (block1.getType().name().contains("FURNACE") && new Random().nextInt(5) == 0) {
                                if(!(getTemperatureLevel(player) > 12)) {
                                    if (block1.getBlockData() instanceof Furnace) {
                                        Furnace furnaceData = (Furnace) block1.getBlockData();
                                        if (furnaceData.isLit())
                                            increaseTemperatureLevel(player, 1.4f, 20);
                                    }
                                }
                            }
                            if(block1.getType().name().contains("GLOWSTONE") && new Random().nextInt(5) == 0) {
                                if(getTemperatureLevel(player) < 16)
                                    increaseTemperatureLevel(player, 0.5f, 15);
                            }
                            if(block1.getType().name().contains("REDSTONE") && new Random().nextInt(7) == 0) {
                                if(getTemperatureLevel(player) < 11) {
                                    if(block1.getBlockData() instanceof Lightable) {
                                        Lightable lightable = (Lightable) block1.getBlockData();
                                        if(lightable.isLit())
                                            increaseTemperatureLevel(player, 0.7f, 10);
                                    } else player.sendMessage("SCHeiß tag");
                                }
                            }
                        }
                        //--------in der nähe von kalt/heiß
                        //--------block
                        if (player.getLocation().clone().subtract(0, 1, 0).getBlock().getType().name().contains("MAGMA") && new Random().nextInt(21) < 20) {
                            increaseTemperatureLevel(player, 2.4f, 15, 15);
                        }
                        if (player.getLocation().getBlock().getType().name().contains("LAVA")) {
                            increaseTemperatureLevel(player, 5.5f, 10, 6);
                        }
                        if (player.getLocation().clone().subtract(0, 1, 0).getBlock().getType().name().contains("SAND") && new Random().nextInt(52) < 42) {
                            increaseTemperatureLevel(player, 0.3f, 15);
                        }
                        //--------block
                    }

                    if (getTemperatureLevel(player) > 17 && new Random().nextInt(18) < 17) {
                        player.damage(new Random().nextBoolean() ? 0.5 : 0.8);
                    }
                    if (getTemperatureLevel(player) > 24) {
                        player.damage(new Random().nextBoolean() ? 0.7 : 1.3);
                    }
                    if (getTemperatureLevel(player) > 26 && new Random().nextInt(5) == 0) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 6 * 20, 2, true, true));
                    }
                    if (getTemperatureLevel(player) > 30 && new Random().nextInt(4) == 0) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 0, true, true));
                    }
                    if (getTemperatureLevel(player) > 30 && new Random().nextInt(4) == 0) {
                        player.damage(0.3);
                        player.setFireTicks(40);
                    }
                    if (getTemperatureLevel(player) > 36 && new Random().nextInt(9) == 0) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 6 * 20, 0, true, true));
                    }
                    if (getTemperatureLevel(player) > 41 && new Random().nextInt(5) == 0) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 7 * 20, 0, true, true));
                        player.damage(0.1);
                        player.setFireTicks(15);
                    }
                    if (getTemperatureLevel(player) > 49) {
                        Location loc = player.getLocation();
                        player.damage(134023);
                        loc.getWorld().strikeLightning(loc);
                        loc.getBlock().setType(Material.FIRE);
                    }

                    if (getTemperatureLevel(player) < -17 && new Random().nextInt(10) < 9) {
                        player.damage(new Random().nextBoolean() ? 0.5 : 0.8);
                    }
                    if (getTemperatureLevel(player) < -25) {
                        player.damage(new Random().nextBoolean() ? 0.2 : 0.4);
                    }
                    if (getTemperatureLevel(player) < -25 && new Random().nextInt(7) == 0) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 7 * 20, 2, true, true));
                    }
                    if (getTemperatureLevel(player) < -31 && new Random().nextInt(6) == 0) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 4 * 20, 0, true, true));
                    }
                    if (getTemperatureLevel(player) < -37 && new Random().nextInt(9) == 0) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 11 * 20, 1, true, true));
                    }
                    if (getTemperatureLevel(player) < -42 && new Random().nextInt(7) == 0) {
                        player.getLocation().clone().add(0, 1, 0).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(1, 1, 0).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(0, 1, 1).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(1, 1, 1).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(0, -1, 0).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(-1, 1, 0).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(0, 1, -1).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(-1, 1, -1).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(1, 1, -1).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(-1, 1, 1).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(0, 0, 0).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(1, 0, 0).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(0, 0, 1).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(1, 0, 1).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(0, -0, 0).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(-1, 0, 0).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(0, 0, -1).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(-1, 0, -1).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(1, 0, -1).getBlock().setType(Material.BLUE_ICE);
                        player.getLocation().clone().add(-1, 0, 1).getBlock().setType(Material.BLUE_ICE);
                    }
                    if (getTemperatureLevel(player) < -48) {
                        List<Material> ices = Arrays.asList(Material.ICE, Material.BLUE_ICE, Material.PACKED_ICE);
                        Location loc = player.getLocation();
                        player.damage(134023);
                        loc.getBlock().setType((Material) ices.toArray()[new Random().nextInt(ices.size())]);
                        loc.add(0, 1, 0);
                        loc.getBlock().setType((Material) ices.toArray()[new Random().nextInt(ices.size())]);
                    }
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 0, 50);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (getTemperatureLevel(event.getEntity()) > 10 && event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            List<String> hotDeathMessages = Arrays.asList("probably jumped into a volcano", "stayed in sunlight for too long", "had a heat stroke");
            int r = new Random().nextInt(hotDeathMessages.size());
            event.setDeathMessage(event.getEntity().getDisplayName() + " §c" + hotDeathMessages.toArray()[r]);
        }
        if (getTemperatureLevel(event.getEntity()) < -10 && event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            List<String> coldDeathMessages = Arrays.asList("took a cold shower", "died in ice", "had a cold adventure");
            int r = new Random().nextInt(coldDeathMessages.size());
            event.setDeathMessage(event.getEntity().getDisplayName() + " §b" + coldDeathMessages.toArray()[r]);
        }
        setDefaultTemperatureLevel(event.getEntity());
    }

    private String colorTemperatureLevel(Player player) {
        float temperatureLevel = getTemperatureLevel(player);
        String finalTemperatureLevel;
        if (temperatureLevel != 0) {
            finalTemperatureLevel = String.valueOf(temperatureLevel).split("\\.")[0] + "." + String.valueOf(temperatureLevel).split("\\.")[1].subSequence(0, 1);
        } else finalTemperatureLevel = String.valueOf(0);
        if (temperatureLevel >= -0.5 && temperatureLevel <= 0.5) {
            return ChatColor.of("#249ad5").toString() + finalTemperatureLevel; //blue fireoly
        } else if (temperatureLevel >= 0.5 && temperatureLevel <= 2.5) {
            return ChatColor.BLACK.toString() + finalTemperatureLevel; //türkis blau  #22b5c7
        } else if (temperatureLevel >= 2.5 && temperatureLevel <= 3.5) {
            return ChatColor.of("#23d9bb").toString() + finalTemperatureLevel; //türkis
        } else if (temperatureLevel >= 3.5 && temperatureLevel <= 5.5) {
            return ChatColor.of("#25d07a").toString() + finalTemperatureLevel; //grün grün grün sind alle meine kleider
        } else if (temperatureLevel >= 5.5 && temperatureLevel <= 7.5) {
            return ChatColor.of("#33ca31").toString() + finalTemperatureLevel; //greener green
        } else if (temperatureLevel >= 7.5 && temperatureLevel <= 9.5) {
            return ChatColor.of("#60e42f").toString() + finalTemperatureLevel; //gelb green
        } else if (temperatureLevel >= 9.5 && temperatureLevel <= 12.5) {
            return ChatColor.of("#a9de31").toString() + finalTemperatureLevel; //GELB GELB green
        } else if (temperatureLevel >= 12.5 && temperatureLevel <= 16.5) {
            return ChatColor.of("#ddee11").toString() + finalTemperatureLevel; //Gelb
        } else if (temperatureLevel >= 16.5 && temperatureLevel <= 23.5) {
            return ChatColor.of("#eec911").toString() + finalTemperatureLevel; //orange
        } else if (temperatureLevel >= 23.5 && temperatureLevel <= 30.5) {
            return ChatColor.of("#eec911").toString() + finalTemperatureLevel; //orange rot
        } else if (temperatureLevel >= 30.5 && temperatureLevel <= 37.5) {
            return ChatColor.of("#ee5e11").toString() + finalTemperatureLevel; //rot orange
        } else if (temperatureLevel >= 37.5 && temperatureLevel <= 45.5) {
            return ChatColor.of("#ee2811").toString() + finalTemperatureLevel; //red
        } else if (temperatureLevel >= 45.5) {
            return ChatColor.DARK_RED.toString() + finalTemperatureLevel;
        } else if (temperatureLevel <= 0.5 && temperatureLevel >= -3) {
            return ChatColor.of("#0f68da").toString() + finalTemperatureLevel; //darker blue fireoly
        } else if (temperatureLevel <= -4 && temperatureLevel >= -9) {
            return ChatColor.of("#0f43da").toString() + finalTemperatureLevel; //even darker blue.
        } else if (temperatureLevel <= -10 && temperatureLevel >= -15) {
            return ChatColor.of("#1339e4").toString() + finalTemperatureLevel; //blue, stop getting darker
        } else if (temperatureLevel <= -16 && temperatureLevel >= -22) {
            return ChatColor.of("#1013c2").toString() + finalTemperatureLevel; //jz reicht aber :rage:
        } else if (temperatureLevel <= -23 && temperatureLevel >= -29) {
            return ChatColor.of("#220da8").toString() + finalTemperatureLevel; //dunkles blue lila
        } else if (temperatureLevel <= -30 && temperatureLevel >= -37) {
            return ChatColor.of("#350c9b").toString() + finalTemperatureLevel; //das wie oben nur stärker
        } else if (temperatureLevel <= -38 && temperatureLevel >= -45) {
            return ChatColor.of("#350c9b").toString() + finalTemperatureLevel; //dunkel violet
        } else if (temperatureLevel <= -46) {
            return ChatColor.of("#350c9b").toString() + finalTemperatureLevel; //sehr dunkel violet pink
        } else return ChatColor.DARK_RED.toString() + finalTemperatureLevel;
    }

    public double getBiomeTemperature(Location location) {
        try {
            BiomeBase biomeBase = ((CraftWorld) location.getWorld()).getHandle().getBiome(new BlockPosition(location.getBlockX(), location.getY(), location.getBlockZ()));
            double biomeTemperature = biomeBase.getAdjustedTemperature(new BlockPosition(location.getBlockX(), location.getY(), location.getBlockZ()));
            return biomeTemperature;
        } catch (NoClassDefFoundError e) { //this happens when server updates to newer or older version
            return 0.0;
        }
    }
}
