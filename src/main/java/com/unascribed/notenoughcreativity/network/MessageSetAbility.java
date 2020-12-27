package com.unascribed.notenoughcreativity.network;

import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.NECPlayer;
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
		if (!(player instanceof NECPlayer)) return;
		NECPlayer nec = (NECPlayer)player;
		if (enabled) {
			nec.nec$getEnabledAbilities().add(Ability.VALUES.get(ability));
		} else {
			nec.nec$getEnabledAbilities().remove(Ability.VALUES.get(ability));
		}
		new MessageAbilities(nec.nec$getEnabledAbilities()).sendTo(player);
	}
	
}
