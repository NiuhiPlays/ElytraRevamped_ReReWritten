package com.niuhi.features.fireworkrockets;

import com.niuhi.config.ModConfig;
import com.niuhi.network.ModNetworking;
import com.niuhi.network.VisualEventType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class RocketFlair {
    private static final Map<UUID, FlairState> ACTIVE_FLAIRS = new HashMap<>();
    private static final int EMIT_INTERVAL_TICKS = 2;

    private RocketFlair() {
    }

    public static void trigger(ServerPlayerEntity player, ItemStack stack) {
        int durationTicks = getFlairDuration(stack);
        if (durationTicks <= 0) {
            durationTicks = 20;
        }
        Integer color = getRocketColor(stack);
        ACTIVE_FLAIRS.put(player.getUuid(), new FlairState(durationTicks, color));
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
            emitFlair(server, player, state.color);
        }
    }

    private static void emitFlair(MinecraftServer server, ServerPlayerEntity source, Integer color) {
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
                ModNetworking.sendVisualEvent(target, VisualEventType.ROCKET_FLAIR, pos, color);
            } else {
                world.spawnParticles(target, ParticleTypes.CAMPFIRE_COSY_SMOKE, false, false,
                        pos.x, pos.y + 0.1, pos.z,
                        6, 0.15, 0.05, 0.15, 0.02);
            }
        }
    }

    private static int getFlairDuration(ItemStack stack) {
        FireworksComponent fireworks = stack.get(DataComponentTypes.FIREWORKS);
        if (fireworks == null) {
            return 20;
        }
        int flight = Math.max(1, fireworks.flightDuration());
        return flight * 20;
    }

    private static Integer getRocketColor(ItemStack stack) {
        FireworksComponent fireworks = stack.get(DataComponentTypes.FIREWORKS);
        if (fireworks == null || fireworks.explosions().isEmpty()) {
            return null;
        }
        FireworkExplosionComponent explosion = fireworks.explosions().get(0);
        IntList colors = explosion.colors();
        if (colors.isEmpty()) {
            return null;
        }
        return colors.getInt(0);
    }

    private static final class FlairState {
        private int remainingTicks;
        private final Integer color;

        private FlairState(int remainingTicks, Integer color) {
            this.remainingTicks = remainingTicks;
            this.color = color;
        }
    }
}
