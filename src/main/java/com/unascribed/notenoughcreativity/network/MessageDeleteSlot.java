package com.unascribed.notenoughcreativity.network;

import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Message;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Side;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.unascribed.notenoughcreativity.ContainerCreativePlus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@ReceivedOn(Side.SERVER)
public class MessageDeleteSlot extends Message {

	@MarshalledAs("varint")
	private int slot;
	
	public MessageDeleteSlot(int slot) {
		super(NotEnoughCreativity.network);
		this.slot = slot;
	}
	
	public MessageDeleteSlot(NetworkContext ctx) {
		super(ctx);
	}
	
	@Override
	protected void handle(PlayerEntity player) {
		if (player.container instanceof ContainerCreativePlus) {
			player.container.getSlot(slot).putStack(ItemStack.EMPTY);
		}
	}
	
}
