package de.hglabor.worldfeatures.utils;

import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSUtils {

    public static void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().b.sendPacket(packet);
    }

}
