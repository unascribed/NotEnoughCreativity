package com.unascribed.notenoughcreativity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class PickBlockHandler {

	public static void pickBlock(PlayerEntity player, RayTraceResult target, boolean exact) {
		World world = player.world;
		
		ItemStack result;
		TileEntity te = null;

		if (target.getType() == RayTraceResult.Type.BLOCK) {
			BlockPos pos = ((BlockRayTraceResult)target).getPos();
			BlockState state = world.getBlockState(pos);

			if (state.getBlock().isAir(state, world, pos)) {
				return;
			}

			if (exact && state.getBlock().hasTileEntity(state)) {
				te = world.getTileEntity(pos);
			}

			result = state.getBlock().getPickBlock(state, target, world, pos, player);
		} else {
			if (target.getType() != RayTraceResult.Type.ENTITY) {
				return;
			}

			try {
				result = ((EntityRayTraceResult)target).getEntity().getPickedResult(target);
			} catch (NoSuchMethodError er) {
				if (er.getMessage().contains("SpawnEggItem")) {
					Entity e = ((EntityRayTraceResult)target).getEntity();
					result = ItemStack.EMPTY;
					for (SpawnEggItem egg : SpawnEggItem.getEggs()) {
						if (egg.hasType(null, e.getType())) {
							result = new ItemStack(egg);
							break;
						}
					}
				} else {
					throw er;
				}
			}
		}

		if (result.isEmpty()) {
			return;
		}

		if (te != null) {
			storeTEInStack(result, te);
		}

		IInventory concat = new InventoryConcat("", ((CPSHAccess)player.container).getMirror(), player.inventory);
		int firstAny = getSlotFor(concat, result);
		
		if (firstAny >= 54 && firstAny < 54+9) {
			player.inventory.currentItem = firstAny-54;
		} else if (firstAny == -1) {
			player.inventory.currentItem = player.inventory.getBestHotbarSlot();

			ItemStack cur = player.inventory.mainInventory.get(player.inventory.currentItem);
			player.inventory.mainInventory.set(player.inventory.currentItem, ItemStack.EMPTY);
			if (AbilityCheck.enabled(player, Ability.PICKSWAP) && !cur.isEmpty() && getSlotFor(concat, cur) == -1) {
				addItem(concat, cur);
			}

			player.inventory.mainInventory.set(player.inventory.currentItem, result);
		} else {
			player.inventory.currentItem = player.inventory.getBestHotbarSlot();
			ItemStack cur = player.inventory.mainInventory.get(player.inventory.currentItem);
			player.inventory.mainInventory.set(player.inventory.currentItem, concat.getStackInSlot(firstAny));
			if (AbilityCheck.enabled(player, Ability.PICKSWAP)) {
				concat.setInventorySlotContents(firstAny, cur);
			}
		}
		if (player instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)player).connection.sendPacket(new SHeldItemChangePacket(player.inventory.currentItem));
		}
	}
	
	public static ItemStack addItem(IInventory inv, ItemStack stack) {
		ItemStack copy = stack.copy();

		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack cur = inv.getStackInSlot(i);

			if (cur.isEmpty()) {
				inv.setInventorySlotContents(i, copy);
				inv.markDirty();
				return ItemStack.EMPTY;
			}

			if (ItemStack.areItemsEqual(cur, copy)) {
				int j = Math.min(inv.getInventoryStackLimit(), cur.getMaxStackSize());
				int k = Math.min(copy.getCount(), j - cur.getCount());

				if (k > 0) {
					cur.grow(k);
					copy.shrink(k);

					if (copy.isEmpty()) {
						inv.markDirty();
						return ItemStack.EMPTY;
					}
				}
			}
		}

		if (copy.getCount() != stack.getCount()) {
			inv.markDirty();
		}

		return copy;
	}
	
	private static int getSlotFor(IInventory inventory, ItemStack stack) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack is = inventory.getStackInSlot(i);
			if (!is.isEmpty() && ItemStack.areItemStacksEqual(stack, is)) {
				return i;
			}
		}
		return -1;
	}

	private static ItemStack storeTEInStack(ItemStack stack, TileEntity te) {
		CompoundNBT teNbt = te.write(new CompoundNBT());

		if (stack.getItem() instanceof SkullItem && teNbt.contains("Owner")) {
			CompoundNBT owner = teNbt.getCompound("Owner");
			CompoundNBT corrected = new CompoundNBT();
			corrected.put("SkullOwner", owner);
			stack.setTag(corrected);
			return stack;
		} else {
			stack.setTagInfo("BlockEntityTag", teNbt);
			CompoundNBT display = new CompoundNBT();
			ListNBT lore = new ListNBT();
			lore.add(StringNBT.valueOf("(+NBT)"));
			display.put("Lore", lore);
			stack.setTagInfo("display", display);
			return stack;
		}
	}

	
}
