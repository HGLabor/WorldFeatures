package de.hglabor.worldfeatures.features.util;

import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.region.Cuboid;
import de.hglabor.worldfeatures.utils.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class SpawnProtFeature extends Feature {

    public SpawnProtFeature() {
        super("SpawnProtection");
    }

    public static final Cuboid SPAWN = new Cuboid(Worlds.OVERWORLD, -25, 140, -23, 24, 240, 23);

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!isEnabled()) {
            return;
        }
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }
        if(SPAWN.contains(player.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(isEnabled()) {
            Block block = event.getBlock();
            if(SPAWN.contains(block.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(isEnabled()) {
            Block block = event.getBlock();
            if(SPAWN.contains(block.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if(isEnabled()) {
            Entity entity = event.getEntity();
            if(SPAWN.contains(entity.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if(isEnabled()) {
            Entity entity = event.getEntity();
            if(SPAWN.contains(entity.getLocation())) {
                event.setCancelled(true);
            }
        }
    }
}
