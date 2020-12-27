package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.screen.PlayerScreenHandler;

@Mixin(PlayerScreenHandler.class)
public interface AccessorPlayerScreenHandler {

	@Accessor("craftingInput")
	CraftingInventory nec$getCraftingInput();
	
	@Accessor("craftingResult")
	CraftingResultInventory nec$getCraftingResult();
	
}
