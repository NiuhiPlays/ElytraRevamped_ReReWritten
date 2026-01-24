package com.niuhi.visuals;

import com.niuhi.config.ModConfig;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public final class BoostParticles {
    private static final Identifier SOUND_ID = Identifier.of("minecraft", "entity.breeze.idle_ground");

    private BoostParticles() {
    }

    public static void play(ClientWorld world, Vec3d pos) {
        ModConfig config = ModConfig.getInstance();
        if (config.visualConfig.BoostParticles) {
            spawnParticles(world, pos);
        }
        if (config.soundConfig.boostSound) {
            VisualSoundUtil.playSound(world, pos, SOUND_ID);
        }
    }

    private static void spawnParticles(ClientWorld world, Vec3d pos) {
        for (int i = 0; i < 8; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 1.2;
            double offsetY = world.random.nextDouble() * 0.6;
            double offsetZ = (world.random.nextDouble() - 0.5) * 1.2;
            world.addParticleClient(ParticleTypes.FLAME,
                    pos.x + offsetX, pos.y + 0.2 + offsetY, pos.z + offsetZ,
                    world.random.nextGaussian() * 0.02,
                    0.08,
                    world.random.nextGaussian() * 0.02);
        }
    }
}
