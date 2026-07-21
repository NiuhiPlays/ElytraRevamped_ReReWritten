package com.niuhi.features.fireworkrockets;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

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
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID uuid = player.getUUID();
            boolean isGliding = player.isFallFlying();
            boolean wasGliding = LAST_GLIDE_STATE.getOrDefault(uuid, false);

            if (isGliding && !wasGliding) {
                GLIDE_START_TICK.put(uuid, serverTick);
            } else if (!isGliding) {
                GLIDE_START_TICK.remove(uuid);
                // Only landing resets the grace rocket; merely stopping the
                // glide (e.g. swapping the elytra out and back) must not.
                if (player.onGround() || player.isInWater()) {
                    USED_GRACE_ROCKET.remove(uuid);
                }
            }

            LAST_GLIDE_STATE.put(uuid, isGliding);
        }
    }

    public static void ensureGlideStart(ServerPlayer player, int serverTick) {
        UUID uuid = player.getUUID();
        if (!GLIDE_START_TICK.containsKey(uuid)) {
            GLIDE_START_TICK.put(uuid, serverTick);
            LAST_GLIDE_STATE.put(uuid, true);
        }
    }

    public static boolean canUseRocket(ServerPlayer player, int serverTick, int gracePeriodTicks) {
        Integer startTick = GLIDE_START_TICK.get(player.getUUID());
        if (startTick == null) {
            return false;
        }
        return serverTick - startTick <= gracePeriodTicks && !USED_GRACE_ROCKET.contains(player.getUUID());
    }

    public static void markRocketUsed(ServerPlayer player) {
        USED_GRACE_ROCKET.add(player.getUUID());
    }
}
