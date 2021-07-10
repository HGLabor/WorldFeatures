package de.hglabor.worldfeatures.features.protocol;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.utils.ClientPayloadBuffer;
import de.hglabor.worldfeatures.utils.JsonUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProtocolFeature extends Feature implements PluginMessageListener {

    private static final NamespacedKey KEY = new NamespacedKey(WorldFeatures.getPlugin(), "telepad");

    private static final String HANDSHAKE = "hglabor:c2s_handshake";
    private static final String TELEPAD_CHANNEL = "hglabor:c2s_telepad_place";
    private static final String OPEN_MAGIC_TABLE = "hglaborsurvival:c2s_open_magic_table";
    private static final String ABORT_ENCHANT = "hglaborsurvival:c2s_abort_enchant";
    private static final String OK_ENCHANT = "hglaborsurvival:c2s_ok_enchant";

    private final static List<Player> playersEnchanting = new ArrayList<>();

    public ProtocolFeature() {
        super("Protocol");
    }

    private final static List<Player> players = new ArrayList<>();

    @Override
    public void onServerStart(Plugin plugin) {
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, TELEPAD_CHANNEL, this);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, OPEN_MAGIC_TABLE, this);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, ABORT_ENCHANT, this);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, OK_ENCHANT, this);
    }

    @Override
    public void onEnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!isEnabled()) {
                    cancel();
                    return;
                }
                for (Player player : players) {
                    player.sendMessage(Component.text(":IGNORE:tps;" + format(Bukkit.getTPS()[0])));
                }
            }
        }.runTaskTimer(WorldFeatures.getPlugin(), 0, 20);
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
        }
    }

    public static void sendTemperatureUpdate(double level, Player player, int decimalColor) {
        if(WorldFeatures.getFeatures().stream().filter(it -> it.getName().equalsIgnoreCase("Protocol")).findFirst().get().isEnabled()) {
            if(players.contains(player)) {
                player.sendMessage(Component.text(":IGNORE:temperature;" + level));
            }
            sendTemperatureColor(player, decimalColor);
        }
    }

    public static void sendRadiation(double level, Player player) {
        if(WorldFeatures.getFeatures().stream().filter(it -> it.getName().equalsIgnoreCase("Protocol")).findFirst().get().isEnabled()) {
            if(players.contains(player)) {
                player.sendMessage(Component.text(":IGNORE:radiation;" + level));
            }
        }
    }

    private static void sendTemperatureColor(Player player, int decimalColor) {
        if(players.contains(player)) {
            player.sendMessage(Component.text(":IGNORE:temperatureColor;" + decimalColor));
        }
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
        if(channel.equals(OPEN_MAGIC_TABLE)) {
            playersEnchanting.add(player);
        }
        if(channel.equals(ABORT_ENCHANT)) {
            playersEnchanting.remove(player);
        }
        if(channel.equals(OK_ENCHANT)) {
            ClientPayloadBuffer payloadBuffer = new ClientPayloadBuffer(message);
            EnchantRequest enchantRequest = JsonUtils.fromJson(JsonUtils.fromString(payloadBuffer.readString()), EnchantRequest.class);
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if(itemStack.getType().isAir()) {
                return;
            }
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(Component.text(enchantRequest.getNewName()));
            for (String enchantment : enchantRequest.getEnchantments().keySet()) {
                Enchantment ench = Enchantment.getByName(enchantment);
                if(ench != null) {
                    itemMeta.addEnchant(ench, enchantRequest.getEnchantments().get(enchantment),true);
                }
            }
            for (String modifier : enchantRequest.getModifiers()) {
                itemMeta.addItemFlags(ItemFlag.valueOf(modifier.toUpperCase()));
            }
            itemStack.setItemMeta(itemMeta);
            playersEnchanting.remove(player);
            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 10, 1);
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

    private static String format(double tps) {
        return ( ( tps > 21.0 ) ? "*" : "" ) + Math.min( Math.round( tps * 100.0 ) / 100.0, 20.0 );
    }

    public static class EnchantRequest {

        private final String newName;
        private final HashMap<String, Integer> enchantments;
        private final List<String> modifiers;

        public EnchantRequest(String newName) {
            this.newName = newName;
            this.enchantments = new HashMap<>();
            this.modifiers = new ArrayList<>();
        }

        public void addEnchantment(String enchantment, int level) {
            this.enchantments.put(enchantment, level);
        }

        public void addModifier(String modifier) {
            this.modifiers.add(modifier);
        }

        public String getNewName() {
            return newName;
        }

        public HashMap<String, Integer> getEnchantments() {
            return enchantments;
        }

        public List<String> getModifiers() {
            return modifiers;
        }
    }
}
