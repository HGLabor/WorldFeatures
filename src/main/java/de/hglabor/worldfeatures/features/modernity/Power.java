package de.hglabor.worldfeatures.features.modernity;

import de.hglabor.worldfeatures.features.Feature;

import java.util.ArrayList;
import java.util.List;

public class Power extends Feature {

    private final List<PowerInfrastructure> powerInfrastructures = new ArrayList<>();

    public Power(String name) {
        super(name);
    }
}
