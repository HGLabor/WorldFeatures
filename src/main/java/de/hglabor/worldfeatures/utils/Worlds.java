package de.hglabor.worldfeatures.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Worlds {

    @NotNull
    public static final World OVERWORLD = Objects.requireNonNull(Bukkit.getWorld("world"));
    public static final World NETHER = Bukkit.getWorld("world_nether");
    public static final World THE_END = Bukkit.getWorld("world_the_end");

}
