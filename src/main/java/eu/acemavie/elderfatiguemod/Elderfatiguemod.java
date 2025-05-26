package eu.acemavie.elderfatiguemod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class Elderfatiguemod implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.isClient) return;

            // Define a large bounding box to encompass all loaded Elder Guardians
            Box searchBox = new Box(-30000000, -64, -30000000, 30000000, 320, 30000000);

            // Iterate over all Elder Guardians in the world
            for (ElderGuardianEntity elderGuardian : world.getEntitiesByClass(ElderGuardianEntity.class, searchBox, entity -> true)) {
                // Apply Mining Fatigue to nearby players
                for (PlayerEntity player : world.getPlayers()) {
                    if (player.squaredDistanceTo(elderGuardian) < 2500) { // within 50 blocks
                        player.addStatusEffect(new StatusEffectInstance(
                                StatusEffects.MINING_FATIGUE,
                                6000, // 5 minutes = 20 ticks * 60 * 5
                                2,    // Amplifier = level 3
                                true, // ambient
                                true, // show particles
                                true  // show icon
                        ));
                    }
                }
            }
        });
    }
}
