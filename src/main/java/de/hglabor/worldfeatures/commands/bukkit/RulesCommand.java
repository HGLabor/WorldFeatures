package de.hglabor.worldfeatures.commands.bukkit;

import de.hglabor.worldfeatures.features.Feature;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
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
        sender.sendMessage(Component.text("Die Regeln sind aktuell nicht definiert. Solange gilt: Sei kein Arsch").style(Style.style(TextColor.color(255, 0, 0))));
        return false;
    }
}
