package de.hglabor.worldfeatures.features.util;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.utils.LuckPermsUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class BroadcastFeature extends Feature {

    private static String[] tips = {"You can ride §bdolphins §7with saddles!", "Change your fontcolor with §b/color <color>§7.", "If your message starts with §b';'§7, you can copy it!", "You can ping users in the chat just by typing their name.", "Questions? §bhttps://discord.gg/CnSkATfpbA", "This server is §banarchy §7so feel free to do what you want", "Need help with extra features? §bhttps://github.com/HGLabor/survival-tutorial"};

    public BroadcastFeature() {
        super("Broadcast");
    }

    @Override
    public void onEnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(isEnabled) {
                    Bukkit.broadcastMessage("§8§l> §7" + tips[new Random().nextInt(tips.length)]);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 10*20,3*60*20);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(isEnabled) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.setPlayerListHeaderFooter("§a\n§b§lHG§f§lLabor.de\n§a\n§b!discord §8| §b/rules §8| §b/tps §8| §b/wiki\n§a", "\n§3§lSurvival Server\n§3" + Bukkit.getOnlinePlayers().size() + "§7 Spieler online\n§a\n§a\n§8§l> §7§o" + tips[new Random().nextInt(tips.length)]);
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 0, 40);
    }
}
