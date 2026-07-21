package com.niuhi;

import com.niuhi.client.VisualEventHandler;
import com.niuhi.network.ModNetworking;
import com.niuhi.network.VisualEventPayload;
import com.niuhi.network.VisualEventType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ElytraRevampedReReWrittenClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModNetworking.registerPayloads();
		ClientPlayNetworking.registerGlobalReceiver(VisualEventPayload.ID, (payload, context) -> {
			int typeId = payload.typeId();
			VisualEventType type = VisualEventType.fromId(typeId);
			int[] colors = payload.hasColor() ? payload.colors() : null;
			context.client().execute(() ->
					VisualEventHandler.handleEvent(type, new net.minecraft.util.math.Vec3d(
							payload.x(), payload.y(), payload.z()), colors));
		});
	}
}
