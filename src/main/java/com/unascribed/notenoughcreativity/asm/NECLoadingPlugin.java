package com.unascribed.notenoughcreativity.asm;

import com.elytradev.mini.MiniCoremod;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.*;

@Name("Not Enough Creativity")
@MCVersion("1.12.2")
@SortingIndex(1001)
@TransformerExclusions({"com.unascribed.notenoughcreativity.asm", "com.elytradev.mini"})
public class NECLoadingPlugin extends MiniCoremod {

	public NECLoadingPlugin() {
		super(MinecraftTransformer.class, ItemRendererTransformer.class);
	}

}
