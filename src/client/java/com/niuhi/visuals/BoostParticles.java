package com.niuhi.visuals;

import com.niuhi.config.ModConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public final class BoostParticles {
    private static final Identifier SOUND_ID = Identifier.fromNamespaceAndPath("minecraft", "entity.breeze.idle_ground");

    private BoostParticles() {
    }

    public static void play(ClientLevel world, Vec3 pos) {
        ModConfig config = ModConfig.getInstance();
        if (config.visualConfig.BoostParticles) {
            spawnParticles(world, pos);
        }
        if (config.soundConfig.boostSound) {
            VisualSoundUtil.playSound(world, pos, SOUND_ID);
        }
    }

    private static void spawnParticles(ClientLevel world, Vec3 pos) {
        for (int i = 0; i < 8; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 1.2;
            double offsetY = world.getRandom().nextDouble() * 0.6;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 1.2;
            world.addParticle(ParticleTypes.FLAME,
                    pos.x + offsetX, pos.y + 0.2 + offsetY, pos.z + offsetZ,
                    world.getRandom().nextGaussian() * 0.02,
                    0.08,
                    world.getRandom().nextGaussian() * 0.02);
        }
    }
}
