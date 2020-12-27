package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;
import com.unascribed.notenoughcreativity.LoaderHandler;
import com.unascribed.notenoughcreativity.NECPlayer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

	@Inject(at=@At("RETURN"), method="getReachDistance()F", cancellable=true)
	public void getReachDistance(CallbackInfoReturnable<Float> ci) {
		if (((NECPlayer)MinecraftClient.getInstance().player).nec$isVanillaReachExtensionEnabled()) {
			ci.setReturnValue(ci.getReturnValue()+8);
		}
	}
	
	@Inject(at=@At("HEAD"), method="hasStatusBars()Z", cancellable=true)
	public void hasStatusBars(CallbackInfoReturnable<Boolean> ci) {
		if (LoaderHandler.isNotForge() && AbilityCheck.enabled(MinecraftClient.getInstance().player, Ability.HEALTH)) {
			ci.setReturnValue(true);
		}
	}
	
}
