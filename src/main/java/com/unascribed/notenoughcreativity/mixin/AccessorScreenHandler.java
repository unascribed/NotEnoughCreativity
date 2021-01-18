package com.unascribed.notenoughcreativity.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ScreenHandler.class)
public interface AccessorScreenHandler {

	@Accessor("listeners")
	List<ScreenHandlerListener> nec$getListeners();
	
	@Accessor("trackedStacks")
	DefaultedList<ItemStack> nec$getTrackedStacks();
	
}
