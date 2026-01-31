package com.niuhi.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

/**
 * This mixin class currently only holds the constructor injection for ServerPlayerEntity.
 * The elytra bounce logic has been moved to LivingEntityMixin which uses the updateFallFlying hook.
 */
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    private ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }
}