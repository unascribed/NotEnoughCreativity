package com.unascribed.notenoughcreativity.network;

import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Message;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Side;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.ReceivedOn;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

@ReceivedOn(Side.SERVER)
public class MessagePickBlock extends Message {

	@MarshalledAs("varint")
	private int x;
	@MarshalledAs("varint")
	private int y;
	@MarshalledAs("varint")
	private int z;
	
	@MarshalledAs("f32")
	private float hitX;
	@MarshalledAs("f32")
	private float hitY;
	@MarshalledAs("f32")
	private float hitZ;
	
	private Direction sideHit;
	
	private boolean exact;

	public MessagePickBlock(BlockPos pos, float hitX, float hitY, float hitZ, Direction sideHit, boolean exact) {
		super(NotEnoughCreativity.network);
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.hitX = hitX;
		this.hitY = hitY;
		this.hitZ = hitZ;
		this.sideHit = sideHit;
		this.exact = exact;
	}
	
	public MessagePickBlock(NetworkContext ctx) {
		super(ctx);
	}
	
	@Override
	protected void handle(PlayerEntity player) {
		if (!NotEnoughCreativity.isCreativePlus(player)) return;
		NotEnoughCreativity.pickBlock(player, new BlockRayTraceResult(new Vector3d(hitX, hitY, hitZ), sideHit, new BlockPos(x, y, z), false), exact);
	}
	
}
