package com.niuhi.config;

import com.niuhi.ElytraRevampedReReWritten;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.gui.screen.Screen;

public class YACL {

    public static Screen getConfigScreen(Screen parent) {
    if (!ElytraRevampedReReWritten.YACL_LOADED) {
        ElytraRevampedReReWritten.LOGGER.info("YACL not loaded");
        return null;
    }

    YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder();



    Screen screen = builder.build().generateScreen(parent);
    return screen;
    }
}
