package de.hglabor.worldfeatures.config;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import net.axay.kspigot.config.PluginFile;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class WorldFeaturesConfig {

    private static YamlConfiguration yamlConfiguration;
    private static final File file = new PluginFile("featrues.yml", null);

    public static void initialize() {
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        for (Feature feature : WorldFeatures.getFeatures()) {
            yamlConfiguration.addDefault("features." + feature.getName() + ".isEnabled", feature.isEnabled());
            if(yamlConfiguration.getBoolean("features." + feature.getName() + ".isEnabled")) {
                if(!feature.isEnabled()) {
                    feature.setEnabled(true);
                } else {
                    feature.setEnabled(false);
                }
            }
        }
    }

    public static void finalizeConfig() {
        for (Feature feature : WorldFeatures.getFeatures()) {
            yamlConfiguration.set("features." + feature.getName() + ".isEnabled", feature.isEnabled());
        }
    }


}
