package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.damage.DamageSource;

@Mixin(DamageSource.class)
public interface InvokerDamageSource {

	@Invoker("setBypassesArmor")
	DamageSource nec$setBypassesArmor();
	@Invoker("setUnblockable")
	DamageSource nec$setUnblockable();
	
}
