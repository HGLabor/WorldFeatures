package de.hglabor.worldfeatures.features.structures.api;

import org.bukkit.World;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SchematicStructure {

    World.Environment envrioment();

    /**
     * put this as low as you can to avoid spamming the world full with dungeons
     * @return the spawnchance of the player
     */

    int spawnChance() default 0;

    int yCooridnate() default 22;

    String schematicFile();

}
