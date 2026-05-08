package com.niuhi.features.campfires;

import com.niuhi.config.ModConfig;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class GridLogic {
    private GridLogic() {
    }

    public static double getBoostMultiplier(Level world, BlockPos campfirePos, ModConfig config) {
        if (!config.boostConfig.enableGridBoost) {
            return 1.0;
        }
        if (isThreeByThree(world, campfirePos, false)) {
            return 1.0 + config.boostConfig.gridThreeByThree;
        }
        if (isTwoByTwo(world, campfirePos, false)) {
            return 1.0 + config.boostConfig.gridTwoByTwo;
        }
        return 1.0;
    }

    public static double getPullMultiplier(Level world, BlockPos campfirePos, ModConfig config) {
        if (!config.pullConfig.enableGridPull) {
            return 1.0;
        }
        if (isThreeByThree(world, campfirePos, true)) {
            return 1.0 + config.pullConfig.gridThreeByThree;
        }
        if (isTwoByTwo(world, campfirePos, true)) {
            return 1.0 + config.pullConfig.gridTwoByTwo;
        }
        return 1.0;
    }

    private static boolean isTwoByTwo(Level world, BlockPos campfirePos, boolean soul) {
        for (int offsetX = -1; offsetX <= 0; offsetX++) {
            for (int offsetZ = -1; offsetZ <= 0; offsetZ++) {
                if (checkSquare(world, campfirePos.offset(offsetX, 0, offsetZ), 2, soul)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isThreeByThree(Level world, BlockPos campfirePos, boolean soul) {
        for (int offsetX = -2; offsetX <= 0; offsetX++) {
            for (int offsetZ = -2; offsetZ <= 0; offsetZ++) {
                if (checkSquare(world, campfirePos.offset(offsetX, 0, offsetZ), 3, soul)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkSquare(Level world, BlockPos origin, int size, boolean soul) {
        for (int dx = 0; dx < size; dx++) {
            for (int dz = 0; dz < size; dz++) {
                BlockState state = world.getBlockState(origin.offset(dx, 0, dz));
                if (!isLitCampfire(state, soul)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isLitCampfire(BlockState state, boolean soul) {
        if (soul) {
            return state.is(Blocks.SOUL_CAMPFIRE) && state.getValue(CampfireBlock.LIT);
        }
        return state.is(Blocks.CAMPFIRE) && state.getValue(CampfireBlock.LIT);
    }
}
