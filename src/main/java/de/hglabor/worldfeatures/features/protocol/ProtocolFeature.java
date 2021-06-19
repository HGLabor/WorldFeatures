package de.hglabor.worldfeatures.features.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.utils.ClientPayloadBuffer;
import net.minecraft.server.v1_16_R3.PacketPlayInCustomPayload;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class ProtocolFeature extends Feature implements PluginMessageListener {

    private static final NamespacedKey KEY = new NamespacedKey(WorldFeatures.getPlugin(), "telepad");

    private static final String TELEPAD_CHANNEL = "hglabor:c2s_telepad_place";

    public ProtocolFeature() {
        super("Protocol");
    }

    @Override
    public void onServerStart(Plugin plugin) {
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, TELEPAD_CHANNEL, this);
    }

    private String locationToString(Location location) {
        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }

    private Location parseLocation(String string) {
        String[] locationInformation = string.split(":");
        World world = Bukkit.getWorld(locationInformation[0]);
        int x = Integer.parseInt(locationInformation[1]);
        int y = Integer.parseInt(locationInformation[2]);
        int z = Integer.parseInt(locationInformation[3]);
        return new Location(world,x,y,z);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if(channel.equals(TELEPAD_CHANNEL)) {
            ClientPayloadBuffer payloadBuffer = new ClientPayloadBuffer(message);
            Location location = parseLocation(payloadBuffer.readString());
            location.getBlock().setType(Material.END_PORTAL_FRAME);
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if(itemStack.getType() != Material.END_PORTAL_FRAME) {
                return;
            }
            ItemMeta meta = itemStack.getItemMeta();
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            ArmorStand armorStand = location.getWorld().spawn(location.clone().subtract(0,1.5,0), ArmorStand.class);
            armorStand.setVisible(false);
            armorStand.setInvulnerable(true);
            for(EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                armorStand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.ADDING_OR_CHANGING);
                armorStand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.REMOVING_OR_CHANGING);
            }
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName("Telepad Exit Route");
            dataContainer.set(KEY, PersistentDataType.STRING, locationToString(player.getLocation()));
            itemStack.setItemMeta(meta);
            location.getWorld().playSound(location, Sound.BLOCK_END_PORTAL_SPAWN,1.0f,1.0f);
            return;
        }
    }
}
