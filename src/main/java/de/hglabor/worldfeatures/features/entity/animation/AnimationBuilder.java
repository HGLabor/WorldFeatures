package de.hglabor.worldfeatures.features.entity.animation;

import de.hglabor.worldfeatures.features.entity.LaborEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class AnimationBuilder {

    private final LaborEntity<?> entity;
    private Vector[] idleOffsets;
    private Consumer<Entity> walkingAnimations;
    private Consumer<Entity> attackAnimation;
    private Consumer<Entity> dieAnimation;
    private Predicate<Entity> shouldAttackWhen;
    private boolean removeOnBlock;
    private boolean doesBite; //xd
    private Location locationGoal;
    private LivingEntity entityGoal;
    private double damage;

    public AnimationBuilder(LaborEntity<?> entity) {
        this.entity = entity;
        this.doesBite = false;
        this.removeOnBlock = false;
        this.walkingAnimations = it -> {
        };
        this.shouldAttackWhen = (it) -> true;
        if(!(entity instanceof IAnimateable<?>)) {
            return;
        }
    }

    public AnimationBuilder withIdleOffset(Vector[] idleOffsetFrames) {
        this.idleOffsets = idleOffsetFrames;
        return this;
    }

    public AnimationBuilder withWalkingAnimation(Consumer<Entity> walkingAnimation) {
        this.walkingAnimations = walkingAnimation;
        return this;
    }

    public AnimationBuilder withAttackAnimation(Consumer<Entity> attackAnimation) {
        this.attackAnimation = attackAnimation;
        return this;
    }

    public AnimationBuilder shouldAttackWhen(Predicate<Entity> shouldAttackWhen) {
        this.shouldAttackWhen = shouldAttackWhen;
        return this;
    }

    public AnimationBuilder withDeathAnimation(Consumer<Entity> deathAnimation) {
        this.dieAnimation = deathAnimation;
        return this;
    }

    public AnimationBuilder withRemoveOnBlock(Boolean shouldRemove) {
        this.removeOnBlock = shouldRemove;
        return this;
    }

    public AnimationBuilder withPathfinderGoal(Location location) {
        this.locationGoal = location;
        return this;
    }

    public AnimationBuilder withPathfinderGoal(LivingEntity livingEntity, double damage) {
        this.doesBite = damage != 0.0;
        if(this.doesBite) {
            this.damage = damage;
            this.entityGoal = livingEntity;
        } else {
            this.locationGoal = livingEntity.getLocation();
        }
        return this;
    }

    public void apply() {
        if(this.dieAnimation != null) {
            entity.applyDeaths(dieAnimation);
        }
        AtomicInteger tick = new AtomicInteger();
        entity.applyRuns(entity -> {
            tick.getAndIncrement();
            if(this.entityGoal != null && !this.entityGoal.isDead()) {
                if(entity.getLocation().distance(entityGoal.getLocation()) < 2.0 && shouldAttackWhen.test(entityGoal)) {
                    if(doesBite) {
                        if(entityGoal instanceof Player) {
                            if(!((Player) entityGoal).isBlocking()) {
                                entityGoal.damage(damage, entity);
                            } else {
                                if(removeOnBlock) {
                                    entity.remove();
                                }
                                return;
                            }
                        } else {
                            entityGoal.damage(damage, entity);
                        }
                        if(attackAnimation != null) {
                            attackAnimation.accept(entityGoal);
                        }
                    } else {
                        if(idleOffsets == null) {
                            return;
                        }
                        if(tick.get() > idleOffsets.length) {
                            tick.set(0);
                        }
                        entity.teleport(entity.getLocation().clone().add(idleOffsets[tick.get()].getX(), idleOffsets[tick.get()].getY(), idleOffsets[tick.get()].getZ()));
                    }
                } else {
                    int x = entityGoal.getLocation().getBlockX();
                    int y = entityGoal.getLocation().getBlockY();
                    int z = entityGoal.getLocation().getBlockZ();
                    Location currentLocation = entity.getLocation().clone();
                    if(currentLocation.getBlockX() > x) {
                        currentLocation.subtract(0.7, 0,0);
                    } else if(currentLocation.getBlockX() != x) {
                        currentLocation.add(0.7, 0,0);
                    }
                    if(currentLocation.getBlockY() > y) {
                        currentLocation.subtract(0, 0.7,0);
                    } else if(currentLocation.getBlockY() != y) {
                        currentLocation.add(0, 0.7,0);
                    }
                    if(currentLocation.getBlockZ() > z) {
                        currentLocation.subtract(0, 0,0.7);
                    } else if(currentLocation.getBlockZ() != z) {
                        currentLocation.add(0, 0,0.7);
                    }
                    entity.teleport(currentLocation);
                    if(walkingAnimations != null) {
                        walkingAnimations.accept(entity);
                    }
                }
            } else if(locationGoal != null && entity.getLocation().distance(locationGoal) > 2.0) {
                int x = locationGoal.getBlockX();
                int y = locationGoal.getBlockY();
                int z = locationGoal.getBlockZ();
                Location currentLocation = entity.getLocation().clone();
                if(currentLocation.getBlockX() > x) {
                    currentLocation.subtract(0.7, 0,0);
                } else if(currentLocation.getBlockX() != x) {
                    currentLocation.add(0.7, 0,0);
                }
                if(currentLocation.getBlockY() > y) {
                    currentLocation.subtract(0, 0.7,0);
                } else if(currentLocation.getBlockY() != y) {
                    currentLocation.add(0, 0.7,0);
                }
                if(currentLocation.getBlockZ() > z) {
                    currentLocation.subtract(0, 0,0.7);
                } else if(currentLocation.getBlockZ() != z) {
                    currentLocation.add(0, 0,0.7);
                }
                entity.teleport(currentLocation);
                walkingAnimations.accept(entity);
            } else {
                if(idleOffsets == null) {
                    return;
                }
                if(tick.get() > idleOffsets.length) {
                    tick.set(0);
                }
                entity.teleport(entity.getLocation().clone().add(idleOffsets[tick.get()].getX(), idleOffsets[tick.get()].getY(), idleOffsets[tick.get()].getZ()));
            }
        });
    }

}
