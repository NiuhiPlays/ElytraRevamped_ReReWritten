package com.niuhi.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class ServerCommands {
    private ServerCommands() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("errrw")
                .then(CommandManager.literal("debug")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("status")
                                .executes(context -> {
                                    ModConfig config = ModConfig.getInstance();
                                    if (!config.debugMode.enableDebugCommands) {
                                        context.getSource().sendFeedback(() -> Text.literal("Debug commands are disabled."), false);
                                        return 0;
                                    }
                                    context.getSource().sendFeedback(() -> Text.literal(getDebugStatus(config)), false);
                                    return 1;
                                }))
                        .then(CommandManager.argument("section", StringArgumentType.word())
                                .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                        .executes(context -> {
                                            ModConfig config = ModConfig.getInstance();
                                            if (!config.debugMode.enableDebugCommands) {
                                                context.getSource().sendFeedback(() -> Text.literal("Debug commands are disabled."), false);
                                                return 0;
                                            }
                                            String section = StringArgumentType.getString(context, "section");
                                            boolean enabled = BoolArgumentType.getBool(context, "enabled");
                                            if (!setDebugSection(config, section, enabled)) {
                                                context.getSource().sendFeedback(() -> Text.literal("Unknown debug section: " + section), false);
                                                return 0;
                                            }
                                            ModConfig.save();
                                            context.getSource().sendFeedback(() -> Text.literal("Debug " + section + " set to " + enabled), false);
                                            return 1;
                                        })))));
    }

    private static boolean setDebugSection(ModConfig config, String section, boolean enabled) {
        return switch (section.toLowerCase()) {
            case "master" -> {
                config.debugMode.enableDebug = enabled;
                yield true;
            }
            case "boost" -> {
                config.debugMode.boostDebug = enabled;
                yield true;
            }
            case "pull" -> {
                config.debugMode.pullDebug = enabled;
                yield true;
            }
            case "drag" -> {
                config.debugMode.dragDebug = enabled;
                yield true;
            }
            case "rocket" -> {
                config.debugMode.rocketDebug = enabled;
                yield true;
            }
            case "elytra" -> {
                config.debugMode.elytraDebug = enabled;
                yield true;
            }
            case "visuals" -> {
                config.debugMode.visualDebug = enabled;
                yield true;
            }
            case "all" -> {
                config.debugMode.enableDebug = enabled;
                config.debugMode.boostDebug = enabled;
                config.debugMode.pullDebug = enabled;
                config.debugMode.dragDebug = enabled;
                config.debugMode.rocketDebug = enabled;
                config.debugMode.elytraDebug = enabled;
                config.debugMode.visualDebug = enabled;
                yield true;
            }
            default -> false;
        };
    }

    private static String getDebugStatus(ModConfig config) {
        return "Debug: master=" + config.debugMode.enableDebug
                + " boost=" + config.debugMode.boostDebug
                + " pull=" + config.debugMode.pullDebug
                + " drag=" + config.debugMode.dragDebug
                + " rocket=" + config.debugMode.rocketDebug
                + " elytra=" + config.debugMode.elytraDebug
                + " visuals=" + config.debugMode.visualDebug;
    }
}
