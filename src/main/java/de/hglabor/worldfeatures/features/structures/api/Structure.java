package de.hglabor.worldfeatures.features.structures.api;

import de.hglabor.worldfeatures.features.Feature;
import org.bukkit.Location;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Structure extends Feature {

    private final Predicate<Location> canSpawnHere;
    private final Consumer<Location> onGenerate;

    public Structure(String name, Predicate<Location> canSpawnHere, Consumer<Location> onGenerate) {
        super(name + "Structure");
        this.canSpawnHere = canSpawnHere;
        this.onGenerate = onGenerate;
    }

    public Boolean canSpawnHere(Location location) {
        return canSpawnHere.test(location);
    }

    public Consumer<Location> onGenerate() {
        return onGenerate;
    }
}
