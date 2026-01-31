package com.niuhi.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityFlagAccessor {
    @Invoker("setFlag")
    void errrw$setFlag(int flag, boolean value);
}
