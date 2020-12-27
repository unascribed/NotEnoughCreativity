package com.unascribed.notenoughcreativity;

import java.util.Locale;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import com.google.common.collect.Maps;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class NEClient {

	public static final NEClient INSTANCE = new NEClient();
	
	public KeyBinding keyDeleteItem;
	public Map<Ability, KeyBinding> abilityKeys;

	public void setup() {
		keyDeleteItem = new KeyBinding("inventory.binSlot", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DELETE, "Not Enough Creativity");
		LoaderHandler.registerKeyBind(keyDeleteItem);
		abilityKeys = Maps.newEnumMap(Ability.class);
		for (Ability a : Ability.VALUES) {
			KeyBinding kb = new KeyBinding("key.notenoughcreativity.toggle_ability."+a.name().toLowerCase(Locale.ROOT), InputUtil.UNKNOWN_KEY.getCode(), "Not Enough Creativity");
			abilityKeys.put(a, kb);
			LoaderHandler.registerKeyBind(kb);
		}
	}
	
}
