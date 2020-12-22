package com.unascribed.notenoughcreativity.compat;

import java.awt.Rectangle;
import java.util.List;

import javax.annotation.Nullable;

import com.unascribed.notenoughcreativity.client.GuiCreativePlus;

import com.google.common.collect.Lists;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IAdvancedGuiHandler;

@JEIPlugin
public class JeiPlugin implements IModPlugin {

	@Override
	public void register(IModRegistry registry) {
		registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler<GuiCreativePlus>() {

			@Override
			@Nullable
			public List<Rectangle> getGuiExtraAreas(GuiCreativePlus g) {
				return Lists.newArrayList(new Rectangle(g.getGuiLeft()-g.getXSizeAddn(), g.getGuiTop(), g.getXSizeAddn(), g.getYSizeAddn()));
			}
			
			@Override
			public Class<GuiCreativePlus> getGuiContainerClass() {
				return GuiCreativePlus.class;
			}
			
		});
	}
	
}
