package com.unascribed.notenoughcreativity;

import net.fabricmc.fabric.api.block.BlockPickInteractionAware;
import net.fabricmc.fabric.api.entity.EntityPickInteractionAware;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

public class FabricAPIHandler {

	public static boolean hasIntelligentPick(Entity entity) {
		return entity instanceof EntityPickInteractionAware;
	}

	public static ItemStack pick(Entity entity, PlayerEntity player, EntityHitResult hit) {
		return ((EntityPickInteractionAware)entity).getPickedStack(player, hit);
	}

	public static boolean hasIntelligentPick(BlockState state) {
		return state.getBlock() instanceof BlockPickInteractionAware;
	}

	public static ItemStack pick(BlockState state, PlayerEntity player, BlockHitResult target) {
		return ((BlockPickInteractionAware)state.getBlock()).getPickedStack(state, player.world, target.getBlockPos(), player, target);
	}

}
