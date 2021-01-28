package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;

import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

	@ModifyVariable(at=@At(value="FIELD", target="net/minecraft/client/options/GameOptions.gamma:D", shift=Shift.BY, by=3),
			method="update(F)V", ordinal=5)
	public float modifyGamma(float orig) {
		return AbilityCheck.enabled(MinecraftClient.getInstance().player, Ability.NIGHTVISION) ? 200 : orig;
	}
	
}
