package com.unascribed.notenoughcreativity.network;

import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Message;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Side;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.ReceivedOn;

import net.minecraft.entity.player.PlayerEntity;

@ReceivedOn(Side.SERVER)
public class MessageSetEnabled extends Message {

	private boolean enabled;

	public MessageSetEnabled(boolean enabled) {
		super(NotEnoughCreativity.network);
		this.enabled = enabled;
	}
	
	public MessageSetEnabled(NetworkContext ctx) {
		super(ctx);
	}
	
	@Override
	protected void handle(PlayerEntity player) {
		if (player.abilities.isCreativeMode) {
			player.getPersistentData().putBoolean("NotEnoughCreativity", enabled);
			NotEnoughCreativity.updateInventory(player);
		}
	}
	
}
