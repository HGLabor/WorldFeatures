package de.hglabor.worldfeatures.features.recipe;

import de.hglabor.worldfeatures.features.Feature;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class RottenFleshRecipeFeature extends Feature {

    public RottenFleshRecipeFeature() {
        super("RottenFleshFeature");
    }

    @Override
    public void onServerStart(Plugin plugin) {
        Bukkit.addRecipe(new FurnaceRecipe(new NamespacedKey(plugin, "hglabor_rottenflesh"), new ItemStack(Material.LEATHER), Material.ROTTEN_FLESH, 12, 40));
    }
}
