package com.unascribed.notenoughcreativity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

public class ForgeHandler {

	public static void firePlayerContainerOpen(PlayerEntity player) {
		MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.container));
	}

}
