package com.niuhi.features.elytra;

import com.niuhi.compat.Accessories;
import net.fabricmc.loader.api.FabricLoader;
import com.niuhi.mixin.EntityFlagAccessor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Input;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ElytraBounce {
    private static final boolean ACCESSORIES_LOADED = FabricLoader.getInstance().isModLoaded("accessories");
    private static final Map<UUID, Integer> TICKS_ON_GROUND = new HashMap<>();
    private static final int MAX_GROUND_TICKS = 5;

    private ElytraBounce() {
    }

    public static boolean shouldKeepGliding(LivingEntity entity, boolean originalValue) {
        if (!(entity instanceof ServerPlayer player)) {
            return originalValue;
        }
        
        UUID uuid = player.getUUID();
        
        // Check conditions: on ground, was flying, holding jump, wearing elytra
        if (!entity.onGround()) {
            TICKS_ON_GROUND.put(uuid, 0);
            return originalValue;
        }
        
        // Check if currently gliding (before this call tries to disable it)
        if (!player.isFallFlying()) {
            TICKS_ON_GROUND.put(uuid, 0);
            return originalValue;
        }
        
        // Check if jump is held
        Input input = player.getLastClientInput();
        if (input == null || !input.jump()) {
            return originalValue;
        }
        
        // Check if wearing elytra
        if (!isWearingElytra(player)) {
            return originalValue;
        }
        
        // Increment ground ticks
        Integer groundTicks = TICKS_ON_GROUND.getOrDefault(uuid, 0);
        groundTicks++;
        TICKS_ON_GROUND.put(uuid, groundTicks);
        
        // Keep gliding for up to MAX_GROUND_TICKS
        if (groundTicks <= MAX_GROUND_TICKS) {
            return true; // Force gliding to stay active
        }
        
        return originalValue;
    }

    public static void updateBounceState(LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        
        UUID uuid = player.getUUID();
        
        // Check conditions
        if (!entity.onGround()) {
            TICKS_ON_GROUND.put(uuid, 0);
            return;
        }
        
        // Check if jump is held
        Input input = player.getLastClientInput();
        if (input == null || !input.jump()) {
            TICKS_ON_GROUND.put(uuid, 0);
            return;
        }
        
        // Check if wearing elytra
        if (!isWearingElytra(player)) {
            TICKS_ON_GROUND.put(uuid, 0);
            return;
        }
        
        // Increment ground ticks
        Integer groundTicks = TICKS_ON_GROUND.getOrDefault(uuid, 0);
        groundTicks++;
        TICKS_ON_GROUND.put(uuid, groundTicks);
        
        // Re-enable gliding flag if still within window
        if (groundTicks <= MAX_GROUND_TICKS && !player.isFallFlying()) {
            ((EntityFlagAccessor) player).errrw$setFlag(7, true);
        } else if (groundTicks > MAX_GROUND_TICKS) {
            TICKS_ON_GROUND.put(uuid, 0);
        }
    }

    private static boolean isWearingElytra(ServerPlayer player) {
        if (player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            return true;
        }
        
        if (ACCESSORIES_LOADED) {
            return Accessories.hasElytraEquipped(player);
        }
        
        return false;
    }

    public static void clearState(UUID playerUUID) {
        TICKS_ON_GROUND.remove(playerUUID);
    }
}
