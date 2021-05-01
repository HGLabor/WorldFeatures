package de.hglabor.worldfeatures.features.util;

import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.utils.LuckPermsUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChatFeature extends Feature implements CommandExecutor, TabCompleter {

    public ChatFeature() {
        super("Chat");
    }

    private static HashMap<UUID, ChatColor> colors = new HashMap<>();

    private static ChatColor getColor(Player player) {
        return colors.getOrDefault(player.getUniqueId(), ChatColor.WHITE);
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!isEnabled) {
            return;
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
                    fmsg = fmsg.replace("@everyone", ChatColor.DARK_RED + "@everyone");
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
                    fmsg = fmsg.replaceAll(p.getName(), ChatColor.GOLD + "" + ChatColor.ITALIC + "@" + p.getName() + getColor(player));
                    if(!player.getScoreboardTags().contains("hidden")) {
                        p.playSound(p.getLocation(), Sound.BLOCK_BELL_USE, 0.3f, 1);
                    }
                    TextComponent component = new TextComponent(TextComponent.fromLegacyText(color + player.getDisplayName() + ChatColor.GRAY + " \u00bb " + getColor(player) + fmsg));
                    component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, player.getName() + " "));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + "Reply to " + ChatColor.GOLD + player.getName())));
                    isPing = true;
                    if(!player.hasPermission("group.muted")) {
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
                ChatColor color = ChatColor.valueOf(args[0].toUpperCase());
                colors.remove(player.getUniqueId());
                colors.put(player.getUniqueId(), color);
                sender.sendMessage("§8§l> §7Color changed to " + color + color.name() + "§7.");
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
        for (ChatColor color : ChatColor.values()) {
            arrayList.add(color.name().toLowerCase());
        }
        return arrayList;
    }

    @Override
    public void onServerStart(Plugin plugin) {
        Bukkit.getPluginCommand("color").setExecutor(this);
        Bukkit.getPluginCommand("color").setTabCompleter(this);
    }

}
