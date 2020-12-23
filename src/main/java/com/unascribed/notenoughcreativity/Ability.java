package com.unascribed.notenoughcreativity;

import net.minecraft.entity.player.EntityPlayer;

public enum Ability {
	NOPICKUP,
	ATTACK,
	HEALTH,
	INSTABREAK,
	PICKSWAP,
	NIGHTVISION,
	NOCLIP,
	DARKMODE
	;
	
	public boolean isEnabled(EntityPlayer player) {
		if (player == null) return false;
		return NotEnoughCreativity.isCreativePlus(player) && (player.getEntityData().getInteger("NotEnoughCreativityAbilities") & (1 << ordinal())) != 0;
	}
}
