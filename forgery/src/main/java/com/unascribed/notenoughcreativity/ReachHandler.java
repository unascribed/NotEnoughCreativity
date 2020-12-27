package com.unascribed.notenoughcreativity;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeMod;

public class ReachHandler {

	private static final AttributeModifier REACH_MODIFIER = new AttributeModifier("Not Enough Creativity Long Reach ability", 8, Operation.ADDITION);
	
	public static void tick(PlayerEntity player) {
		ModifiableAttributeInstance reach = player.getAttributeManager().createInstanceIfAbsent(ForgeMod.REACH_DISTANCE.get());
		if (!player.abilities.isCreativeMode) {
			if (reach.hasModifier(REACH_MODIFIER)) {
				reach.removeModifier(REACH_MODIFIER);
			}
		} else {
			if (AbilityCheck.enabled(player, Ability.LONGREACH)) {
				if (!reach.hasModifier(REACH_MODIFIER)) {
					reach.applyNonPersistentModifier(REACH_MODIFIER);
				}
			} else {
				if (reach.hasModifier(REACH_MODIFIER)) {
					reach.removeModifier(REACH_MODIFIER);
				}
			}
		}
	}

}
