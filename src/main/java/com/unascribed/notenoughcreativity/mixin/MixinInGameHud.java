package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;
import com.unascribed.notenoughcreativity.LoaderHandler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Mixin(InGameHud.class)
public class MixinInGameHud {

	@Unique
	private boolean nec$needRestoreHealth = false;
	@Unique
	private float nec$oldHealth;
	
	@Inject(at=@At("HEAD"), method="renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V")
	public void renderStatusBarsPre(MatrixStack matrices, CallbackInfo ci) {
		GlStateManager.pushMatrix();
		if (LoaderHandler.isNotForge() && AbilityCheck.enabled(MinecraftClient.getInstance().player, Ability.HEALTH)) {
			GlStateManager.translatef(0, 6, 0);
			if (MinecraftClient.getInstance().player.getHealth() < 0.2) {
				nec$needRestoreHealth = true;
				nec$oldHealth = MinecraftClient.getInstance().player.getHealth();
				MinecraftClient.getInstance().player.setHealth(0);
			}
		}
	}
	
	@Inject(at=@At("TAIL"), method="renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V")
	public void renderStatusBarsPost(MatrixStack matrices, CallbackInfo ci) {
		if (nec$needRestoreHealth) {
			nec$needRestoreHealth = false;
			MinecraftClient.getInstance().player.setHealth(nec$oldHealth);
		}
		GlStateManager.popMatrix();
	}
	
	@Inject(at=@At("HEAD"), method="getHeartCount(Lnet/minecraft/entity/LivingEntity;)I", cancellable=true)
	public void getHeartCount(LivingEntity entity, CallbackInfoReturnable<Integer> ci) {
		if (LoaderHandler.isNotForge() && entity == null && AbilityCheck.enabled(MinecraftClient.getInstance().player, Ability.HEALTH)) {
			// prevents food rendering
			ci.setReturnValue(-1);
		}
	}
	
}
