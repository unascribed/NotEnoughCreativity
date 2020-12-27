package com.unascribed.notenoughcreativity.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
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
import com.unascribed.notenoughcreativity.network.MessagePickBlock;
import com.unascribed.notenoughcreativity.network.MessagePickEntity;
import com.unascribed.notenoughcreativity.network.MessageSetAbility;

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
	
	@Unique
	private boolean nec$needRestoreGamma;
	@Unique
	private double nec$oldGamma;
	
	@Inject(at=@At("HEAD"), method="render(Z)V", cancellable=true)
	public void renderPre(boolean tick, CallbackInfo ci) {
		if (AbilityCheck.enabled(player, Ability.NIGHTVISION)) {
			nec$needRestoreGamma = true;
			nec$oldGamma = options.gamma;
			options.gamma = 200;
		}
	}
	
	@Inject(at=@At("RETURN"), method="render(Z)V", cancellable=true)
	public void renderPost(boolean tick, CallbackInfo ci) {
		if (nec$needRestoreGamma) {
			nec$needRestoreGamma = false;
			options.gamma = nec$oldGamma;
		}
	}
	
	@ModifyVariable(at=@At("HEAD"), method="openScreen(Lnet/minecraft/client/gui/screen/Screen;)V", index=1)
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
			if (cp) {
				new MessageDeleteSlot(82+player.inventory.selectedSlot).sendToServer();
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
		if (AbilityCheck.enabled(player, Ability.INSTABREAK)) {
			((AccessorClientPlayerInteractionManager)interactionManager).nec$setBlockBreakingCooldown(0);
		}
	}
	
	@Inject(at=@At("HEAD"), method="doItemPick()V", cancellable=true)
	public void doItemPick(CallbackInfo ci) {
		if (NotEnoughCreativity.isCreativePlus(player)) {
			HitResult hr = crosshairTarget;
			if (hr != null) {
				if (hr instanceof BlockHitResult) {
					new MessagePickBlock(((BlockHitResult)hr).getBlockPos(), (float)hr.getPos().x, (float)hr.getPos().y, (float)hr.getPos().z, ((BlockHitResult) hr).getSide(), Screen.hasControlDown()).sendToServer();
				} else if (hr instanceof EntityHitResult) {
					new MessagePickEntity(((EntityHitResult)hr).getEntity().getEntityId(), (float)hr.getPos().x, (float)hr.getPos().y, (float)hr.getPos().z, Screen.hasControlDown()).sendToServer();
				}
			}
			ci.cancel();
		}
	}
	
}
