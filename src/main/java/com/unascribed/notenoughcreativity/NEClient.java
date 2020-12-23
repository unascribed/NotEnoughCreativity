package com.unascribed.notenoughcreativity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.notenoughcreativity.client.GuiCreativePlus;
import com.unascribed.notenoughcreativity.network.MessageSetEnabled;
import com.unascribed.notenoughcreativity.network.MessagePickBlock;
import com.unascribed.notenoughcreativity.network.MessagePickEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent.ClickInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.CrashReportExtender;

public class NEClient {

	private static final Field blockHitDelay = findField(PlayerController.class, "field_78781_i", "blockHitDelay");
	private static final Method renderAir = findMethod(ForgeIngameGui.class, new Class<?>[]{int.class, int.class, MatrixStack.class}, "renderAir");
	
	public static final NEClient INSTANCE = new NEClient();
	
	private static final ResourceLocation SWAP = new ResourceLocation("notenoughcreativity", "textures/gui/swap.png");
	
	public static void setup() {
		INSTANCE.setupInst();
	}
	
	public void setupInst() {
		MinecraftForge.EVENT_BUS.addListener(this::onClick);
		MinecraftForge.EVENT_BUS.addListener(this::onRenderTick);
		MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
		MinecraftForge.EVENT_BUS.addListener(this::onDisplayGui);
		MinecraftForge.EVENT_BUS.addListener(this::onDrawOverlay);
		MinecraftForge.EVENT_BUS.addListener(this::onGuiClick);
		MinecraftForge.EVENT_BUS.addListener(this::onPostRenderGui);
		MinecraftForge.EVENT_BUS.addListener(this::onRenderTooltipPost);
		MinecraftForge.EVENT_BUS.addListener(this::onRenderTooltipPre);
		CrashReportExtender.registerCrashCallable("", () -> {
			if (needRestoreGamma) {
				needRestoreGamma = false;
				Minecraft.getInstance().gameSettings.gamma = oldGamma;
			}
			return "";
		});
	}
	
	private static Field findField(Class<?> clazz, String... names) {
		for (String name : names) {
			try {
				Field f = clazz.getDeclaredField(name);
				f.setAccessible(true);
				return f;
			} catch (Throwable t) {}
		}
		throw new IllegalArgumentException("Cannot find field "+names[0]);
	}
	
	private static Method findMethod(Class<?> clazz, Class<?>[] parameterTypes, String... names) {
		for (String name : names) {
			try {
				Method m = clazz.getDeclaredMethod(name, parameterTypes);
				m.setAccessible(true);
				return m;
			} catch (Throwable t) {}
		}
		throw new IllegalArgumentException("Cannot find method "+names[0]);
	}

	public void onDrawOverlay(RenderGameOverlayEvent.Post e) {
		if (e.getType() == ElementType.BOSSHEALTH) {
			if (Ability.HEALTH.isEnabled(Minecraft.getInstance().player) && !Minecraft.getInstance().playerController.shouldDrawHUD()) {
				GlStateManager.pushMatrix();
				GlStateManager.translatef(0, 6, 0);
				float origHealth = Minecraft.getInstance().player.getHealth();
				if (Minecraft.getInstance().player.getHealth() < 0.2) {
					Minecraft.getInstance().player.setHealth(0);
				}
				((ForgeIngameGui)Minecraft.getInstance().ingameGUI).renderHealth(e.getWindow().getScaledWidth(), e.getWindow().getScaledHeight(), e.getMatrixStack());
				try {
					renderAir.invoke(Minecraft.getInstance().ingameGUI, e.getWindow().getScaledWidth(), e.getWindow().getScaledHeight(), e.getMatrixStack());
				} catch (Throwable t) {
					t.printStackTrace();
				}
				Minecraft.getInstance().player.setHealth(origHealth);
				GlStateManager.popMatrix();
			}
		}
	}
	
	private boolean needRestoreGamma = false;
	private double oldGamma;
	
	public void onRenderTick(RenderTickEvent e) {
		if (e.phase == Phase.START) {
			if (Ability.NIGHTVISION.isEnabled(Minecraft.getInstance().player)) {
				needRestoreGamma = true;
				oldGamma = Minecraft.getInstance().gameSettings.gamma;
				Minecraft.getInstance().gameSettings.gamma = 200;
			}
		} else if (needRestoreGamma) {
			needRestoreGamma = false;
			Minecraft.getInstance().gameSettings.gamma = oldGamma;
		}
	}
	
