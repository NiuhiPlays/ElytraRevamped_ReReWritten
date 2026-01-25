package com.niuhi.visuals;

import com.niuhi.config.ModConfig;
import com.niuhi.network.ModNetworking;
import com.niuhi.network.VisualEventType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public final class ServerVisuals {
    private static final double RANGE_SQUARED = 64.0 * 64.0;

    private static final Identifier BOOST_SOUND_ID = Identifier.of("minecraft", "entity.breeze.idle_ground");
    private static final Identifier PULL_SOUND_ID = Identifier.of("minecraft", "entity.breeze.idle_air");
    private static final Identifier DRAG_SOUND_ID = Identifier.of("minecraft", "entity.breeze.land");

    private ServerVisuals() {
    }

    public static void broadcastBoost(ServerPlayerEntity source) {
        broadcast(source, VisualEventType.BOOST, ParticleTypes.FLAME, BOOST_SOUND_ID);
    }

    public static void broadcastPull(ServerPlayerEntity source) {
        broadcast(source, VisualEventType.PULL, ParticleTypes.SOUL_FIRE_FLAME, PULL_SOUND_ID);
    }

    public static void broadcastDrag(ServerPlayerEntity source) {
        broadcast(source, VisualEventType.DRAG, ParticleTypes.CLOUD, DRAG_SOUND_ID);
    }

    private static void broadcast(ServerPlayerEntity source, VisualEventType type, ParticleEffect fallbackParticle, Identifier soundId) {
        ServerWorld world = source.getWorld();
        Vec3d pos = source.getPos();
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

        for (ServerPlayerEntity target : world.getServer().getPlayerManager().getPlayerList()) {
            if (target.getWorld() != world) {
                continue;
            }
            if (target.squaredDistanceTo(pos) > RANGE_SQUARED) {
                continue;
            }

            if (ModNetworking.canSendVisuals(target)) {
                if (particlesEnabled || soundsEnabled) {
                    ModNetworking.sendVisualEvent(target, type, pos);
                }
                continue;
            }

            if (soundsEnabled) {
                SoundEvent sound = Registries.SOUND_EVENT.get(soundId);
                if (sound != null) {
                    target.playSoundToPlayer(sound, SoundCategory.PLAYERS, 0.8f, 1.0f);
                }
            }
        }
    }
}
