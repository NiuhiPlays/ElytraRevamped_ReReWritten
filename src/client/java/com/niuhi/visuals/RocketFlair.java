package com.niuhi.visuals;

import com.niuhi.client.particle.ColoredCampfireSmokeParticleFactory;
import com.niuhi.config.ModConfig;
import com.niuhi.particle.ModParticles;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public final class RocketFlair {
    private static final Identifier SOUND_ID = Identifier.of("minecraft", "entity.breeze.wind_burst");

    private RocketFlair() {
    }

    public static void play(ClientWorld world, Vec3d pos, Integer color) {
        ModConfig config = ModConfig.getInstance();
        if (config.visualConfig.RocketParticles) {
            int rgb = color != null ? color : 0xFFFFFF;
            spawnParticles(world, pos, rgb);
        }
        if (config.soundConfig.rocketSound) {
            VisualSoundUtil.playSound(world, pos, SOUND_ID);
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