	public void onClientTick(ClientTickEvent e) {
		if (e.phase == Phase.END) {
			if (Ability.INSTABREAK.isEnabled(Minecraft.getInstance().player)) {
				try {
					blockHitDelay.set(Minecraft.getInstance().playerController, 0);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
	
	public void onRenderTooltipPre(RenderTooltipEvent.Color e) {
		GlStateManager.pushMatrix();
		Screen gui = Minecraft.getInstance().currentScreen;
		if (gui instanceof CreativeScreen) {
			CreativeScreen gcc = (CreativeScreen)gui;
			if (gcc.getSelectedTabIndex() == ItemGroup.INVENTORY.getIndex()) {
				GlStateManager.depthMask(true);
				GlStateManager.enableDepthTest();
				GlStateManager.depthFunc(GL11.GL_ALWAYS);
				GlStateManager.translatef(0, 0, 100);
			}
		}
	}
	
	public void onRenderTooltipPost(RenderTooltipEvent.PostText e) {
		GlStateManager.popMatrix();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
	}
	
	public void onDisplayGui(GuiOpenEvent e) {
		if (e.getGui() instanceof CreativeScreen) {
			ClientPlayerEntity p = Minecraft.getInstance().player;
			if (NotEnoughCreativity.isCreativePlus(p)) {
				e.setGui(new GuiCreativePlus(new ContainerCreativePlus(p)));
			}
		}
	}
	
	public void onPostRenderGui(GuiScreenEvent.DrawScreenEvent.Post e) {
		if (e.getGui() instanceof CreativeScreen) {
			CreativeScreen gcc = (CreativeScreen)e.getGui();
			if (gcc.getSelectedTabIndex() == ItemGroup.INVENTORY.getIndex()) {
				GlStateManager.pushMatrix();
				GlStateManager.enableDepthTest();
				GlStateManager.depthFunc(GL11.GL_LEQUAL);
				GlStateManager.disableLighting();
				GlStateManager.color4f(1, 1, 1, 1);
				GlStateManager.translatef(0, 0, 0);
				Minecraft.getInstance().getTextureManager().bindTexture(SWAP);
				int x = gcc.getGuiLeft()+172;
				int y = gcc.getGuiTop()+89;
				AbstractGui.blit(e.getMatrixStack(), x, y, 0, 0, 18, 18, 18, 18);
				int mX = e.getMouseX();
				int mY = e.getMouseY();
				if (mX >= x && mX < x+18 &&
						mY >= y && mY < y+18) {
					AbstractGui.fill(e.getMatrixStack(), x+1, y+1, x+17, y+17, 0x80FFFFFF);
					gcc.renderTooltip(e.getMatrixStack(), new TranslationTextComponent("notenoughcreativity.swap"), mX, mY);
				}
				GlStateManager.disableDepthTest();
				GlStateManager.popMatrix();
			}
		}
	}
	
	public void onGuiClick(GuiScreenEvent.MouseClickedEvent e) {
		if (e.getGui() instanceof CreativeScreen) {
			CreativeScreen gcc = (CreativeScreen)e.getGui();
			if (gcc.getSelectedTabIndex() == ItemGroup.INVENTORY.getIndex()) {
				if (e.getButton() == 0) {
					double mX = e.getMouseX();
					double mY = e.getMouseY();
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
	
	public void onClick(ClickInputEvent e) {
		if (e.getKeyBinding() == Minecraft.getInstance().gameSettings.keyBindPickBlock) {
			if (NotEnoughCreativity.isCreativePlus(Minecraft.getInstance().player)) {
				RayTraceResult rtr = Minecraft.getInstance().objectMouseOver;
				if (rtr != null) {
					if (rtr instanceof BlockRayTraceResult) {
						new MessagePickBlock(((BlockRayTraceResult)rtr).getPos(), (float)rtr.getHitVec().x, (float)rtr.getHitVec().y, (float)rtr.getHitVec().z, ((BlockRayTraceResult) rtr).getFace(), Screen.hasControlDown()).sendToServer();
					} else if (rtr instanceof EntityRayTraceResult) {
						new MessagePickEntity(((EntityRayTraceResult)rtr).getEntity().getEntityId(), (float)rtr.getHitVec().x, (float)rtr.getHitVec().y, (float)rtr.getHitVec().z, Screen.hasControlDown()).sendToServer();
					}
				}
				e.setCanceled(true);
			}
		}
	}
	
}
