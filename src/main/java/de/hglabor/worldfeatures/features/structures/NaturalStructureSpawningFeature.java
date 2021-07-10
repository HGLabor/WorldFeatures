package de.hglabor.worldfeatures.features.structures;

import de.hglabor.utils.noriskutils.ChanceUtils;
import de.hglabor.utils.noriskutils.WorldEditUtils;
import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.features.structures.api.SchematicStructure;
import de.hglabor.worldfeatures.features.structures.api.Structure;
import de.hglabor.worldfeatures.utils.BetterChanceUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;

import java.io.File;

public class NaturalStructureSpawningFeature extends Feature {

    public NaturalStructureSpawningFeature() {
        super("NaturalStructureSpawning");
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if(!isEnabled) {
            return;
        }
        Chunk chunk = event.getChunk();
        if(event.isNewChunk()) {
            Bukkit.getScheduler().runTaskLater(WorldFeatures.getPlugin(), () -> {
                for (Feature feature : WorldFeatures.getFeatures()) {
                    if(feature instanceof Structure) {
                        Structure structure = (Structure) feature;
                        Class<? extends Structure> clazz = structure.getClass();
                        SchematicStructure schematicStructure = clazz.getAnnotation(SchematicStructure.class);
                        if(schematicStructure != null) {
                            int y = (schematicStructure.yCooridnate() == Integer.MAX_VALUE) ? chunk.getWorld().getHighestBlockYAt(chunk.getBlock(9, schematicStructure.yCooridnate(),9).getLocation())+1 : schematicStructure.yCooridnate();
                            if(structure.canSpawnHere(chunk.getBlock(9, y,9).getLocation()) && chunk.getWorld().getEnvironment() == schematicStructure.envrioment()) {
                                if(BetterChanceUtils.rollRarely(schematicStructure.spawnChance())) {
                                    WorldEditUtils.pasteSchematic(chunk.getWorld(), chunk.getBlock(9, y,9).getLocation(), new File(WorldFeatures.getPlugin().getDataFolder(), "/schematics/" + schematicStructure.schematicFile()));
                                    structure.onGenerate().accept(chunk.getBlock(9, y,9).getLocation());
                                    int x = chunk.getBlock(9, y,9).getLocation().getBlockX();
                                    int z = chunk.getBlock(9, y,9).getLocation().getBlockZ();
                                    logger.info("Generated " + structure.getName() + " at " + x + " " + y + " " + z);
                                    break;
                                }
                            }
                        }
                    }
                }
            }, 80);
        }
    }
}
