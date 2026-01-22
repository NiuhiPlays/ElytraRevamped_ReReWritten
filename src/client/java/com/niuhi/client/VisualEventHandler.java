package com.niuhi.client;

import com.niuhi.client.particle.ColoredCampfireSmokeParticleFactory;
import com.niuhi.config.ModConfig;
import com.niuhi.network.VisualEventType;
import com.niuhi.particle.ModParticles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public final class VisualEventHandler {
    private static final Identifier BREEZE_SOUND_ID = Identifier.of("minecraft", "entity.breeze.wind_charge_burst");
    private static final Identifier FALLBACK_SOUND_ID = Identifier.of("minecraft", "block.campfire.crackle");

    private VisualEventHandler() {
    }

    public static void handleEvent(VisualEventType type, Vec3d position, Integer color) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        if (world == null) {
            return;
        }

        ModConfig config = ModConfig.getInstance();

        switch (type) {
            case BOOST -> {
                if (config.visualConfig.BoostParticles) {
                    spawnBoostParticles(world, position);
                }
                if (config.soundConfig.boostSound) {
                    playSound(world, position, BREEZE_SOUND_ID);
                }
            }
            case PULL -> {
                if (config.visualConfig.PullParticles) {
                    spawnPullParticles(world, position);
                }
                if (config.soundConfig.pullSound) {
                    playSound(world, position, BREEZE_SOUND_ID);
                }
            }
            case DRAG -> {
                if (config.visualConfig.DragParticles) {
                    spawnDragParticles(world, position);
                }
                if (config.soundConfig.dragSound) {
                    playSound(world, position, BREEZE_SOUND_ID);
                }
            }
            case ROCKET_FLAIR -> {
                if (config.visualConfig.RocketParticles) {
                    int rgb = color != null ? color : 0xFFFFFF;
                    spawnRocketParticles(world, position, rgb);
                }
                if (config.soundConfig.rocketSound) {
                    playSound(world, position, Identifier.of("minecraft", "entity.firework_rocket.launch"));
                }
            }
        }
    }

    private static void spawnBoostParticles(ClientWorld world, Vec3d pos) {
        for (int i = 0; i < 8; i++) {
            world.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                    pos.x, pos.y + 0.2, pos.z,
                    world.random.nextGaussian() * 0.02,
                    0.08,
                    world.random.nextGaussian() * 0.02);
        }
    }

    private static void spawnPullParticles(ClientWorld world, Vec3d pos) {
        for (int i = 0; i < 8; i++) {
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    pos.x, pos.y + 0.2, pos.z,
                    world.random.nextGaussian() * 0.02,
                    -0.02,
                    world.random.nextGaussian() * 0.02);
        }
    }

    private static void spawnDragParticles(ClientWorld world, Vec3d pos) {
        for (int i = 0; i < 6; i++) {
            world.addParticle(ParticleTypes.CLOUD,
                    pos.x, pos.y + 0.1, pos.z,
                    world.random.nextGaussian() * 0.01,
                    0.01,
                    world.random.nextGaussian() * 0.01);
        }
    }

    private static void spawnRocketParticles(ClientWorld world, Vec3d pos, int rgb) {
        ColoredCampfireSmokeParticleFactory.setNextColor(rgb);
        for (int i = 0; i < 8; i++) {
            world.addParticle(ModParticles.COLORED_CAMPFIRE_SMOKE,
                    pos.x, pos.y + 0.1, pos.z,
                    world.random.nextGaussian() * 0.02,
                    0.02,
                    world.random.nextGaussian() * 0.02);
        }
    }

    private static void playSound(ClientWorld world, Vec3d pos, Identifier id) {
        SoundEvent sound = null;
        if (Registries.SOUND_EVENT.containsId(id)) {
            sound = Registries.SOUND_EVENT.get(id);
        } else if (Registries.SOUND_EVENT.containsId(FALLBACK_SOUND_ID)) {
            sound = Registries.SOUND_EVENT.get(FALLBACK_SOUND_ID);
        }
        if (sound == null) {
            return;
        }
        world.playSound(pos.x, pos.y, pos.z, sound, SoundCategory.PLAYERS, 0.8f, 1.0f, false);
    }
}
