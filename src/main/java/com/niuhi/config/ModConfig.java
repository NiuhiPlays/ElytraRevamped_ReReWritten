package com.niuhi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Path;

public class ModConfig {
    public BoostConfig boostConfig = new BoostConfig();
    public PullConfig pullConfig = new PullConfig();
    public DragConfig dragConfig = new DragConfig();
    public RocketConfig rocketConfig = new RocketConfig();
    public ElytraConfig elytraConfig = new ElytraConfig();
    public VisualConfig visualConfig = new VisualConfig();
    public SoundConfig soundConfig = new SoundConfig();
    public DebugMode debugMode = new DebugMode();

    // All config options for Boost
    public static class BoostConfig {
        public boolean enableBoost = true;
        public double boostAmount = 0.3;
        public int detectionHeight = 10;

        public boolean enableHayBoost = true;
        public double hayBoostAmount = 0.5;
        public int hayDetectionHeight = 25;

        public boolean enableGridBoost = true;
        public double gridTwoByTwo = 0.25;
        public double gridThreeByThree = 0.50;
        public boolean exponentialBoost = true;

        public int boostCooldownTicks = 2;
    }

    // All config options for Pull
    public static class PullConfig {
        public boolean enablePull = true;
        public double pullAmount = 0.3;
        public int detectionHeight = 10;

        public boolean enableHayPull = true;
        public double hayPullAmount = 0.5;
        public int hayDetectionHeight = 25;

        public boolean enableGridPull = true;
        public double gridTwoByTwo = 0.25;
        public double gridThreeByThree = 0.50;
        public boolean exponentialPull = true;

        public int pullCooldownTicks = 2;
    }

    // All config options for Drag
    public static class DragConfig {
        public boolean enableControllableDrag = true;
        public double dragAmount = 0.92;

        public boolean enableAirDrag = false;
        public double airDragAmount = 0.95;
    }

    // All config options for Rockets
    public static class RocketConfig {
        public boolean DisableRockets = true;
        public boolean rocketFlair = true;

        public boolean initialBoost = false;
        public int gracePeriodTicks = 20;

        public boolean midflightBoost = false;
        public double midflightBoostAmount = 1.0;
    }

    // All config options for the Elytra & Extra's
    public static class ElytraConfig {
        public boolean enableMod = true;
        public boolean enableBounce = true;

        public boolean riptideNerf = true;
        public int riptideCooldown = 600;
    }

    // All config options for Visuals and Sounds
    public static class VisualConfig {
        public boolean BoostParticles = true;
        public boolean PullParticles = true;
        public boolean DragParticles = true;
        public boolean RocketParticles = true;
        public boolean ColoredParticles = true;
        public boolean AirDragParticles = true;
    }

    // All config options for Sounds
    public static class SoundConfig {
        public boolean boostSound = true;
        public boolean pullSound = true;
        public boolean dragSound = true;
        public boolean rocketSound = true;
    }

    // All config options for Debugging
    public static class DebugMode {
        public boolean enableDebug = false;
        public boolean enableDebugCommands = false;

        public boolean boostDebug = false;
        public boolean pullDebug = false;
        public boolean dragDebug = false;
        public boolean rocketDebug = false;
        public boolean elytraDebug = false;
        public boolean visualDebug = false;
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("Elytra_Revamped_RRW.json");
    private static ModConfig INSTANCE;

    public static ModConfig init() {
        if (INSTANCE == null) {
            INSTANCE = loadManual();
        }
        return INSTANCE;
    }

    private static ModConfig loadManual() {
        File configFile = CONFIG_PATH.toFile();
        if (configFile.exists()) {
            try (java.io.FileReader reader = new java.io.FileReader(configFile)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) {
                    return loaded;
                }
            } catch (java.io.IOException e) {
                System.err.println("Error loading ER config: " + e.getMessage());
            }
        }
        ModConfig config = new ModConfig();
        saveManual(config);
        return config;
    }

    private static void saveManual(ModConfig config) {
        try (java.io.FileWriter writer = new java.io.FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(config, writer);
        } catch (java.io.IOException e) {
            System.err.println("Error saving ER config: " + e.getMessage());
        }
    }

    public static void save() {
        if (INSTANCE != null) {
            saveManual(INSTANCE);
        }
    }

    public static ModConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = loadManual(); // Ensure instance is always available
        }
        return INSTANCE;
    }
}
