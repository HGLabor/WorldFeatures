package de.hglabor.worldfeatures.utils.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public record GuiButtonClickAction(Player player, GuiButton button, InventoryClickEvent bukkitEvent) {

    public InventoryClickEvent getBukkitEvent() {
        return bukkitEvent;
    }

    public Player getPlayer() {
        return player;
    }

    public GuiButton getButton() {
        return button;
    }
}
