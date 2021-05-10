package de.hglabor.worldfeatures.features.armor;

import de.hglabor.utils.noriskutils.pvpbots.PvPBot;
import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GasFeature extends Feature {

    public GasFeature() {
        super("Gas");
    }

    @Override
    public void onEnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!isEnabled) {
                    cancel();
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (Block block : getNearbyBlocks(player.getLocation(), 4)) {
                            boolean foundGas = false;
                            for (int i = 0; i < 255; i++) {
                                Location location = block.getLocation().clone();
                                location.setY(i);
                                if(location.getBlock().getType() == Material.ANCIENT_DEBRIS || location.getBlock().getType() == Material.EMERALD_ORE) {
                                    if(block.getBiome() == null) {
                                        if(block.getWorld().getName().equalsIgnoreCase("world")) {
                                            foundGas = true;
                                        }
                                    }
                                }
                            }
                            if(foundGas) {
                                for (Block newBlock : getNearbyBlocks(block.getLocation(), 1)) {
                                    block.setMetadata("gas", new FixedMetadataValue(WorldFeatures.getPlugin(), true));
                                }
                            }
                        }
                        int count = 0;
                        for (Block block : getNearbyBlocks(player.getLocation(), 4)) {
                            if(block.hasMetadata("gas")) {
                                count++;
                                if(!hasGasMask(player)) {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 30, 3, true,true));
                                    player.damage(8);
                                } else {
                                    if(!hasGasFilters(player)) {
                                        player.damage(4);
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 30, 3, true,true));
                                        player.sendActionBar(Component.text(ChatColor.WHITE + "No gasfilters found."));
                                    } else {
                                        if(new Random().nextInt(45) < 7) {
                                            decreaseFilters(player);
                                        }
                                        decreaseGasMaskDurability(player);
                                        player.sendActionBar(Component.text(ChatColor.WHITE + "Filtering §2" + count + "m3 Gas"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 0, 20);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!isEnabled) {
                    cancel();
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        int count = 0;
                        for (Block block : getNearbyBlocks(player.getLocation(), 12)) {
                            if(block.hasMetadata("gas")) {
                                count++;
                                if(count > 270) {
                                    break;
                                }
                                for (int i = -4; i < 13; i++) {
                                    if(new Random().nextDouble() > 0.30) {
                                        block.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation().clone().add(0,i,0), 0, new Particle.DustOptions(Color.GREEN, 2f));
                                    }
                                    if(new Random().nextDouble() == 1) {
                                        block.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation().clone().add(0,i,0), 0, new Particle.DustOptions(Color.GREEN, 2f));
                                        block.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation().clone().add(0,i,0), 0, new Particle.DustOptions(Color.OLIVE, 2f));
                                    }
                                    if(new Random().nextDouble() > 0.60) {
                                        block.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation().clone().add(0,i,0), 0, new Particle.DustOptions(Color.OLIVE, 2f));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 0, 20);
    }

    private void registerGasFilter(Plugin plugin) {
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "hglabor_gasfilter"), new ItemBuilder(Material.CLAY_BALL).withCustomModelData(2).setName(ChatColor.WHITE + "Gasfilter").build());
        shapelessRecipe.addIngredient(Material.CLAY_BALL);
        shapelessRecipe.addIngredient(Material.SEA_PICKLE);
        shapelessRecipe.addIngredient(Material.IRON_NUGGET);
        Bukkit.addRecipe(shapelessRecipe);
    }

    private void registerGasMask(Plugin plugin) {
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "hglabor_gasmask"), new ItemBuilder(Material.NETHERITE_HELMET).withCustomModelData(2).setName(ChatColor.YELLOW + "Gasmask").withDescription("§7Diese Gasmaske verbraucht 2 Filter pro m3 Gas (pro Sekunde).").build());
        shapelessRecipe.addIngredient(Material.NETHERITE_HELMET);
        shapelessRecipe.addIngredient(Material.WITHER_SKELETON_SKULL);
        shapelessRecipe.addIngredient(Material.GLASS_PANE);
        Bukkit.addRecipe(shapelessRecipe);
    }

    @Override
    public void onServerStart(Plugin plugin) {
        registerGasFilter(plugin);
        registerGasMask(plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        for (Block block : getNearbyBlocks(event.getEntity().getLocation(), 4)) {
            if(block.hasMetadata("gas")) {
                event.setDeathMessage(event.getEntity().getDisplayName() + " §fdied of gas");
                break;
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(isEnabled) {
            if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.STRUCTURE_BLOCK) {
                event.setCancelled(true);
                event.getBlock().setMetadata("gas", new FixedMetadataValue(WorldFeatures.getPlugin(), true));
            }
            if(event.getBlock().hasMetadata("gas")) {
                event.getBlock().removeMetadata("gas", WorldFeatures.getPlugin());
            }
        }
    }

    @EventHandler
    public void onPotionSplash(ProjectileHitEvent event) {
        if(event.getHitBlock() != null && event.getEntity() instanceof ThrownPotion) {
            for (Block block : getNearbyBlocks(event.getHitBlock().getLocation(), 5)) {
                block.removeMetadata("gas", WorldFeatures.getPlugin());
            }
        }
    }

    private void decreaseFilters(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if(itemStack != null) {
                if(itemStack.getType() == Material.CLAY_BALL) {
                    if(itemStack.hasItemMeta()) {
                        if(itemStack.getItemMeta().hasCustomModelData()) {
                            if(itemStack.getItemMeta().getCustomModelData() == 2) {
                                itemStack.setAmount(itemStack.getAmount()-1);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean hasGasFilters(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if(itemStack != null) {
                if(itemStack.getType() == Material.CLAY_BALL) {
                    if(itemStack.hasItemMeta()) {
                        if(itemStack.getItemMeta().hasCustomModelData()) {
                            if(itemStack.getItemMeta().getCustomModelData() == 2) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean hasGasMask(Player player) {
        try {
            ItemStack itemStack = Objects.requireNonNull(player.getEquipment()).getItem(EquipmentSlot.HEAD);
            if(itemStack.getType() == Material.NETHERITE_HELMET) {
                if(itemStack.hasItemMeta()) {
                    if(itemStack.getItemMeta().hasCustomModelData()) {
                        return itemStack.getItemMeta().getCustomModelData() == 2;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void decreaseGasMaskDurability(Player player) {
        ItemStack itemStack = player.getEquipment().getItem(EquipmentSlot.HEAD);
        if(itemStack.getType() == Material.NETHERITE_HELMET) {
            itemStack.setDurability((short) (itemStack.getDurability()+0.9));
        }
    }

    public List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                blocks.add(new Location(location.getWorld(),x,location.getBlockY(),z).getBlock());
                blocks.add(new Location(location.getWorld(),x,location.getBlockY()-1,z).getBlock());
                blocks.add(new Location(location.getWorld(),x,location.getBlockY()+1,z).getBlock());
                for (int i = -0; i < 4; i++) {
                    blocks.add(new Location(location.getWorld(),x,location.getBlockY()+i,z).getBlock());
                    blocks.add(new Location(location.getWorld(),x,location.getBlockY()-i,z).getBlock());
                }
            }
        }
        return blocks;
    }

}
