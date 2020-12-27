package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;

@Mixin(ActiveRenderInfo.class)
public class MixinActiveRenderInfo {

	@Shadow
	private Entity renderViewEntity;
	
	@Inject(at=@At("HEAD"), method="calcCameraDistance(D)D", cancellable=true)
	public void calcCameraDistance(double startingDistance, CallbackInfoReturnable<Double> ci) {
		if (renderViewEntity != null && renderViewEntity.noClip) {
			ci.setReturnValue(startingDistance);
		}
	}
	
}
