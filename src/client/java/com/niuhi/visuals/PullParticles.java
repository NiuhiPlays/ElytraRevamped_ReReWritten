package com.niuhi.visuals;

import com.niuhi.config.ModConfig;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public final class PullParticles {
    private static final Identifier SOUND_ID = Identifier.of("minecraft", "entity.breeze.idle_air");

    private PullParticles() {
    }

    public static void play(ClientWorld world, Vec3d pos) {
        ModConfig config = ModConfig.getInstance();
        if (config.visualConfig.PullParticles) {
            spawnParticles(world, pos);
        }
        if (config.soundConfig.pullSound) {
            VisualSoundUtil.playSound(world, pos, SOUND_ID);
        }
    }

    private static void spawnParticles(ClientWorld world, Vec3d pos) {
        for (int i = 0; i < 8; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 1.2;
            double offsetY = world.random.nextDouble() * 0.6;
            double offsetZ = (world.random.nextDouble() - 0.5) * 1.2;
            world.addParticleClient(ParticleTypes.SOUL_FIRE_FLAME,
                    pos.x + offsetX, pos.y + 0.2 + offsetY, pos.z + offsetZ,
                    world.random.nextGaussian() * 0.02,
                    -0.02,
                    world.random.nextGaussian() * 0.02);
        }
    }
}
