package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;
import com.unascribed.notenoughcreativity.client.Stipple;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

	@Inject(at=@At("HEAD"), method="renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V")
	public void renderHandPre(float tickDelta, MatrixStack matrices, Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
		if (AbilityCheck.enabled(player, Ability.NOCLIP)) {
			Stipple.grey30();
			Stipple.enable();
		}
	}
	
	@Inject(at=@At("TAIL"), method="renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V")
	public void renderHandPost(float tickDelta, MatrixStack matrices, Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
		Stipple.disable();
	}
	
}
