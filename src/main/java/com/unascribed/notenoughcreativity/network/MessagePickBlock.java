package com.unascribed.notenoughcreativity.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

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
	
	private EnumFacing sideHit;
	
	private boolean exact;

	public MessagePickBlock(BlockPos pos, float hitX, float hitY, float hitZ, EnumFacing sideHit, boolean exact) {
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
	protected void handle(EntityPlayer player) {
		if (!NotEnoughCreativity.isCreativePlus(player)) return;
		NotEnoughCreativity.pickBlock(player, new RayTraceResult(new Vec3d(hitX, hitY, hitZ), sideHit, new BlockPos(x, y, z)), exact);
	}
	
}
