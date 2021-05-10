package de.hglabor.worldfeatures;

import de.hglabor.worldfeatures.commands.FeatureCommand;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.features.armor.GasFeature;
import de.hglabor.worldfeatures.features.armor.JetpackFeature;
import de.hglabor.worldfeatures.features.recipe.RottenFleshRecipeFeature;
import de.hglabor.worldfeatures.features.travel.DolphinRidingFeature;
import de.hglabor.worldfeatures.features.travel.ParachuteFeature;
import de.hglabor.worldfeatures.features.travel.TeleporterFeature;
import de.hglabor.worldfeatures.features.util.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;

public final class WorldFeatures extends JavaPlugin {

    private static Collection<Feature> features = new ArrayList<>();
    private static Plugin plugin;

    public static Collection<Feature> getFeatures() {
        return features;
    }

    public void registerFeature(Feature feature) {
        features.add(feature);
        Bukkit.getPluginManager().registerEvents(feature, plugin);
        feature.onEnable();
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        registerFeature(new DolphinRidingFeature());
        registerFeature(new ParachuteFeature());
        registerFeature(new JetpackFeature());
        registerFeature(new WikiCommandFeature());
        registerFeature(new ResourcepackFeature());
        registerFeature(new TemperaturesFeature());
        registerFeature(new RottenFleshRecipeFeature());
        registerFeature(new BroadcastFeature());
        registerFeature(new ChatFeature());
        registerFeature(new LootDropFeature());
        registerFeature(new GasFeature());
        registerFeature(new IllegalItemsFeature());
        registerFeature(new SpawnProtFeature());
        registerFeature(new ContributorFeature());
        registerFeature(new TeleporterFeature());
        for (Feature feature : getFeatures()) {
            feature.onServerStart(plugin);
        }
        getCommand("feature").setExecutor(new FeatureCommand());
        getCommand("feature").setTabCompleter(new FeatureCommand());
    }
}
