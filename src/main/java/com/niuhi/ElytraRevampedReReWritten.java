package com.niuhi;

import com.niuhi.config.ModConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElytraRevampedReReWritten implements ModInitializer {
	public static final String MOD_ID = "elytra-revamped-rerewritten";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final boolean ACCESSSORIES_LOADED = FabricLoader.getInstance().isModLoaded("accessories");
	public static final boolean YACL_LOADED = FabricLoader.getInstance().isModLoaded("yet_another_config_lib");

	@Override
	public void onInitialize() {
		LOGGER.info("Flying into the world! But rewritten.");

		if (ACCESSSORIES_LOADED){
			LOGGER.info("Accessories mod detected! Now checking for capes...");
		}
		if (YACL_LOADED){
			LOGGER.info("YACL detected! Enabling In-Game configurations...");
		}

		ModConfig config = ModConfig.getInstance();
		if (config.debugMode.enableDebug) {
			LOGGER.info("Debug mode is ENABLED. Detailed logging on the way!");
			LOGGER.info("Debug Settings: BoostDebug={}, PullDebug={}, DragDebug={}, RocketDebug={}, ElytraDebug={}, VisualDebug={}",
					config.debugMode.boostDebug,
					config.debugMode.pullDebug,
					config.debugMode.dragDebug,
					config.debugMode.rocketDebug,
					config.debugMode.elytraDebug,
					config.debugMode.visualDebug
			);
		} else {
			LOGGER.info("Debug mode is DISABLED. Basic logging on the way!");
		}
	}
}