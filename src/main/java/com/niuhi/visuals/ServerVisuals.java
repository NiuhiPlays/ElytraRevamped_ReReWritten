package com.niuhi.visuals;

import com.niuhi.config.ModConfig;
import com.niuhi.network.ModNetworking;
import com.niuhi.network.VisualEventType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public final class ServerVisuals {
    private static final double RANGE_SQUARED = 64.0 * 64.0;

    private static final Identifier BOOST_SOUND_ID = Identifier.fromNamespaceAndPath("minecraft", "entity.breeze.idle_ground");
    private static final Identifier PULL_SOUND_ID = Identifier.fromNamespaceAndPath("minecraft", "entity.breeze.idle_air");
    private static final Identifier DRAG_SOUND_ID = Identifier.fromNamespaceAndPath("minecraft", "entity.breeze.land");

    private ServerVisuals() {
    }

    public static void broadcastBoost(ServerPlayer source) {
        broadcast(source, VisualEventType.BOOST, ParticleTypes.FLAME, BOOST_SOUND_ID);
    }

    public static void broadcastPull(ServerPlayer source) {
        broadcast(source, VisualEventType.PULL, ParticleTypes.SOUL_FIRE_FLAME, PULL_SOUND_ID);
    }

    public static void broadcastDrag(ServerPlayer source) {
        broadcast(source, VisualEventType.DRAG, ParticleTypes.CLOUD, DRAG_SOUND_ID);
    }

    private static void broadcast(ServerPlayer source, VisualEventType type, ParticleOptions fallbackParticle, Identifier soundId) {
        ServerLevel world = source.level();
        Vec3 pos = source.position();
        ModConfig config = ModConfig.getInstance();

        boolean particlesEnabled = switch (type) {
            case BOOST -> config.visualConfig.BoostParticles;
            case PULL -> config.visualConfig.PullParticles;
            case DRAG -> config.visualConfig.DragParticles;
            default -> false;
        };
        boolean soundsEnabled = switch (type) {
            case BOOST -> config.soundConfig.boostSound;
            case PULL -> config.soundConfig.pullSound;
            case DRAG -> config.soundConfig.dragSound;
            default -> false;
        };

        assert world.getServer() != null;
        boolean playedFallbackSound = false;
        for (ServerPlayer target : world.getServer().getPlayerList().getPlayers()) {
            if (target.level() != world) {
                continue;
            }
            if (target.distanceToSqr(pos) > RANGE_SQUARED) {
                continue;
            }

            if (ModNetworking.canSendVisuals(target)) {
                if (particlesEnabled || soundsEnabled) {
                    ModNetworking.sendVisualEvent(target, type, pos);
                }
                continue;
            }

            if (particlesEnabled) {
                spawnFallbackParticles(world, target, pos, fallbackParticle, type);
            }

            if (soundsEnabled) {
                SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(soundId);
                if (sound != null) {
                    if (!playedFallbackSound) {
                        world.playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.PLAYERS, 1.0f, 1.0f);
                        playedFallbackSound = true;
                    }
                }
            }
        }
    }

    private static void spawnFallbackParticles(ServerLevel world, ServerPlayer target, Vec3 pos,
                                               ParticleOptions particle, VisualEventType type) {
        int count;
        double offsetX;
        double offsetY;
        double offsetZ;
        double speed;
        double yPos;

        switch (type) {
            case BOOST -> {
                count = 8;
                offsetX = 0.6;
                offsetY = 0.3;
                offsetZ = 0.6;
                speed = 0.04;
                yPos = pos.y + 0.2;
            }
            case PULL -> {
                count = 8;
                offsetX = 0.6;
                offsetY = 0.3;
                offsetZ = 0.6;
                speed = 0.02;
                yPos = pos.y + 0.2;
            }
            case DRAG -> {
                count = 6;
                offsetX = 0.15;
                offsetY = 0.05;
                offsetZ = 0.15;
                speed = 0.01;
                yPos = pos.y + 0.1;
            }
            default -> {
                count = 6;
                offsetX = 0.2;
                offsetY = 0.2;
                offsetZ = 0.2;
                speed = 0.01;
                yPos = pos.y;
            }
        }

        world.sendParticles(target, particle, false, false,
                pos.x, yPos, pos.z,
                count, offsetX, offsetY, offsetZ, speed);
    }
}
