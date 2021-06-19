package de.hglabor.worldfeatures.features.modernity;

import de.hglabor.utils.noriskutils.WorldEditUtils;
import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.structures.api.SchematicStructure;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class PowerGenerator implements Powerable {

    private double producing;
    private PowerInfrastructure infrastructure;
    private Location location;
    private int size;
    private String name;
    private ItemStack itemStack;

    private boolean isInUse;

    public PowerGenerator(PowerInfrastructure infrastructure, Location location, int size, String name, ItemStack itemStack) {
        this.producing = 0;
        this.infrastructure = infrastructure;
        this.location = location;
        this.size = size;
        this.name = name;
        this.itemStack = itemStack;
        this.isInUse = false;
    }

    public void setProducing(double producing) {
        this.producing = producing;
    }

    public double getProducing() {
        return producing;
    }

    public PowerInfrastructure getInfrastructure() {
        return infrastructure;
    }

    public Location getLocation() {
        return location;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public abstract void destroy(boolean isRadical);

    public abstract void place();

    public void placeSchematic() {
        SchematicStructure schematicStructure = getClass().getAnnotation(SchematicStructure.class);
        if(schematicStructure == null) {
            return;
        }
        WorldEditUtils.pasteSchematic(location.getWorld(), location, new File(WorldFeatures.getPlugin().getDataFolder(), "/schematics/" + schematicStructure.schematicFile()));
        Block reactor = location.getBlock();
        reactor.setMetadata("reactor", new FixedMetadataValue(WorldFeatures.getPlugin(), name.toLowerCase()));
        for (Block block : getNearbyBlocks()) {
            block.setMetadata("powergenerator", new FixedMetadataValue(WorldFeatures.getPlugin(), true));
            block.setMetadata("gentype", new FixedMetadataValue(WorldFeatures.getPlugin(), name.toLowerCase()));
        }
        place();
    }

    public List<Block> getNearbyBlocks() {
        List<Block> blocks = new ArrayList<Block>();
        for (int x = location.getBlockX() - size; x <= location.getBlockX() + size; x++) {
            for (int z = location.getBlockZ() - size; z <= location.getBlockZ() + size; z++) {
                for (int y = location.getBlockY() - size; y <= location.getBlockY() + size; y++) {
                    blocks.add(new Location(location.getWorld(),x,y,z).getBlock());
                    blocks.add(new Location(location.getWorld(),x,y-1,z).getBlock());
                    blocks.add(new Location(location.getWorld(),x,y+1,z).getBlock());
                    for (int i = -0; i < 4; i++) {
                        blocks.add(new Location(location.getWorld(),x,y+i,z).getBlock());
                        blocks.add(new Location(location.getWorld(),x,y-i,z).getBlock());
                    }
                }
            }
        }
        return blocks;
    }

    public List<Block> getNearbyBlocksWithoutY() {
        List<Block> blocks = new ArrayList<Block>();
        for (int x = location.getBlockX() - size; x <= location.getBlockX() + size; x++) {
            for (int z = location.getBlockZ() - size; z <= location.getBlockZ() + size; z++) {
                blocks.add(new Location(location.getWorld(),x,location.getBlockY(),z).getBlock());
                blocks.add(new Location(location.getWorld(),x,location.getBlockY()-1,z).getBlock());
                blocks.add(new Location(location.getWorld(),x,location.getBlockY()+1,z).getBlock());
                for (int i = -0; i < 4; i++) {
                    blocks.add(new Location(location.getWorld(),x,location.getBlockY()+i,z).getBlock());
                    blocks.add(new Location(location.getWorld(),x,location.getBlockY()-i,z).getBlock());
                }
            }
        }
        return blocks;
    }

    @Override
    public void setPower(double power) {
        setProducing(power);
    }

    @Override
    public void setInUse(boolean isInuse) {
        this.isInUse = isInuse;
    }

    @Override
    public boolean isInUse() {
        return this.isInUse;
    }

    @Override
    public double getPower() {
        return getProducing();
    }
}
