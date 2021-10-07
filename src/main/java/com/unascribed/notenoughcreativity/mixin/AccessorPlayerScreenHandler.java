package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

@Mixin(PlayerScreenHandler.class)
public interface AccessorPlayerScreenHandler {

	@Accessor("craftingInput")
	CraftingInventory nec$getCraftingInput();
	@Accessor("craftingInput")
	void nec$setCraftingInput(CraftingInventory ci);
	
	@Accessor("craftingResult")
	CraftingResultInventory nec$getCraftingResult();
	
	@Accessor("EMPTY_ARMOR_SLOT_TEXTURES")
	static Identifier[] nec$getEmptyArmorSlotTextures() { throw new AbstractMethodError(); }
	
}
