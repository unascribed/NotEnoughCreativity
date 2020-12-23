package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;

@Mixin(Entity.class)
public class MixinEntity {

	@Shadow
	public boolean noClip;
	
	@Inject(at=@At("HEAD"), method="isPoseClear(Lnet/minecraft/entity/Pose;)Z", cancellable=true)
	public void isPoseClear(Pose pose, CallbackInfoReturnable<Boolean> ci) {
		if (noClip) ci.setReturnValue(true);
	}
	
}
