package com.niuhi.compat;

import eu.pb4.trinkets.api.TrinketAttachment;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles compatibility with the Trinkets Updated mod
 * Only call it when "trinkets_updated" is loaded, otherwise the Trinkets classes are missing
 */
public class Trinkets {
    private static final Logger LOGGER = LoggerFactory.getLogger("elytra-revamped-compat");
    private static boolean hasWarned = false;

    public static boolean hasElytraEquipped(ServerPlayer player) {
        try {
            TrinketAttachment attachment = TrinketsApi.getAttachment(player);
            if (attachment == null) {
                return false;
            }

            // requireActive skips cosmetic slots, which can't be used to glide
            return attachment.isEquipped(stack -> stack.is(Items.ELYTRA), true);
        } catch (Throwable e) {
            if (!hasWarned) {
                LOGGER.error("Error checking for elytra trinkets: {}", e.toString());
                hasWarned = true;
            }
            return false;
        }
    }
}
