package com.niuhi.features.fireworkrockets;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import com.niuhi.util.ServerTick;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

public final class DisableRockets {
    private DisableRockets() {
    }

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClientSide()) {
                return InteractionResult.PASS;
            }

            ModConfig config = ModConfig.getInstance();
            if (!player.getItemInHand(hand).is(Items.FIREWORK_ROCKET)) {
                return InteractionResult.PASS;
            }

            if (!(player instanceof ServerPlayer serverPlayer) || !serverPlayer.isFallFlying()) {
                return InteractionResult.PASS;
            }

            int serverTick = ServerTick.getServerTicks();
            RocketGrace.ensureGlideStart(serverPlayer, serverTick);

            if (!config.rocketConfig.DisableRockets) {
                return InteractionResult.PASS;
            }

            if (config.rocketConfig.initialBoost
                    && RocketGrace.canUseRocket(serverPlayer, serverTick, config.rocketConfig.gracePeriodTicks)) {
                RocketGrace.markRocketUsed(serverPlayer);
                DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
                logger.log("ROCKET", "Allowed grace rocket for " + serverPlayer.getName().getString()
                        + " tick=" + serverTick);
                return InteractionResult.PASS;
            }

            DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
            logger.log("ROCKET", "Blocked rocket for " + serverPlayer.getName().getString()
                    + " tick=" + serverTick);
            FlightBoost.applyMidflightBoost(serverPlayer, player.getItemInHand(hand), config);
            if (config.rocketConfig.rocketFlair) {
                RocketFlair.trigger(serverPlayer, player.getItemInHand(hand));
            }
            // Every midflight rocket use costs a rocket (except in creative),
            // boosted or not — otherwise blocked rockets are free to spam.
            player.getItemInHand(hand).consume(1, serverPlayer);
            return InteractionResult.FAIL;
        });
    }
}
