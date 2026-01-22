package com.niuhi.client;

import com.niuhi.network.VisualEventType;
import com.niuhi.visuals.AirDragParticles;
import com.niuhi.visuals.BoostParticles;
import com.niuhi.visuals.DragParticles;
import com.niuhi.visuals.PullParticles;
import com.niuhi.visuals.RocketFlair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

public final class VisualEventHandler {
    private VisualEventHandler() {
    }

    public static void handleEvent(VisualEventType type, Vec3d position, int[] colors) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) {
            return;
        }

        switch (type) {
            case BOOST -> BoostParticles.play(world, position);
            case PULL -> PullParticles.play(world, position);
            case DRAG -> DragParticles.play(world, position);
            case ROCKET_FLAIR -> RocketFlair.play(world, position, colors);
            case AIR_DRAG -> AirDragParticles.play(world, position);
        }
    }
}
