package com.unascribed.notenoughcreativity.network;

import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Message;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Side;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.unascribed.notenoughcreativity.CreativePlusScreenHandler;
import com.unascribed.notenoughcreativity.NECPlayer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

@ReceivedOn(Side.SERVER)
public class MessageRecallPosition extends Message {

	public MessageRecallPosition() {
		super(NotEnoughCreativity.network);
	}
	
	public MessageRecallPosition(NetworkContext ctx) {
		super(ctx);
	}
	
	@Override
	protected void handle(PlayerEntity player) {
		if (player.playerScreenHandler instanceof CreativePlusScreenHandler) {
			Vec3d saved = ((NECPlayer)player).nec$getSavedPosition();
			if (saved == null) {
				player.sendMessage(new TranslatableText("msg.notenoughcreativity.position_recall.fail.no_pos"), true);
			} else {
				Identifier dim = ((NECPlayer)player).nec$getSavedDimension();
				ServerWorld target;
				if (!player.world.getRegistryKey().getValue().equals(dim)) {
					ServerWorld world = player.world.getServer().getWorld(RegistryKey.of(Registry.WORLD_KEY, dim));
					if (world == null) {
						player.sendMessage(new TranslatableText("msg.notenoughcreativity.position_recall.fail.no_dim"), true);
						return;
					}
					target = world;
				} else {
					target = (ServerWorld)player.world;
				}
				NotEnoughCreativity.teleportWithEffects((ServerPlayerEntity)player, target, saved);
				player.sendMessage(new TranslatableText("msg.notenoughcreativity.position_recall.success"), true);
			}
		}
	}
	
}
