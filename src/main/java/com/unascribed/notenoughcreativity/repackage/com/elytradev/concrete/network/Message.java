/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2018:
 * 	Una Thompson (unascribed),
 * 	Isaac Ellingson (Falkreon),
 * 	Jamie Mansfield (jamierocks),
 * 	Alex Ponebshek (capitalthree),
 * 	and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network;

import java.util.Map;

import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.Asynchronous;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.exception.BadMessageException;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.exception.WrongSideException;

import com.google.common.collect.Maps;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public abstract class Message {
	private static final class ClassInfo {
		public final boolean async;
		public final Side side;
		public ClassInfo(boolean async, Side side) {
			this.async = async;
			this.side = side;
		}
	}
	private static final Map<Class<?>, ClassInfo> classInfo = Maps.newHashMap();
	
	
	private transient final NetworkContext ctx;
	
	private transient final Side side;
	private transient final boolean async;
	
	public Message(NetworkContext ctx) {
		this.ctx = ctx;
		
		ClassInfo ci = classInfo.get(getClass());
		if (ci == null) {
			ReceivedOn ro = getClass().getDeclaredAnnotation(ReceivedOn.class);
			if (ro == null) {
				throw new BadMessageException("Must specify @ReceivedOn");
			} else {
				side = ro.value();
			}
			
			async = getClass().getDeclaredAnnotation(Asynchronous.class) != null;
			classInfo.put(getClass(), new ClassInfo(async, side));
		} else {
			async = ci.async;
			side = ci.side;
		}
		
	}
	
	@Environment(EnvType.CLIENT)
	void doHandleClient() {
		if (async) {
			handle(MinecraftClient.getInstance().player);
		} else {
			MinecraftClient.getInstance().execute(new Runnable() {
				@Override
				@Environment(EnvType.CLIENT)
				public void run() {
					handle(MinecraftClient.getInstance().player);
				}
			});
		}
	}
	
	void doHandleServer(PlayerEntity sender) {
		if (async) {
			handle(sender);
		} else {
			((ServerWorld)sender.world).getServer().execute(() -> handle(sender));
		}
	}
	
	/**
	 * Handles this Message when received.
	 *
	 * @param player The player that sent this Message if received on the server.
	 *               The player that received this Message if received on the client.
	 */
	protected abstract void handle(PlayerEntity player);
	
	Side getSide() {
		return side;
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that can
	 * see the given entity.
	 */
	public final void sendToAllWatching(Entity e) {
		if (side == Side.SERVER) wrongSide();
		if (e.world instanceof ServerWorld) {
			ServerWorld srv = (ServerWorld) e.world;
			CustomPayloadS2CPacket packet = toClientboundVanillaPacket();
			srv.getChunkManager().sendToNearbyPlayers(e, packet);
		}
	}
	
	/**
	 * For use on the server-side. Sends this Message to the given player.
	 */
	public final void sendTo(PlayerEntity player) {
		if (side == Side.SERVER) wrongSide();
		if (player instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) player).networkHandler.sendPacket(toClientboundVanillaPacket());
		}
	}
	
	/**
	 * For use on the <i>client</i>-side. This is the only valid method for use
	 * on the client side.
	 */
	@Environment(EnvType.CLIENT)
	public final void sendToServer() {
		if (side == Side.CLIENT) wrongSide();
		ClientPlayNetworkHandler conn = MinecraftClient.getInstance().getNetworkHandler();
		if (conn == null) throw new IllegalStateException("Cannot send a message while not connected");
		conn.sendPacket(toServerboundVanillaPacket());
	}
	
	/**
	 * Mainly intended for internal use, but can be useful for more complex
	 * use cases.
	 */
	public final CustomPayloadC2SPacket toServerboundVanillaPacket() {
		return new CustomPayloadC2SPacket(ctx.channel, ctx.getPayloadFrom(this));
	}
	
	/**
	 * Mainly intended for internal use, but can be useful for more complex
	 * use cases.
	 */
	public final CustomPayloadS2CPacket toClientboundVanillaPacket() {
		return new CustomPayloadS2CPacket(ctx.channel, ctx.getPayloadFrom(this));
	}
	
	
	private void wrongSide() {
		throw new WrongSideException(getClass() + " cannot be sent from side " + side);
	}
}
