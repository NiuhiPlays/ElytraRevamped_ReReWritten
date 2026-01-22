package com.niuhi.features.fireworkrockets;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class RocketGrace {
    private static final Map<UUID, Integer> GLIDE_START_TICK = new HashMap<>();
    private static final Set<UUID> USED_GRACE_ROCKET = new HashSet<>();
    private static final Map<UUID, Boolean> LAST_GLIDE_STATE = new HashMap<>();

    private RocketGrace() {
    }

    public static void onServerTick(int serverTick, MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            UUID uuid = player.getUuid();
            boolean isGliding = player.isGliding();
            boolean wasGliding = LAST_GLIDE_STATE.getOrDefault(uuid, false);

            if (isGliding && !wasGliding) {
                GLIDE_START_TICK.put(uuid, serverTick);
                USED_GRACE_ROCKET.remove(uuid);
            } else if (!isGliding) {
                GLIDE_START_TICK.remove(uuid);
                USED_GRACE_ROCKET.remove(uuid);
            }

            LAST_GLIDE_STATE.put(uuid, isGliding);
        }
    }

    public static void ensureGlideStart(ServerPlayerEntity player, int serverTick) {
        UUID uuid = player.getUuid();
        if (!GLIDE_START_TICK.containsKey(uuid)) {
            GLIDE_START_TICK.put(uuid, serverTick);
            USED_GRACE_ROCKET.remove(uuid);
            LAST_GLIDE_STATE.put(uuid, true);
        }
    }

    public static boolean canUseRocket(ServerPlayerEntity player, int serverTick, int gracePeriodTicks) {
        Integer startTick = GLIDE_START_TICK.get(player.getUuid());
        if (startTick == null) {
            return false;
        }
        return serverTick - startTick <= gracePeriodTicks && !USED_GRACE_ROCKET.contains(player.getUuid());
    }

    public static void markRocketUsed(ServerPlayerEntity player) {
        USED_GRACE_ROCKET.add(player.getUuid());
    }
}
