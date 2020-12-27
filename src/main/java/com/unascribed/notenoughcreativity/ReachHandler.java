package com.unascribed.notenoughcreativity;

import net.minecraft.entity.player.PlayerEntity;

// Fabric version. See Forge version in the "forgery" subdir that uses Forge's REACH_DISTANCE attribute.
public class ReachHandler {

	public static void tick(PlayerEntity player) {
		((NECPlayer)player).nec$setVanillaReachExtensionEnabled(AbilityCheck.enabled(player, Ability.LONGREACH));
	}

	

}
