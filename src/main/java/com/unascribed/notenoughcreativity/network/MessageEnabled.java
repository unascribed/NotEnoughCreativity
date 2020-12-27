package com.unascribed.notenoughcreativity.network;

import com.unascribed.notenoughcreativity.CreativePlusScreenHandler;
import com.unascribed.notenoughcreativity.NECPlayer;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.client.CreativePlusScreen;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Message;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Side;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.Asynchronous;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.ReceivedOn;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

@ReceivedOn(Side.CLIENT)
@Asynchronous
public class MessageEnabled extends Message {

	private boolean enabled;

	public MessageEnabled(boolean enabled) {
		super(NotEnoughCreativity.network);
		this.enabled = enabled;
	}
	
	public MessageEnabled(NetworkContext ctx) {
		super(ctx);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(PlayerEntity player) {
		// block the network thread via submitAndJoin to ensure we get a word in edgewise before
		// the slot packets arrive and attempt to set invalid indices in the creative screen
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.submitAndJoin(new Runnable() {
			@Override
			@Environment(EnvType.CLIENT)
			public void run() {
				((NECPlayer)mc.player).nec$setCreativePlusEnabled(enabled);
				NotEnoughCreativity.updateInventory(mc.player);
				if (enabled) {
					if (mc.currentScreen instanceof CreativeInventoryScreen) {
						mc.openScreen(new CreativePlusScreen(new CreativePlusScreenHandler(mc.player)));
					}
				} else {
					if (mc.currentScreen instanceof CreativePlusScreen) {
						mc.openScreen(new CreativeInventoryScreen(mc.player));
					}
				}
			}
		});
	}
	
}
