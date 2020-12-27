package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;

@Mixin(Camera.class)
public class MixinCamera {

	@Shadow
	private Entity focusedEntity;
	
	@Inject(at=@At("HEAD"), method="clipToSpace(D)D", cancellable=true)
	public void clipToSpace(double desired, CallbackInfoReturnable<Double> ci) {
		if (focusedEntity != null && focusedEntity.noClip) {
			ci.setReturnValue(desired);
		}
	}
	
}
