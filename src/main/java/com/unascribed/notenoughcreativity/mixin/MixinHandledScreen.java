package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.unascribed.notenoughcreativity.client.CreativePlusScreen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;

@Mixin(HandledScreen.class)
public class MixinHandledScreen {

	@ModifyConstant(method="render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
			constant=@Constant(intValue=-2130706433), expect=2, require=0)
	public int getSlotHoverColor(int orig) {
		Object self = this;
		if (self instanceof CreativePlusScreen) {
			return ((CreativePlusScreen)self).getSlotHoverColor();
		}
		return orig;
	}
	
}
