package de.hglabor.worldfeatures.features.util;

import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.utils.LuckPermsUtils;
import net.axay.kspigot.chat.KColors;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChatFeature extends Feature implements CommandExecutor, TabCompleter {

    private final List<String> blacklist = List.of("report", "hacker", "kann ein admin", "bann");

    public ChatFeature() {
        super("Chat");
    }

    private static HashMap<UUID, net.md_5.bungee.api.ChatColor> colors = new HashMap<>();

    private static net.md_5.bungee.api.ChatColor getColor(Player player) {
        return colors.getOrDefault(player.getUniqueId(), net.md_5.bungee.api.ChatColor.WHITE);
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!isEnabled) {
            player.sendMessage("§8§l> §cThe chat is currently disabled.");
            event.setCancelled(true);
            return;
        }
        for (String word : blacklist) {
            if(event.getMessage().toLowerCase().contains(word.toLowerCase())) {
                player.sendMessage("§8§l> §fThe word §e\"" + word + "\"§f is blacklisted due to the §6/rules §fon this server.");
                event.setCancelled(true);
                return;
            }
        }
        if(event.getMessage().equalsIgnoreCase("!discord")) {
            event.setCancelled(true);
            player.sendMessage("§8§l> §7Official Survival-Discord: §bhttps://discord.gg/CnSkATfpbA");
            return;
        }
        boolean isPing = false;
        net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.of(LuckPermsUtils.getGroupNameColor(player));
        if (!event.isCancelled()) {
            event.setCancelled(true);
            String fmsg = event.getMessage();
            if (player.isOp()) {
                fmsg = fmsg.replace("&", "\u00A7");
                if(fmsg.contains("@everyone")) {
                    fmsg = fmsg.replace("@everyone", ChatColor.DARK_RED + "@everyone" + getColor(player));
                    Bukkit.broadcastMessage(color + player.getDisplayName() + ChatColor.GRAY + " \u00bb " + getColor(player) + fmsg);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_BELL_USE, 0.3f, 1);
                    }
                    return;
                }
            }
            boolean isCopyAble = fmsg.startsWith(";");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (fmsg.contains(p.getName())) {
                    fmsg = fmsg.replaceAll(p.getName(), net.md_5.bungee.api.ChatColor.of(new Color(13881511)) + "" + ChatColor.ITALIC + "@" + p.getName() + getColor(player));
                    TextComponent component = new TextComponent(TextComponent.fromLegacyText(color + player.getDisplayName() + ChatColor.GRAY + " \u00bb " + getColor(player) + fmsg));
                    component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, player.getName() + " "));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + "Reply to " + ChatColor.GOLD + player.getName())));
                    isPing = true;
                    if(!player.hasPermission("group.muted")) {
                        p.playSound(p.getLocation(), Sound.BLOCK_BELL_USE, 0.3f, 1);
                        for (Player pl : Bukkit.getOnlinePlayers()) {
                            pl.spigot().sendMessage(component);
                        }
                    } else {
                        player.spigot().sendMessage(component);
                    }
                    break;
                }
            }
            if (!isPing) {
                if (!isCopyAble) {
                    if(!player.hasPermission("group.muted")) {
                        Bukkit.broadcastMessage(color + player.getDisplayName() + ChatColor.GRAY + " \u00bb " + getColor(player) + fmsg);
                    } else {
                        player.sendMessage(color + player.getDisplayName() + ChatColor.GRAY + " \u00bb " + getColor(player) + fmsg);
                    }
                } else {
                    TextComponent component = new TextComponent(TextComponent.fromLegacyText(color + player.getDisplayName() + ChatColor.GRAY + " \u00bb " + getColor(player) + fmsg.substring(1)));
                    component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.COPY_TO_CLIPBOARD, event.getMessage().substring(1)));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + "Copy this message")));
                    if(!player.getScoreboardTags().contains("hidden")) {
                        for (Player pl : Bukkit.getOnlinePlayers()) {
                            pl.spigot().sendMessage(component);
                        }
                    } else {
                        player.spigot().sendMessage(component);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if(args.length == 1) {
            try {
                net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.WHITE;
                for (Field field : KColors.class.getFields()) {
                    if(field.getName().equalsIgnoreCase(args[0])) {
                        color = (net.md_5.bungee.api.ChatColor) field.get(KColors.class);
                        break;
                    }
                }
                colors.remove(player.getUniqueId());
                colors.put(player.getUniqueId(), color);
                sender.sendMessage("§8§l> §7Color changed to " + color + args[0].toUpperCase() + "§7.");
            } catch (Exception ex) {
                sender.sendMessage("§8§l> §cInvalid color.");
            }
        } else {
            sender.sendMessage("§8§l> §cInvalid color.");
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Field field : KColors.class.getFields()) {
            arrayList.add(field.getName());
        }
        return arrayList;
    }

    @Override
    public void onServerStart(Plugin plugin) {
        Bukkit.getPluginCommand("color").setExecutor(this);
        Bukkit.getPluginCommand("color").setTabCompleter(this);
    }

}
