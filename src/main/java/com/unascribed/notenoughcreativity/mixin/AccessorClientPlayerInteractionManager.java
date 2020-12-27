package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.network.ClientPlayerInteractionManager;

@Mixin(ClientPlayerInteractionManager.class)
public interface AccessorClientPlayerInteractionManager {

	@Accessor("blockBreakingCooldown")
	void nec$setBlockBreakingCooldown(int i);
	
}
