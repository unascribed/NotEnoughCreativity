package com.unascribed.notenoughcreativity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InventoryConcat implements Inventory {
	private final Inventory first;
	private final Inventory second;

	public InventoryConcat(Inventory first, Inventory second) {
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
	public int size() {
		return first.size() + second.size();
	}

	@Override
	public boolean isEmpty() {
		return first.isEmpty() && second.isEmpty();
	}

	@Override
	public ItemStack getStack(int index) {
		return index >= first.size()
				? second.getStack(index - first.size())
				: first.getStack(index);
	}

	@Override
	public ItemStack removeStack(int index, int count) {
		return index >= first.size()
				? second.removeStack(index - first.size(), count)
				: first.removeStack(index, count);
	}

	@Override
	public ItemStack removeStack(int index) {
		return index >= first.size()
				? second.removeStack(index - first.size())
				: first.removeStack(index);
	}

	@Override
	public void setStack(int index, ItemStack stack) {
		if (index >= first.size()) {
			second.setStack(index - first.size(), stack);
		} else {
			first.setStack(index, stack);
		}
	}

	@Override
	public int getMaxCountPerStack() {
		return first.getMaxCountPerStack();
	}

	@Override
	public void markDirty() {
		first.markDirty();
		second.markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return first.canPlayerUse(player)
				&& second.canPlayerUse(player);
	}
	
	@Override
	public void onOpen(PlayerEntity player) {
		first.onOpen(player);
		second.onOpen(player);
	}

	@Override
	public void onClose(PlayerEntity player) {
		first.onClose(player);
		second.onClose(player);
	}

	@Override
	public boolean isValid(int index, ItemStack stack) {
		return index >= first.size()
				? second.isValid(index - first.size(), stack)
				: first.isValid(index, stack);
	}

	@Override
	public void clear() {
		first.clear();
		second.clear();
	}
}