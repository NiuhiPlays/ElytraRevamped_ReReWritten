package com.niuhi.util;

import net.minecraft.world.entity.Entity;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * Flags an entity so the server sends its velocity to the client next tick.
 * <p>
 * Minecraft 26.3 renamed {@code Entity.hurtMarked} to {@code Entity.syncVelocity}.
 * 26.x runs with unobfuscated names, so the field is resolved by name at runtime
 * to keep a single jar working on 26.1, 26.2 and 26.3+.
 */
public final class VelocitySync {
    private static final VarHandle FLAG = resolve("syncVelocity", "hurtMarked");

    private VelocitySync() {
    }

    public static void mark(Entity entity) {
        FLAG.set(entity, true);
    }

    private static VarHandle resolve(String... names) {
        MethodHandles.Lookup lookup = MethodHandles.publicLookup();
        for (String name : names) {
            try {
                return lookup.findVarHandle(Entity.class, name, boolean.class);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }
        throw new IllegalStateException("No velocity sync field found on Entity (tried syncVelocity, hurtMarked)");
    }
}
