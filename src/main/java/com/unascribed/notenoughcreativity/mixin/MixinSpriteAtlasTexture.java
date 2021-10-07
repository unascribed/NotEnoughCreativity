package com.unascribed.notenoughcreativity.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.unascribed.notenoughcreativity.client.CreativePlusScreen;

import com.google.common.collect.Sets;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

@Mixin(SpriteAtlasTexture.class)
public class MixinSpriteAtlasTexture {

	@Shadow @Final
	private Identifier id;
	
	@ModifyArg(at=@At(value="INVOKE", target="net/minecraft/client/texture/SpriteAtlasTexture.loadSprites(Lnet/minecraft/resource/ResourceManager;Ljava/util/Set;)Ljava/util/Collection;"),
			method="stitch(Lnet/minecraft/resource/ResourceManager;Ljava/util/stream/Stream;Lnet/minecraft/util/profiler/Profiler;I)Lnet/minecraft/client/texture/SpriteAtlasTexture$Data;")
	public Set<Identifier> modifySprites(Set<Identifier> orig) {
		if (SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE.equals(id)) {
			Set<Identifier> copy = Sets.newHashSet(orig);
			copy.add(CreativePlusScreen.DARK_EMPTY_HELMET_SLOT_TEXTURE);
			copy.add(CreativePlusScreen.DARK_EMPTY_CHESTPLATE_SLOT_TEXTURE);
			copy.add(CreativePlusScreen.DARK_EMPTY_LEGGINGS_SLOT_TEXTURE);
			copy.add(CreativePlusScreen.DARK_EMPTY_BOOTS_SLOT_TEXTURE);
			copy.add(CreativePlusScreen.DARK_EMPTY_OFFHAND_SLOT_TEXTURE);
			return copy;
		}
		return orig;
	}
	
}
