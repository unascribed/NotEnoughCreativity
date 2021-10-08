package com.unascribed.notenoughcreativity;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;

public class LoaderHandler {

	public static boolean isFabricAPIAvailable() {
		return FabricLoader.getInstance().isModLoaded("fabric");
	}
	
	public static boolean isNotForge() {
		return true;
	}

	public static void registerKeyBind(KeyBinding key) {
		if (isFabricAPIAvailable()) {
			KeyBindingHelper.registerKeyBinding(key);
		}
	}

}
