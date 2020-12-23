package com.unascribed.notenoughcreativity.client;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Keyboard;

import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.ContainerCreativePlus;
import com.unascribed.notenoughcreativity.network.MessageDeleteSlot;
import com.unascribed.notenoughcreativity.network.MessageSetAbility;

import com.google.common.collect.Lists;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class GuiCreativePlus extends GuiContainer {

	private static final ResourceLocation BG = new ResourceLocation("notenoughcreativity", "textures/gui/inventory.png");
	
	private final List<GuiParticle> particles = Lists.newArrayList();
	
	private final ContainerCreativePlus container;
	
	private int xSizeAddn;
	private int ySizeAddn;
	
	private Ability hoveredAbility;
	
	public GuiCreativePlus(ContainerCreativePlus container) {
		super(container);
		this.container = container;
		xSizeAddn = 52;
		ySizeAddn = 162;
		xSize = 198;
		ySize = 212;
	}
	
	public int getXSizeAddn() {
		return xSizeAddn;
	}
	
	public int getYSizeAddn() {
		return ySizeAddn;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		hoveredAbility = null;
		
		int x = getGuiLeft()+getXSize()-18;
		int y = getGuiTop()+4;
		mc.renderEngine.bindTexture(BG);
		GlStateManager.disableLighting();
		for (Ability a : Ability.values()) {
			drawRect(x, y, x+11, y+11, 0xFF000000);
			if (a.isEnabled(mc.player)) {
				GlStateManager.color(1, 1, 0);
			} else {
				GlStateManager.color(0.5f, 0.5f, 0.5f);
			}
			drawModalRectWithCustomSizedTexture(x+1, y+1, 251+(a.ordinal()*9), 0, 9, 9, 384, 384);
			if (a.isEnabled(mc.player)) {
				GlStateManager.color(1, 1, 1, 0.2f);
				ThreadLocalRandom r = ThreadLocalRandom.current();
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.disableAlpha();
				for (int i = 0; i < 6; i++) {
					GlStateManager.pushMatrix();
					GlStateManager.translate(r.nextFloat()-r.nextFloat(), r.nextFloat()-r.nextFloat(), 0);
					drawModalRectWithCustomSizedTexture(x+1, y+1, 251+(a.ordinal()*9), 0, 9, 9, 384, 384);
					GlStateManager.popMatrix();
				}
				GlStateManager.enableAlpha();
			}
			if (mouseX >= x && mouseX < x+11 && mouseY >= y && mouseY < y+11) {
				hoveredAbility = a;
			}
			x -= 12;
		}
		
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableAlpha();
		for (GuiParticle gp : particles) {
			gp.render(partialTicks);
		}
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		if (getSlotUnderMouse() == container.deleteSlot) {
			drawHoveringText(I18n.format("inventory.binSlot"), mouseX, mouseY);
		} else if (getSlotUnderMouse() == container.returnSlot) {
			drawHoveringText(I18n.format("notenoughcreativity.exit"), mouseX, mouseY);
		} else if (hoveredAbility != null) {
			drawHoveringText(Lists.newArrayList(
					(hoveredAbility.isEnabled(mc.player) ? "§e" : "")+I18n.format("notenoughcreativity.ability."+(hoveredAbility.name().toLowerCase(Locale.ROOT))+".name"),
					"§7"+I18n.format("notenoughcreativity.ability."+(hoveredAbility.name().toLowerCase(Locale.ROOT))+".desc")
				), mouseX, mouseY);
		} else {
			renderHoveredToolTip(mouseX, mouseY);
		}
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		Iterator<GuiParticle> iter = particles.iterator();
		while (iter.hasNext()) {
			GuiParticle gp = iter.next();
			if (gp.expired || gp.posY < -1) {
				iter.remove();
			} else {
				gp.update();
			}
		}
		int x = getGuiLeft()+getXSize()-18;
		int y = getGuiTop()+4;
		mc.renderEngine.bindTexture(BG);
		GlStateManager.disableLighting();
		for (Ability a : Ability.values()) {
			if (a.isEnabled(mc.player) && Math.random() < 0.2) {
				GuiParticle gp = new GuiParticle(x+2+(Math.random()*7), y+2+(Math.random()*7));
				gp.color = 0xFFFFFF55;
				gp.motionX = (Math.random()-0.5)/2;
				gp.motionY = -1;
				gp.gravity = 0;
				gp.maxAge = 300;
				particles.add(gp);
			}
			x -= 12;
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.renderEngine.bindTexture(BG);
		int x = ((width-xSize)/2)-xSizeAddn;
		int y = (height-ySize)/2;
		if (Ability.DARKMODE.isEnabled(mc.player)) {
			GlStateManager.color(0.2f, 0.2f, 0.3f, 1);
		} else {
			GlStateManager.color(1, 1, 1, 1);
		}
		drawModalRectWithCustomSizedTexture(x, y, 0, 0, xSize+xSizeAddn, ySize, 384, 384);
		if (Ability.DARKMODE.isEnabled(mc.player)) {
			GlStateManager.color(1, 1, 0, 1);
			drawModalRectWithCustomSizedTexture(x+61, y+166, 61, 166, 16, 16, 384, 384);
			GlStateManager.color(1, 0.4f, 0.2f, 1);
			drawModalRectWithCustomSizedTexture(x+61, y+188, 61, 188, 16, 16, 384, 384);
		}
		GlStateManager.color(1, 1, 1, 1);
		GuiInventory.drawEntityOnScreen(x+51, y+82, 30, x+51-mouseX, y+30-mouseY, mc.player);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		int col = 0x404040;
		if (Ability.DARKMODE.isEnabled(mc.player)) {
			col = 0xFFFFFF;
		}
		fontRenderer.drawString(I18n.format("notenoughcreativity.title"), -xSizeAddn+7, 6, col);
		fontRenderer.drawString(I18n.format("container.crafting"), -xSizeAddn+7, 108, col);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (hoveredAbility != null && mouseButton == 0) {
			boolean newState = !hoveredAbility.isEnabled(mc.player);
			new MessageSetAbility(hoveredAbility, newState).sendToServer();
			if (newState) {
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2));
			} else {
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_ZOMBIE_INFECT, 2));
			}
			return;
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_DELETE) {
			if (getSlotUnderMouse() != null) {
				new MessageDeleteSlot(getSlotUnderMouse().slotNumber).sendToServer();
			}
			return;
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected boolean hasClickedOutside(int mX, int mY, int x, int y) {
		return mX < x-xSizeAddn || mY < y || mX >= x+xSize || mY >= (mX >= x-xSizeAddn && mX < x ? y+ySizeAddn : y+ySize);
	}
	
}
