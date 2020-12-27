package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.unascribed.notenoughcreativity.NECPlayer;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {

	@Shadow
	public ServerPlayerEntity player;
	
	@ModifyConstant(method="processBlockBreakingAction(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;Lnet/minecraft/util/math/Direction;I)V",
			constant=@Constant(doubleValue=36), require=0)
	public double modifyBlockBreakingDistance(double orig) {
		if (((NECPlayer)player).nec$isVanillaReachExtensionEnabled()) {
			return 14*14;
		}
		return orig;
	}
	
}
