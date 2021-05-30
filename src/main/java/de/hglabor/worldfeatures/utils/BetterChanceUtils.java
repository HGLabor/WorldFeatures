package de.hglabor.worldfeatures.utils;

import java.util.Random;

public final class BetterChanceUtils {

    private static final Random random = new Random();

    private BetterChanceUtils() {
    }

    public static boolean rollRarely(int maximalChance) {
        return random.nextInt(9999) + 1 <= maximalChance;
    }

}
