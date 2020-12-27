package com.unascribed.notenoughcreativity.mixin;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.notenoughcreativity.network.MessageSetEnabled;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.CreativeScreenHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen extends AbstractInventoryScreen<CreativeScreenHandler> {

	@Unique
	private static final Identifier NEC$SWAP = new Identifier("notenoughcreativity", "textures/gui/swap.png");
	
	public MixinCreativeInventoryScreen(CreativeScreenHandler screenHandler,
			PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Inject(at=@At("HEAD"), method="mouseClicked(DDI)Z", cancellable=true)
	public void mouseClicked(double mX, double mY, int button, CallbackInfoReturnable<Boolean> ci) {
		CreativeInventoryScreen self = (CreativeInventoryScreen)(Object)this;
		if (self.getSelectedTab() == ItemGroup.INVENTORY.getIndex()) {
			if (button == 0) {
				int x = this.x+172;
				int y = this.y+89;
				if (mX >= x && mX < x+18 &&
						mY >= y && mY < y+18) {
					new MessageSetEnabled(true).sendToServer();
					ci.setReturnValue(true);
				}
			}
		}
	}
	
	@Inject(at=@At("TAIL"), method="drawBackground(Lnet/minecraft/client/util/math/MatrixStack;FII)V")
	public void drawBackground(MatrixStack matrices, float partialTicks, int mX, int mY, CallbackInfo ci) {
		CreativeInventoryScreen self = (CreativeInventoryScreen)(Object)this;
		if (self.getSelectedTab() == ItemGroup.INVENTORY.getIndex()) {
			GlStateManager.pushMatrix();
			GlStateManager.enableDepthTest();
			GlStateManager.depthFunc(GL11.GL_LEQUAL);
			GlStateManager.disableLighting();
			GlStateManager.color4f(1, 1, 1, 1);
			GlStateManager.translatef(0, 0, 0);
			client.getTextureManager().bindTexture(NEC$SWAP);
			int x = this.x+172;
			int y = this.y+89;
			DrawableHelper.drawTexture(matrices, x, y, 0, 0, 18, 18, 18, 18);
			GlStateManager.disableDepthTest();
			GlStateManager.popMatrix();
		}
	}
	
	@Inject(at=@At("TAIL"), method="render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V")
	public void render(MatrixStack matrices, int mX, int mY, float partialTicks, CallbackInfo ci) {
		CreativeInventoryScreen self = (CreativeInventoryScreen)(Object)this;
		if (self.getSelectedTab() == ItemGroup.INVENTORY.getIndex()) {
			int x = this.x+172;
			int y = this.y+89;
			if (mX >= x && mX < x+18 &&
					mY >= y && mY < y+18) {
				DrawableHelper.fill(matrices, x+1, y+1, x+17, y+17, 0x80FFFFFF);
				renderTooltip(matrices, new TranslatableText("notenoughcreativity.swap"), mX, mY);
			}
		}
	}
	
}
