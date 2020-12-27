package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;
import com.unascribed.notenoughcreativity.NECPlayer;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {

	public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}
	
	@Inject(at=@At("HEAD"), method="onSpawn()V")
	public void onSpawn(CallbackInfo ci) {
		NotEnoughCreativity.updateInventory(this);
	}
	
	@Inject(at=@At("HEAD"), method="onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", cancellable=true)
	public void onDeath(DamageSource src, CallbackInfo ci) {
		PlayerEntity self = (PlayerEntity)this;
		if (AbilityCheck.enabled(self, Ability.HEALTH) && !src.isOutOfWorld()) {
			System.out.println("no death allowed");
			self.setHealth(0.1f);
			ci.cancel();
		}
	}
	
	@Inject(at=@At("HEAD"), method="copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V", cancellable=true)
	public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		if (this instanceof NECPlayer) {
			if (oldPlayer instanceof NECPlayer) {
				NECPlayer us = (NECPlayer)this;
				NECPlayer them = (NECPlayer)oldPlayer;
				us.nec$setCreativePlusEnabled(them.nec$isCreativePlusEnabled());
				us.nec$getEnabledAbilities().addAll(them.nec$getEnabledAbilities());
				for (int i = 0; i < us.nec$getCreativePlusInventory().size(); i++) {
					us.nec$getCreativePlusInventory().setStack(i, them.nec$getCreativePlusInventory().getStack(i));
				}
				NotEnoughCreativity.updateInventory(this);
			}
		}
	}
	
	@Inject(at=@At("TAIL"), method="worldChanged(Lnet/minecraft/server/world/ServerWorld;)V")
	public void worldChanged(ServerWorld world, CallbackInfo ci) {
		NotEnoughCreativity.updateInventory(this);
	}
	
}