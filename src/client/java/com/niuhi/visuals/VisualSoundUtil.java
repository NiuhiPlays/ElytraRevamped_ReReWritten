package com.niuhi.visuals;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public final class VisualSoundUtil {
    private static final Identifier FALLBACK_SOUND_ID = Identifier.fromNamespaceAndPath("minecraft", "block.campfire.crackle");

    private VisualSoundUtil() {
    }

    public static void playSound(ClientLevel world, Vec3 pos, Identifier id) {
        SoundEvent sound = null;
        if (BuiltInRegistries.SOUND_EVENT.containsKey(id)) {
            sound = BuiltInRegistries.SOUND_EVENT.getValue(id);
        } else if (BuiltInRegistries.SOUND_EVENT.containsKey(FALLBACK_SOUND_ID)) {
            sound = BuiltInRegistries.SOUND_EVENT.getValue(FALLBACK_SOUND_ID);
        }
        if (sound == null) {
            return;
        }
        world.playLocalSound(pos.x, pos.y, pos.z, sound, SoundSource.PLAYERS, 0.8f, 1.0f, false);
    }
}
