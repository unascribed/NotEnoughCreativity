package com.unascribed.notenoughcreativity;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.unascribed.notenoughcreativity.mixin.AccessorPlayerScreenHandler;
import com.unascribed.notenoughcreativity.mixin.AccessorScreenHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public class CreativePlusScreenHandler extends PlayerScreenHandler implements CPSHAccess {

	public static final Identifier DARK_EMPTY_HELMET_SLOT_TEXTURE = new Identifier("notenoughcreativity", "gui/empty_armor_slot_helmet");
	public static final Identifier DARK_EMPTY_CHESTPLATE_SLOT_TEXTURE = new Identifier("notenoughcreativity", "gui/empty_armor_slot_chestplate");
	public static final Identifier DARK_EMPTY_LEGGINGS_SLOT_TEXTURE = new Identifier("notenoughcreativity", "gui/empty_armor_slot_leggings");
	public static final Identifier DARK_EMPTY_BOOTS_SLOT_TEXTURE = new Identifier("notenoughcreativity", "gui/empty_armor_slot_boots");
	public static final Identifier DARK_EMPTY_OFFHAND_SLOT_TEXTURE = new Identifier("notenoughcreativity", "gui/empty_armor_slot_shield");
	
	private static final Identifier[] ARMOR_TEX = new Identifier[]{EMPTY_BOOTS_SLOT_TEXTURE, EMPTY_LEGGINGS_SLOT_TEXTURE, EMPTY_CHESTPLATE_SLOT_TEXTURE, EMPTY_HELMET_SLOT_TEXTURE};
	private static final Identifier[] DARK_ARMOR_TEX = new Identifier[]{DARK_EMPTY_BOOTS_SLOT_TEXTURE, DARK_EMPTY_LEGGINGS_SLOT_TEXTURE, DARK_EMPTY_CHESTPLATE_SLOT_TEXTURE, DARK_EMPTY_HELMET_SLOT_TEXTURE};
	
	private final PlayerEntity player;
	
	public final Inventory mirror;
	
	public Slot deleteSlot, returnSlot;
	
	private boolean fastbench = false;
	private CraftingInventory craftingInput;
	private CraftingResultInventory craftingResult;
	
	public CreativePlusScreenHandler(PlayerEntity player) {
		super(player.inventory, !player.world.isClient, player);
		// FastWorkbench may have replaced the result slot, so keep it
		clearExcFirst(slots);
		clearExcFirst(((AccessorScreenHandler)this).nec$getTrackedStacks());
		
		Slot resSlot = slots.get(0);
		resSlot.x = 9;
		resSlot.y = 130;
		
		craftingInput = ((AccessorPlayerScreenHandler)this).nec$getCraftingInput();
		craftingResult = ((AccessorPlayerScreenHandler)this).nec$getCraftingResult();
		
		this.player = player;
		
		mirror = ((NECPlayer)player).nec$getCreativePlusInventory();
		
		int bX = 31;
		int bY = 18;
		int invOfsY = 112;
		
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
				addSlot(new Slot(craftingInput, x + y * 2, -44 + x * 18, 120 + y * 18));
			}
		}
		
		EquipmentSlot[] equipmentSlots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
		for (int k = 0; k < 4; ++k) {
			EquipmentSlot es = equipmentSlots[k];
			addSlot(new Slot(playerInv, 36 + (3 - k), -44, 18 + k * 18) {
				@Override
				public int getMaxItemCount() {
					return 1;
				}
				@Override
				public boolean canInsert(ItemStack stack) {
					return MobEntity.getPreferredEquipmentSlot(stack) == es;
				}
				@Override
				@Environment(EnvType.CLIENT)
				public Pair<Identifier, Identifier> getBackgroundSprite() {
					return Pair.of(BLOCK_ATLAS_TEXTURE,
							(AbilityCheck.enabled(player, Ability.DARKMODE) ? DARK_ARMOR_TEX : ARMOR_TEX)[es.getEntitySlotId()]);
				}
			});
		}
		
		addSlot(new Slot(playerInv, 40, 9, 90) {
			@Override
			@Environment(EnvType.CLIENT)
			public Pair<Identifier, Identifier> getBackgroundSprite() {
				return Pair.of(BLOCK_ATLAS_TEXTURE,
						AbilityCheck.enabled(player, Ability.DARKMODE) ? DARK_EMPTY_OFFHAND_SLOT_TEXTURE : EMPTY_OFFHAND_ARMOR_SLOT);
			}
		});
		
		addSlot(deleteSlot = new Slot(null, 40, 9, 188) {
			@Override
			public void setStack(ItemStack stack) {
				// delete it
			}
			
			@Override
			public ItemStack getStack() {
				return ItemStack.EMPTY;
			}

			@Override
			public void markDirty() {
			}
			
			@Override
			public ItemStack takeStack(int amount) {
				return ItemStack.EMPTY;
			}
			
			@Override
			public int getMaxItemCount() {
				return 64;
			}
			
		});
		
		addSlot(returnSlot = new Slot(null, 80, 9, 166) {
			@Override
			public void setStack(ItemStack stack) {
			}
			
			@Override
			public ItemStack getStack() {
				return ItemStack.EMPTY;
			}
			
			@Override
			public void markDirty() {
			}
			
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}
			
			@Override
			public ItemStack takeStack(int amount) {
				return ItemStack.EMPTY;
			}
			
			@Override
			public int getMaxItemCount() {
				return 0;
			}
			
		});
	}
	
	private void clearExcFirst(List<?> li) {
		if (li.size() <= 1) return;
		li.subList(1, li.size()).clear();
	}

	@Override
	public Inventory getMirror() {
		return mirror;
	}
	
	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot != null && slot.hasStack()) {
			ItemStack stack = slot.getStack();
			result = stack.copy();
			// crafting output
			if (index == 0) {
				if (!insertItem(stack, 1, 91, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(stack, result);
			} else {
				// not armor slots
				if (index < 95 || index > 98) {
					insertItem(stack, 95, 99, false);
				}
				if (slot.inventory == mirror) {
					if (!insertItem(stack, 82, 91, false)) {
						if (!insertItem(stack, 55, 82, false))
							return ItemStack.EMPTY;
					}
				} else {
					if (!insertItem(stack, 1, 55, false))
						return ItemStack.EMPTY;
				}
			}
			if (stack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (stack.getCount() == result.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack remainder = slot.onTakeItem(player, stack);

			if (index == 0) {
				player.dropItem(remainder, false);
			}
		}

		return result;
	}
	
	@Override
	public ItemStack onSlotClick(int slotId, int dragType, SlotActionType actionType, PlayerEntity player) {
		if (slotId >= 0) {
			Slot slot = getSlot(slotId);
			if (slot == deleteSlot && actionType == SlotActionType.QUICK_MOVE) {
				mirror.clear();
				mirror.markDirty();
				player.inventory.clear();
				craftingInput.clear();
				craftingResult.clear();
			} else if (slot == returnSlot) {
				((NECPlayer)player).nec$setCreativePlusEnabled(false);
				NotEnoughCreativity.updateInventory(player);
			}
		}
		return super.onSlotClick(slotId, dragType, actionType, player);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return player == this.player;
	}
	
}
