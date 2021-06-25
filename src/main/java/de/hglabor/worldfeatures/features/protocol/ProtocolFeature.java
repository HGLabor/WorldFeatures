package de.hglabor.worldfeatures.features.protocol;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.utils.ClientPayloadBuffer;
import de.hglabor.worldfeatures.utils.NMSUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutCustomPayload;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProtocolFeature extends Feature implements PluginMessageListener {

    private static final NamespacedKey KEY = new NamespacedKey(WorldFeatures.getPlugin(), "telepad");

    private static final String HANDSHAKE = "hglabor:c2s_handshake";
    private static final String TELEPAD_CHANNEL = "hglabor:c2s_telepad_place";

    public ProtocolFeature() {
        super("Protocol");
    }

    private final static List<Player> players = new ArrayList<>();

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

    public static void sendGasUpdate(int filtering, Player player) {
        if(WorldFeatures.getFeatures().stream().filter(it -> it.getName().equalsIgnoreCase("Protocol")).findFirst().get().isEnabled()) {
            if(players.contains(player)) {
                player.sendMessage(Component.text(":IGNORE:gas;" + filtering));
            }
            /*
            ClientPayloadBuffer clientPayloadBuffer = new ClientPayloadBuffer();
            clientPayloadBuffer.writeString(filtering + "");
            PacketPlayOutCustomPayload payloadPacket = new PacketPlayOutCustomPayload(new MinecraftKey("hglabor:s2c_gas"), new PacketDataSerializer(clientPayloadBuffer.getByteBuf()));
            NMSUtils.sendPacket(player, payloadPacket);
             */
        }
    }

    public static void sendTemperatureUpdate(double level, Player player, int decimalColor) {
        if(WorldFeatures.getFeatures().stream().filter(it -> it.getName().equalsIgnoreCase("Protocol")).findFirst().get().isEnabled()) {
            if(players.contains(player)) {
                player.sendMessage(Component.text(":IGNORE:temperature;" + level));
            }
            sendTemperatureColor(player, decimalColor);
            /*
            ClientPayloadBuffer clientPayloadBuffer = new ClientPayloadBuffer();
            clientPayloadBuffer.writeString(level + "");
            PacketPlayOutCustomPayload payloadPacket = new PacketPlayOutCustomPayload(new MinecraftKey("hglabor:s2c_temperature"), new PacketDataSerializer(clientPayloadBuffer.getByteBuf()));
            NMSUtils.sendPacket(player, payloadPacket);
            sendTemperatureColor(player, decimalColor);
             */
        }
    }

    private static void sendTemperatureColor(Player player, int decimalColor) {
        if(players.contains(player)) {
            player.sendMessage(Component.text(":IGNORE:temperatureColor;" + decimalColor));
        }
        /*
        ClientPayloadBuffer clientPayloadBuffer = new ClientPayloadBuffer();
        clientPayloadBuffer.writeString(decimalColor + "");
        PacketPlayOutCustomPayload payloadPacket = new PacketPlayOutCustomPayload(new MinecraftKey("hglabor:s2c_temperature_color"), new PacketDataSerializer(clientPayloadBuffer.getByteBuf()));
        NMSUtils.sendPacket(player, payloadPacket);
         */
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(players.contains(player)) {
            players.remove(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("Welcome to survival (hglabor:heartbeat)"));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(event.getMessage().contains("hglabor:heartbeat")) {
            event.setCancelled(true);
            players.add(player);
        }
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        player.sendMessage(channel);
        if(channel.equals(HANDSHAKE)) {
            players.add(player);
        }
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
