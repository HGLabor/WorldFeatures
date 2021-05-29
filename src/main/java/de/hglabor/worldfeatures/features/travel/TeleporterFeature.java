package de.hglabor.worldfeatures.features.travel;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.utils.ItemBuilder;
import de.hglabor.worldfeatures.utils.gui.inventory.InventoryBuilder;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class TeleporterFeature extends Feature {

    public TeleporterFeature() {
        super("Teleporters");
    }

    private static final NamespacedKey KEY = new NamespacedKey(WorldFeatures.getPlugin(), "telepad");
    private static final NamespacedKey RECIPE_KEY = new NamespacedKey(WorldFeatures.getPlugin(), "telepad_recipe");

    @Override
    public void onServerStart(Plugin plugin) {
        ShapelessRecipe recipe = new ShapelessRecipe(RECIPE_KEY, new ItemBuilder(Material.END_PORTAL_FRAME).setName("§dTelepad").build());
        recipe.addIngredient(Material.END_CRYSTAL);
        recipe.addIngredient(Material.ENDER_EYE);
        recipe.addIngredient(Material.DRAGON_BREATH);
        Bukkit.addRecipe(recipe);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(!isEnabled) {
            return;
        }
        if(!event.hasItem()) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        if(itemStack.getType() != Material.END_PORTAL_FRAME) {
            return;
        }
        event.setCancelled(true);
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if(dataContainer.get(KEY, PersistentDataType.STRING) == null) {
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL,1.0f,1.0f);
            player.openInventory(new InventoryBuilder()
            .withName(ChatColor.DARK_PURPLE + "Teleport Points")
                    .withItem(new ItemBuilder(Material.BLAZE_POWDER).setName(ChatColor.LIGHT_PURPLE + "Create one").withDescription("§7   ", "§7§oCreate a warp point where you can warp to.").build(), 0, whoClicked -> {
                        whoClicked.getLocation().clone().subtract(0,1,0).getBlock().setType(Material.END_PORTAL_FRAME);
                                ArmorStand armorStand = whoClicked.getWorld().spawn(whoClicked.getLocation().clone().subtract(0,1.5,0), ArmorStand.class);
                                armorStand.setVisible(false);
                                armorStand.setInvulnerable(true);
                                for(EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                                    armorStand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.ADDING_OR_CHANGING);
                                    armorStand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.REMOVING_OR_CHANGING);
                                }
                                armorStand.setCustomNameVisible(true);
                                armorStand.setCustomName("Telepad Exit Route");
                                dataContainer.set(KEY, PersistentDataType.STRING, locationToString(player.getLocation()));
                                itemStack.setItemMeta(meta);
                                whoClicked.getWorld().playSound(whoClicked.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN,1.0f,1.0f);
                                whoClicked.closeInventory();
                    })
                    .withSlots(9)
                    .build());
        } else {
            Location location = parseLocation(dataContainer.get(KEY, PersistentDataType.STRING));
            player.openInventory(new InventoryBuilder()
                    .withName(ChatColor.DARK_PURPLE + "Teleport Points")
                    .withItem(new ItemBuilder(Material.ENDER_EYE).setName(ChatColor.LIGHT_PURPLE + "Exit Route #1").withDescription("§7   ", "§7§oRoute exit: §f§o" + location.getWorld().getName() + "§7§o, §f§o" + location.getBlockX() + "§7§o, §f§o" + location.getBlockY() + "§7§o, §f§o" + location.getBlockZ()).build(), 4, whoClicked -> {
                                whoClicked.getWorld().playSound(whoClicked.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL,1.0f,1.0f);
                                whoClicked.teleport(location);
                                whoClicked.closeInventory();
                            }
                    )
                    .withType(InventoryType.DISPENSER)
                    .build());
        }
    }

    private String locationToString(Location location) {
        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }

    private Location parseLocation(String string) {
        String[] locationInformation = string.split(":");
        World world = Bukkit.getWorld(locationInformation[0]);
        int x = Integer.parseInt(locationInformation[1]);
        int y = Integer.parseInt(locationInformation[2]);
        int z = Integer.parseInt(locationInformation[3]);
        return new Location(world,x,y,z);
    }

}
