package de.hglabor.worldfeatures.features.util;

import de.hglabor.worldfeatures.features.Feature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class ContributorFeature extends Feature {

    public ContributorFeature() {
        super("ContributorStar");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!isEnabled) {
            return;
        }
        Player player = event.getPlayer();
        if(player.hasPermission("hglabor.survival.contributor")) {
            player.setPlayerListName("§e⭐ §7" + player.getPlayerListName());
        }
    }
}
