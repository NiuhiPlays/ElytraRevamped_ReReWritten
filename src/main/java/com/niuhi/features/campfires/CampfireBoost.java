package com.niuhi.features.campfires;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import com.niuhi.visuals.ServerVisuals;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CampfireBoost {
    private static final Map<UUID, Integer> LAST_BOOST_TICK = new HashMap<>();
    private static final double MIN_BOOST_SCALE = 0.15;

    private CampfireBoost() {
    }

    public static void tryBoost(ServerPlayer player, ModConfig config, int serverTick) {
        int cooldown = Math.max(0, config.boostConfig.boostCooldownTicks);
        Integer lastTick = LAST_BOOST_TICK.get(player.getUUID());
        if (lastTick != null && serverTick - lastTick < cooldown) {
            return;
        }

        BoostResult result = findBoostStrength(player, config);
        if (result.strength <= 0.0) {
            return;
        }

        Vec3 velocity = player.getDeltaMovement();
        player.setDeltaMovement(velocity.x, velocity.y + result.strength, velocity.z);
        player.hurtMarked = true;

        LAST_BOOST_TICK.put(player.getUUID(), serverTick);
        ServerVisuals.broadcastBoost(player);

        DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
        logger.log("BOOST", "Applied boost=" + result.strength
                + " distance=" + String.format("%.2f", result.distance)
                + " hay=" + result.hasHay
                + " gridMultiplier=" + String.format("%.2f", result.gridMultiplier)
                + " campfire=" + formatPos(result.campfirePos)
                + " player=" + player.getName().getString());
    }

    private static BoostResult findBoostStrength(ServerPlayer player, ModConfig config) {
        Level world = player.level();
        BlockPos playerPos = player.blockPosition();

        int maxRange = config.boostConfig.detectionHeight;
        if (config.boostConfig.enableHayBoost) {
            maxRange = Math.max(maxRange, config.boostConfig.hayDetectionHeight);
        }

        BoostResult best = new BoostResult();

        for (int dy = 1; dy <= maxRange; dy++) {
            BlockPos basePos = playerPos.below(dy);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos campfirePos = basePos.offset(dx, 0, dz);
                    BlockState state = world.getBlockState(campfirePos);
                    if (!isLitCampfire(state)) {
                        continue;
                    }

                    boolean hasHay = config.boostConfig.enableHayBoost && HayLogic.hasHayBelow(world, campfirePos);
                    int range = hasHay ? config.boostConfig.hayDetectionHeight : config.boostConfig.detectionHeight;
                    double distance = player.getY() - (campfirePos.getY() + 0.5);
                    if (distance < 1.0 || distance > range) {
                        continue;
                    }

                    double baseAmount = hasHay ? config.boostConfig.hayBoostAmount : config.boostConfig.boostAmount;
                    double multiplier = GridLogic.getBoostMultiplier(world, campfirePos, config);
                    double scale = getScale(config.boostConfig.exponentialBoost, distance, range);
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

    private static boolean isLitCampfire(BlockState state) {
        return state.is(Blocks.CAMPFIRE) && state.getValue(CampfireBlock.LIT);
    }

    private static double getScale(boolean exponential, double distance, int range) {
        if (!exponential || range <= 1) {
            return 1.0;
        }
        double ratio = (distance - 1.0) / (double) (range - 1);
        ratio = Mth.clamp(ratio, 0.0, 1.0);
        return Math.max(MIN_BOOST_SCALE, 1.0 - ratio);
    }

    private static String formatPos(BlockPos pos) {
        if (pos == null) {
            return "unknown";
        }
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    private static final class BoostResult {
        private double strength = 0.0;
        private double distance = 0.0;
        private BlockPos campfirePos;
        private boolean hasHay;
        private double gridMultiplier = 1.0;
    }
}
