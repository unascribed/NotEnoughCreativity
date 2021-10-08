package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;
import com.unascribed.notenoughcreativity.network.MessageOtherNoclipping;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(Entity.class)
public class MixinEntity {

	@Shadow
	public boolean noClip;
	
	@Inject(at=@At("HEAD"), method="wouldPoseNotCollide(Lnet/minecraft/entity/EntityPose;)Z", cancellable=true)
	public void wouldPoseNotCollide(EntityPose pose, CallbackInfoReturnable<Boolean> ci) {
		if (noClip) ci.setReturnValue(true);
	}
	
	@Inject(at=@At("HEAD"), method="onStartedTrackingBy(Lnet/minecraft/server/network/ServerPlayerEntity;)V")
	public void onStartedTrackingBy(ServerPlayerEntity tracker, CallbackInfo ci) {
		Entity self = (Entity)(Object)this;
		if (self instanceof PlayerEntity) {
			new MessageOtherNoclipping(self.getId(), AbilityCheck.enabled((PlayerEntity)self, Ability.NOCLIP)).sendTo(tracker);
		}
	}
	
}
