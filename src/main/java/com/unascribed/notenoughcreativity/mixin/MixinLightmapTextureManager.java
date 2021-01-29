package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.LightmapTextureManager;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

	@Redirect(at=@At(value="FIELD", target="net/minecraft/client/options/GameOptions.gamma:D"),
			method="update(F)V")
	public double modifyGamma(GameOptions subject) {
		return AbilityCheck.enabled(MinecraftClient.getInstance().player, Ability.NIGHTVISION) ? 200 : subject.gamma;
	}
	
}
