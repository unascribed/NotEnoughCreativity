package com.unascribed.notenoughcreativity;

import java.util.Set;

import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public interface NECPlayer {

	Set<Ability> nec$getEnabledAbilities();
	
	boolean nec$isCreativePlusEnabled();
	void nec$setCreativePlusEnabled(boolean enabled);
	
	boolean nec$isNoclipping();
	void nec$setNoclipping(boolean noclipping);
	
	Inventory nec$getCreativePlusInventory();
	
	Identifier nec$getSavedDimension();
	void nec$setSavedDimension(Identifier dim);
	Vec3d nec$getSavedPosition();
	void nec$setSavedPosition(Vec3d pos);
	
	// NOT USED under Forge! See ReachHandler
	boolean nec$isVanillaReachExtensionEnabled();
	void nec$setVanillaReachExtensionEnabled(boolean enabled);
	
}
