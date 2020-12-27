package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.client.Stipple;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

	@Inject(at=@At("HEAD"), method="renderHand(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/ActiveRenderInfo;F)V")
	public void renderHandPre(MatrixStack matrices, ActiveRenderInfo ari, float partialTicks, CallbackInfo ci) {
		if (Ability.NOCLIP.isEnabled(Minecraft.getInstance().player)) {
			Stipple.grey30();
			Stipple.enable();
		}
	}
	
	@Inject(at=@At("TAIL"), method="renderHand(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/ActiveRenderInfo;F)V")
	public void renderHandPost(MatrixStack matrices, ActiveRenderInfo ari, float partialTicks, CallbackInfo ci) {
		Stipple.disable();
	}
	
}
