package com.niuhi.features.extra;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RiptideCooldown {
    private static final Map<UUID, Integer> PENDING_COOLDOWN = new HashMap<>();

    private RiptideCooldown() {
    }

    public static void clearOnLanding(ServerPlayer player, ModConfig config) {
        if (!config.elytraConfig.riptideNerf) {
            return;
        }
        if (player.isFallFlying() || !player.onGround()) {
            return;
        }
        Identifier group = player.getCooldowns().getCooldownGroup(new ItemStack(Items.TRIDENT));
        player.getCooldowns().removeCooldown(group);
    }

    public static void applyPending(ServerPlayer player, ModConfig config, int serverTick) {
        if (!config.elytraConfig.riptideNerf) {
            return;
        }
        Integer cooldown = PENDING_COOLDOWN.remove(player.getUUID());
        if (cooldown == null || cooldown <= 0) {
            return;
        }
        player.getCooldowns().addCooldown(new ItemStack(Items.TRIDENT), cooldown);
    }

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClientSide()) {
                return InteractionResult.PASS;
            }
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResult.PASS;
            }

            ModConfig config = ModConfig.getInstance();
            if (!config.elytraConfig.riptideNerf) {
                return InteractionResult.PASS;
            }

            var stack = player.getItemInHand(hand);
            if (!stack.is(Items.TRIDENT)) {
                return InteractionResult.PASS;
            }
            if (serverPlayer.getCooldowns().isOnCooldown(stack)) {
                return InteractionResult.FAIL;
            }
            var enchantmentRegistry = serverPlayer.level()
                    .registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT);
            Enchantment riptide = enchantmentRegistry.getValue(Enchantments.RIPTIDE);
            if (riptide == null) {
                return InteractionResult.PASS;
            }
            Holder<Enchantment> riptideEntry = enchantmentRegistry.wrapAsHolder(riptide);
            if (EnchantmentHelper.getItemEnchantmentLevel(riptideEntry, stack) <= 0) {
                return InteractionResult.PASS;
            }

            if (!serverPlayer.isFallFlying()) {
                return InteractionResult.PASS;
            }

            int cooldown = Math.max(0, config.elytraConfig.riptideCooldown);
            if (cooldown > 0) {
                PENDING_COOLDOWN.put(serverPlayer.getUUID(), cooldown);
                DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
                logger.log("ELYTRA", "Applied riptide cooldown=" + cooldown
                        + " player=" + serverPlayer.getName().getString());
            }

            return InteractionResult.PASS;
        });
    }
}
