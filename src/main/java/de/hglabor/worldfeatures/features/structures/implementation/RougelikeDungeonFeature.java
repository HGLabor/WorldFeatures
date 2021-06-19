package de.hglabor.worldfeatures.features.structures.implementation;

import de.hglabor.worldfeatures.features.structures.api.SchematicStructure;
import de.hglabor.worldfeatures.features.structures.api.Structure;
import org.bukkit.World;

@SchematicStructure(
        envrioment = World.Environment.NORMAL,
        spawnChance = 17,
        yCooridnate = 42,
        schematicFile = "dungeons/rougelike.schem"
)
public class RougelikeDungeonFeature extends Structure {

    public RougelikeDungeonFeature() {
        super("RougelikeDungeon", it -> false, it -> {});
    }

}
