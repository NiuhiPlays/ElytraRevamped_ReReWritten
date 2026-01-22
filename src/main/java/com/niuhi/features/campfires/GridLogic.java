package com.niuhi.features.campfires;

import com.niuhi.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class GridLogic {
    private GridLogic() {
    }

    public static double getBoostMultiplier(World world, BlockPos campfirePos, ModConfig config) {
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

    public static double getPullMultiplier(World world, BlockPos campfirePos, ModConfig config) {
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

    private static boolean isTwoByTwo(World world, BlockPos campfirePos, boolean soul) {
        for (int offsetX = -1; offsetX <= 0; offsetX++) {
            for (int offsetZ = -1; offsetZ <= 0; offsetZ++) {
                if (checkSquare(world, campfirePos.add(offsetX, 0, offsetZ), 2, soul)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isThreeByThree(World world, BlockPos campfirePos, boolean soul) {
        for (int offsetX = -2; offsetX <= 0; offsetX++) {
            for (int offsetZ = -2; offsetZ <= 0; offsetZ++) {
                if (checkSquare(world, campfirePos.add(offsetX, 0, offsetZ), 3, soul)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkSquare(World world, BlockPos origin, int size, boolean soul) {
        for (int dx = 0; dx < size; dx++) {
            for (int dz = 0; dz < size; dz++) {
                BlockState state = world.getBlockState(origin.add(dx, 0, dz));
                if (!isLitCampfire(state, soul)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isLitCampfire(BlockState state, boolean soul) {
        if (soul) {
            return state.isOf(Blocks.SOUL_CAMPFIRE) && state.get(CampfireBlock.LIT);
        }
        return state.isOf(Blocks.CAMPFIRE) && state.get(CampfireBlock.LIT);
    }
}
