package com.unascribed.notenoughcreativity.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.unascribed.notenoughcreativity.ContainerCreativePlus;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.client.GuiCreativePlus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ReceivedOn(Side.CLIENT)
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
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer player) {
		player.getEntityData().setBoolean("NotEnoughCreativity", enabled);
		NotEnoughCreativity.updateInventory(player);
		if (enabled) {
			if (Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiCreativePlus(new ContainerCreativePlus(player)));
			}
		} else {
			if (Minecraft.getMinecraft().currentScreen instanceof GuiCreativePlus) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiContainerCreative(player));
			}
		}
	}
	
}
