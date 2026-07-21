package com.niuhi.visuals;

import com.niuhi.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

public final class RocketFlair {
    private RocketFlair() {
    }

    public static void play(ClientWorld world, Vec3d pos, int[] colors) {
        ModConfig config = ModConfig.getInstance();
        if (config.visualConfig.RocketParticles) {
            spawnParticles(world, pos, colors);
        }
    }

    private static void spawnParticles(ClientWorld world, Vec3d pos, int[] colors) {
        // Tinted vanilla campfire smoke instead of a custom particle type: a
        // mod-registered particle would end up in the synced particle registry
        // and kick joining clients that don't have the mod installed.
        int[] palette = (colors != null && colors.length > 0) ? colors : new int[] { 0xFFFFFF };
        for (int i = 0; i < 8; i++) {
            int rgb = palette[i % palette.length];
            Particle particle = MinecraftClient.getInstance().particleManager.addParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    pos.x, pos.y + 0.1, pos.z,
                    world.random.nextGaussian() * 0.02,
                    0.02,
                    world.random.nextGaussian() * 0.02);
            if (particle == null) {
                continue;
            }
            particle.setMaxAge(40 + world.random.nextInt(20));
            if (particle instanceof BillboardParticle billboard) {
                billboard.setColor(((rgb >> 16) & 0xFF) / 255.0f,
                        ((rgb >> 8) & 0xFF) / 255.0f,
                        (rgb & 0xFF) / 255.0f);
            }
        }
    }
}
