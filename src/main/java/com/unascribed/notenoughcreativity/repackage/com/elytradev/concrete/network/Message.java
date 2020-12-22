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

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
	
	@OnlyIn(Dist.CLIENT)
	void doHandleClient() {
		if (async) {
			handle(Minecraft.getInstance().player);
		} else {
			Minecraft.getInstance().enqueue(new Runnable() {
				@Override
				@OnlyIn(Dist.CLIENT)
				public void run() {
					handle(Minecraft.getInstance().player);
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
	 * For use on the server-side. Sends this Message to the given player.
	 */
	public final void sendTo(PlayerEntity player) {
		if (side == Side.SERVER) wrongSide();
		if (player instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) player).connection.sendPacket(toClientboundVanillaPacket());
		}
	}
	
	/**
	 * For use on the <i>client</i>-side. This is the only valid method for use
	 * on the client side.
	 */
	@OnlyIn(Dist.CLIENT)
	public final void sendToServer() {
		if (side == Side.CLIENT) wrongSide();
		ClientPlayNetHandler conn = Minecraft.getInstance().getConnection();
		if (conn == null) throw new IllegalStateException("Cannot send a message while not connected");
		conn.sendPacket(toServerboundVanillaPacket());
	}
	
	/**
	 * Mainly intended for internal use, but can be useful for more complex
	 * use cases.
	 */
	public final CCustomPayloadPacket toServerboundVanillaPacket() {
		return new CCustomPayloadPacket(ctx.channel, ctx.getPayloadFrom(this));
	}
	
	/**
	 * Mainly intended for internal use, but can be useful for more complex
	 * use cases.
	 */
	public final SCustomPayloadPlayPacket toClientboundVanillaPacket() {
		return new SCustomPayloadPlayPacket(ctx.channel, ctx.getPayloadFrom(this));
	}
	
	
	private void wrongSide() {
		throw new WrongSideException(getClass() + " cannot be sent from side " + side);
	}
}
