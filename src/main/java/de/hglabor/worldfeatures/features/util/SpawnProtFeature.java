package de.hglabor.worldfeatures.features.util;

import de.hglabor.worldfeatures.features.Feature;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class SpawnProtFeature extends Feature {
    public SpawnProtFeature() {
        super("SpawnProtection");
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!isEnabled) {
            return;
        }
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        if(event.getEntity().getWorld().getName().equalsIgnoreCase("world")) {
            if(event.getEntity().getLocation().distance(new Location(Bukkit.getWorld("world"), 0, 60, 0)) < 50) {
                event.setCancelled(true);
            }
        }
    }
}
