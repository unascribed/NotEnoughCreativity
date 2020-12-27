package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.notenoughcreativity.NECPlayer;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;

import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
	
	@Shadow
	public ServerPlayerEntity player;
	
	@Inject(at=@At("HEAD"), method="onCustomPayload(Lnet/minecraft/network/packet/c2s/play/CustomPayloadC2SPacket;)V", cancellable=true)
	public void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		for (NetworkContext ctx : NetworkContext.contexts) {
			if (ctx.handleCustomPacket((ServerPlayNetworkHandler)(Object)this, packet)) {
				ci.cancel();
				return;
			}
		}
	}
	
	// no require as Forge also patches these, so they may be missing
	
	@ModifyConstant(method="onPlayerInteractBlock(Lnet/minecraft/network/packet/c2s/play/PlayerInteractBlockC2SPacket;)V",
			constant=@Constant(doubleValue=64), require=0)
	public double modifyOnPlayerInteractBlockDistance(double orig) {
		if (((NECPlayer)player).nec$isVanillaReachExtensionEnabled()) {
			return 16*16;
		}
		return orig;
	}
	
	@ModifyConstant(method="onPlayerInteractEntity(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;)V",
			constant=@Constant(doubleValue=36), require=0)
	public double modifyOnPlayerInteractEntityDistance(double orig) {
		if (((NECPlayer)player).nec$isVanillaReachExtensionEnabled()) {
			return 14*14;
		}
		return orig;
	}
	
}
