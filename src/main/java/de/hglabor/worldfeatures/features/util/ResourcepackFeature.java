package de.hglabor.worldfeatures.features.util;

import de.hglabor.worldfeatures.features.Feature;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class ResourcepackFeature extends Feature {

    public ResourcepackFeature() {
        super("Resourcepack");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(!isEnabled) {
            return;
        }
        Player player = event.getPlayer();
        player.setResourcePack("https://github.com/HGLabor/survival-tutorial/releases/download/0.0.6/HGLaborSurvivalLite-v0.0.7.zip");
    }

    @EventHandler
    public void onPlayerResourcePackEvent(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        if(!isEnabled) {
            return;
        }
        if(event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 10);
            player.sendTitle("\uE000", ChatColor.AQUA + "" + ChatColor.BOLD + "HG" + ChatColor.WHITE + ChatColor.BOLD + "Labor.de", 10, 50, 20);
        }
        if(event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            player.sendMessage(ChatColor.DARK_RED + "Please accept the server-resourcepack!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 0);
            player.sendTitle(ChatColor.DARK_RED + "\u2639", ChatColor.AQUA + "" + ChatColor.BOLD + "HG" + ChatColor.WHITE + ChatColor.BOLD + "Labor.de", 10, 20, 20);
        }
    }

}
