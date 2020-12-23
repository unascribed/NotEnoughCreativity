package com.unascribed.notenoughcreativity;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class DummyTextureAtlasSprite extends TextureAtlasSprite {

	public static final DummyTextureAtlasSprite INSTANCE = new DummyTextureAtlasSprite();
	
	private DummyTextureAtlasSprite() {
		super("notenoughcreativity:dummy");
		setIconWidth(16);
		setIconHeight(16);
		initSprite(16, 16, 0, 0, false);
	}

}
