package com.unascribed.notenoughcreativity;

import com.unascribed.notenoughcreativity.mixin.AccessorPlayerScreenHandler;
import com.unascribed.notenoughcreativity.mixin.AccessorSlot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class CreativePlusScreenHandler extends PlayerScreenHandler implements CPSHAccess {

	public static final Inventory DUMMY_INVENTORY = new Inventory() {
		
		@Override
		public void clear() {
			
		}
		
		@Override
		public int size() {
			return 0;
		}
		
		@Override
		public void setStack(int slot, ItemStack stack) {
			
		}
		
		@Override
		public ItemStack removeStack(int slot, int amount) {
			return ItemStack.EMPTY;
		}
		
		@Override
		public ItemStack removeStack(int slot) {
			return ItemStack.EMPTY;
		}
		
		@Override
		public void markDirty() {
			
		}
		
		@Override
		public boolean isEmpty() {
			return true;
		}
		
		@Override
		public ItemStack getStack(int slot) {
			return ItemStack.EMPTY;
		}
		
		@Override
		public boolean canPlayerUse(PlayerEntity player) {
			return false;
		}
	};
	
	private final PlayerEntity player;
	
	public final Inventory mirror;
	
	public Slot deleteSlot, returnSlot;
	
	private CraftingInventory craftingInput;
	private CraftingResultInventory craftingResult;
	
	public CreativePlusScreenHandler(PlayerEntity player) {
		super(player.getInventory(), !player.world.isClient, player);
		craftingInput = ((AccessorPlayerScreenHandler)this).nec$getCraftingInput();
		craftingResult = ((AccessorPlayerScreenHandler)this).nec$getCraftingResult();
		
		this.player = player;
		
		mirror = ((NECPlayer)player).nec$getCreativePlusInventory();
		
		int bX = 31;
		int bY = 18;
		
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(mirror, x + y * 9, bX + x * 18, bY + y * 18));
			}
		}
		
		for (Slot s : slots) {
			if (s.inventory == player.getInventory()) {
				if (s.getClass() == Slot.class) {
					// normal inventory
					s.x += 23;
					s.y += 46;
				} else if (((AccessorSlot)s).nec$getIndex() == 40) {
					// offhand
					s.x = 9;
					s.y = 90;
				} else {
					// armor
					s.x -= 52;
					s.y += 10;
				}
			} else if (s.inventory == craftingInput) {
				s.x -= 142;
				s.y += 102;
			} else if (s.inventory == craftingResult) {
				s.x = 9;
				s.y = 130;
			}
		}
		
		addSlot(deleteSlot = new Slot(DUMMY_INVENTORY, 40, 9, 188) {
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
		
		addSlot(returnSlot = new Slot(DUMMY_INVENTORY, 80, 9, 166) {
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
				// main inventory
				if (!insertItem(stack, 9, 45, true)) {
					return ItemStack.EMPTY;
				}
				// mirror inventory
				if (!insertItem(stack, 46, 100, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickTransfer(stack, result);
			} else {
				// not armor slots
				if (index > 8) {
					insertItem(stack, 5, 9, false);
				}
				if (slot.inventory == mirror) {
					// go from mirror to hotbar
					if (!insertItem(stack, 36, 45, false)) {
						// or main inventory
						if (!insertItem(stack, 9, 36, false))
							return ItemStack.EMPTY;
					}
				} else if (index > 8 || index < 5) {
					// elsewhere to mirror
					if (!insertItem(stack, 46, 100, false)) {
						return ItemStack.EMPTY;
					}
				} else {
					// armor to anywhere
					if (!insertItem(stack, 9, 100, false)) {
						return ItemStack.EMPTY;
					}
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

			slot.onTakeItem(player, stack);

			if (index == 0) {
				player.dropItem(stack, false);
			}
		}

		return result;
	}
	
	@Override
	public void onSlotClick(int slotId, int dragType, SlotActionType actionType, PlayerEntity player) {
		if (slotId >= 0) {
			Slot slot = getSlot(slotId);
			if (slot == deleteSlot && actionType == SlotActionType.QUICK_MOVE) {
				mirror.clear();
				mirror.markDirty();
				player.getInventory().clear();
				craftingInput.clear();
				craftingResult.clear();
				return;
			} else if (slot == returnSlot) {
				((NECPlayer)player).nec$setCreativePlusEnabled(false);
				NotEnoughCreativity.updateInventory(player);
				return;
			}
		}
		super.onSlotClick(slotId, dragType, actionType, player);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return player == this.player;
	}
	
}
