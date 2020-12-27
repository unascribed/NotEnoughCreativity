package com.unascribed.notenoughcreativity;

import net.minecraft.entity.player.PlayerEntity;

// this is real funky because it needs to be valid with MCP and Yarn mappings and cannot directly
// reference classes other than Ability. by some miracle, PlayerEntity is mapped identically in both
// MCP and Yarn

// some people solve this issue by using Mojmap as a "common ground". i refuse to do that
public interface AbilityCheck {

	boolean isAbilityEnabled(PlayerEntity player, Ability a);
	
	AbilityCheck INSTANCE = createInstance();
	
	static AbilityCheck createInstance() {
		try {
			return (AbilityCheck)Class.forName("com.unascribed.notenoughcreativity.AbilityCheckImpl").getConstructor().newInstance();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	static boolean enabled(PlayerEntity player, Ability a) {
		return INSTANCE.isAbilityEnabled(player, a);
	}
	
}
