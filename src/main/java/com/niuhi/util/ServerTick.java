package com.niuhi.util;

import com.niuhi.config.ModConfig;
import com.niuhi.features.campfires.CampfireBoost;
import com.niuhi.features.campfires.CampfirePull;
import com.niuhi.features.drag.AirDrag;
import com.niuhi.features.drag.ControllableDrag;
import com.niuhi.features.elytra.ElytraDetection;
import com.niuhi.features.extra.RiptideCooldown;
import com.niuhi.features.fireworkrockets.RocketFlair;
import com.niuhi.features.fireworkrockets.RocketGrace;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class ServerTick {
    private static final ElytraDetection ELYTRA_DETECTION = new ElytraDetection();
    private static int serverTicks = 0;

    private ServerTick() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ServerTick::onEndServerTick);
    }

    public static int getServerTicks() {
        return serverTicks;
    }

    private static void onEndServerTick(MinecraftServer server) {
        serverTicks++;
        ModConfig config = ModConfig.getInstance();

        RocketGrace.onServerTick(serverTicks, server);
        RocketFlair.tick(server);

        if (!config.elytraConfig.enableMod) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            RiptideCooldown.clearOnLanding(player, config);
            RiptideCooldown.applyPending(player, config, serverTicks);
            
            if (!ELYTRA_DETECTION.isFlying(player)) {
                continue;
            }

            if (config.boostConfig.enableBoost) {
                CampfireBoost.tryBoost(player, config, serverTicks);
            }
            if (config.pullConfig.enablePull) {
                CampfirePull.tryPull(player, config, serverTicks);
            }
            ControllableDrag.tryApply(player, config, serverTicks);
            AirDrag.tryApply(player, config, serverTicks);
        }
    }
}
