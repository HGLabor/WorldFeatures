package de.hglabor.worldfeatures.features.modernity;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class PowerNode {

    private World world;
    private int x;
    private int y;
    private int z;

    public PowerNode(String world, int x, int y, int z) {
        this.world = Bukkit.getWorld(world);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public World getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
