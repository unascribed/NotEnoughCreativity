package com.unascribed.notenoughcreativity.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

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
	protected void handle(EntityPlayer player) {
		if (player.capabilities.isCreativeMode) {
			player.getEntityData().setBoolean("NotEnoughCreativity", enabled);
			NotEnoughCreativity.updateInventory(player);
		}
	}
	
}
