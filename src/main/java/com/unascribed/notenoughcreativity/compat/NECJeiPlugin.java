package com.unascribed.notenoughcreativity.compat;

import java.util.List;

import com.unascribed.notenoughcreativity.client.GuiCreativePlus;

import com.google.common.collect.Lists;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class NECJeiPlugin implements IModPlugin {

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
		registry.addGuiContainerHandler(GuiCreativePlus.class, new IGuiContainerHandler<GuiCreativePlus>() {

			@Override
			public List<Rectangle2d> getGuiExtraAreas(GuiCreativePlus g) {
				return Lists.newArrayList(new Rectangle2d(g.getGuiLeft()-g.getXSizeAddn(), g.getGuiTop(), g.getXSizeAddn(), g.getYSizeAddn()));
			}
			
		});
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation("notenoughcreativity", "main");
	}
	
}
