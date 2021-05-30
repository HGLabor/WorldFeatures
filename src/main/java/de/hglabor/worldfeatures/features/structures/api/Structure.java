package de.hglabor.worldfeatures.features.structures.api;

import de.hglabor.worldfeatures.features.Feature;
import org.bukkit.Location;
import java.util.function.Predicate;

public abstract class Structure extends Feature {

    private final Predicate<Location> canSpawnHere;

    public Structure(String name, Predicate<Location> canSpawnHere) {
        super(name + "Structure");
        this.canSpawnHere = canSpawnHere;
    }

    public Boolean canSpawnHere(Location location) {
        return canSpawnHere.test(location);
    }
}
