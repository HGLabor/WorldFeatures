package de.hglabor.worldfeatures.commands.implementation;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.commands.api.AbstractCommand;
import de.hglabor.worldfeatures.commands.api.CommandCompleter;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.features.entity.LaborEntity;
import de.hglabor.worldfeatures.utils.Identifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class SpawnEntityCommand extends AbstractCommand {

    private static CommandCompleter commandCompleter;

    public SpawnEntityCommand() {
        super("spawnentity", "hglabor.survival.spawnentity");
        commandCompleter = new CommandCompleter();
        WorldFeatures.getFeatures().stream().filter(it -> it instanceof LaborEntity<?>).collect(Collectors.toList()).forEach(it -> commandCompleter.addSuggestion(1, ((LaborEntity<?>) it).getIdentifier().toString()));
        for (EntityType entityType : EntityType.values()) {
            Class<?> entityClass = entityType.getEntityClass();
            if(entityClass != null) {
                commandCompleter.addSuggestion(2, entityClass.getName());
            }
        }
        commandCompleter.suggest(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handle(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            if(args.length == 2) {
                for (Feature entityFeature : WorldFeatures.getFeatures().stream().filter(it -> it instanceof LaborEntity<?>).collect(Collectors.toList())) {
                    LaborEntity<?> laborEntity = (LaborEntity<?>) entityFeature;
                    if(laborEntity.getIdentifier().equals(new Identifier(args[0].split(":")[0],args[0].split(":")[1]))) {
                        LaborEntity<?> entity = laborEntity.getNewInstance();
                        entity.prepareSpawn(sender);
                        try {
                            entity.spawn((Class<? extends Entity>) Class.forName(args[1]), ((Player) sender).getLocation(), 3);
                        } catch (ClassNotFoundException e) {
                            sender.sendMessage("§8§l> §cOrigin class not found.");
                        }
                    }
                }
            } else {
                sender.sendMessage(Component.text("Entity or origin class missing.").style(Style.style(TextColor.color(255, 0, 0))));
            }
        }
    }
}
