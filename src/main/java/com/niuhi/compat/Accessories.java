package com.niuhi.compat;


import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles compatibility with the Accessories mod
 * It's designed to be safe to load even when the mod isn't present
 */
public class Accessories {
    private static final Logger LOGGER = LoggerFactory.getLogger("elytra-revamped-compat");
    private static boolean hasWarned = false;

    public static boolean hasElytraEquipped(ServerPlayer player) {
/*        try {
            Class.forName("io.wispforest.accessories.api.AccessoriesCapability");

            var capability = io.wispforest.accessories.api.AccessoriesCapability.get(player);
            if (capability == null) {
                return false;
            }

            return capability.isEquipped(
                    io.wispforest.accessories.api.caching.ItemStackBasedPredicate.ofItem(Items.ELYTRA)
            );

        } catch (ClassNotFoundException e) {
            if (!hasWarned) {
                LOGGER.info("Accessories mod not found, disabling accessories integration");
                hasWarned = true;
            }
            return false;
        } catch (Exception e) {
            if (!hasWarned) {
                LOGGER.error("Error checking for elytra accessories: {}", e.getMessage());
                hasWarned = true;
            }
            return false;
        }*/
        return false;
        // uncomment above code when Accessories is ported to 26.1
    }
}