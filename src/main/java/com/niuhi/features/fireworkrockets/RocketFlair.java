package com.niuhi.features.fireworkrockets;

import com.niuhi.config.ModConfig;
import com.niuhi.network.ModNetworking;
import com.niuhi.network.VisualEventType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class RocketFlair {
    private static final Map<UUID, FlairState> ACTIVE_FLAIRS = new HashMap<>();
    private static final int EMIT_INTERVAL_TICKS = 2;
    private static final Identifier SOUND_ID = Identifier.fromNamespaceAndPath("minecraft", "entity.breeze.wind_burst");

    private RocketFlair() {
    }

    public static void trigger(ServerPlayer player, ItemStack stack) {
        int durationTicks = getFlairDuration(stack);
        if (durationTicks <= 0) {
            durationTicks = 20;
        }
        int[] colors = getRocketColors(stack);
        ACTIVE_FLAIRS.put(player.getUUID(), new FlairState(durationTicks, colors));
        playActivationSound(player);
    }

    public static void tick(MinecraftServer server) {
        if (ACTIVE_FLAIRS.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, FlairState>> iterator = ACTIVE_FLAIRS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, FlairState> entry = iterator.next();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
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

    private static void emitFlair(MinecraftServer server, ServerPlayer source, int[] colors) {
        ServerLevel world = source.level();
        Vec3 pos = source.position();
        ModConfig config = ModConfig.getInstance();
        if (!config.rocketConfig.rocketFlair) {
            return;
        }

        for (ServerPlayer target : server.getPlayerList().getPlayers()) {
            if (target.level() != world) {
                continue;
            }
            if (target.distanceToSqr(pos) > 64 * 64) {
                continue;
            }
            if (ModNetworking.canSendVisuals(target)) {
                ModNetworking.sendVisualEvent(target, VisualEventType.ROCKET_FLAIR, pos, colors);
            } else {
                world.sendParticles(target, ParticleTypes.CAMPFIRE_COSY_SMOKE, false, false,
                        pos.x, pos.y + 0.1, pos.z,
                        6, 0.15, 0.05, 0.15, 0.02);
            }
        }
    }

    private static void playActivationSound(ServerPlayer player) {
        ModConfig config = ModConfig.getInstance();
        if (!config.soundConfig.rocketSound) {
            return;
        }
        ServerLevel world = player.level();
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(SOUND_ID);
        if (sound == null) {
            return;
        }
        Vec3 pos = player.position();
        world.playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.PLAYERS, 0.9f, 1.0f);
    }

    private static int getFlairDuration(ItemStack stack) {
        Fireworks fireworks = stack.get(DataComponents.FIREWORKS);
        if (fireworks == null) {
            return 20;
        }
        int flight = Math.max(1, fireworks.flightDuration());
        return flight * 20;
    }

    private static int[] getRocketColors(ItemStack stack) {
        Fireworks fireworks = stack.get(DataComponents.FIREWORKS);
        if (fireworks == null || fireworks.explosions().isEmpty()) {
            return new int[0];
        }
        FireworkExplosion explosion = fireworks.explosions().getFirst();
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
