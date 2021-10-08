package com.unascribed.notenoughcreativity.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.hit.HitResult;

import java.util.Locale;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;
import com.unascribed.notenoughcreativity.CreativePlusScreenHandler;
import com.unascribed.notenoughcreativity.NEClient;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.client.CreativePlusScreen;
import com.unascribed.notenoughcreativity.network.MessageDeleteSlot;
import com.unascribed.notenoughcreativity.network.MessageRecallPosition;
import com.unascribed.notenoughcreativity.network.MessageSavePosition;
import com.unascribed.notenoughcreativity.network.MessageSetAbility;
import com.unascribed.notenoughcreativity.network.MessageWarp;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

	@Shadow
	public ClientPlayerEntity player;
	@Shadow
	public HitResult crosshairTarget;
	@Shadow @Final
	public GameOptions options;
	@Shadow
	public ClientPlayerInteractionManager interactionManager;
	@Shadow
	public boolean chunkCullingEnabled;
	
	@Unique
	private boolean nec$wasNoclipping;
	@Unique
	private double nec$oldGamma;
	
	@Inject(at=@At("HEAD"), method="render(Z)V", cancellable=true)
	public void renderPre(boolean tick, CallbackInfo ci) {
		if (AbilityCheck.enabled(player, Ability.NOCLIP)) {
			nec$wasNoclipping = true;
			chunkCullingEnabled = false;
		} else if (nec$wasNoclipping) {
			chunkCullingEnabled = true;
		}
	}
	
	@ModifyVariable(at=@At("HEAD"), method="setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", index=1)
	public Screen modifyOpenScreen(Screen screen) {
		if (screen instanceof CreativeInventoryScreen) {
			if (NotEnoughCreativity.isCreativePlus(player)) {
				return new CreativePlusScreen(new CreativePlusScreenHandler(player));
			}
		}
		return screen;
	}
	
	@Inject(at=@At("TAIL"), method="tick()V")
	public void tick(CallbackInfo ci) {
		boolean cp = NotEnoughCreativity.isCreativePlus(player);
		if (NEClient.INSTANCE.keyDeleteItem.isPressed()) {
			if (cp && !player.getMainHandStack().isEmpty()) {
				new MessageDeleteSlot(player.getInventory().selectedSlot+36).sendToServer();
			}
		}
		for (Map.Entry<Ability, KeyBinding> en : NEClient.INSTANCE.abilityKeys.entrySet()) {
			if (en.getValue().wasPressed()) {
				if (cp) {
					boolean newState = !AbilityCheck.enabled(player, en.getKey());
					new MessageSetAbility(en.getKey(), newState).sendToServer();
					CreativePlusScreen.playAbilityToggleSound(en.getKey(), newState);
					player.sendMessage(new TranslatableText("msg.notenoughcreativity.ability_toggle."+newState,
							new TranslatableText("notenoughcreativity.ability."+en.getKey().name().toLowerCase(Locale.ROOT)+".name")), true);
				}
			}
		}
		if (NEClient.INSTANCE.keyRecallPosition.wasPressed() && cp) {
			new MessageRecallPosition().sendToServer();
		}
		if (NEClient.INSTANCE.keySavePosition.wasPressed() && cp) {
			new MessageSavePosition().sendToServer();
		}
		if (NEClient.INSTANCE.keyWarp.wasPressed() && cp) {
			new MessageWarp().sendToServer();
		}
		if (AbilityCheck.enabled(player, Ability.INSTABREAK)) {
			((AccessorClientPlayerInteractionManager)interactionManager).nec$setBlockBreakingCooldown(0);
		}
	}
	
}
