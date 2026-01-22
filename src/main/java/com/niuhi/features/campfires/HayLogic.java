package com.niuhi.features.campfires;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class HayLogic {
    private HayLogic() {
    }

    public static boolean hasHayBelow(World world, BlockPos campfirePos) {
        return world.getBlockState(campfirePos.down()).isOf(Blocks.HAY_BLOCK);
    }
}
