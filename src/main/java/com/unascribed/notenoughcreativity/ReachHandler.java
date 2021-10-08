package com.unascribed.notenoughcreativity;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;

// Fabric version. See Forge version in the "forgery" subdir that uses Forge's REACH_DISTANCE attribute.
public class ReachHandler {

	private interface FabricImpl {
		void tick(PlayerEntity player);
	}
	
	private static FabricImpl impl;
	
	public static void tick(PlayerEntity player) {
		if (impl == null) {
			if (FabricLoader.getInstance().isModLoaded("reach-entity-attributes")) {
				impl = new REAFabricImpl();
			} else {
				impl = new RawFabricImpl();
			}
		}
		impl.tick(player);
	}

	private static class REAFabricImpl implements FabricImpl {

		private static final EntityAttributeModifier REACH_MODIFIER = new EntityAttributeModifier("Not Enough Creativity Long Reach ability", 8, Operation.ADDITION);
		
		@Override
		public void tick(PlayerEntity player) {
			EntityAttributeInstance reach = player.getAttributes().getCustomInstance(ReachEntityAttributes.REACH);
			if (!player.getAbilities().creativeMode) {
				if (reach.hasModifier(REACH_MODIFIER)) {
					reach.removeModifier(REACH_MODIFIER);
				}
			} else {
				if (AbilityCheck.enabled(player, Ability.LONGREACH)) {
					if (!reach.hasModifier(REACH_MODIFIER)) {
						reach.addTemporaryModifier(REACH_MODIFIER);
					}
				} else {
					if (reach.hasModifier(REACH_MODIFIER)) {
						reach.removeModifier(REACH_MODIFIER);
					}
				}
			}
		}
		
	}
	
	private static class RawFabricImpl implements FabricImpl {

		@Override
		public void tick(PlayerEntity player) {
			((NECPlayer)player).nec$setVanillaReachExtensionEnabled(AbilityCheck.enabled(player, Ability.LONGREACH));
		}
		
	}

}
