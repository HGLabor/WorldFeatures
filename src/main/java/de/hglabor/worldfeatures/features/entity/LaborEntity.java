package de.hglabor.worldfeatures.features.entity;

import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.features.entity.animation.IAnimateable;
import de.hglabor.worldfeatures.utils.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class LaborEntity<T extends Entity> extends Feature {

    private Entity originEntity;
    private Consumer<Entity> runConsumer;
    private Consumer<Entity> deathConsumer;
    private Predicate<Location> shouldSpawnHere;
    private Identifier identifier;
    private int spawnLikeIHood;
    private double ySpawnOffset;
    private Class<? extends Entity> originBukkitEntityClass;

    public LaborEntity(Identifier identifier, int spawnLikeIHood) {
        super(identifier.getKey() + "Entity");
        this.identifier = identifier;
        this.spawnLikeIHood = spawnLikeIHood;
        this.shouldSpawnHere = it -> true;
        this.ySpawnOffset = 1.5;
        this.originBukkitEntityClass = ArmorStand.class;
    }

    public LaborEntity(Identifier identifier, int spawnLikeIHood, Predicate<Location> shouldSpawnHere) {
        super(identifier.getKey() + "Entity");
        this.identifier = identifier;
        this.spawnLikeIHood = spawnLikeIHood;
        this.shouldSpawnHere = shouldSpawnHere;
        this.ySpawnOffset = 1.5;
        this.originBukkitEntityClass = ArmorStand.class;
    }

    public LaborEntity(Identifier identifier) {
        super(identifier.getKey() + "Entity");
        this.identifier = identifier;
        this.spawnLikeIHood = 0;
        this.shouldSpawnHere = it -> true;
        this.ySpawnOffset = 1.5;
        this.originBukkitEntityClass = ArmorStand.class;
    }

    public void withYOffset(double offset) {
        this.ySpawnOffset = offset;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public int getSpawnLikeIHood() {
        return spawnLikeIHood;
    }

    public Class<? extends Entity> getOriginBukkitEntityClass() {
        return originBukkitEntityClass;
    }

    public abstract LaborEntity<T> getNewInstance();

    public void prepareSpawn(Object obj) {

    }

    public void afterSpawn(T entity) {

    }

    public Entity getBukkitEntity() {
        return originEntity;
    }

    public double getySpawnOffset() {
        return ySpawnOffset;
    }

    public Predicate<Location> shouldSpawnHere() {
        return shouldSpawnHere;
    }

    @SuppressWarnings("unchecked")
    public void spawn(Class<? extends Entity> origin, Location location, int tickDelta) {
        originEntity = Bukkit.getWorld("world").spawn(location.clone().add(0,ySpawnOffset,0), origin);
        if(this instanceof IAnimateable<?>) {
            IAnimateable<T> iAnimateable = (IAnimateable<T>) this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(originEntity.isDead()) {
                        if(deathConsumer != null) {
                            deathConsumer.accept(originEntity);
                        }
                        cancel();
                    } else {
                        if(runConsumer != null) {
                            runConsumer.accept(originEntity);
                        }
                    }
                    iAnimateable.setLivingAnimations((T) originEntity, Float.parseFloat(String.valueOf(tickDelta)));
                }
            }.runTaskTimer(WorldFeatures.getPlugin(), 2, tickDelta);
        }
        afterSpawn((T) originEntity);
    }

    public void applyDeaths(Consumer<Entity> deathConsumer) {
        this.deathConsumer = deathConsumer;
    }

    public void applyRuns(Consumer<Entity> runs) {
        this.runConsumer = runs;
    }
}
