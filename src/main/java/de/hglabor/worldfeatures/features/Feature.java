package de.hglabor.worldfeatures.features;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public abstract class Feature implements Listener {

    protected static Logger logger = Bukkit.getLogger();
    protected boolean isEnabled;
    protected final String name;

    public Feature(String name) {
        this.isEnabled = true;
        this.name = name;
    }

    public Feature(String name, boolean isEnabled) {
        this.isEnabled = isEnabled;
        this.name = name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        if(isEnabled) {
            onEnable();
        }
    }

    public String getName() {
        return name;
    }

    public void onEnable() {

    }

    public void onServerStart(Plugin plugin) {

    }
}
