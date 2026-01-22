package com.niuhi.visuals;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public final class VisualSoundUtil {
    private static final Identifier FALLBACK_SOUND_ID = Identifier.of("minecraft", "block.campfire.crackle");

    private VisualSoundUtil() {
    }

    public static void playSound(ClientWorld world, Vec3d pos, Identifier id) {
        SoundEvent sound = null;
        if (Registries.SOUND_EVENT.containsId(id)) {
            sound = Registries.SOUND_EVENT.get(id);
        } else if (Registries.SOUND_EVENT.containsId(FALLBACK_SOUND_ID)) {
            sound = Registries.SOUND_EVENT.get(FALLBACK_SOUND_ID);
        }
        if (sound == null) {
            return;
        }
        world.playSound(pos.x, pos.y, pos.z, sound, SoundCategory.PLAYERS, 0.8f, 1.0f, false);
    }
}
