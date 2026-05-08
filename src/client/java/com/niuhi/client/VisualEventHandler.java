package com.niuhi.client;

import com.niuhi.network.VisualEventType;
import com.niuhi.visuals.BoostParticles;
import com.niuhi.visuals.DragParticles;
import com.niuhi.visuals.PullParticles;
import com.niuhi.visuals.RocketFlair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

public final class VisualEventHandler {
    private VisualEventHandler() {
    }

    public static void handleEvent(VisualEventType type, Vec3 position, int[] colors) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        switch (type) {
            case BOOST -> BoostParticles.play(world, position);
            case PULL -> PullParticles.play(world, position);
            case DRAG -> DragParticles.play(world, position);
            case ROCKET_FLAIR -> RocketFlair.play(world, position, colors);
        }
    }
}
