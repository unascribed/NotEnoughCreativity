package com.unascribed.notenoughcreativity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryConcat implements IInventory {
	private final String name;
	private final IInventory first;
	private final IInventory second;

	public InventoryConcat(String name, IInventory first, IInventory second) {
		this.name = name;

		if (first == null) {
			first = second;
		}

		if (second == null) {
			second = first;
		}

		this.first = first;
		this.second = second;
	}

	@Override
	public int getSizeInventory() {
		return first.getSizeInventory() + second.getSizeInventory();
	}

	@Override
	public boolean isEmpty() {
		return first.isEmpty() && second.isEmpty();
	}

	@Override
	public String getName() {
		if (first.hasCustomName()) {
			return first.getName();
		} else {
			return second.hasCustomName() ? second.getName() : name;
		}
	}

	@Override
	public boolean hasCustomName() {
		return first.hasCustomName() || second.hasCustomName();
	}

	@Override
	public ITextComponent getDisplayName() {
		return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName());
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return index >= first.getSizeInventory()
				? second.getStackInSlot(index - first.getSizeInventory())
				: first.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return index >= first.getSizeInventory()
				? second.decrStackSize(index - first.getSizeInventory(), count)
				: first.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return index >= first.getSizeInventory()
				? second.removeStackFromSlot(index - first.getSizeInventory())
				: first.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (index >= first.getSizeInventory()) {
			second.setInventorySlotContents(index - first.getSizeInventory(), stack);
		} else {
			first.setInventorySlotContents(index, stack);
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return first.getInventoryStackLimit();
	}

	@Override
	public void markDirty() {
		first.markDirty();
		second.markDirty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return first.isUsableByPlayer(player)
				&& second.isUsableByPlayer(player);
	}

	@Override
	public void openInventory(EntityPlayer player) {
		first.openInventory(player);
		second.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		first.closeInventory(player);
		second.closeInventory(player);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		first.clear();
		second.clear();
	}
}