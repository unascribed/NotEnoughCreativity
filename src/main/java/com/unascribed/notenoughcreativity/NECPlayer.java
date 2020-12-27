package com.unascribed.notenoughcreativity;

import java.util.Set;

import net.minecraft.inventory.Inventory;

public interface NECPlayer {

	Set<Ability> nec$getEnabledAbilities();
	
	boolean nec$isCreativePlusEnabled();
	void nec$setCreativePlusEnabled(boolean enabled);
	
	boolean nec$isNoclipping();
	void nec$setNoclipping(boolean noclipping);
	
	Inventory nec$getCreativePlusInventory();
	
	// NOT USED under Forge! See ReachHandler
	boolean nec$isVanillaReachExtensionEnabled();
	void nec$setVanillaReachExtensionEnabled(boolean enabled);
	
}
