package com.unascribed.notenoughcreativity.client;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.ContainerCreativePlus;
import com.unascribed.notenoughcreativity.network.MessageDeleteSlot;
import com.unascribed.notenoughcreativity.network.MessageSetAbility;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiCreativePlus extends ContainerScreen<ContainerCreativePlus> {

	private static final ResourceLocation BG = new ResourceLocation("notenoughcreativity", "textures/gui/inventory.png");
	
	private final List<GuiParticle> particles = Lists.newArrayList();
	
	private final ContainerCreativePlus container;
	
	private int xSizeAddn;
	private int ySizeAddn;
	
	private Ability hoveredAbility;
	
	public GuiCreativePlus(ContainerCreativePlus container) {
		super(container, Minecraft.getInstance().player.inventory, new TranslationTextComponent("notenoughcreativity.title"));
		Minecraft.getInstance().player.openContainer = container;
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
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		
		hoveredAbility = null;
		
		int x = getGuiLeft()+getXSize()-18;
		int y = getGuiTop()+4;
		minecraft.getTextureManager().bindTexture(BG);
		GlStateManager.disableLighting();
		for (Ability a : Ability.values()) {
			fill(matrixStack, x, y, x+11, y+11, 0xFF000000);
			if (a.isEnabled(minecraft.player)) {
				GlStateManager.color4f(1, 1, 0, 1);
			} else {
				GlStateManager.color4f(0.5f, 0.5f, 0.5f, 1);
			}
			blit(matrixStack, x+1, y+1, 251+(a.ordinal()*9), 0, 9, 9, 384, 384);
			if (a.isEnabled(minecraft.player)) {
				GlStateManager.color4f(1, 1, 1, 0.2f);
				ThreadLocalRandom r = ThreadLocalRandom.current();
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.disableAlphaTest();
				for (int i = 0; i < 6; i++) {
					GlStateManager.pushMatrix();
					GlStateManager.translatef(r.nextFloat()-r.nextFloat(), r.nextFloat()-r.nextFloat(), 0);
					blit(matrixStack, x+1, y+1, 251+(a.ordinal()*9), 0, 9, 9, 384, 384);
					GlStateManager.popMatrix();
				}
				GlStateManager.enableAlphaTest();
			}
			if (mouseX >= x && mouseX < x+11 && mouseY >= y && mouseY < y+11) {
				hoveredAbility = a;
			}
			x -= 12;
		}
		
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableAlphaTest();
		for (GuiParticle gp : particles) {
			gp.render(partialTicks);
		}
		GlStateManager.enableTexture();
		GlStateManager.enableAlphaTest();
		if (getSlotUnderMouse() == container.deleteSlot) {
			renderTooltip(matrixStack, Lists.newArrayList(IReorderingProcessor.fromString(I18n.format("inventory.binSlot"), Style.EMPTY)), mouseX, mouseY);
		} else if (getSlotUnderMouse() == container.returnSlot) {
			renderTooltip(matrixStack, Lists.newArrayList(IReorderingProcessor.fromString(I18n.format("notenoughcreativity.exit"), Style.EMPTY)), mouseX, mouseY);
		} else if (hoveredAbility != null) {
			List<IReorderingProcessor> li = Lists.newArrayList(font.trimStringToWidth(ITextProperties.func_240652_a_((hoveredAbility.isEnabled(minecraft.player) ? "§e" : "")+I18n.format("notenoughcreativity.ability."+(hoveredAbility.name().toLowerCase(Locale.ROOT))+".name")), 200));
			li.addAll(font.trimStringToWidth(ITextProperties.func_240652_a_("§7"+I18n.format("notenoughcreativity.ability."+(hoveredAbility.name().toLowerCase(Locale.ROOT))+".desc")), 200));
			renderTooltip(matrixStack, li, mouseX, mouseY);
		} else {
			renderHoveredTooltip(matrixStack, mouseX, mouseY);
		}
	}
	
	@Override
	public void tick() {
		super.tick();
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
		minecraft.getTextureManager().bindTexture(BG);
		GlStateManager.disableLighting();
		for (Ability a : Ability.values()) {
			if (a.isEnabled(minecraft.player) && Math.random() < 0.2) {
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
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		minecraft.textureManager.bindTexture(BG);
		int x = ((width-xSize)/2)-xSizeAddn;
		int y = (height-ySize)/2;
		if (Ability.DARKMODE.isEnabled(minecraft.player)) {
			GlStateManager.color4f(0.2f, 0.2f, 0.3f, 1);
		} else {
			GlStateManager.color4f(1, 1, 1, 1);
		}
		blit(matrixStack, x, y, 0, 0, xSize+xSizeAddn, ySize, 384, 384);
		if (Ability.DARKMODE.isEnabled(minecraft.player)) {
			GlStateManager.color4f(1, 1, 0, 1);
			blit(matrixStack, x+61, y+166, 61, 166, 16, 16, 384, 384);
			GlStateManager.color4f(1, 0.4f, 0.2f, 1);
			blit(matrixStack, x+61, y+188, 61, 188, 16, 16, 384, 384);
		}
		GlStateManager.color4f(1, 1, 1, 1);
		InventoryScreen.drawEntityOnScreen(x+51, y+82, 30, x+51-mouseX, y+30-mouseY, minecraft.player);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		int col = 0x404040;
		if (Ability.DARKMODE.isEnabled(minecraft.player)) {
			col = 0xFFFFFF;
		}
		font.drawString(matrixStack, I18n.format("notenoughcreativity.title"), -xSizeAddn+7, 6, col);
		font.drawString(matrixStack, I18n.format("container.crafting"), -xSizeAddn+7, 108, col);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (hoveredAbility != null && mouseButton == 0) {
			boolean newState = !hoveredAbility.isEnabled(minecraft.player);
			new MessageSetAbility(hoveredAbility, newState).sendToServer();
			if (newState) {
				minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2));
			} else {
				minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.ENTITY_ZOMBIE_INFECT, 2));
			}
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_DELETE) {
			if (getSlotUnderMouse() != null) {
				new MessageDeleteSlot(getSlotUnderMouse().slotNumber).sendToServer();
			}
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	protected boolean hasClickedOutside(double mX, double mY, int x, int y, int button) {
		return mX < x-xSizeAddn || mY < y || mX >= x+xSize || mY >= (mX >= x-xSizeAddn && mX < x ? y+ySizeAddn : y+ySize);
	}
	
}
