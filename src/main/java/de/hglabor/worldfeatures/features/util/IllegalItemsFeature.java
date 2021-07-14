package de.hglabor.worldfeatures.features.util;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class IllegalItemsFeature extends Feature {

    public IllegalItemsFeature() {
        super("ItemProtector");
    }

    private void checkForIllegalItems() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for(ItemStack itemStack : player.getInventory().getContents()) {
                if(itemStack != null) {
                    if(isIllegalItem(itemStack)) {
                        itemStack.setAmount(0);
                    }
                }
            }
            //This is basiclly useless
            /*
            for(ItemStack itemStack : player.getEnderChest().getContents()) {
                if(itemStack != null) {
                    if(isIllegalItem(itemStack)) {
                        itemStack.setAmount(0);
                    }
                }
            }
             */
        }
    }

    @Override
    public void onEnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(isEnabled) {
                    checkForIllegalItems();
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 1,1);
    }

    private boolean isIllegalItem(ItemStack itemStack) {
        boolean result = false;
        if(itemStack.getType() == Material.BEDROCK || itemStack.getType() == Material.BARRIER || itemStack.getType().name().contains("COMMAND") || itemStack.getType().name().contains("SPAWN_EGG") || itemStack.getType() == Material.DRAGON_EGG) {
            result = true;
        }
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            if(itemStack.getEnchantmentLevel(enchantment) > 5) {
                result = true;
            }
        }
        if(itemStack.getItemMeta() != null) {
            if(itemStack.getItemMeta().hasCustomModelData()) {
                result = false;
            }
        }
        return result;
    }
}
