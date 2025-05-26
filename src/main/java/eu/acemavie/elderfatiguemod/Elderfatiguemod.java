package eu.acemavie.elderfatiguemod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class Elderfatiguemod implements ModInitializer {
    private static final int EFFECT_RADIUS_SQUARED = 2500; // The squared radius for applying Mining Fatigue (50 blocks squared)
    private static final int SCAN_RADIUS = 3000; // The range to scan for Elder Guardians (3000 blocks)
    private static final int EFFECT_DURATION = 6000; // The duration of Mining Fatigue effect in ticks (6000 ticks = 5 minutes)
    private static final int EFFECT_AMPLIFIER = 2; // The level of Mining Fatigue (2 = Mining Fatigue III)
    private static final int TICK_INTERVAL = 20; // Tick interval for applying effect.

    private int tickCounter = 0;

    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.isClient || !(world instanceof ServerWorld)) return;

            tickCounter++;
            if (tickCounter < TICK_INTERVAL) return;
            tickCounter = 0;

            ServerWorld serverWorld = (ServerWorld) world;
            BlockPos spawnPos = serverWorld.getSpawnPos();

            Box searchBox = new Box(
                    spawnPos.getX() - SCAN_RADIUS, -64, spawnPos.getZ() - SCAN_RADIUS,
                    spawnPos.getX() + SCAN_RADIUS, 320, spawnPos.getZ() + SCAN_RADIUS
            );

            for (ElderGuardianEntity elderGuardian : serverWorld.getEntitiesByClass(ElderGuardianEntity.class, searchBox, entity -> true)) {
                // Apply Mining Fatigue to nearby players
                for (PlayerEntity player : serverWorld.getPlayers()) {
                    if (player.squaredDistanceTo(elderGuardian) < EFFECT_RADIUS_SQUARED) {
                        player.addStatusEffect(new StatusEffectInstance(
                                StatusEffects.MINING_FATIGUE,
                                EFFECT_DURATION,
                                EFFECT_AMPLIFIER,
                                true,
                                true,
                                true
                        ));
                    }
                }
            }
        });
    }
}
