package de.hglabor.worldfeatures.commands.api;

import de.hglabor.worldfeatures.WorldFeatures;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandCompleter {

    private HashMap<Integer, List<String>> suggestionMap = new HashMap<>();

    public void addSuggestion(int arg, String suggestion) {
        ArrayList<String> list;
        if(suggestionMap.containsKey(arg)) {
            list = (ArrayList<String>) suggestionMap.get(arg);
            list.add(suggestion);
            suggestionMap.remove(arg);
        } else {
            list = new ArrayList<>();
            list.add(suggestion);
        }
        suggestionMap.put(arg, list);
    }

    public void suggest(AbstractCommand abstractCommand) {
        WorldFeatures.getPlugin().getCommand(abstractCommand.getName()).setTabCompleter(new TabCompleter() {
            @NotNull
            @Override
            public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
                for (int i : suggestionMap.keySet()) {
                    if(args.length == i) {
                        return suggestionMap.get(i);
                    }
                }
                return new ArrayList<>();
            }
        });
    }

}
