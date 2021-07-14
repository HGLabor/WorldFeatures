package de.hglabor.worldfeatures.features.util;

import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.region.Cuboid;
import de.hglabor.worldfeatures.utils.Worlds;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.ArrayList;

public class SpawnProtFeature extends Feature {

    public SpawnProtFeature() {
        super("SpawnProtection");
    }

    public static final Cuboid SPAWN = new Cuboid(Worlds.OVERWORLD, -25, 140, -23, 24, 240, 23);
    public static final Cuboid GREATER_SPAWN_REGION = new Cuboid(Worlds.OVERWORLD, -40, 140, -40, 40, 240, 40);

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
    public void onMove(PlayerMoveEvent event) {
        if(!isEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        if(GREATER_SPAWN_REGION.contains(player.getLocation())) {
            if(player.getFallDistance() > 3) {
                player.setGliding(true);
                player.setVelocity(player.getLocation().getDirection().multiply(3));
            }
        }
        if(player.isOnGround()) {
            boosted.remove(player);
            player.setGliding(false);
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if(GREATER_SPAWN_REGION.contains(to) && !GREATER_SPAWN_REGION.contains(from)) {
            player.sendMessage(Component.text("You entered greater spawn region, you can now glide!"));
        } else if(GREATER_SPAWN_REGION.contains(from) && !GREATER_SPAWN_REGION.contains(to)) {
            player.sendMessage(Component.text("You left greater spawn region, you can no longer glide!"));
        }
    }

    private ArrayList<Player> boosted = new ArrayList<Player>();

    @EventHandler
    public void onToggleGlide(EntityToggleGlideEvent event) {
        if(!isEnabled()) {
            return;
        }
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(player.getEquipment().getChestplate() != null) {
                if(!player.getEquipment().getChestplate().getType().equals(Material.ELYTRA)) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSwapItem(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if(player.isGliding() && GREATER_SPAWN_REGION.contains(player.getLocation())) {
            if(!boosted.contains(player)) {
                event.setCancelled(true);
                player.setVelocity(player.getLocation().getDirection().multiply(2));
                boosted.add(player);
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
