package com.niuhi.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DebugLogger {

    private final String modId;
    private final ModConfig config; // ← Replace with your actual config class reference
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DebugLogger(String modId, ModConfig config) {
        this.modId = modId;
        this.config = config;
    }


    public void log(String section, String message) {
        // Master debug toggle
        if (!config.debugMode.enableDebug) return;

        // Section-specific toggle
        if (!isSectionEnabled(section)) return;

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String output = String.format("[%s] (%s) %s: %s",
                modId.toUpperCase(),
                timestamp,
                section.toUpperCase(),
                message
        );

        System.out.println(output);
    }

    /**
     * Helper that checks if a section is enabled in your config.
     * Adjust this to match how your config stores section toggles.
     */
    private boolean isSectionEnabled(String section) {
        return switch (section.toUpperCase()) {
            case "BOOST" -> config.debugMode.boostDebug;
            case "PULL" -> config.debugMode.pullDebug;
            case "DRAG" -> config.debugMode.dragDebug;
            case "ROCKET" -> config.debugMode.rocketDebug;
            case "ELYTRA" -> config.debugMode.elytraDebug;
            case "VISUALS" -> config.debugMode.visualDebug;
            default -> false;
        };
    }
}