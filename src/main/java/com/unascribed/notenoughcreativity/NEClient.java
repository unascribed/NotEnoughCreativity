package com.unascribed.notenoughcreativity;

import java.util.Locale;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import com.unascribed.notenoughcreativity.client.CreativePlusScreen;
import com.unascribed.notenoughcreativity.network.MessageSetAbility;

import com.google.common.collect.Maps;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;

public class NEClient {

	public static final NEClient INSTANCE = new NEClient();
	
	public KeyBinding keyDeleteItem;
	public Map<Ability, KeyBinding> abilityKeys;
	public KeyBinding keySavePosition;
	public KeyBinding keyRecallPosition;
	public KeyBinding keyUp;
	public KeyBinding keyDown;
	public KeyBinding keyHoldNoClip;
	public KeyBinding keyHoldSuperSpeed;
	public KeyBinding keyWarp;

	public void setup() {
		keyDeleteItem = new KeyBinding("inventory.binSlot", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DELETE, "Not Enough Creativity");
		LoaderHandler.registerKeyBind(keyDeleteItem);
		abilityKeys = Maps.newEnumMap(Ability.class);
		for (Ability a : Ability.VALUES) {
			KeyBinding kb = new KeyBinding("key.notenoughcreativity.toggle_ability."+a.name().toLowerCase(Locale.ROOT), InputUtil.UNKNOWN_KEY.getCode(), "Not Enough Creativity");
			abilityKeys.put(a, kb);
			LoaderHandler.registerKeyBind(kb);
		}
		keySavePosition = new KeyBinding("key.notenoughcreativity.save_position", InputUtil.UNKNOWN_KEY.getCode(), "Not Enough Creativity");
		LoaderHandler.registerKeyBind(keySavePosition);
		keyRecallPosition = new KeyBinding("key.notenoughcreativity.recall_position", InputUtil.UNKNOWN_KEY.getCode(), "Not Enough Creativity");
		LoaderHandler.registerKeyBind(keyRecallPosition);
		keyUp = new KeyBinding("key.notenoughcreativity.up", InputUtil.UNKNOWN_KEY.getCode(), "Not Enough Creativity Free Flight");
		LoaderHandler.registerKeyBind(keyUp);
		keyDown = new KeyBinding("key.notenoughcreativity.down", InputUtil.UNKNOWN_KEY.getCode(), "Not Enough Creativity Free Flight");
		LoaderHandler.registerKeyBind(keyDown);
		keyHoldNoClip = new KeyBinding("key.notenoughcreativity.hold.noclip", InputUtil.UNKNOWN_KEY.getCode(), "Not Enough Creativity") {
			@Override
			public void setPressed(boolean pressed) {
				if (pressed != isPressed()) {
					new MessageSetAbility(Ability.NOCLIP, pressed).sendToServer();
					CreativePlusScreen.playAbilityToggleSound(Ability.NOCLIP, pressed);
					MinecraftClient.getInstance().player.sendMessage(new TranslatableText("msg.notenoughcreativity.ability_toggle."+pressed,
							new TranslatableText("notenoughcreativity.ability.noclip.name")), true);
				}
				super.setPressed(pressed);
			}
		};
		LoaderHandler.registerKeyBind(keyHoldNoClip);
		keyHoldSuperSpeed = new KeyBinding("key.notenoughcreativity.hold.super_speed", InputUtil.UNKNOWN_KEY.getCode(), "Not Enough Creativity") {
			@Override
			public void setPressed(boolean pressed) {
				if (pressed != isPressed()) {
					new MessageSetAbility(Ability.SUPER_SPEED, pressed).sendToServer();
					CreativePlusScreen.playAbilityToggleSound(Ability.SUPER_SPEED, pressed);
					MinecraftClient.getInstance().player.sendMessage(new TranslatableText("msg.notenoughcreativity.ability_toggle."+pressed,
							new TranslatableText("notenoughcreativity.ability.super_speed.name")), true);
				}
				super.setPressed(pressed);
			}
		};
		LoaderHandler.registerKeyBind(keyHoldSuperSpeed);
		keyWarp = new KeyBinding("key.notenoughcreativity.warp", InputUtil.UNKNOWN_KEY.getCode(), "Not Enough Creativity");
		LoaderHandler.registerKeyBind(keyWarp);
	}
	
}
