package com.unascribed.notenoughcreativity.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

@ReceivedOn(Side.SERVER)
public class MessagePickEntity extends Message {

	@MarshalledAs("varint")
	private int entityId;
	
	@MarshalledAs("f32")
	private float hitX;
	@MarshalledAs("f32")
	private float hitY;
	@MarshalledAs("f32")
	private float hitZ;
	
	private boolean exact;

	public MessagePickEntity(int entityId, float hitX, float hitY, float hitZ, boolean exact) {
		super(NotEnoughCreativity.network);
		this.entityId = entityId;
		this.hitX = hitX;
		this.hitY = hitY;
		this.hitZ = hitZ;
		this.exact = exact;
	}
	
	public MessagePickEntity(NetworkContext ctx) {
		super(ctx);
	}
	
	@Override
	protected void handle(EntityPlayer player) {
		if (!NotEnoughCreativity.isCreativePlus(player)) return;
		Entity e = player.world.getEntityByID(entityId);
		if (e == null) return;
		NotEnoughCreativity.pickBlock(player, new RayTraceResult(e, new Vec3d(hitX, hitY, hitZ)), exact);
	}
	
}
