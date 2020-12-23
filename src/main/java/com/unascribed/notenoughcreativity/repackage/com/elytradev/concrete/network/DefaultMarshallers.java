/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2018:
 * 	Una Thompson (unascribed),
 * 	Isaac Ellingson (Falkreon),
 * 	Jamie Mansfield (jamierocks),
 * 	Alex Ponebshek (capitalthree),
 * 	and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.exception.BadMessageException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

/**
 * A set of default marshallers for common data types.
 * <p>
 * Any marshaller can be used for lists by putting "-list" after its name.
 */
public final class DefaultMarshallers {
	
	/**
	 * Unsigned 8-bit (1 byte) integer.
	 * <p>
	 * Aliases: u8, uint8, ubyte
	 */
	public static final Marshaller<? extends Number> UINT8 = weld(Number::intValue, PacketBuffer::writeByte, PacketBuffer::readUnsignedByte);
	/**
	 * Signed 8-bit (1 byte) integer.
	 * <p>
	 * Aliases: i8, int8, byte
	 */
	public static final Marshaller<? extends Number> INT8 = weld(Number::intValue, PacketBuffer::writeByte, PacketBuffer::readByte);
	
	
	/**
	 * Unsigned 16-bit (2 byte) integer.
	 * <p>
	 * Aliases: u16, uint16, ushort
	 */
	public static final Marshaller<? extends Number> UINT16 = weld(Number::intValue, PacketBuffer::writeShort, PacketBuffer::readUnsignedShort);
	/**
	 * 16-bit (2 byte) character.
	 * <p>
	 * Aliases: char
	 */
	public static final Marshaller<Character> CHAR = new CharacterMarshaller();
	/**
	 * Signed 16-bit (2 byte) integer.
	 * <p>
	 * Aliases: i16, int16, short
	 */
	public static final Marshaller<? extends Number> INT16 = weld(Number::intValue, PacketBuffer::writeShort, PacketBuffer::readShort);
	
	
	/**
	 * Unsigned 24-bit (3 byte) integer.
	 * <p>
	 * Aliases: u24, uint24, umedium
	 */
	public static final Marshaller<? extends Number> UINT24 = weld(Number::intValue, PacketBuffer::writeMedium, PacketBuffer::readUnsignedMedium);
	/**
	 * Signed 24-bit (3 byte) integer.
	 * <p>
	 * Aliases: i24, int24, medium
	 */
	public static final Marshaller<? extends Number> INT24 = weld(Number::intValue, PacketBuffer::writeMedium, PacketBuffer::readMedium);
	
	
	/**
	 * Unsigned 32-bit (4 byte) integer.
	 * <p>
	 * Aliases: u32, uint32, uint, uinteger
	 */
	public static final Marshaller<? extends Number> UINT32 = weld(Number::intValue, PacketBuffer::writeInt, PacketBuffer::readUnsignedInt);
	/**
	 * Signed 32-bit (4 byte) integer.
	 * <p>
	 * Aliases: i32, int32, int, integer
	 */
	public static final Marshaller<? extends Number> INT32 = weld(Number::intValue, PacketBuffer::writeInt, PacketBuffer::readInt);
	
	
	/**
	 * Signed 64-bit (8 byte) integer.
	 * <p>
	 * Aliases: i64, int64, long
	 */
	public static final Marshaller<? extends Number> INT64 = weld(Number::longValue, PacketBuffer::writeLong, PacketBuffer::readLong);
	
	
	/**
	 * 32-bit floating point.
	 * <p>
	 * Aliases: f32, float
	 */
	public static final Marshaller<? extends Number> FLOAT = weld(Number::floatValue, PacketBuffer::writeFloat, PacketBuffer::readFloat);
	
	
	/**
	 * 64-bit floating point.
	 * <p>
	 * Aliases: f64, double
	 */
	public static final Marshaller<? extends Number> DOUBLE = weld(Number::doubleValue, PacketBuffer::writeDouble, PacketBuffer::readDouble);
	
	
	/**
	 * Protobuf variable sized integer. Not efficient for negative numbers.
	 * <p>
	 * Aliases: varint
	 */
	public static final Marshaller<? extends Number> VARINT = new VarIntMarshaller();
	
	
	/**
	 * Compound NBT tag.
	 */
	public static final Marshaller<CompoundNBT> NBT = weld(PacketBuffer::writeCompoundTag, PacketBuffer::readCompoundTag);
	
	
	/**
	 * 64-bit packed BlockPos.
	 */
	public static final Marshaller<BlockPos> BLOCKPOS = new BlockPosMarshaller();
	
	
	/**
	 * UTF-8 varint-length-prefixed string.
	 */
	public static final Marshaller<String> STRING = weld(PacketBuffer::writeString, pb -> pb.readString(32767));
	
