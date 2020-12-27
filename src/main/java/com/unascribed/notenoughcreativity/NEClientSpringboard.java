package com.unascribed.notenoughcreativity;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class NEClientSpringboard {

	public static void setup() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			NEClient.INSTANCE.setup();
		}
	}
	
}
