package com.unascribed.notenoughcreativity.client;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;
import com.unascribed.notenoughcreativity.CPSAccess;
import com.unascribed.notenoughcreativity.CreativePlusScreenHandler;
import com.unascribed.notenoughcreativity.LoaderHandler;
import com.unascribed.notenoughcreativity.NEClient;
import com.unascribed.notenoughcreativity.mixin.AccessorFocusedSlot;
import com.unascribed.notenoughcreativity.mixin.AccessorPlayerScreenHandler;
import com.unascribed.notenoughcreativity.network.MessageDeleteSlot;
import com.unascribed.notenoughcreativity.network.MessageSetAbility;

import com.google.common.collect.Lists;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class CreativePlusScreen extends HandledScreen<CreativePlusScreenHandler> implements CPSAccess {

	private static final Identifier BG = new Identifier("notenoughcreativity", "textures/gui/inventory.png");
	
	public static final Identifier DARK_EMPTY_HELMET_SLOT_TEXTURE = new Identifier("notenoughcreativity", "gui/empty_armor_slot_helmet");
	public static final Identifier DARK_EMPTY_CHESTPLATE_SLOT_TEXTURE = new Identifier("notenoughcreativity", "gui/empty_armor_slot_chestplate");
	public static final Identifier DARK_EMPTY_LEGGINGS_SLOT_TEXTURE = new Identifier("notenoughcreativity", "gui/empty_armor_slot_leggings");
	public static final Identifier DARK_EMPTY_BOOTS_SLOT_TEXTURE = new Identifier("notenoughcreativity", "gui/empty_armor_slot_boots");
	public static final Identifier DARK_EMPTY_OFFHAND_SLOT_TEXTURE = new Identifier("notenoughcreativity", "gui/empty_armor_slot_shield");
	
	public static final Identifier[] ARMOR_TEX = AccessorPlayerScreenHandler.nec$getEmptyArmorSlotTextures().clone();
	public static final Identifier[] DARK_ARMOR_TEX = new Identifier[]{DARK_EMPTY_BOOTS_SLOT_TEXTURE, DARK_EMPTY_LEGGINGS_SLOT_TEXTURE, DARK_EMPTY_CHESTPLATE_SLOT_TEXTURE, DARK_EMPTY_HELMET_SLOT_TEXTURE};
	
	private static final boolean DISABLE_PARTICLES = Boolean.getBoolean("notenoughcreativity.disableGuiParticles");
	
	private final List<GuiParticle> particles = Lists.newArrayList();
	
	private final CreativePlusScreenHandler container;
	
	private int backgroundWidthAddn;
	private int backgroundHeightAddn;
	
	private Ability hoveredAbility;
	
	public CreativePlusScreen(CreativePlusScreenHandler handler) {
		super(handler, MinecraftClient.getInstance().player.inventory, new TranslatableText("notenoughcreativity.title"));
		MinecraftClient.getInstance().player.currentScreenHandler = handler;
		this.container = handler;
		backgroundWidthAddn = 52;
		backgroundHeightAddn = 162;
		backgroundWidth = 199;
		backgroundHeight = 212;
	}
	
	@Override
	protected void init() {
		super.init();
		try {
			String pkg = LoaderHandler.isNotForge() ? "screen" : "gui";
			Class<?> curiosScreen = Class.forName("top.theillusivec4.curios.client."+pkg+".CuriosScreen");
			Pair<Integer, Integer> ofs = (Pair<Integer, Integer>)curiosScreen.getMethod("getButtonOffset", boolean.class).invoke(null, false);
			Class<?> curiosButton = Class.forName("top.theillusivec4.curios.client."+pkg+".CuriosButton");
			// package-private on Forge :(
			Constructor<?> cons = curiosButton.getDeclaredConstructor(HandledScreen.class, int.class, int.class, int.class, int.class,
					int.class, int.class, int.class, Identifier.class);
			cons.setAccessible(true);
			// HandledScreen<?> parentGui, int xIn, int yIn, int widthIn, int heightIn,
			//  int textureOffsetX, int textureOffsetY, int yDiffText, Identifier identifier
			addButton((AbstractButtonWidget)cons.newInstance(this, x+ofs.getLeft()-52, y+ofs.getRight()+93, 14, 14, 50, 0, 14, new Identifier("curios", "textures/gui/inventory.png")));
		} catch (ClassNotFoundException ignore) {
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getBackgroundWidth() {
		return backgroundWidth;
	}
	
	public int getBackgroundHeight() {
		return backgroundHeight;
	}
	
	@Override
	public int getBackgroundWidthAddn() {
		return backgroundWidthAddn;
	}
	
	@Override
	public int getBackgroundHeightAddn() {
		return backgroundHeightAddn;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
		Identifier origHelmet = PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE;
		Identifier origChest = PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE;
		Identifier origLegs = PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE;
		Identifier origBoots = PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE;
		Identifier origOffhand = PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT;
		boolean dark = AbilityCheck.enabled(client.player, Ability.DARKMODE);
		if (dark) {
			PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE = DARK_EMPTY_HELMET_SLOT_TEXTURE;
			PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE = DARK_EMPTY_CHESTPLATE_SLOT_TEXTURE;
			PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE = DARK_EMPTY_LEGGINGS_SLOT_TEXTURE;
			PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE = DARK_EMPTY_BOOTS_SLOT_TEXTURE;
			PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT = DARK_EMPTY_OFFHAND_SLOT_TEXTURE;
			System.arraycopy(DARK_ARMOR_TEX, 0, AccessorPlayerScreenHandler.nec$getEmptyArmorSlotTextures(), 0, DARK_ARMOR_TEX.length);
		}
		try {
			renderBackground(matrices);
			super.render(matrices, mouseX, mouseY, partialTicks);
			hoveredAbility = null;
			
			int x = this.x+backgroundWidth-18;
			int y = this.y+4;
			client.getTextureManager().bindTexture(BG);
			GlStateManager.disableLighting();
			for (Ability a : Ability.VALUES_SORTED) {
				fill(matrices, x, y, x+11, y+11, 0xFF000000);
				if (AbilityCheck.enabled(client.player, a)) {
					GlStateManager.color4f(1, 1, 0, 1);
				} else {
					GlStateManager.color4f(0.5f, 0.5f, 0.5f, 1);
				}
				drawTexture(matrices, x+1, y+1, 251+(a.ordinal()*9), 0, 9, 9, 384, 384);
				if (AbilityCheck.enabled(client.player, a)) {
					GlStateManager.color4f(1, 1, 1, 0.2f);
					ThreadLocalRandom r = ThreadLocalRandom.current();
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GlStateManager.disableAlphaTest();
					for (int i = 0; i < 6; i++) {
						GlStateManager.pushMatrix();
						GlStateManager.translatef(r.nextFloat()-r.nextFloat(), r.nextFloat()-r.nextFloat(), 0);
						drawTexture(matrices, x+1, y+1, 251+(a.ordinal()*9), 0, 9, 9, 384, 384);
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
			Slot foc = ((AccessorFocusedSlot)this).nec$getFocusedSlot();
			if (foc == container.deleteSlot) {
				renderTooltip(matrices, new TranslatableText("inventory.binSlot"), mouseX, mouseY);
			} else if (foc == container.returnSlot) {
				renderTooltip(matrices, new TranslatableText("notenoughcreativity.exit"), mouseX, mouseY);
			} else if (hoveredAbility != null) {
				List<OrderedText> li = Lists.newArrayList(textRenderer.wrapLines(StringVisitable.plain((AbilityCheck.enabled(client.player, hoveredAbility) ? "§e" : "")+I18n.translate("notenoughcreativity.ability."+(hoveredAbility.name().toLowerCase(Locale.ROOT))+".name")), 200));
				li.addAll(textRenderer.wrapLines(StringVisitable.plain("§7"+I18n.translate("notenoughcreativity.ability."+(hoveredAbility.name().toLowerCase(Locale.ROOT))+".desc")), 200));
				renderOrderedTooltip(matrices, li, mouseX, mouseY);
			} else {
				drawMouseoverTooltip(matrices, mouseX, mouseY);
			}
		} finally {
			if (dark) {
				PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE = origHelmet;
				PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE = origChest;
				PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE = origLegs;
				PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE = origBoots;
				PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT = origOffhand;
				System.arraycopy(ARMOR_TEX, 0, AccessorPlayerScreenHandler.nec$getEmptyArmorSlotTextures(), 0, ARMOR_TEX.length);
			}
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
		int x = this.x+backgroundWidth-18;
		int y = this.y+4;
		if (DISABLE_PARTICLES) return;
		for (Ability a : Ability.VALUES_SORTED) {
			if (AbilityCheck.enabled(client.player, a) && Math.random() < 0.2) {
				GuiParticle gp = new GuiParticle(x+2+(Math.random()*7), y+2+(Math.random()*7));
				gp.color = 0xFFFFFF55;
				gp.motionX = (Math.random()-0.5)/2;
				gp.motionY = -1;
				gp.gravity = 0;
				gp.maxAge = 100;
				particles.add(gp);
			}
			x -= 12;
		}
	}
	
	@Override
	protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
		client.getTextureManager().bindTexture(BG);
		int x = ((width-backgroundWidth)/2)-backgroundWidthAddn;
		int y = (height-backgroundHeight)/2;
		if (AbilityCheck.enabled(client.player, Ability.DARKMODE)) {
			GlStateManager.color4f(0.2f, 0.2f, 0.3f, 1);
		} else {
			GlStateManager.color4f(1, 1, 1, 1);
		}
		drawTexture(matrices, x, y, 0, 0, backgroundWidth+backgroundWidthAddn, backgroundHeight, 384, 384);
		if (AbilityCheck.enabled(client.player, Ability.DARKMODE)) {
			GlStateManager.color4f(1, 1, 0, 1);
			drawTexture(matrices, x+61, y+166, 61, 166, 16, 16, 384, 384);
			GlStateManager.color4f(1, 0.4f, 0.2f, 1);
			drawTexture(matrices, x+61, y+188, 61, 188, 16, 16, 384, 384);
		}
		GlStateManager.color4f(1, 1, 1, 1);
		InventoryScreen.drawEntity(x+51, y+82, 30, x+51-mouseX, y+30-mouseY, client.player);
	}
	
	public int getSlotHoverColor() {
		return AbilityCheck.enabled(client.player, Ability.DARKMODE) ? 0x40FFFFFF : 0x80FFFFFF;
	}
	
	@Override
	protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
		int col = 0x192022;
		if (AbilityCheck.enabled(client.player, Ability.DARKMODE)) {
			col = 0xAAAADD;
		}
		textRenderer.draw(matrixStack, I18n.translate("notenoughcreativity.title"), -backgroundWidthAddn+7, 6, col);
		textRenderer.draw(matrixStack, I18n.translate("container.crafting"), -backgroundWidthAddn+7, 108, col);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (hoveredAbility != null && mouseButton == 0) {
			boolean newState = !AbilityCheck.enabled(client.player, hoveredAbility);
			new MessageSetAbility(hoveredAbility, newState).sendToServer();
			playAbilityToggleSound(hoveredAbility, newState);
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public static void playAbilityToggleSound(Ability a, boolean newState) {
		SoundEvent sound = SoundEvents.ENTITY_GHAST_SCREAM;
		switch (a) {
			case NOPICKUP: sound = SoundEvents.BLOCK_NOTE_BLOCK_SNARE; break;
			case ATTACK: sound = SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE; break;
			case HEALTH: sound = SoundEvents.BLOCK_NOTE_BLOCK_HAT; break;
			case INSTABREAK: sound = SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM; break;
			case NIGHTVISION: sound = SoundEvents.BLOCK_END_PORTAL_FRAME_FILL; break;
			case NOCLIP: sound = SoundEvents.ENTITY_EVOKER_CAST_SPELL; break;
			case DARKMODE: sound = SoundEvents.BLOCK_NOTE_BLOCK_BELL; break;
			case LONGREACH: sound = SoundEvents.BLOCK_NOTE_BLOCK_FLUTE; break;
			case FREE_FLIGHT: sound = SoundEvents.BLOCK_NOTE_BLOCK_PLING; break;
			case SUPER_SPEED: sound = SoundEvents.BLOCK_NOTE_BLOCK_BANJO; break;
		}
		float mod = 1;
		if (a == Ability.NOCLIP) {
			mod = 1.5f;
		}
		MinecraftClient mc = MinecraftClient.getInstance();
		if (newState) {
			mc.getSoundManager().play(PositionedSoundInstance.master(sound, 1.1f*mod, 0.5f));
		} else {
			mc.getSoundManager().play(PositionedSoundInstance.master(sound, 0.7f*mod, 0.5f));
		}
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (NEClient.INSTANCE.keyDeleteItem.matchesKey(keyCode, scanCode)) {
			Slot foc = ((AccessorFocusedSlot)this).nec$getFocusedSlot();
			if (foc != null) {
				new MessageDeleteSlot(foc.id).sendToServer();
			}
			return true;
		}
		for (Map.Entry<Ability, KeyBinding> en : NEClient.INSTANCE.abilityKeys.entrySet()) {
			if (en.getValue().matchesKey(keyCode, scanCode)) {
				hoveredAbility = en.getKey();
				mouseClicked(0, 0, 0);
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	protected boolean isClickOutsideBounds(double mX, double mY, int x, int y, int button) {
		return mX < x-backgroundWidthAddn || mY < y || mX >= x+backgroundWidth || mY >= (mX >= x-backgroundWidthAddn && mX < x ? y+backgroundHeightAddn : y+backgroundHeight);
	}

}
