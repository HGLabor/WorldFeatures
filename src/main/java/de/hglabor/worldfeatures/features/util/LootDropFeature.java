package de.hglabor.worldfeatures.features.util;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class LootDropFeature extends Feature {

    public LootDropFeature() {
        super("LootDrops");
    }

    private static Location lootDropLocation = new Location(Bukkit.getWorld("world"), 0,250, 0);
    private static Material[] lootDropLoot = {Material.DIAMOND_BLOCK, Material.ANCIENT_DEBRIS, Material.NETHER_BRICK, Material.GOLD_INGOT, Material.GOLDEN_APPLE, Material.NETHER_BRICK, Material.GOLD_INGOT, Material.SCUTE, Material.PRISMARINE_SHARD, Material.PRISMARINE_CRYSTALS, Material.NETHER_WART, Material.LAPIS_LAZULI, Material.LAPIS_LAZULI, Material.LAPIS_LAZULI, Material.IRON_NUGGET, Material.LAPIS_LAZULI, Material.GOLD_INGOT, Material.GOLD_INGOT, Material.ENCHANTED_GOLDEN_APPLE, Material.NETHERITE_SCRAP, Material.DIAMOND, Material.IRON_NUGGET, Material.OBSIDIAN, Material.ARROW, Material.SPECTRAL_ARROW, Material.TNT, Material.IRON_BLOCK, Material.GOLD_NUGGET, Material.BAKED_POTATO, Material.ROTTEN_FLESH};

    @Override
    public void onEnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!isEnabled) {
                    cancel();
                } else {
                    lootDropLocation.setX(new Random().nextInt(100000)-new Random().nextInt(100000));
                    lootDropLocation.setZ(new Random().nextInt(100000)-new Random().nextInt(100000));
                    spawnLootDrop();
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 10*20, 7200*20);
    }

    private void spawnLootDrop() {
        lootDropLocation.setY(lootDropLocation.getWorld().getHighestBlockYAt(lootDropLocation));
        Bukkit.broadcastMessage("§8§l> §7Lootdrop at §b" + lootDropLocation.getBlockX() + " " + lootDropLocation.getBlockY() + " " + lootDropLocation.getBlockZ() + " §7in §b3:00 §7minutes.");
        World world = lootDropLocation.getWorld();
        FallingBlock fallingBlock = world.spawnFallingBlock(lootDropLocation.clone().add(0,405,0), new MaterialData(Material.BARREL));
        fallingBlock.setDropItem(false);
        fallingBlock.setHurtEntities(true);
        final int[] i = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                i[0]++;
                if(i[0] == 1500) {
                    cancel();
                } else {
                    world.spawnParticle(Particle.EXPLOSION_LARGE, lootDropLocation, 3);
                    world.playSound(lootDropLocation, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 10f);
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 0, 2);
        Bukkit.getScheduler().runTaskLater(WorldFeatures.getPlugin(), () -> {
            Bukkit.getScheduler().runTaskLater(WorldFeatures.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    fallingBlock.remove();
                    lootDropLocation.getBlock().setType(Material.CHEST);
                    Chest chest = (Chest) lootDropLocation.getBlock().getState();
                    Inventory inventory = chest.getBlockInventory();
                    for (int i = 0; i < 18; i++) {
                        int randomSlot = new Random().nextInt(inventory.getSize()-1);
                        inventory.setItem(randomSlot, new ItemStack(lootDropLoot[new Random().nextInt(lootDropLoot.length)], new Random().nextInt(5)));
                    }
                    inventory.setItem(14, new ItemBuilder(Material.FEATHER).withCustomModelData(1).setName(ChatColor.YELLOW + "Parachute").build());
                }
            }, 30L);
        }, 3*60*20);
    }
}
