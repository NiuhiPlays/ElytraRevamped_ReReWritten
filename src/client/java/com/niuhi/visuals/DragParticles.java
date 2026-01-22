package com.niuhi.visuals;

import com.niuhi.config.ModConfig;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public final class DragParticles {
    private static final Identifier SOUND_ID = Identifier.of("minecraft", "entity.breeze.land");

    private DragParticles() {
    }

    public static void play(ClientWorld world, Vec3d pos) {
        ModConfig config = ModConfig.getInstance();
        if (config.visualConfig.DragParticles) {
            spawnParticles(world, pos);
        }
        if (config.soundConfig.dragSound) {
            if (world.random.nextInt(5) == 0) {
                VisualSoundUtil.playSound(world, pos, SOUND_ID);
            }
        }
    }

    private static void spawnParticles(ClientWorld world, Vec3d pos) {
        for (int i = 0; i < 6; i++) {
            world.addParticle(ParticleTypes.CLOUD,
                    pos.x, pos.y + 0.1, pos.z,
                    world.random.nextGaussian() * 0.01,
                    0.01,
                    world.random.nextGaussian() * 0.01);
        }
    }
}
