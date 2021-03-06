package com.unascribed.notenoughcreativity.network;

import com.unascribed.notenoughcreativity.NECPlayer;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Message;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Side;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.ReceivedOn;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@ReceivedOn(Side.CLIENT)
public class MessageOtherNoclipping extends Message {

	@MarshalledAs("varint")
	private int entityId;
	private boolean enabled;
	
	public MessageOtherNoclipping(int entityId, boolean enabled) {
		super(NotEnoughCreativity.network);
		this.entityId = entityId;
		this.enabled = enabled;
	}
	
	public MessageOtherNoclipping(NetworkContext ctx) {
		super(ctx);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(PlayerEntity player) {
		Entity e = player.world.getEntityById(entityId);
		if (e instanceof NECPlayer) {
			((NECPlayer)e).nec$setNoclipping(enabled);
		}
	}
	
}
