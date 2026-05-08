package com.niuhi.config;

import com.niuhi.ElytraRevampedReReWritten;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {

        return parent -> {
            if (ElytraRevampedReReWritten.YACL_LOADED) {
                return YACL.getConfigScreen(parent);
            }

            return new Screen(Component.literal("Config Unavailable")) {
                @Override
                protected void init() {
                    this.addRenderableWidget(Button.builder(
                                    Component.literal("YACL Not Installed, Use JSON file"),
                                    button -> {
                                        this.onClose();
                                    }
                            )
                            .pos(this.width / 2 - 100, this.height / 2)
                            .size(200, 20)
                            .build());
                }
            };
        };
    }
}