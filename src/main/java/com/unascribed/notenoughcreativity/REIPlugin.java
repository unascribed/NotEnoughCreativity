package com.unascribed.notenoughcreativity;

import java.util.Collections;

import com.unascribed.notenoughcreativity.client.CreativePlusScreen;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;

public class REIPlugin implements REIClientPlugin {

	@Override
	public void registerExclusionZones(ExclusionZones zones) {
		zones.register(CreativePlusScreen.class, (_screen) -> {
			CreativePlusScreen screen = _screen;
			return Collections.singletonList(new Rectangle(screen.getX()-screen.getBackgroundWidthAddn(), screen.getY(), screen.getBackgroundWidth()+screen.getBackgroundWidthAddn(), screen.getBackgroundHeight()));
		});
	}
	
	@Override
	public String getPluginProviderName() {
		return "NotEnoughCreativity";
	}
	
}
