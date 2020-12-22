package com.unascribed.notenoughcreativity;

import net.minecraft.entity.player.PlayerEntity;

public enum Ability {
	NOPICKUP,
	ATTACK,
	HEALTH,
	INSTABREAK,
	PICKSWAP,
	;
	
	public boolean isEnabled(PlayerEntity player) {
		if (player == null) return false;
		return NotEnoughCreativity.isCreativePlus(player) && (player.getPersistentData().getInt("NotEnoughCreativityAbilities") & (1 << ordinal())) != 0;
	}
}
