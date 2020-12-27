package com.unascribed.notenoughcreativity;

import net.minecraft.entity.player.PlayerEntity;

public class AbilityCheckImpl implements AbilityCheck {

	@Override
	public boolean isAbilityEnabled(PlayerEntity player, Ability a) {
		if (player == null) return false;
		return NotEnoughCreativity.isCreativePlus(player) && player instanceof NECPlayer && ((NECPlayer)player).nec$getEnabledAbilities().contains(a);
	}

}
