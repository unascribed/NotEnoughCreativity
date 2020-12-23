package com.unascribed.notenoughcreativity.network;

import com.unascribed.notenoughcreativity.ContainerCreativePlus;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.client.GuiCreativePlus;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Message;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.Side;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.Asynchronous;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.annotation.type.ReceivedOn;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
	@OnlyIn(Dist.CLIENT)
	protected void handle(PlayerEntity player) {
		// block the network thread via runImmediately to ensure we get a word in edgewise before
		// the slot packets arrive and attempt to set invalid indices in the creative screen
		Minecraft.getInstance().runImmediately(() -> {
			player.getPersistentData().putBoolean("NotEnoughCreativity", enabled);
			NotEnoughCreativity.updateInventory(player);
			if (enabled) {
				if (Minecraft.getInstance().currentScreen instanceof CreativeScreen) {
					Minecraft.getInstance().displayGuiScreen(new GuiCreativePlus(new ContainerCreativePlus(player)));
				}
			} else {
				if (Minecraft.getInstance().currentScreen instanceof GuiCreativePlus) {
					Minecraft.getInstance().displayGuiScreen(new CreativeScreen(player));
				}
			}
		});
	}
	
}
