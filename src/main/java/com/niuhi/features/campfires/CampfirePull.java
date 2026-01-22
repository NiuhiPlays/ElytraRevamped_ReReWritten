package com.niuhi.features.campfires;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import com.niuhi.visuals.ServerVisuals;
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
    private static final double MIN_PULL_SCALE = 0.15;

    private CampfirePull() {
    }

    public static void tryPull(ServerPlayerEntity player, ModConfig config, int serverTick) {
        int cooldown = Math.max(0, config.pullConfig.pullCooldownTicks);
        Integer lastTick = LAST_PULL_TICK.get(player.getUuid());
        if (lastTick != null && serverTick - lastTick < cooldown) {
            return;
        }

        PullResult result = findPullStrength(player, config);
        if (result.strength <= 0.0) {
            return;
        }

        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x, velocity.y - result.strength, velocity.z);
        player.velocityModified = true;

        LAST_PULL_TICK.put(player.getUuid(), serverTick);
        ServerVisuals.broadcastPull(player);

        DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
        logger.log("PULL", "Applied pull=" + result.strength
                + " distance=" + String.format("%.2f", result.distance)
                + " hay=" + result.hasHay
                + " gridMultiplier=" + String.format("%.2f", result.gridMultiplier)
                + " campfire=" + formatPos(result.campfirePos)
                + " player=" + player.getName().getString());
    }

    private static PullResult findPullStrength(ServerPlayerEntity player, ModConfig config) {
        World world = player.getWorld();
        BlockPos playerPos = player.getBlockPos();

        int maxRange = config.pullConfig.detectionHeight;
        if (config.pullConfig.enableHayPull) {
            maxRange = Math.max(maxRange, config.pullConfig.hayDetectionHeight);
        }

        PullResult best = new PullResult();

        for (int dy = 1; dy <= maxRange; dy++) {
            BlockPos basePos = playerPos.down(dy);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos campfirePos = basePos.add(dx, 0, dz);
                    BlockState state = world.getBlockState(campfirePos);
                    if (!isLitSoulCampfire(state)) {
                        continue;
                    }

                    boolean hasHay = config.pullConfig.enableHayPull && HayLogic.hasHayBelow(world, campfirePos);
                    int range = hasHay ? config.pullConfig.hayDetectionHeight : config.pullConfig.detectionHeight;
                    double distance = player.getY() - (campfirePos.getY() + 0.5);
                    if (distance < 1.0 || distance > range) {
                        continue;
                    }

                    double baseAmount = hasHay ? config.pullConfig.hayPullAmount : config.pullConfig.pullAmount;
                    double multiplier = GridLogic.getPullMultiplier(world, campfirePos, config);
                    double scale = getScale(config.pullConfig.exponentialPull, distance, range);
                    double strength = baseAmount * multiplier * scale;

                    if (strength > best.strength) {
                        best.strength = strength;
                        best.distance = distance;
                        best.campfirePos = campfirePos;
                        best.hasHay = hasHay;
                        best.gridMultiplier = multiplier;
                    }
                }
            }
        }

        return best;
    }

    private static boolean isLitSoulCampfire(BlockState state) {
        return state.isOf(Blocks.SOUL_CAMPFIRE) && state.get(CampfireBlock.LIT);
    }

    private static double getScale(boolean exponential, double distance, int range) {
        if (!exponential || range <= 1) {
            return 1.0;
        }
        double ratio = (distance - 1.0) / (double) (range - 1);
        ratio = MathHelper.clamp(ratio, 0.0, 1.0);
        return Math.max(MIN_PULL_SCALE, Math.pow(ratio, 2));
    }

    private static String formatPos(BlockPos pos) {
        if (pos == null) {
            return "unknown";
        }
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    private static final class PullResult {
        private double strength = 0.0;
        private double distance = 0.0;
        private BlockPos campfirePos;
        private boolean hasHay;
        private double gridMultiplier = 1.0;
    }
}
