package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

@Mixin(HandledScreen.class)
public interface AccessorFocusedSlot {

	@Accessor("focusedSlot")
	Slot nec$getFocusedSlot();
	
}
