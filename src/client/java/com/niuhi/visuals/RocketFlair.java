package com.niuhi.visuals;

import com.niuhi.client.particle.ColoredCampfireSmokeParticleFactory;
import com.niuhi.config.ModConfig;
import com.niuhi.particle.ModParticles;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

public final class RocketFlair {
    private RocketFlair() {
    }

    public static void play(ClientWorld world, Vec3d pos, Integer color) {
        ModConfig config = ModConfig.getInstance();
        if (config.visualConfig.RocketParticles) {
            int rgb = color != null ? color : 0xFFFFFF;
            spawnParticles(world, pos, rgb);
        }
    }

    private static void spawnParticles(ClientWorld world, Vec3d pos, int rgb) {
        ColoredCampfireSmokeParticleFactory.setNextColor(rgb);
        for (int i = 0; i < 8; i++) {
            world.addParticle(ModParticles.COLORED_CAMPFIRE_SMOKE,
                    pos.x, pos.y + 0.1, pos.z,
                    world.random.nextGaussian() * 0.02,
                    0.02,
                    world.random.nextGaussian() * 0.02);
        }
    }
}
