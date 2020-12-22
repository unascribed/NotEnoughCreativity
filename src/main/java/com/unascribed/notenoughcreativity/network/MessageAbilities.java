package com.unascribed.notenoughcreativity.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ReceivedOn(Side.CLIENT)
public class MessageAbilities extends Message {

	@MarshalledAs("varint")
	private int bits;
	
	public MessageAbilities(int bits) {
		super(NotEnoughCreativity.network);
		this.bits = bits;
	}
	
	public MessageAbilities(Iterable<Ability> enabled) {
		super(NotEnoughCreativity.network);
		for (Ability a : enabled) {
			bits |= 1 << a.ordinal();
		}
	}
	
	public MessageAbilities(NetworkContext ctx) {
		super(ctx);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer player) {
		player.getEntityData().setInteger("NotEnoughCreativityAbilities", bits);
	}
	
}
