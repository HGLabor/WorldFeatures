package de.hglabor.worldfeatures.commands.bukkit;

import de.hglabor.worldfeatures.features.Feature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RulesCommand extends Feature implements CommandExecutor {

    public RulesCommand() {
        super("Rules");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("§cThis server is anarchy.");
        return false;
    }
}
