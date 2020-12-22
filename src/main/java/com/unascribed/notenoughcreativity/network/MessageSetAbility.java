package com.unascribed.notenoughcreativity.network;

import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Message;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Side;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.ReceivedOn;

import net.minecraft.entity.player.PlayerEntity;

@ReceivedOn(Side.SERVER)
public class MessageSetAbility extends Message {

	@MarshalledAs("varint")
	private int ability;
	private boolean enabled;
	
	public MessageSetAbility(Ability ability, boolean enabled) {
		super(NotEnoughCreativity.network);
		this.ability = ability.ordinal();
		this.enabled = enabled;
	}
	
	public MessageSetAbility(NetworkContext ctx) {
		super(ctx);
	}
	
	@Override
	protected void handle(PlayerEntity player) {
		int i = player.getPersistentData().getInt("NotEnoughCreativityAbilities");
		int bit = 1 << ability;
		if (enabled) {
			i |= bit;
		} else {
			i &= ~bit;
		}
		player.getPersistentData().putInt("NotEnoughCreativityAbilities", i);
		new MessageAbilities(i).sendTo(player);
	}
	
}
