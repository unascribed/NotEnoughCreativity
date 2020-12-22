package com.unascribed.notenoughcreativity.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

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
	protected void handle(EntityPlayer player) {
		int i = player.getEntityData().getInteger("NotEnoughCreativityAbilities");
		int bit = 1 << ability;
		if (enabled) {
			i |= bit;
		} else {
			i &= ~bit;
		}
		player.getEntityData().setInteger("NotEnoughCreativityAbilities", i);
		new MessageAbilities(i).sendTo(player);
	}
	
}
