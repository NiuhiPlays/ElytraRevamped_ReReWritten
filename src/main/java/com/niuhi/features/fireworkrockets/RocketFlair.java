package com.niuhi.features.fireworkrockets;

import com.niuhi.config.ModConfig;
import com.niuhi.network.ModNetworking;
import com.niuhi.network.VisualEventType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class RocketFlair {
    private static final Map<UUID, FlairState> ACTIVE_FLAIRS = new HashMap<>();
    private static final int EMIT_INTERVAL_TICKS = 2;
    private static final Identifier SOUND_ID = Identifier.of("minecraft", "entity.breeze.wind_burst");

    private RocketFlair() {
    }

    public static void trigger(ServerPlayerEntity player, ItemStack stack) {
        int durationTicks = getFlairDuration(stack);
        if (durationTicks <= 0) {
            durationTicks = 20;
        }
        int[] colors = getRocketColors(stack);
        ACTIVE_FLAIRS.put(player.getUuid(), new FlairState(durationTicks, colors));
        playActivationSound(player);
    }

    public static void tick(MinecraftServer server) {
        if (ACTIVE_FLAIRS.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, FlairState>> iterator = ACTIVE_FLAIRS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, FlairState> entry = iterator.next();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player == null) {
                iterator.remove();
                continue;
            }
            FlairState state = entry.getValue();
            state.remainingTicks--;
            if (state.remainingTicks < 0) {
                iterator.remove();
                continue;
            }
            if (state.remainingTicks % EMIT_INTERVAL_TICKS != 0) {
                continue;
            }
            emitFlair(server, player, state.colors);
        }
    }

    private static void emitFlair(MinecraftServer server, ServerPlayerEntity source, int[] colors) {
        ServerWorld world = source.getServerWorld();
        Vec3d pos = source.getPos();
        ModConfig config = ModConfig.getInstance();
        if (!config.rocketConfig.rocketFlair) {
            return;
        }

        for (ServerPlayerEntity target : server.getPlayerManager().getPlayerList()) {
            if (target.getWorld() != world) {
                continue;
            }
            if (target.squaredDistanceTo(pos) > 64 * 64) {
                continue;
            }
            if (ModNetworking.canSendVisuals(target)) {
                ModNetworking.sendVisualEvent(target, VisualEventType.ROCKET_FLAIR, pos, colors);
            } else {
                world.spawnParticles(target, ParticleTypes.CAMPFIRE_COSY_SMOKE, false, false,
                        pos.x, pos.y + 0.1, pos.z,
                        6, 0.15, 0.05, 0.15, 0.02);
            }
        }
    }

    private static void playActivationSound(ServerPlayerEntity player) {
        ModConfig config = ModConfig.getInstance();
        if (!config.soundConfig.rocketSound) {
            return;
        }
        ServerWorld world = player.getServerWorld();
        SoundEvent sound = Registries.SOUND_EVENT.get(SOUND_ID);
        if (sound == null) {
            return;
        }
        Vec3d pos = player.getPos();
        world.playSound(null, pos.x, pos.y, pos.z, sound, SoundCategory.PLAYERS, 0.9f, 1.0f);
    }

    private static int getFlairDuration(ItemStack stack) {
        FireworksComponent fireworks = stack.get(DataComponentTypes.FIREWORKS);
        if (fireworks == null) {
            return 20;
        }
        int flight = Math.max(1, fireworks.flightDuration());
        return flight * 20;
    }

    private static int[] getRocketColors(ItemStack stack) {
        FireworksComponent fireworks = stack.get(DataComponentTypes.FIREWORKS);
        if (fireworks == null || fireworks.explosions().isEmpty()) {
            return new int[0];
        }
        FireworkExplosionComponent explosion = fireworks.explosions().get(0);
        IntList colors = explosion.colors();
        if (colors.isEmpty()) {
            return new int[0];
        }
        int[] result = new int[colors.size()];
        for (int i = 0; i < colors.size(); i++) {
            result[i] = colors.getInt(i);
        }
        return result;
    }

    private static final class FlairState {
        private int remainingTicks;
        private final int[] colors;

        private FlairState(int remainingTicks, int[] colors) {
            this.remainingTicks = remainingTicks;
            this.colors = colors;
        }
    }
}
