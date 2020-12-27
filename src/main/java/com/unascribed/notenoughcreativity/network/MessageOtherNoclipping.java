package com.unascribed.notenoughcreativity.network;

import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer player) {
		Entity e = player.world.getEntityByID(entityId);
		if (e != null) {
			e.getEntityData().setBoolean("NotEnoughCreativityNoclipping", enabled);
		}
	}
	
}
