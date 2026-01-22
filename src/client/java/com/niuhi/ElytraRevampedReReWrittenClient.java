package com.niuhi;

import com.niuhi.client.VisualEventHandler;
import com.niuhi.client.particle.ColoredCampfireSmokeParticleFactory;
import com.niuhi.network.ModNetworking;
import com.niuhi.network.VisualEventPayload;
import com.niuhi.network.VisualEventType;
import com.niuhi.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ElytraRevampedReReWrittenClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModNetworking.registerPayloads();
		ClientPlayNetworking.registerGlobalReceiver(VisualEventPayload.ID, (payload, context) -> {
			int typeId = payload.typeId();
			VisualEventType type = VisualEventType.fromId(typeId);
			Integer color = payload.hasColor() ? payload.color() : null;
			context.client().execute(() ->
					VisualEventHandler.handleEvent(type, new net.minecraft.util.math.Vec3d(
							payload.x(), payload.y(), payload.z()), color));
		});

		ParticleFactoryRegistry.getInstance().register(ModParticles.COLORED_CAMPFIRE_SMOKE,
				ColoredCampfireSmokeParticleFactory::new);
	}
}
