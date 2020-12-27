package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(ItemEntity.class)
public class MixinItemEntity {

	@Inject(at=@At("HEAD"), method="onPlayerCollision(Lnet/minecraft/entity/player/PlayerEntity;)V", cancellable=true)
	public void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
		if (AbilityCheck.enabled(player, Ability.NOPICKUP)) {
			ci.cancel();
		}
	}
	
}
