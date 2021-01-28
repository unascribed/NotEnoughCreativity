package com.unascribed.notenoughcreativity.network;

import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Message;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Side;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.unascribed.notenoughcreativity.CreativePlusScreenHandler;
import com.unascribed.notenoughcreativity.NECPlayer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;

@ReceivedOn(Side.SERVER)
public class MessageSavePosition extends Message {

	public MessageSavePosition() {
		super(NotEnoughCreativity.network);
	}
	
	public MessageSavePosition(NetworkContext ctx) {
		super(ctx);
	}
	
	@Override
	protected void handle(PlayerEntity player) {
		if (player.playerScreenHandler instanceof CreativePlusScreenHandler) {
			((NECPlayer)player).nec$setSavedPosition(player.getPos());
			((NECPlayer)player).nec$setSavedDimension(player.world.getRegistryKey().getValue());
			player.sendMessage(new TranslatableText("msg.notenoughcreativity.saved_position"), true);
			Box box = player.getBoundingBox();
			((ServerWorld)player.world).spawnParticles(ParticleTypes.WITCH, box.getCenter().x, player.getPos().y, box.getCenter().z,
					100, box.getXLength()/2, 0, box.getZLength()/2, 0);
			player.world.playSound(null, player.getPos().x, player.getPos().y, player.getPos().z, SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR, player.getSoundCategory(), 1, 1);
		}
	}
	
}