	/**
	 * Packed ItemStack.
	 */
	public static final Marshaller<ItemStack> ITEMSTACK = weld(PacketBuffer::writeItemStack, PacketBuffer::readItemStack);
	
	
	/**
	 * A bytebuf, only writes the readable bytes.
	 */
	public static final Marshaller<? extends ByteBuf> BYTEBUF = new ByteBufMarshaller();
	
	
	
	private static final Map<String, Marshaller<?>> byName = Maps.newHashMap();
	
	
	static {
		put(UINT8, "u8", "uint8", "ubyte");
		put(INT8, "i8", "int8", "byte");
		
		put(UINT16, "u16", "uint16", "ushort");
		put(CHAR, "char");
		put(INT16, "i16", "int16", "short");
		
		put(UINT24, "u24", "uint24", "umedium");
		put(INT24, "i24", "int24", "medium");
		
		put(UINT32, "u32", "uint32", "uint", "uinteger");
		put(INT32, "i32", "int32", "int", "integer");
		
		put(INT64, "i64", "int64", "long");
		
		put(FLOAT, "f32", "float");
		put(DOUBLE, "f64", "double");
		
		put(VARINT, "varint");
		
		put(NBT, "nbt");
		
		put(BLOCKPOS, "blockpos");
		
		put(STRING, "string", "str", "utf8");
		
		put(ITEMSTACK, "item", "stack", "itemstack");
	}
	
	private static void put(Marshaller<?> m, String... names) {
		for (String name : names) {
			byName.put(name, m);
		}
	}
	
	
	
	public static class ListMarshaller<T> implements Marshaller<List<T>> {
		private final Marshaller<T> underlying;
		
		public ListMarshaller(Marshaller<T> underlying) {
			this.underlying = underlying;
		}
		
		@Override
		public List<T> unmarshal(PacketBuffer in) {
			int size = in.readVarInt();
			List<T> li = Lists.newArrayListWithCapacity(size);
			for (int i = 0; i < size; i++) {
				li.add(underlying.unmarshal(in));
			}
			return li;
		}

		@Override
		public void marshal(PacketBuffer out, List<T> li) {
			if (li == null) {
				out.writeVarInt(0);
			} else {
				out.writeVarInt(li.size());
				for (T t : li) {
					underlying.marshal(out, t);
				}
			}
		}
		
	}
	
	private static class BlockPosMarshaller implements Marshaller<BlockPos> {

		@Override
		public BlockPos unmarshal(PacketBuffer in) {
			return BlockPos.fromLong(in.readLong());
		}

		@Override
		public void marshal(PacketBuffer out, BlockPos t) {
			out.writeLong(t.toLong());
		}

	}
	
	private static class VarIntMarshaller implements Marshaller<Number> {

		@Override
		public Number unmarshal(PacketBuffer in) {
			return in.readVarInt();
		}

		@Override
		public void marshal(PacketBuffer out, Number t) {
			out.writeVarInt(t.intValue());
		}

	}
	
	private static class EnumMarshaller<T extends Enum<T>> implements Marshaller<T> {
		private final Class<T> clazz;
		private final T[] constants;
		
		public EnumMarshaller(Class<T> clazz) {
			this.clazz = clazz;
			this.constants = clazz.getEnumConstants();
		}
		
		@Override
		public T unmarshal(PacketBuffer in) {
			int ordinal;
			if (constants.length < 256) {
				ordinal = in.readUnsignedByte();
			} else if (constants.length < 65536) {
				ordinal = in.readUnsignedShort();
			} else if (constants.length < 16777216) {
				ordinal = in.readUnsignedMedium();
			} else {
				ordinal = Ints.checkedCast(in.readUnsignedInt());
			}
			return constants[ordinal];
		}

