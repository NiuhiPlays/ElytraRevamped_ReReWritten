package com.niuhi.features.fireworkrockets;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.Vec3;

public final class FlightBoost {
    private FlightBoost() {
    }

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClientSide()) {
                return InteractionResult.PASS;
            }
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResult.PASS;
            }
            if (!serverPlayer.isFallFlying()) {
                return InteractionResult.PASS;
            }

            ModConfig config = ModConfig.getInstance();
            if (!config.rocketConfig.midflightBoost || config.rocketConfig.DisableRockets) {
                return InteractionResult.PASS;
            }

            var stack = serverPlayer.getItemInHand(hand);
            if (!stack.is(Items.FIREWORK_ROCKET)) {
                return InteractionResult.PASS;
            }

            applyMidflightBoost(serverPlayer, stack, config);

            return InteractionResult.PASS;
        });
    }

    public static void applyMidflightBoost(ServerPlayer player, ItemStack stack, ModConfig config) {
        if (!config.rocketConfig.midflightBoost) {
            return;
        }
        Fireworks fireworks = stack.get(DataComponents.FIREWORKS);
        if (fireworks == null || fireworks.explosions().isEmpty()) {
            return;
        }

        double boost = Math.max(0.0, config.rocketConfig.midflightBoostAmount);
        if (boost <= 0.0) {
            return;
        }

        Vec3 direction = player.getLookAngle().normalize();
        player.push(direction.x * boost, direction.y * boost, direction.z * boost);
        player.hurtMarked = true;

        DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
        logger.log("ROCKET", "Applied midflight boost=" + String.format("%.2f", boost)
                + " player=" + player.getName().getString());
    }
}
