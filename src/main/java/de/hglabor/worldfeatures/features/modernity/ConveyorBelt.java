package de.hglabor.worldfeatures.features.modernity;

import de.hglabor.worldfeatures.features.Feature;
import de.hglabor.worldfeatures.features.entity.LaborEntity;
import de.hglabor.worldfeatures.features.entity.animation.IAnimateable;
import de.hglabor.worldfeatures.utils.Identifier;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;

public class ConveyorBelt extends Feature {

    public ConveyorBelt() {
        super("ConveyorBelt");
    }

    public static class ConveyorBeltEntity extends LaborEntity<ArmorStand> implements IAnimateable<ArmorStand> {

        public ConveyorBeltEntity() {
            super(new Identifier("conveyorbelt_entity"));
        }

        @Override
        public LaborEntity<ArmorStand> getNewInstance() {
            return null;
        }

        @Override
        public void setLivingAnimations(ArmorStand entity, float tickDelta) {

        }
    }

}
