package de.hglabor.worldfeatures.commands;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FeatureCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("hglabor.managefeatures")) {
            if(args.length == 2) {
                for (Feature feature : WorldFeatures.getFeatures()) {
                    if(args[1].equalsIgnoreCase(feature.getName().toLowerCase())) {
                        feature.setEnabled(args[0].equalsIgnoreCase("enable"));
                        sender.sendMessage(ChatColor.GRAY + feature.getName() + " is now: " + ChatColor.AQUA + feature.isEnabled());
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Try /feature <enable / disable> <feature>");
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 1) {
            return Arrays.asList("enable", "disable");
        } else if(args.length == 2) {
            List<String> list = new ArrayList<>();
            for (Feature feature : WorldFeatures.getFeatures()) {
                list.add(feature.getName().toLowerCase());
            }
            return list;
        }
        return Collections.emptyList();
    }
}
