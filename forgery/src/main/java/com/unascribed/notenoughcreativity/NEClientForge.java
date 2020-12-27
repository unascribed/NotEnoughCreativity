package com.unascribed.notenoughcreativity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.MinecraftForge;

public class NEClientForge {

	private static final MethodHandle renderAir = unreflect(ForgeIngameGui.class, new Class<?>[]{int.class, int.class, MatrixStack.class}, "renderAir");
	
	public static void setup() {
		MinecraftForge.EVENT_BUS.addListener((RenderGameOverlayEvent.Post e) -> {
			if (e.getType() == ElementType.BOSSHEALTH) {
				if (AbilityCheck.enabled(Minecraft.getInstance().player, Ability.HEALTH) && !Minecraft.getInstance().playerController.shouldDrawHUD()) {
					GlStateManager.pushMatrix();
					GlStateManager.translatef(0, 6, 0);
					float origHealth = Minecraft.getInstance().player.getHealth();
					if (Minecraft.getInstance().player.getHealth() < 0.2) {
						Minecraft.getInstance().player.setHealth(0);
					}
					((ForgeIngameGui)Minecraft.getInstance().ingameGUI).renderHealth(e.getWindow().getScaledWidth(), e.getWindow().getScaledHeight(), e.getMatrixStack());
					try {
						renderAir.invokeExact((ForgeIngameGui)Minecraft.getInstance().ingameGUI, e.getWindow().getScaledWidth(), e.getWindow().getScaledHeight(), e.getMatrixStack());
					} catch (Throwable t) {
						t.printStackTrace();
					}
					Minecraft.getInstance().player.setHealth(origHealth);
					GlStateManager.popMatrix();
				}
			}
		});
	}
	
	private static MethodHandle unreflect(Class<?> clazz, Class<?>[] parameterTypes, String... names) {
		for (String name : names) {
			try {
				Method m = clazz.getDeclaredMethod(name, parameterTypes);
				m.setAccessible(true);
				return MethodHandles.lookup().unreflect(m);
			} catch (Throwable t) {}
		}
		throw new IllegalArgumentException("Cannot find method "+names[0]);
	}
	
}
