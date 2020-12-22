package com.unascribed.notenoughcreativity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.unascribed.notenoughcreativity.client.GuiCreativePlus;
import com.unascribed.notenoughcreativity.network.MessageSetEnabled;
import com.unascribed.notenoughcreativity.network.MessagePickBlock;
import com.unascribed.notenoughcreativity.network.MessagePickEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class NEClient {

	private static final Field blockHitDelay = ObfuscationReflectionHelper.findField(PlayerControllerMP.class, "field_78781_i");
	private static final Method renderAir = ObfuscationReflectionHelper.findMethod(GuiIngameForge.class, "renderAir", void.class, int.class, int.class);
	
	public static final NEClient INSTANCE = new NEClient();
	
	private static final ResourceLocation SWAP = new ResourceLocation("notenoughcreativity", "textures/gui/swap.png");
	
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onDrawOverlay(RenderGameOverlayEvent.Post e) {
		if (e.getType() == ElementType.BOSSHEALTH) {
			if (Ability.HEALTH.isEnabled(Minecraft.getMinecraft().player) && !Minecraft.getMinecraft().playerController.shouldDrawHUD()) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, 6, 0);
				float origHealth = Minecraft.getMinecraft().player.getHealth();
				if (Minecraft.getMinecraft().player.getHealth() < 0.2) {
					Minecraft.getMinecraft().player.setHealth(0);
				}
				((GuiIngameForge)Minecraft.getMinecraft().ingameGUI).renderHealth(e.getResolution().getScaledWidth(), e.getResolution().getScaledHeight());
				try {
					renderAir.invoke(Minecraft.getMinecraft().ingameGUI, e.getResolution().getScaledWidth(), e.getResolution().getScaledHeight());
				} catch (Throwable t) {
					t.printStackTrace();
				}
				Minecraft.getMinecraft().player.setHealth(origHealth);
				GlStateManager.popMatrix();
			}
		}
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e) {
		if (e.phase == Phase.END) {
			if (Ability.INSTABREAK.isEnabled(Minecraft.getMinecraft().player)) {
				try {
					blockHitDelay.set(Minecraft.getMinecraft().playerController, 0);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onRenderTooltipPre(RenderTooltipEvent.Color e) {
		GlStateManager.pushMatrix();
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if (gui instanceof GuiContainerCreative) {
			GuiContainerCreative gcc = (GuiContainerCreative)gui;
			if (gcc.getSelectedTabIndex() == CreativeTabs.INVENTORY.getTabIndex()) {
				GlStateManager.depthMask(true);
				GlStateManager.enableDepth();
				GlStateManager.depthFunc(GL11.GL_ALWAYS);
				GlStateManager.translate(0, 0, 100);
			}
		}
	}
	
	@SubscribeEvent
	public void onRenderTooltipPost(RenderTooltipEvent.PostText e) {
		GlStateManager.popMatrix();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
	}
	
	@SubscribeEvent
	public void onDisplayGui(GuiOpenEvent e) {
		if (e.getGui() instanceof GuiContainerCreative) {
			EntityPlayerSP p = Minecraft.getMinecraft().player;
			if (p.getEntityData().getBoolean("NotEnoughCreativity")) {
				e.setGui(new GuiCreativePlus(new ContainerCreativePlus(p)));
			}
		}
	}
	
	@SubscribeEvent
	public void onPostRenderGui(GuiScreenEvent.DrawScreenEvent.Post e) {
		if (e.getGui() instanceof GuiContainerCreative) {
			GuiContainerCreative gcc = (GuiContainerCreative)e.getGui();
			if (gcc.getSelectedTabIndex() == CreativeTabs.INVENTORY.getTabIndex()) {
				GlStateManager.pushMatrix();
				GlStateManager.enableDepth();
				GlStateManager.depthFunc(GL11.GL_LEQUAL);
				GlStateManager.disableLighting();
				GlStateManager.color(1, 1, 1);
				GlStateManager.translate(0, 0, 0);
				Minecraft.getMinecraft().renderEngine.bindTexture(SWAP);
				int x = gcc.getGuiLeft()+172;
				int y = gcc.getGuiTop()+89;
				Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 18, 18, 18, 18);
				int mX = e.getMouseX();
				int mY = e.getMouseY();
				if (mX >= x && mX < x+18 &&
						mY >= y && mY < y+18) {
					Gui.drawRect(x+1, y+1, x+17, y+17, 0x80FFFFFF);
					gcc.drawHoveringText(I18n.format("notenoughcreativity.swap"), mX, mY);
				}
				GlStateManager.disableDepth();
				GlStateManager.popMatrix();
			}
		}
	}
	
	@SubscribeEvent
	public void onGuiClick(GuiScreenEvent.MouseInputEvent.Pre e) {
		if (e.getGui() instanceof GuiContainerCreative) {
			GuiContainerCreative gcc = (GuiContainerCreative)e.getGui();
			if (gcc.getSelectedTabIndex() == CreativeTabs.INVENTORY.getTabIndex()) {
				if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0) {
					int mX = Mouse.getEventX() * e.getGui().width / e.getGui().mc.displayWidth;
					int mY = e.getGui().height - Mouse.getEventY() * e.getGui().height / e.getGui().mc.displayHeight - 1;
					int x = gcc.getGuiLeft()+172;
					int y = gcc.getGuiTop()+89;
					if (mX >= x && mX < x+18 &&
							mY >= y && mY < y+18) {
						new MessageSetEnabled(true).sendToServer();
						e.setCanceled(true);
					}
				}
			}
		}
	}
	
	public static void middleClickMouse() {
		if (NotEnoughCreativity.isCreativePlus(Minecraft.getMinecraft().player)) {
			RayTraceResult rtr = Minecraft.getMinecraft().objectMouseOver;
			if (rtr != null) {
				if (rtr.typeOfHit == Type.BLOCK) {
					new MessagePickBlock(rtr.getBlockPos(), (float)rtr.hitVec.x, (float)rtr.hitVec.y, (float)rtr.hitVec.z, rtr.sideHit, GuiScreen.isCtrlKeyDown()).sendToServer();
				} else if (rtr.typeOfHit == Type.ENTITY) {
					new MessagePickEntity(rtr.entityHit.getEntityId(), (float)rtr.hitVec.x, (float)rtr.hitVec.y, (float)rtr.hitVec.z, GuiScreen.isCtrlKeyDown()).sendToServer();
				}
			}
			// prevent default handler from running
			Minecraft.getMinecraft().objectMouseOver = null;
		}
	}
	
}
