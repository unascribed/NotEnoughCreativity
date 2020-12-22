package com.unascribed.notenoughcreativity.network;

import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Message;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Side;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.ReceivedOn;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
	@OnlyIn(Dist.CLIENT)
	protected void handle(PlayerEntity player) {
		player.getPersistentData().putInt("NotEnoughCreativityAbilities", bits);
	}
	
}
