package de.hglabor.worldfeatures.features.entity;

import de.hglabor.utils.noriskutils.ChanceUtils;
import de.hglabor.worldfeatures.WorldFeatures;
import de.hglabor.worldfeatures.features.Feature;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.stream.Collectors;

public class NaturalLaborEntitySpawningFeature extends Feature {

    public NaturalLaborEntitySpawningFeature() {
        super("NaturalLaborEntitySpawning");
    }

    @Override
    public void onServerStart(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!isEnabled) {
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (Feature entityFeature : WorldFeatures.getFeatures().stream().filter(it -> it instanceof LaborEntity<?>).collect(Collectors.toList())) {
                        LaborEntity<?> laborEntity = (LaborEntity<?>) entityFeature;
                        if(laborEntity.getSpawnLikeIHood() == 0) {
                            return;
                        }
                        if(!ChanceUtils.roll(laborEntity.getSpawnLikeIHood())) {
                            return;
                        }
                        Location location = player.getLocation().clone();
                        location.add(new Random().nextInt(30)-new Random().nextInt(30), laborEntity.getySpawnOffset(), new Random().nextInt(30)-new Random().nextInt(30));
                        if(laborEntity.shouldSpawnHere().test(location)) {
                            LaborEntity<?> finalEntity = laborEntity.getNewInstance();
                            finalEntity.prepareSpawn(location);
                            finalEntity.spawn(finalEntity.getOriginBukkitEntityClass(), location, 3);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if(finalEntity.getBukkitEntity().isDead()) {
                                        cancel();
                                        return;
                                    }
                                    if(finalEntity.getBukkitEntity().getTicksLived() > 7200*20) {
                                        if(!finalEntity.getBukkitEntity().getChunk().isLoaded()) {
                                            finalEntity.getBukkitEntity().remove();
                                            cancel();
                                        }
                                    }
                                }
                            }.runTaskTimer(plugin, 0, 20);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 40);
    }
}
