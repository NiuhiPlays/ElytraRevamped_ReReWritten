package com.niuhi.mixin;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.compat.Accessories;
import com.niuhi.config.ModConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Unique
    private boolean errrwPrevOnGround = false;
    @Unique
    private int errrwGroundTicks = 0;

    @Unique
    private static final int ERRRW_WINDOW_TICKS = 5;
    @Unique
    private static final double ERRRW_MIN_HOP_Y = 0.35;
    @Unique
    private static final double ERRRW_MAX_HOP_Y = 0.9;

    private ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void errrwBunnyHop(CallbackInfo ci) {
        ModConfig config = ModConfig.getInstance();
        if (!config.elytraConfig.enableBounce) {
            return;
        }

        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        PlayerInput input = self.getPlayerInput();
        boolean jumpHeld = input != null && input.jump();
        boolean onGround = self.isOnGround();

        if (onGround) {
            errrwGroundTicks++;
        } else {
            errrwGroundTicks = 0;
        }

        if (jumpHeld && errrwPrevOnGround && !onGround && hasElytraEquipped(self)) {
            if (errrwGroundTicks <= ERRRW_WINDOW_TICKS) {
                double currentY = self.getVelocity().y;
                double targetY = Math.min(Math.max(currentY, ERRRW_MIN_HOP_Y), ERRRW_MAX_HOP_Y);
                self.setVelocity(self.getVelocity().x, targetY, self.getVelocity().z);
                self.knockedBack = true;
                self.startGliding();
            }
        }

        errrwPrevOnGround = onGround;
    }

    @Unique
    private static boolean hasElytraEquipped(ServerPlayerEntity player) {
        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        if (chest != null && chest.isOf(Items.ELYTRA)) {
            return true;
        }
        return ElytraRevampedReReWritten.ACCESSSORIES_LOADED && Accessories.hasElytraEquipped(player);
    }
}