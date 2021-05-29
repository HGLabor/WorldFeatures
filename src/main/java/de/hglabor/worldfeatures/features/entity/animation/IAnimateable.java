package de.hglabor.worldfeatures.features.entity.animation;

import org.bukkit.entity.Entity;

public interface IAnimateable<T extends Entity> {

    void setLivingAnimations(T entity, float tickDelta);
}
