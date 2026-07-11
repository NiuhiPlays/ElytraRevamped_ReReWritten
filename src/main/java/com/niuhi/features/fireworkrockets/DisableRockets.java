package com.niuhi.features.fireworkrockets;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import com.niuhi.util.ServerTick;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public final class DisableRockets {
    private DisableRockets() {
    }

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient) {
                return ActionResult.PASS;
            }

            ModConfig config = ModConfig.getInstance();
            if (!player.getStackInHand(hand).isOf(Items.FIREWORK_ROCKET)) {
                return ActionResult.PASS;
            }

            if (!(player instanceof ServerPlayerEntity serverPlayer) || !serverPlayer.isGliding()) {
                return ActionResult.PASS;
            }

            int serverTick = ServerTick.getServerTicks();
            RocketGrace.ensureGlideStart(serverPlayer, serverTick);

            if (!config.rocketConfig.DisableRockets) {
                return ActionResult.PASS;
            }

            if (config.rocketConfig.initialBoost
                    && RocketGrace.canUseRocket(serverPlayer, serverTick, config.rocketConfig.gracePeriodTicks)) {
                RocketGrace.markRocketUsed(serverPlayer);
                DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
                logger.log("ROCKET", "Allowed grace rocket for " + serverPlayer.getName().getString()
                        + " tick=" + serverTick);
                return ActionResult.PASS;
            }

            DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
            logger.log("ROCKET", "Blocked rocket for " + serverPlayer.getName().getString()
                    + " tick=" + serverTick);
            boolean boosted = FlightBoost.applyMidflightBoost(serverPlayer, player.getStackInHand(hand), config);
            if (config.rocketConfig.rocketFlair) {
                RocketFlair.trigger(serverPlayer, player.getStackInHand(hand));
            }
            if (boosted) {
                player.getStackInHand(hand).decrementUnlessCreative(1, serverPlayer);
            }
            return ActionResult.FAIL;
        });
    }
}
