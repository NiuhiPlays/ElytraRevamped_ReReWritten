package com.niuhi.features.campfires;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class HayLogic {
    private HayLogic() {
    }

    public static boolean hasHayBelow(Level world, BlockPos campfirePos) {
        return world.getBlockState(campfirePos.below()).is(Blocks.HAY_BLOCK);
    }
}
