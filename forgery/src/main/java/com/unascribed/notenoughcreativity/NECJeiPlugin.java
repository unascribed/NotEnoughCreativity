package com.unascribed.notenoughcreativity;

import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class NECJeiPlugin implements IModPlugin {

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
		try {
			registry.addGuiContainerHandler((Class)Class.forName("com.unascribed.notenoughcreativity.client.CreativePlusScreen"), new IGuiContainerHandler<ContainerScreen<?>>() {

				@Override
				public List<Rectangle2d> getGuiExtraAreas(ContainerScreen<?> g) {
					return Lists.newArrayList(new Rectangle2d(g.getGuiLeft()-((CPSAccess)g).getBackgroundWidthAddn(), g.getGuiTop(), ((CPSAccess)g).getBackgroundWidthAddn(), ((CPSAccess)g).getBackgroundHeightAddn()));
				}
				
			});
		} catch (ClassNotFoundException e) {
			throw (NoClassDefFoundError)new NoClassDefFoundError().initCause(e);
		}
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation("notenoughcreativity", "main");
	}
	
}