		@Override
		public void marshal(PacketBuffer out, T t) {
			if (constants.length < 256) {
				out.writeByte(t.ordinal());
			} else if (constants.length < 65536) {
				out.writeShort(t.ordinal());
			} else if (constants.length < 16777216) {
				out.writeMedium(t.ordinal());
			} else {
				out.writeInt(t.ordinal());
			}
		}
		
	}
	
	private static class ByteBufMarshaller implements Marshaller<ByteBuf> {
		
		@Override
		public ByteBuf unmarshal(PacketBuffer in) {
			int length = in.readVarInt();
			
			return in.readBytes(length);
		}
		
		@Override
		public void marshal(PacketBuffer out, ByteBuf t) {
			if (t != null) {
				out.writeVarInt(t.readableBytes());
				out.writeBytes(t.readBytes(t.readableBytes()));
			} else {
				out.writeVarInt(0);
			}
		}
		
	}
	
	private static class CharacterMarshaller implements Marshaller<Character> {
		
		@Override
		public Character unmarshal(PacketBuffer in) {
			return in.readChar();
		}
		
		@Override
		public void marshal(PacketBuffer out, Character t) {
			out.writeChar(t);
		}
		
	}
	
	
	private static <F, R extends Number> Marshaller<? extends Number> weld(Function<Number, F> converter, Serializer<F> serializer, Deserializer<R> deserializer) {
		return new Marshaller<Number>() {
			@Override
			public void marshal(PacketBuffer out, Number number) {
				serializer.serialize(out, converter.apply(number));
			}
			@Override
			public R unmarshal(PacketBuffer in) {
				return deserializer.deserialize(in);
			}
		};
	}
	
	private static <T> Marshaller<T> weld(Serializer<T> serializer, Deserializer<T> deserializer) {
		return new Marshaller<T>() {
			@Override
			public void marshal(PacketBuffer out, T t) {
				serializer.serialize(out, t);
			}
			@Override
			public T unmarshal(PacketBuffer in) {
				return deserializer.deserialize(in);
			}
		};
	}
	
	private interface Serializer<T> {
		void serialize(PacketBuffer out, T t);
	}
	private interface Deserializer<T> {
		T deserialize(PacketBuffer in);
	}
	
	public static <T> Marshaller<T> getByName(String name) {
		if (name.endsWith("-list")) {
			name = name.substring(0, name.length() - 5);
			// lists of lists!
			Marshaller<T> m = getByName(name);
			if (m != null) {
				return new ListMarshaller(m);
			} else {
				return null;
			}
		} else {
			if (byName.containsKey(name.toLowerCase(Locale.ROOT))) {
				return (Marshaller<T>) byName.get(name.toLowerCase(Locale.ROOT));
			} else {
				Marshaller<T> marshaller = null;
				try {
					Class<?> clazz = Class.forName(name);
					if (Marshaller.class.isAssignableFrom(clazz)) {
						try {
							Field inst = clazz.getDeclaredField("INSTANCE");
							inst.setAccessible(true);
							marshaller = (Marshaller<T>) inst.get(null);
						} catch (Exception e) {
						}
						if (marshaller == null) {
							try {
								Constructor<?> cons = clazz.getConstructor();
								marshaller = (Marshaller<T>) cons.newInstance();
							} catch (Exception e) {
								throw new BadMessageException("Cannot instanciate marshaller class " + clazz.getName());
							}
						}
					}
				} catch (Exception e) {
					return null;
				}
				return marshaller;
			}
		}
	}

	public static <T> Marshaller<T> getByType(Class<T> type) {
		if (char.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type)) {
			return (Marshaller<T>) CHAR;
		} else if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
			return (Marshaller<T>) FLOAT;
		} else if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
			return (Marshaller<T>) DOUBLE;
		} else if (String.class.isAssignableFrom(type)) {
			return (Marshaller<T>) STRING;
		} else if (BlockPos.class.isAssignableFrom(type)) {
			return (Marshaller<T>) BLOCKPOS;
		} else if (CompoundNBT.class.isAssignableFrom(type)) {
			return (Marshaller<T>) NBT;
		} else if (type.isEnum()) {
			return new EnumMarshaller(type);
		} else if (ItemStack.class.isAssignableFrom(type)) {
			return (Marshaller<T>) ITEMSTACK;
		} else if (ByteBuf.class.isAssignableFrom(type)) {
			return (Marshaller<T>) BYTEBUF;
		}
		return null;
	}
	
	private DefaultMarshallers() {}
}
