package com.unascribed.notenoughcreativity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class NEClientSpringboard {

	public static void setup() {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			try {
				Class<?> clazz = Class.forName("com.unascribed.notenoughcreativity.NEClient");
				clazz.getMethod("setup").invoke(clazz.getField("INSTANCE").get(null));
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
			NEClientForge.setup();
		}
	}
	
}
