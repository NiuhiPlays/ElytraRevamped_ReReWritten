package com.niuhi.visuals;

import com.niuhi.client.particle.ColoredCampfireSmokeParticleFactory;
import com.niuhi.config.ModConfig;
import com.niuhi.particle.ModParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

public final class RocketFlair {
    private RocketFlair() {
    }

    public static void play(ClientLevel world, Vec3 pos, int[] colors) {
        ModConfig config = ModConfig.getInstance();
        if (config.visualConfig.RocketParticles) {
            spawnParticles(world, pos, colors);
        }
    }

    private static void spawnParticles(ClientLevel world, Vec3 pos, int[] colors) {
        int[] palette = (colors != null && colors.length > 0) ? colors : new int[] { 0xFFFFFF };
        for (int i = 0; i < 8; i++) {
            int color = palette[i % palette.length];
            ColoredCampfireSmokeParticleFactory.setNextColor(color);
            world.addParticle(ModParticles.COLORED_CAMPFIRE_SMOKE,
                    pos.x, pos.y + 0.1, pos.z,
                    world.getRandom().nextGaussian() * 0.02,
                    0.02,
                    world.getRandom().nextGaussian() * 0.02);
        }
    }
}
