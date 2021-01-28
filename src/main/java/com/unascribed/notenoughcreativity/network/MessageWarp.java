package com.unascribed.notenoughcreativity.network;

import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Message;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Side;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.unascribed.notenoughcreativity.CreativePlusScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@ReceivedOn(Side.SERVER)
public class MessageWarp extends Message {

	public MessageWarp() {
		super(NotEnoughCreativity.network);
	}
	
	public MessageWarp(NetworkContext ctx) {
		super(ctx);
	}
	
	@Override
	protected void handle(PlayerEntity player) {
		if (player.playerScreenHandler instanceof CreativePlusScreenHandler) {
			NotEnoughCreativity.teleportWithEffects((ServerPlayerEntity)player, (ServerWorld)player.world, player.raycast(256, 0, false).getPos());
		}
	}
	
}
