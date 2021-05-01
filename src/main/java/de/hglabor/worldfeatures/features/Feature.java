package de.hglabor.worldfeatures.features;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class Feature implements Listener {

    protected boolean isEnabled;
    protected final String name;

    public Feature(String name) {
        this.isEnabled = true;
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
