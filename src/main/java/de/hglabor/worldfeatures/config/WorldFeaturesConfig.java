package de.hglabor.worldfeatures.config;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import net.axay.kspigot.config.PluginFile;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class WorldFeaturesConfig {

    private static YamlConfiguration yamlConfiguration;
    private static final File file = new PluginFile("features.yml", null);

    public static void initialize() {
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        for (Feature feature : WorldFeatures.getFeatures()) {
            yamlConfiguration.addDefault("features." + feature.getName() + ".isEnabled", feature.isEnabled());
            if(yamlConfiguration.getBoolean("features." + feature.getName() + ".isEnabled")) {
                if(!feature.isEnabled()) {
                    feature.setEnabled(true);
                }
            } else {
                if(feature.isEnabled()) {
                    feature.setEnabled(false);
                }
            }
        }
        try {
            yamlConfiguration.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void finalizeConfig() {
        for (Feature feature : WorldFeatures.getFeatures()) {
            try {
                yamlConfiguration.set("features." + feature.getName() + ".isEnabled", feature.isEnabled());
                yamlConfiguration.save(file);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }


}
