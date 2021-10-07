package com.unascribed.notenoughcreativity.client;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Stipple {
	
	private static final ByteBuffer buf = BufferUtils.createByteBuffer(128);
	
	private static Boolean canUseStipple = System.getProperty("notenoughcreativity.forceStipple") == null ? null : Boolean.getBoolean("notenoughcreativity.forceStipple");
	
	private static boolean canUse() {
		if (canUseStipple == null) {
			canUseStipple = !(GL11.glGetString(GL11.GL_VENDOR).contains("NVIDIA"));
		}
		return canUseStipple;
	}
	
	private static void stipple(long[] arr) {
		if (!canUse()) return;
		buf.asLongBuffer().put(arr);
		GL11.glPolygonStipple(buf);
	}
	
	public static void enable() {
		if (!canUse()) return;
		GL11.glEnable(GL11.GL_POLYGON_STIPPLE);
	}
	public static void disable() {
		if (!canUse()) return;
		GL11.glDisable(GL11.GL_POLYGON_STIPPLE);
	}
	
	public static void   grey0() { stipple(GREY_0); }
	public static void   grey5() { stipple(GREY_5); }
	public static void  grey10() { stipple(GREY_10); }
	public static void  grey15() { stipple(GREY_15); }
	public static void  grey20() { stipple(GREY_20); }
	public static void  grey25() { stipple(GREY_25); }
	public static void  grey30() { stipple(GREY_30); }
	public static void  grey35() { stipple(GREY_35); }
	public static void  grey40() { stipple(GREY_40); }
	public static void  grey45() { stipple(GREY_45); }
	public static void  grey50() { stipple(GREY_50); }
	public static void  grey55() { stipple(GREY_55); }
	public static void  grey60() { stipple(GREY_60); }
	public static void  grey65() { stipple(GREY_65); }
	public static void  grey70() { stipple(GREY_70); }
	public static void  grey75() { stipple(GREY_75); }
	public static void  grey80() { stipple(GREY_80); }
	public static void  grey85() { stipple(GREY_85); }
	public static void  grey90() { stipple(GREY_90); }
	public static void  grey95() { stipple(GREY_95); }
	public static void grey100() { stipple(GREY_100); }
	public static void random() {
		ThreadLocalRandom.current().nextBytes(SCRATCH);
		buf.slice().put(SCRATCH);
		GL11.glPolygonStipple(buf);
	}
	
	
	private static final byte[] SCRATCH = new byte[128];
	private static final long[]   GREY_0 = { 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L };
	private static final long[]   GREY_5 = { 0x0000000000000000L, 0x2222222200000000L, 0x0000000000000000L, 0x2020202000000000L, 0x0000000000000000L, 0x2222222200000000L, 0x0000000000000000L, 0x2020202000000000L, 0x0000000000000000L, 0x2222222200000000L, 0x0000000000000000L, 0x2020202000000000L, 0x0000000000000000L, 0x2222222200000000L, 0x0000000000000000L, 0x2020202000000000L };
	private static final long[]  GREY_10 = { 0x8888888800000000L, 0x2020202000000000L, 0x8888888800000000L, 0x2202220200000000L, 0x8888888800000000L, 0x2020202000000000L, 0x8888888800000000L, 0x0202020200000000L, 0x8888888800000000L, 0x2020202000000000L, 0x8888888800000000L, 0x2202220200000000L, 0x8888888800000000L, 0x2020202000000000L, 0x8888888800000000L, 0x0202020200000000L };
	private static final long[]  GREY_15 = { 0x0000000088888888L, 0x00000000a2a2a222L, 0x0000000088888888L, 0x00000000222a222aL, 0x0000000088888888L, 0x00000000a2a2a222L, 0x0000000088888888L, 0x000000002a222a22L, 0x0000000088888888L, 0x00000000a2a2a222L, 0x0000000088888888L, 0x00000000222a222aL, 0x0000000088888888L, 0x00000000a2a2a222L, 0x0000000088888888L, 0x000000002a222a22L };
	private static final long[]  GREY_20 = { 0x0000000055555555L, 0x0000000044444444L, 0x0000000055555555L, 0x0000000044454445L, 0x0000000055555555L, 0x0000000044444444L, 0x0000000055555555L, 0x0000000045454545L, 0x0000000055555555L, 0x0000000044444444L, 0x0000000055555555L, 0x0000000044454445L, 0x0000000055555555L, 0x0000000044444444L, 0x0000000055555555L, 0x0000000045454545L };
	private static final long[]  GREY_25 = { 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L, 0xaaaaaaaa00000000L };
	private static final long[]  GREY_30 = { 0xaaaaaaaa00000000L, 0xaaaaaaaa11111111L, 0xaaaaaaaa00000000L, 0xaaaaaaaa01110111L, 0xaaaaaaaa00000000L, 0xaaaaaaaa11111111L, 0xaaaaaaaa00000000L, 0xaaaaaaaa01010101L, 0xaaaaaaaa00000000L, 0xaaaaaaaa11111111L, 0xaaaaaaaa00000000L, 0xaaaaaaaa01110111L, 0xaaaaaaaa00000000L, 0xaaaaaaaa11111111L, 0xaaaaaaaa00000000L, 0xaaaaaaaa01010101L };
	private static final long[]  GREY_35 = { 0x2222222255555555L, 0x8080808055555555L, 0x2222222255555555L, 0x0888088855555555L, 0x2222222255555555L, 0x8080808055555555L, 0x2222222255555555L, 0x0808080855555555L, 0x2222222255555555L, 0x8080808055555555L, 0x2222222255555555L, 0x0888088855555555L, 0x2222222255555555L, 0x8080808055555555L, 0x2222222255555555L, 0x0808080855555555L };
	private static final long[]  GREY_40 = { 0xaaaaaaaa11111111L, 0xaaaaaaaa54545454L, 0xaaaaaaaa11111111L, 0xaaaaaaaa45444544L, 0xaaaaaaaa11111111L, 0xaaaaaaaa54545454L, 0xaaaaaaaa11111111L, 0xaaaaaaaa44454445L, 0xaaaaaaaa11111111L, 0xaaaaaaaa54545454L, 0xaaaaaaaa11111111L, 0xaaaaaaaa45444544L, 0xaaaaaaaa11111111L, 0xaaaaaaaa54545454L, 0xaaaaaaaa11111111L, 0xaaaaaaaa44454445L };
	private static final long[]  GREY_45 = { 0xaaaaaaaa44444444L, 0xaaaaaaaa55555555L, 0xaaaaaaaa54545454L, 0xaaaaaaaa55555555L, 0xaaaaaaaa44444444L, 0xaaaaaaaa55555555L, 0xaaaaaaaa54445444L, 0xaaaaaaaa55555555L, 0xaaaaaaaa44444444L, 0xaaaaaaaa55555555L, 0xaaaaaaaa54545454L, 0xaaaaaaaa55555555L, 0xaaaaaaaa44444444L, 0xaaaaaaaa55555555L, 0xaaaaaaaa54445444L, 0xaaaaaaaa55555555L };
	private static final long[]  GREY_50 = { 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L, 0xaaaaaaaa55555555L };
	private static final long[]  GREY_55 = { 0x55555555aaaaaaaaL, 0xd5d5d5d5aaaaaaaaL, 0x55555555aaaaaaaaL, 0xddddddddaaaaaaaaL, 0x55555555aaaaaaaaL, 0xd5ddd5ddaaaaaaaaL, 0x55555555aaaaaaaaL, 0xddddddddaaaaaaaaL, 0x55555555aaaaaaaaL, 0xd5d5d5d5aaaaaaaaL, 0x55555555aaaaaaaaL, 0xddddddddaaaaaaaaL, 0x55555555aaaaaaaaL, 0xd5ddd5ddaaaaaaaaL, 0x55555555aaaaaaaaL, 0xddddddddaaaaaaaaL };
	private static final long[]  GREY_60 = { 0x55555555eeeeeeeeL, 0x55555555babbbabbL, 0x55555555eeeeeeeeL, 0x55555555ababababL, 0x55555555eeeeeeeeL, 0x55555555bbbabbbaL, 0x55555555eeeeeeeeL, 0x55555555ababababL, 0x55555555eeeeeeeeL, 0x55555555babbbabbL, 0x55555555eeeeeeeeL, 0x55555555ababababL, 0x55555555eeeeeeeeL, 0x55555555bbbabbbaL, 0x55555555eeeeeeeeL, 0x55555555ababababL };
	private static final long[]  GREY_65 = { 0x55555555bbbbbbbbL, 0x55555555fefefefeL, 0x55555555bbbbbbbbL, 0x55555555efeeefeeL, 0x55555555bbbbbbbbL, 0x55555555fefefefeL, 0x55555555bbbbbbbbL, 0x55555555efefefefL, 0x55555555bbbbbbbbL, 0x55555555fefefefeL, 0x55555555bbbbbbbbL, 0x55555555efeeefeeL, 0x55555555bbbbbbbbL, 0x55555555fefefefeL, 0x55555555bbbbbbbbL, 0x55555555efefefefL };
	private static final long[]  GREY_70 = { 0xffffffff55555555L, 0xeeeeeeee55555555L, 0xffffffff55555555L, 0xfefefefe55555555L, 0xffffffff55555555L, 0xeeeeeeee55555555L, 0xffffffff55555555L, 0xfeeefeee55555555L, 0xffffffff55555555L, 0xeeeeeeee55555555L, 0xffffffff55555555L, 0xfefefefe55555555L, 0xffffffff55555555L, 0xeeeeeeee55555555L, 0xffffffff55555555L, 0xfeeefeee55555555L };
	private static final long[]  GREY_75 = { 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L, 0xffffffff55555555L };
	private static final long[]  GREY_80 = { 0xaaaaaaaaffffffffL, 0xeeaeeeaeffffffffL, 0xaaaaaaaaffffffffL, 0xeeeeeeeeffffffffL, 0xaaaaaaaaffffffffL, 0xaeaeaeaeffffffffL, 0xaaaaaaaaffffffffL, 0xeeeeeeeeffffffffL, 0xaaaaaaaaffffffffL, 0xeeaeeeaeffffffffL, 0xaaaaaaaaffffffffL, 0xeeeeeeeeffffffffL, 0xaaaaaaaaffffffffL, 0xaeaeaeaeffffffffL, 0xaaaaaaaaffffffffL, 0xeeeeeeeeffffffffL };
	private static final long[]  GREY_85 = { 0x75777577ffffffffL, 0xddddddddffffffffL, 0x57575757ffffffffL, 0xddddddddffffffffL, 0x77757775ffffffffL, 0xddddddddffffffffL, 0x57575757ffffffffL, 0xddddddddffffffffL, 0x75777577ffffffffL, 0xddddddddffffffffL, 0x57575757ffffffffL, 0xddddddddffffffffL, 0x77757775ffffffffL, 0xddddddddffffffffL, 0x57575757ffffffffL, 0xddddddddffffffffL };
	private static final long[]  GREY_90 = { 0xfffffffff7f7f7f7L, 0xffffffffddddddddL, 0xffffffff777f777fL, 0xffffffffddddddddL, 0xfffffffff7f7f7f7L, 0xffffffffddddddddL, 0xffffffff7f7f7f7fL, 0xffffffffddddddddL, 0xfffffffff7f7f7f7L, 0xffffffffddddddddL, 0xffffffff777f777fL, 0xffffffffddddddddL, 0xfffffffff7f7f7f7L, 0xffffffffddddddddL, 0xffffffff7f7f7f7fL, 0xffffffffddddddddL };
	private static final long[]  GREY_95 = { 0xffffffffffffffffL, 0xeeeeeeeeffffffffL, 0xffffffffffffffffL, 0xefefefefffffffffL, 0xffffffffffffffffL, 0xeeeeeeeeffffffffL, 0xffffffffffffffffL, 0xefefefefffffffffL, 0xffffffffffffffffL, 0xeeeeeeeeffffffffL, 0xffffffffffffffffL, 0xefefefefffffffffL, 0xffffffffffffffffL, 0xeeeeeeeeffffffffL, 0xffffffffffffffffL, 0xefefefefffffffffL };
	private static final long[] GREY_100 = { 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL };
	
}
