package com.unascribed.notenoughcreativity;

import com.unascribed.notenoughcreativity.network.MessageAbilities;
import com.unascribed.notenoughcreativity.network.MessageDeleteSlot;
import com.unascribed.notenoughcreativity.network.MessageEnabled;
import com.unascribed.notenoughcreativity.network.MessageOtherNoclipping;
import com.unascribed.notenoughcreativity.network.MessageRecallPosition;
import com.unascribed.notenoughcreativity.network.MessageSavePosition;
import com.unascribed.notenoughcreativity.network.MessageSetAbility;
import com.unascribed.notenoughcreativity.network.MessageSetEnabled;
import com.unascribed.notenoughcreativity.network.MessageWarp;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class NotEnoughCreativity implements ModInitializer {

	public static NetworkContext network;
	
	public static NotEnoughCreativity INSTANCE;
	
	public NotEnoughCreativity() {
		INSTANCE = this;
	}
	
	@Override
	public void onInitialize() {
		network = NetworkContext.forChannel(new Identifier("notenoughcreativity", "main"));
		network.register(MessageSetEnabled.class);
		network.register(MessageEnabled.class);
		network.register(MessageSetAbility.class);
		network.register(MessageAbilities.class);
		network.register(MessageDeleteSlot.class);
		network.register(MessageOtherNoclipping.class);
		network.register(MessageSavePosition.class);
		network.register(MessageRecallPosition.class);
		network.register(MessageWarp.class);
		NEClientSpringboard.setup();
	}
	
	public static boolean isCreativePlus(PlayerEntity ep) {
		return ep != null && ep.abilities.creativeMode && ep instanceof NECPlayer && ((NECPlayer)ep).nec$isCreativePlusEnabled();
	}
	
	public static void updateInventory(PlayerEntity player) {
		boolean enabled = isCreativePlus(player);
		if (!player.world.isClient) {
			new MessageEnabled(enabled).sendTo(player);
			if (enabled) {
				new MessageAbilities(((NECPlayer)player).nec$getEnabledAbilities()).sendTo(player);
			}
		}
		PlayerScreenHandler orig = player.playerScreenHandler;
		PlayerScreenHandler nw;
		if (enabled) {
			if (orig instanceof CreativePlusScreenHandler) return;
			nw = new CreativePlusScreenHandler(player);
		} else {
			if (!(orig instanceof CreativePlusScreenHandler)) return;
			nw = new PlayerScreenHandler(player.inventory, !player.world.isClient, player);
		}
		player.playerScreenHandler = nw;
		if (orig == player.currentScreenHandler) {
			player.currentScreenHandler = nw;
		}
		if (player instanceof ScreenHandlerListener) {
			nw.addListener((ScreenHandlerListener)player);
		}
	}

	public static void teleportWithEffects(ServerPlayerEntity player, ServerWorld target, Vec3d pos) {
		ServerWorld src = (ServerWorld)player.world;
		Vec3d vel = player.getVelocity();
		Box box = player.getBoundingBox();
		src.spawnParticles(ParticleTypes.WITCH, box.getCenter().x, box.getCenter().y, box.getCenter().z,
				40, box.getXLength(), box.getYLength(), box.getZLength(), 0);
		src.spawnParticles(ParticleTypes.POOF, box.getCenter().x, box.getCenter().y, box.getCenter().z,
				30, box.getXLength()/2, box.getYLength()/2, box.getZLength()/2, 0);
		player.teleport(target, pos.x, pos.y, pos.z, player.yaw, player.pitch);
		player.setVelocity(vel);
		player.velocityModified = true;
		target.spawnParticles(ParticleTypes.WITCH, pos.x, pos.y+(player.getHeight()/2), pos.z,
				20, box.getXLength(), box.getYLength(), box.getZLength(), 0);
		target.spawnParticles(ParticleTypes.POOF, pos.x, pos.y+(player.getHeight()/2), pos.z,
				10, box.getXLength()/2, box.getYLength()/2, box.getZLength()/2, 0);
		player.world.playSound(null, player.getPos().x, player.getPos().y, player.getPos().z, SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, player.getSoundCategory(), 1, 1);
	}

}
