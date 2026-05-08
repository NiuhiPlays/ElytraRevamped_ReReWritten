package com.niuhi.mixin;

import com.niuhi.config.ModConfig;
import com.niuhi.features.elytra.ElytraBounce;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityBounceMixin {

    @Inject(method = "travel", at = @At("TAIL"), cancellable = false)
    private void errrw$postTravel(Vec3 movementInput, CallbackInfo ci) {
        ModConfig config = ModConfig.getInstance();
        if (!config.elytraConfig.enableBounce) {
            return;
        }
        
        LivingEntity self = (LivingEntity) (Object) this;
        ElytraBounce.updateBounceState(self);
    }
}