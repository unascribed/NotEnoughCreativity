package com.unascribed.notenoughcreativity;

import java.util.Collections;

import com.unascribed.notenoughcreativity.client.CreativePlusScreen;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class REIPlugin implements REIPluginV0 {

	@Override
	public void registerBounds(DisplayHelper displayHelper) {
		BaseBoundsHandler.getInstance().registerExclusionZones(CreativePlusScreen.class, () -> {
			CreativePlusScreen screen = (CreativePlusScreen)MinecraftClient.getInstance().currentScreen;
			return Collections.singletonList(new Rectangle(screen.getX()-screen.getBackgroundWidthAddn(), screen.getY(), screen.getBackgroundWidth()+screen.getBackgroundWidthAddn(), screen.getBackgroundHeight()));
		});
	}
	
	@Override
	public Identifier getPluginIdentifier() {
		return new Identifier("notenoughcreativity", "main");
	}

}
