package com.unascribed.notenoughcreativity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.unascribed.notenoughcreativity.client.GuiCreativePlus;
import com.unascribed.notenoughcreativity.client.Stipple;
import com.unascribed.notenoughcreativity.network.MessageSetEnabled;

import com.google.common.collect.Maps;

import com.unascribed.notenoughcreativity.network.MessageDeleteSlot;
import com.unascribed.notenoughcreativity.network.MessagePickBlock;
import com.unascribed.notenoughcreativity.network.MessagePickEntity;
import com.unascribed.notenoughcreativity.network.MessageSetAbility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class NEClient {

	private static final Field blockHitDelay = findField(PlayerControllerMP.class, "field_78781_i", "blockHitDelay");
	private static final Method renderAir = findMethod(GuiIngameForge.class, new Class<?>[]{int.class, int.class}, "renderAir");
	
	public static final NEClient INSTANCE = new NEClient();
	
	private static final ResourceLocation SWAP = new ResourceLocation("notenoughcreativity", "textures/gui/swap.png");
	
	public KeyBinding keyDeleteItem;
	public Map<Ability, KeyBinding> abilityKeys;
	
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
		
		keyDeleteItem = new KeyBinding("inventory.binSlot", KeyConflictContext.UNIVERSAL, Keyboard.KEY_DELETE, "Not Enough Creativity");
		ClientRegistry.registerKeyBinding(keyDeleteItem);
		abilityKeys = Maps.newEnumMap(Ability.class);
		for (Ability a : Ability.VALUES) {
			KeyBinding kb = new KeyBinding("key.notenoughcreativity.toggle_ability."+a.name().toLowerCase(Locale.ROOT), Keyboard.KEY_NONE, "Not Enough Creativity");
			abilityKeys.put(a, kb);
			ClientRegistry.registerKeyBinding(kb);
		}
		
		FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() {
			
			@Override
			public String call() throws Exception {
				if (needRestoreGamma) {
					needRestoreGamma = false;
					Minecraft.getMinecraft().gameSettings.gammaSetting = oldGamma;
				}
				return "";
			}
			
			@Override
			public String getLabel() {
				return "";
			}
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
	
	private boolean wasNoclipping = false;
	private boolean needRestoreGamma = false;
	private float oldGamma;
	
	@SubscribeEvent
	public void onRenderTick(RenderTickEvent e) {
		if (e.phase == Phase.START) {
			if (Ability.NIGHTVISION.isEnabled(Minecraft.getMinecraft().player)) {
				needRestoreGamma = true;
				oldGamma = Minecraft.getMinecraft().gameSettings.gammaSetting;
				Minecraft.getMinecraft().gameSettings.gammaSetting = 200;
			}
			if (Ability.NOCLIP.isEnabled(Minecraft.getMinecraft().player)) {
				wasNoclipping = true;
				// Yarn name: "chunkCullingEnabled"
				// MCP, why are you like this?
				Minecraft.getMinecraft().renderChunksMany = false;
			} else if (wasNoclipping) {
				Minecraft.getMinecraft().renderChunksMany = true;
			}
		} else {
			if (needRestoreGamma) {
				needRestoreGamma = false;
				Minecraft.getMinecraft().gameSettings.gammaSetting = oldGamma;
			}
		}
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e) {
		if (e.phase == Phase.END) {
			boolean cp = NotEnoughCreativity.isCreativePlus(Minecraft.getMinecraft().player);
			if (keyDeleteItem.isPressed()) {
				if (cp) {
					new MessageDeleteSlot(82+Minecraft.getMinecraft().player.inventory.currentItem).sendToServer();
				}
			}
			for (Map.Entry<Ability, KeyBinding> en : abilityKeys.entrySet()) {
				if (en.getValue().isPressed()) {
					if (cp) {
						boolean newState = !en.getKey().isEnabled(Minecraft.getMinecraft().player);
						new MessageSetAbility(en.getKey(), newState).sendToServer();
						GuiCreativePlus.playAbilityToggleSound(en.getKey(), newState);
						Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentTranslation("msg.notenoughcreativity.ability_toggle."+newState,
								new TextComponentTranslation("notenoughcreativity.ability."+en.getKey().name().toLowerCase(Locale.ROOT)+".name")), true);
					}
				}
			}
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
	public void onRenderPlayerPre(RenderPlayerEvent.Pre e) {
		if (e.getEntityPlayer().getEntityData().getBoolean("NotEnoughCreativityNoclipping") || Ability.NOCLIP.isEnabled(e.getEntityPlayer())) {
			Stipple.grey30();
			Stipple.enable();
		}
	}
	
	@SubscribeEvent
	public void onRenderPlayerPost(RenderPlayerEvent.Post e) {
		if (e.getEntityPlayer().getEntityData().getBoolean("NotEnoughCreativityNoclipping") || Ability.NOCLIP.isEnabled(e.getEntityPlayer())) {
			Stipple.disable();
		}
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
	
	public static void preRenderItemInFirstPerson() {
		if (Ability.NOCLIP.isEnabled(Minecraft.getMinecraft().player)) {
			Stipple.grey30();
			Stipple.enable();
		}
	}

	public static void postRenderItemInFirstPerson() {
		Stipple.disable();
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
