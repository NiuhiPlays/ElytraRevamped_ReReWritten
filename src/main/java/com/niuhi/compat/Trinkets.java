package com.niuhi.compat;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles compatibility with the Trinkets (Updated) mod
 * Only call it when "trinkets" is loaded, otherwise the Trinkets classes are missing
 */
public class Trinkets {
    private static final Logger LOGGER = LoggerFactory.getLogger("elytra-revamped-compat");
    private static boolean hasWarned = false;

    public static boolean hasElytraEquipped(ServerPlayerEntity player) {
        try {
            return TrinketsApi.getTrinketComponent(player)
                    .map(component -> component.isEquipped(stack -> stack.isOf(Items.ELYTRA)))
                    .orElse(false);
        } catch (Throwable e) {
            if (!hasWarned) {
                LOGGER.error("Error checking for elytra trinkets: {}", e.toString());
                hasWarned = true;
            }
            return false;
        }
    }
}
