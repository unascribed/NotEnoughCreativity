package com.unascribed.notenoughcreativity.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;
import com.unascribed.notenoughcreativity.NECPlayer;
import com.unascribed.notenoughcreativity.client.Stipple;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRenderer {

	@Inject(at=@At("HEAD"), method="render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
	public void renderPre(AbstractClientPlayerEntity player, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (vertexConsumers instanceof VertexConsumerProvider.Immediate && ((NECPlayer)player).nec$isNoclipping() || AbilityCheck.enabled(player, Ability.NOCLIP)) {
			Stipple.grey30();
			Stipple.enable();
		}
	}
	
	@Inject(at=@At("TAIL"), method="render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
	public void renderPost(AbstractClientPlayerEntity player, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (vertexConsumers instanceof VertexConsumerProvider.Immediate && ((NECPlayer)player).nec$isNoclipping() || AbilityCheck.enabled(player, Ability.NOCLIP)) {
			VertexConsumerProvider.Immediate imm = (VertexConsumerProvider.Immediate)vertexConsumers;
			if (((NECPlayer)player).nec$isNoclipping() || AbilityCheck.enabled(player, Ability.NOCLIP)) {
				imm.draw();
				Stipple.disable();
			}
		}
	}
	
}
