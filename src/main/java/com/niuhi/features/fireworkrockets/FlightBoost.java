package com.niuhi.features.fireworkrockets;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

public final class FlightBoost {
    private FlightBoost() {
    }

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient()) {
                return ActionResult.PASS;
            }
            if (!(player instanceof ServerPlayerEntity serverPlayer)) {
                return ActionResult.PASS;
            }
            if (!serverPlayer.isGliding()) {
                return ActionResult.PASS;
            }

            ModConfig config = ModConfig.getInstance();
            if (!config.rocketConfig.midflightBoost || config.rocketConfig.DisableRockets) {
                return ActionResult.PASS;
            }

            var stack = serverPlayer.getStackInHand(hand);
            if (!stack.isOf(Items.FIREWORK_ROCKET)) {
                return ActionResult.PASS;
            }

            applyMidflightBoost(serverPlayer, stack, config);

            return ActionResult.PASS;
        });
    }

    public static boolean applyMidflightBoost(ServerPlayerEntity player, ItemStack stack, ModConfig config) {
        if (!config.rocketConfig.midflightBoost) {
            return false;
        }
        FireworksComponent fireworks = stack.get(DataComponentTypes.FIREWORKS);
        if (fireworks == null || fireworks.explosions().isEmpty()) {
            return false;
        }
        boolean hasStarExplosion = fireworks.explosions().stream()
                .anyMatch(explosion -> explosion.shape() == FireworkExplosionComponent.Type.STAR);
        if (!hasStarExplosion) {
            return false;
        }

        double boost = Math.max(0.0, config.rocketConfig.midflightBoostAmount);
        if (boost <= 0.0) {
            return false;
        }

        Vec3d direction = player.getRotationVector().normalize();
        player.addVelocity(direction.x * boost, direction.y * boost, direction.z * boost);
        player.velocityModified = true;

        DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
        logger.log("ROCKET", "Applied midflight boost=" + String.format("%.2f", boost)
                + " player=" + player.getName().getString());
        return true;
    }
}
