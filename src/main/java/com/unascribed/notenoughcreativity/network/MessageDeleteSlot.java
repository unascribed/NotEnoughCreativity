package com.unascribed.notenoughcreativity.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.ContainerCreativePlus;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;

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
	protected void handle(EntityPlayer player) {
		if (player.inventoryContainer instanceof ContainerCreativePlus) {
			player.inventoryContainer.getSlot(slot).putStack(ItemStack.EMPTY);
		}
	}
	
}
