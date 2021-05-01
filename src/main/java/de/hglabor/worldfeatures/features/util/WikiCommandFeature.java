package de.hglabor.worldfeatures.features.util;

import de.hglabor.worldfeatures.features.Feature;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WikiCommandFeature extends Feature implements CommandExecutor, TabCompleter {
    public WikiCommandFeature() {
        super("wiki");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("read")) {
                TextComponent component = new TextComponent(TextComponent.fromLegacyText(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "> " + ChatColor.GRAY + "Klicke §b§nhier§7 um die Wikiseite zu öffnen."));
                component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraft.gamepedia.com/" + args[1]));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + "Open the wikipage")));
                sender.spigot().sendMessage(component);
            } else if (args[0].equalsIgnoreCase("search")) {
                TextComponent component = new TextComponent(TextComponent.fromLegacyText(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "> " + ChatColor.GRAY + "Klicke §b§nhier§7 um die Wikisuche zu öffnen"));
                component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraft.gamepedia.com/Special:Search?search=" + args[1] + "&go=Go"));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + "Open the wikisearch")));
                sender.spigot().sendMessage(component);
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(Arrays.asList("read", "search"));
        }
        return Collections.emptyList();
    }

    @Override
    public void onServerStart(Plugin plugin) {
        Bukkit.getPluginCommand("wiki").setExecutor(this);
        Bukkit.getPluginCommand("wiki").setTabCompleter(this);
    }
}
