package com.unascribed.notenoughcreativity;

import com.unascribed.notenoughcreativity.network.MessageAbilities;
import com.unascribed.notenoughcreativity.network.MessageDeleteSlot;
import com.unascribed.notenoughcreativity.network.MessageEnabled;
import com.unascribed.notenoughcreativity.network.MessageOtherNoclipping;
import com.unascribed.notenoughcreativity.network.MessagePickBlock;
import com.unascribed.notenoughcreativity.network.MessagePickEntity;
import com.unascribed.notenoughcreativity.network.MessageSetAbility;
import com.unascribed.notenoughcreativity.network.MessageSetEnabled;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;

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
		network.register(MessagePickBlock.class);
		network.register(MessagePickEntity.class);
		network.register(MessageOtherNoclipping.class);
		NEClientSpringboard.setup();
	}
	
	public static boolean isCreativePlus(PlayerEntity ep) {
		return ep != null && ep.abilities.creativeMode && ep instanceof NECPlayer && ((NECPlayer)ep).nec$isCreativePlusEnabled();
	}
	
	public static void updateInventory(PlayerEntity player) {
		boolean enabled = isCreativePlus(player);
		PlayerScreenHandler orig = player.playerScreenHandler;
		PlayerScreenHandler nw;
		if (enabled) {
			nw = new CreativePlusScreenHandler(player);
		} else {
			nw = new PlayerScreenHandler(player.inventory, !player.world.isClient, player);
		}
		if (!player.world.isClient) {
			new MessageEnabled(enabled).sendTo(player);
			if (enabled) {
				new MessageAbilities(((NECPlayer)player).nec$getEnabledAbilities()).sendTo(player);
			}
		}
		player.playerScreenHandler = nw;
		if (orig == player.currentScreenHandler) {
			player.currentScreenHandler = nw;
		}
		if (player instanceof ScreenHandlerListener) {
			nw.addListener((ScreenHandlerListener)player);
		}
	}

	public static void pickBlock(PlayerEntity player, HitResult target, boolean exact) {
		PickBlockHandler.pickBlock(player, target, exact);
	}

}
