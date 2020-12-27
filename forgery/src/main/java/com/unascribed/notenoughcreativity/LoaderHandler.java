package com.unascribed.notenoughcreativity;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class LoaderHandler {

	public static boolean isFabricAPIAvailable() {
		return false;
	}
	
	public static boolean isNotForge() {
		return false;
	}
	
	public static void registerKeyBind(KeyBinding key) {
		ClientRegistry.registerKeyBinding(key);
	}

}
