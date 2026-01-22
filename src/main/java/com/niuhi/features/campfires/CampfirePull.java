package com.niuhi.features.campfires;

import com.niuhi.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CampfirePull {
    private static final Map<UUID, Integer> LAST_PULL_TICK = new HashMap<>();

    private CampfirePull() {
    }

    public static void tryPull(ServerPlayerEntity player, ModConfig config, int serverTick) {
        int cooldown = Math.max(0, config.pullConfig.pullCooldownTicks);
        Integer lastTick = LAST_PULL_TICK.get(player.getUuid());
        if (lastTick != null && serverTick - lastTick < cooldown) {
            return;
        }

        double strength = findPullStrength(player, config);
        if (strength <= 0.0) {
            return;
        }

        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x, velocity.y - strength, velocity.z);
        player.velocityModified = true;

        LAST_PULL_TICK.put(player.getUuid(), serverTick);
    }

    private static double findPullStrength(ServerPlayerEntity player, ModConfig config) {
        World world = player.getWorld();
        BlockPos playerPos = player.getBlockPos();

        int maxRange = config.pullConfig.detectionHeight;
        if (config.pullConfig.enableHayPull) {
            maxRange = Math.max(maxRange, config.pullConfig.hayDetectionHeight);
        }

        double bestStrength = 0.0;

        for (int dy = 1; dy <= maxRange; dy++) {
            BlockPos campfirePos = playerPos.down(dy);
            BlockState state = world.getBlockState(campfirePos);
            if (!isLitSoulCampfire(state)) {
                continue;
            }

            boolean hasHay = config.pullConfig.enableHayPull && HayLogic.hasHayBelow(world, campfirePos);
            int range = hasHay ? config.pullConfig.hayDetectionHeight : config.pullConfig.detectionHeight;
            if (dy > range) {
                continue;
            }

            double baseAmount = hasHay ? config.pullConfig.hayPullAmount : config.pullConfig.pullAmount;
            double multiplier = GridLogic.getPullMultiplier(world, campfirePos, config);
            double scale = getScale(config.pullConfig.exponentialPull, dy, range);
            double strength = baseAmount * multiplier * scale;

            if (strength > bestStrength) {
                bestStrength = strength;
            }
        }

        return bestStrength;
    }

    private static boolean isLitSoulCampfire(BlockState state) {
        return state.isOf(Blocks.SOUL_CAMPFIRE) && state.get(CampfireBlock.LIT);
    }

    private static double getScale(boolean exponential, int distance, int range) {
        if (!exponential || range <= 1) {
            return 1.0;
        }
        double ratio = (double) (distance - 1) / (double) (range - 1);
        ratio = MathHelper.clamp(ratio, 0.0, 1.0);
        return Math.pow(ratio, 2);
    }
}
