package com.niuhi.visuals;

import com.niuhi.config.ModConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public final class DragParticles {
    private static final Identifier SOUND_ID = Identifier.fromNamespaceAndPath("minecraft", "entity.breeze.land");

    private DragParticles() {
    }

    public static void play(ClientLevel world, Vec3 pos) {
        ModConfig config = ModConfig.getInstance();
        if (config.visualConfig.DragParticles) {
            spawnParticles(world, pos);
        }
        if (config.soundConfig.dragSound) {
            if (world.getRandom().nextInt(5) == 0) {
                VisualSoundUtil.playSound(world, pos, SOUND_ID);
            }
        }
    }

    private static void spawnParticles(ClientLevel world, Vec3 pos) {
        for (int i = 0; i < 6; i++) {
            world.addParticle(ParticleTypes.CLOUD,
                    pos.x, pos.y + 0.1, pos.z,
                    world.getRandom().nextGaussian() * 0.01,
                    0.01,
                    world.getRandom().nextGaussian() * 0.01);
        }
    }
}
