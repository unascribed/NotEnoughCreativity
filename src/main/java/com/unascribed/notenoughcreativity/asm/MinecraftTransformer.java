package com.unascribed.notenoughcreativity.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;

import com.elytradev.mini.MiniTransformer;
import com.elytradev.mini.PatchContext;
import com.elytradev.mini.annotation.Patch;

@Patch.Class("net.minecraft.client.Minecraft")
public class MinecraftTransformer extends MiniTransformer {

	@Patch.Method(mcp="middleClickMouse", srg="func_147112_ai", descriptor="()V")
	public void patchMiddleClickMouse(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/unascribed/notenoughcreativity/NEClient",
				"middleClickMouse", "()V", false));
	}
	
}
