package com.unascribed.notenoughcreativity.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.elytradev.mini.MiniTransformer;
import com.elytradev.mini.PatchContext;
import com.elytradev.mini.annotation.Patch;

@Patch.Class("net.minecraft.client.renderer.ItemRenderer")
public class ItemRendererTransformer extends MiniTransformer {
	
	@Patch.Method(mcp="renderItemInFirstPerson", srg="func_78440_a", descriptor="(F)V")
	public void patchRenderItemInFirstPerson(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/unascribed/notenoughcreativity/NEClient",
				"preRenderItemInFirstPerson", "()V", false));
		ctx.search(new InsnNode(RETURN)).jumpBefore();
		ctx.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/unascribed/notenoughcreativity/NEClient",
				"postRenderItemInFirstPerson", "()V", false));
	}
	
}
