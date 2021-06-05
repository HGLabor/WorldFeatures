package de.hglabor.worldfeatures;

import de.hglabor.worldfeatures.commands.bukkit.FeatureCommand;
import de.hglabor.worldfeatures.commands.bukkit.RulesCommand;
import de.hglabor.worldfeatures.commands.implementation.SpawnEntityCommand;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.features.armor.GasFeature;
import de.hglabor.worldfeatures.features.armor.JetpackFeature;
import de.hglabor.worldfeatures.features.entity.NaturalLaborEntitySpawningFeature;
import de.hglabor.worldfeatures.features.entity.implementation.BirdEntityFeature;
import de.hglabor.worldfeatures.features.entity.implementation.RaptorEntityFeature;
import de.hglabor.worldfeatures.features.protocol.ProtocolFeature;
import de.hglabor.worldfeatures.features.recipe.RottenFleshRecipeFeature;
import de.hglabor.worldfeatures.features.structures.NaturalStructureSpawningFeature;
import de.hglabor.worldfeatures.features.structures.implementation.RougelikeDungeonFeature;
import de.hglabor.worldfeatures.features.travel.DolphinRidingFeature;
import de.hglabor.worldfeatures.features.travel.ParachuteFeature;
import de.hglabor.worldfeatures.features.travel.TeleporterFeature;
import de.hglabor.worldfeatures.features.util.*;
import de.hglabor.worldfeatures.kotlin.features.LootableBodiesFeature;
import net.axay.kspigot.main.KSpigot;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;

public final class WorldFeatures extends KSpigot {

    private static Collection<Feature> features = new ArrayList<>();
    private static WorldFeatures plugin;

    public static Collection<Feature> getFeatures() {
        return features;
    }

    public void registerFeature(Feature feature) {
        features.add(feature);
        Bukkit.getPluginManager().registerEvents(feature, plugin);
        feature.onEnable();
    }

    public static WorldFeatures getPlugin() {
        return plugin;
    }

    @Override
    public void startup() {
        plugin = this;
        if(!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        registerFeature(new DolphinRidingFeature());
        registerFeature(new ParachuteFeature());
        registerFeature(new JetpackFeature());
        registerFeature(new WikiCommandFeature());
        registerFeature(new ResourcepackFeature());
        registerFeature(new TemperaturesFeature());
        registerFeature(new RottenFleshRecipeFeature());
        registerFeature(new BroadcastFeature());
        registerFeature(new RulesCommand());
        registerFeature(new ChatFeature());
        registerFeature(new LootDropFeature());
        registerFeature(new GasFeature());
        registerFeature(new IllegalItemsFeature());
        //registerFeature(new SpawnProtFeature());
        registerFeature(new ContributorFeature());
        registerFeature(new TeleporterFeature());
        registerFeature(new LootableBodiesFeature());
        registerFeature(new ProtocolFeature());
        registerFeature(new NaturalStructureSpawningFeature());
        registerFeature(new NaturalLaborEntitySpawningFeature());
        registerFeature(new RougelikeDungeonFeature());
        registerFeature(new RaptorEntityFeature());
        registerFeature(new BirdEntityFeature());
        for (Feature feature : getFeatures()) {
            feature.onServerStart(plugin);
        }
        getCommand("feature").setExecutor(new FeatureCommand());
        getCommand("feature").setTabCompleter(new FeatureCommand());
        getCommand("rules").setExecutor(new RulesCommand());
        new SpawnEntityCommand();
    }
}
