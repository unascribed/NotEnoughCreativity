package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

	@Inject(at=@At("HEAD"), method="drop(Lnet/minecraft/entity/damage/DamageSource;)V", cancellable=true)
	public void drop(DamageSource src, CallbackInfo ci) {
		LivingEntity self = (LivingEntity)(Object)this;
		if (self instanceof PlayerEntity) return;
		Entity attacker = self.getAttacker();
		if (attacker instanceof PlayerEntity) {
			PlayerEntity p = (PlayerEntity)attacker;
			if (AbilityCheck.enabled(p, Ability.ATTACK)) {
				ci.cancel();
			}
		}
	}
	
}
