package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.screen.slot.Slot;

@Mixin(Slot.class)
public interface AccessorSlot {

	@Accessor("index")
	int nec$getIndex();
	
}
