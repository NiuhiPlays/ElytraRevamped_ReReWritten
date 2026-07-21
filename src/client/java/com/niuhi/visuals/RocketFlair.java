package com.niuhi.visuals;

import com.niuhi.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.particles.ParticleTypes;
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
        // Tinted vanilla campfire smoke instead of a custom particle type: a
        // mod-registered particle would end up in the synced particle registry
        // and kick joining clients that don't have the mod installed.
        int[] palette = (colors != null && colors.length > 0) ? colors : new int[] { 0xFFFFFF };
        for (int i = 0; i < 8; i++) {
            int rgb = palette[i % palette.length];
            Particle particle = Minecraft.getInstance().particleEngine.createParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    pos.x, pos.y + 0.1, pos.z,
                    world.getRandom().nextGaussian() * 0.02,
                    0.02,
                    world.getRandom().nextGaussian() * 0.02);
            if (particle == null) {
                continue;
            }
            particle.setLifetime(40 + world.getRandom().nextInt(20));
            if (particle instanceof SingleQuadParticle quad) {
                quad.setColor(((rgb >> 16) & 0xFF) / 255.0f,
                        ((rgb >> 8) & 0xFF) / 255.0f,
                        (rgb & 0xFF) / 255.0f);
            }
        }
    }
}
