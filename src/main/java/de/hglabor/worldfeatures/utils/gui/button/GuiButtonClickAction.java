package de.hglabor.worldfeatures.utils.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiButtonClickAction {

    private Player player;
    private GuiButton button;
    private InventoryClickEvent bukkitEvent;

    public GuiButtonClickAction(Player player, GuiButton button, InventoryClickEvent bukkitEvent) {
        this.player = player;
        this.button = button;
        this.bukkitEvent = bukkitEvent;
    }

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
