package com.unascribed.notenoughcreativity;

import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;

public class ContainerCreativePlus extends PlayerContainer {

	private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET};
	
	private final PlayerEntity player;
	
	public final Inventory mirror = new Inventory(54);
	
	private boolean acceptSlots;
	
	public Slot deleteSlot, returnSlot;
	
	public ContainerCreativePlus(PlayerEntity player) {
		super(player.inventory, !player.world.isRemote, player);
		acceptSlots = true;
		this.player = player;
		
		
		ListNBT in = player.getPersistentData().getList("NotEnoughCreativityInventory", NBT.TAG_COMPOUND);
		for (int i = 0; i < in.size(); i++) {
			CompoundNBT tag = in.getCompound(i);
			mirror.setInventorySlotContents(tag.getInt("Slot"), ItemStack.read(tag));
		}
		mirror.addListener((inv) -> {
			ListNBT out = new ListNBT();
			for (int i = 0; i < mirror.getSizeInventory(); i++) {
				ItemStack is = mirror.getStackInSlot(i);
				if (is.isEmpty()) continue;
				CompoundNBT comp = is.serializeNBT();
				comp.putByte("Slot", (byte)i);
				out.add(comp);
			}
			player.getPersistentData().put("NotEnoughCreativityInventory", out);
		});
		
		int bX = 31;
		int bY = 18;
		int invOfsY = 112;
		
		addSlot(new CraftingResultSlot(player, craftMatrix, craftResult, 0, 9, 130));
		
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(mirror, x + y * 9, bX + x * 18, bY + y * 18));
			}
		}

		PlayerInventory playerInv = player.inventory;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(playerInv, x + y * 9 + 9, bX + x * 18, bY + invOfsY + y * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(playerInv, i, bX + i * 18, bY + invOfsY + 58));
		}
		
		for (int y = 0; y < 2; ++y) {
			for (int x = 0; x < 2; ++x) {
				addSlot(new Slot(craftMatrix, x + y * 2, -44 + x * 18, 120 + y * 18));
			}
		}
		
		EquipmentSlotType[] equipmentSlots = {EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
		for (int k = 0; k < 4; ++k) {
			EquipmentSlotType entityequipmentslot = equipmentSlots[k];
			addSlot(new Slot(playerInv, 36 + (3 - k), -44, 18 + k * 18) {
				@Override
				public int getSlotStackLimit() {
					return 1;
				}
				@Override
				public boolean isItemValid(ItemStack stack) {
					return stack.getItem().canEquip(stack, entityequipmentslot, player);
				}
				@Override
				@OnlyIn(Dist.CLIENT)
				public Pair<ResourceLocation, ResourceLocation> getBackground() {
					return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, ARMOR_SLOT_TEXTURES[entityequipmentslot.getIndex()]);
				}
			});
		}
		
		addSlot(new Slot(playerInv, 40, 9, 90) {
			@Override
			@OnlyIn(Dist.CLIENT)
			public Pair<ResourceLocation, ResourceLocation> getBackground() {
				return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD);
			}
		});
		
		addSlot(deleteSlot = new Slot(null, 40, 9, 188) {
			@Override
			public void putStack(ItemStack stack) {
				// delete it
			}
			
			@Override
			public ItemStack getStack() {
				return ItemStack.EMPTY;
			}
			
			@Override
			public void onSlotChanged() {
			}
			
			@Override
			public ItemStack decrStackSize(int amount) {
				return ItemStack.EMPTY;
			}
			
			@Override
			public int getSlotStackLimit() {
				return 64;
			}
			
		});
		
		addSlot(returnSlot = new Slot(null, 80, 9, 166) {
			@Override
			public void putStack(ItemStack stack) {
			}
			
			@Override
			public ItemStack getStack() {
				return ItemStack.EMPTY;
			}
			
			@Override
			public void onSlotChanged() {
			}
			
			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}
			
			@Override
			public ItemStack decrStackSize(int amount) {
				return ItemStack.EMPTY;
			}
			
			@Override
			public int getSlotStackLimit() {
				return 0;
			}
			
		});
	}
	
	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			result = stack.copy();
			// crafting output
			if (index == 0) {
				if (!mergeItemStack(stack, 1, 91, true)) {
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(stack, result);
			} else {
				// not armor slots
				if (index < 95 || index > 98) {
					mergeItemStack(stack, 95, 99, false);
				}
				if (slot.inventory == mirror) {
					if (!mergeItemStack(stack, 82, 91, false)) {
						if (!mergeItemStack(stack, 55, 82, false))
							return ItemStack.EMPTY;
					}
				} else {
					if (!mergeItemStack(stack, 1, 55, false))
						return ItemStack.EMPTY;
				}
			}
			if (stack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (stack.getCount() == result.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack remainder = slot.onTake(player, stack);

			if (index == 0) {
				player.dropItem(remainder, false);
			}
		}

		return result;
	}
	
	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
		if (slotId >= 0) {
			Slot slot = getSlot(slotId);
			if (slot == deleteSlot && clickType == ClickType.QUICK_MOVE) {
				mirror.clear();
				mirror.markDirty();
				player.inventory.clear();
				craftMatrix.clear();
				craftResult.clear();
			} else if (slot == returnSlot) {
				player.getPersistentData().putBoolean("NotEnoughCreativity", false);
				NotEnoughCreativity.updateInventory(player);
			}
		}
		return super.slotClick(slotId, dragType, clickType, player);
	}
	
	@Override
	protected Slot addSlot(Slot slotIn) {
		if (!acceptSlots) return slotIn;
		return super.addSlot(slotIn);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return playerIn == player;
	}
	
}
