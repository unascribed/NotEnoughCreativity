package com.unascribed.notenoughcreativity;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCreativePlus extends ContainerPlayer {

	private final EntityPlayer player;
	
	public final InventoryBasic mirror = new InventoryBasic("", false, 54);
	
	private boolean acceptSlots;
	
	public Slot deleteSlot, returnSlot;
	
	public ContainerCreativePlus(EntityPlayer player) {
		super(player.inventory, !player.world.isRemote, player);
		acceptSlots = true;
		this.player = player;
		
		NBTTagList in = player.getEntityData().getTagList("NotEnoughCreativityInventory", NBT.TAG_COMPOUND);
		for (int i = 0; i < in.tagCount(); i++) {
			NBTTagCompound tag = in.getCompoundTagAt(i);
			mirror.setInventorySlotContents(tag.getInteger("Slot"), new ItemStack(tag));
		}
		mirror.addInventoryChangeListener((inv) -> {
			NBTTagList out = new NBTTagList();
			for (int i = 0; i < mirror.getSizeInventory(); i++) {
				ItemStack is = mirror.getStackInSlot(i);
				if (is.isEmpty()) continue;
				NBTTagCompound comp = is.serializeNBT();
				comp.setByte("Slot", (byte)i);
				out.appendTag(comp);
			}
			player.getEntityData().setTag("NotEnoughCreativityInventory", out);
		});
		
		int bX = 31;
		int bY = 18;
		int invOfsY = 112;
		
		addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, 0, 9, 130));
		
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(mirror, x + y * 9, bX + x * 18, bY + y * 18));
			}
		}

		InventoryPlayer playerInv = player.inventory;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, bX + x * 18, bY + invOfsY + y * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(playerInv, i, bX + i * 18, bY + invOfsY + 58));
		}
		
		for (int y = 0; y < 2; ++y) {
			for (int x = 0; x < 2; ++x) {
				addSlotToContainer(new Slot(craftMatrix, x + y * 2, -44 + x * 18, 120 + y * 18));
			}
		}
		
		EntityEquipmentSlot[] equipmentSlots = {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
		for (int k = 0; k < 4; ++k) {
			EntityEquipmentSlot entityequipmentslot = equipmentSlots[k];
			addSlotToContainer(new Slot(playerInv, 36 + (3 - k), -44, 18 + k * 18) {
				@Override
				public int getSlotStackLimit() {
					return 1;
				}
				@Override
				public boolean isItemValid(ItemStack stack) {
					return stack.getItem().isValidArmor(stack, entityequipmentslot, player);
				}
				@Override
				public boolean canTakeStack(EntityPlayer playerIn) {
					return super.canTakeStack(playerIn);
				}
				@Override
				public ResourceLocation getBackgroundLocation() {
					if (Ability.DARKMODE.isEnabled(player)) {
						return new ResourceLocation(ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()].replace("minecraft:items/", "notenoughcreativity:textures/gui/")+".png");
					}
					return super.getBackgroundLocation();
				}
				@Override
				@Nullable
				@SideOnly(Side.CLIENT)
				public TextureAtlasSprite getBackgroundSprite() {
					if (Ability.DARKMODE.isEnabled(player)) {
						return DummyTextureAtlasSprite.INSTANCE;
					}
					return super.getBackgroundSprite();
				}
				@Override
				@Nullable
				public String getSlotTexture() {
					return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
				}
			});
		}
		
		addSlotToContainer(new Slot(playerInv, 40, 9, 90) {
			@Override
			public ResourceLocation getBackgroundLocation() {
				if (Ability.DARKMODE.isEnabled(player)) {
					return new ResourceLocation("notenoughcreativity:textures/gui/empty_armor_slot_shield.png");
				}
				return super.getBackgroundLocation();
			}
			@Override
			@Nullable
			@SideOnly(Side.CLIENT)
			public TextureAtlasSprite getBackgroundSprite() {
				if (Ability.DARKMODE.isEnabled(player)) {
					return DummyTextureAtlasSprite.INSTANCE;
				}
				return super.getBackgroundSprite();
			}
			@Override
			@Nullable
			public String getSlotTexture() {
				return "minecraft:items/empty_armor_slot_shield";
			}
		});
		
		addSlotToContainer(deleteSlot = new Slot(null, 40, 9, 188) {
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
			public boolean isHere(IInventory inv, int slotIn) {
				return false;
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
		
		addSlotToContainer(returnSlot = new Slot(null, 80, 9, 166) {
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
			public boolean isHere(IInventory inv, int slotIn) {
				return false;
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
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
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
	public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
		if (slotId >= 0) {
			Slot slot = getSlot(slotId);
			if (slot == deleteSlot && clickType == ClickType.QUICK_MOVE) {
				mirror.clear();
				mirror.markDirty();
				player.inventory.clear();
				craftMatrix.clear();
				craftResult.clear();
			} else if (slot == returnSlot) {
				player.getEntityData().setBoolean("NotEnoughCreativity", false);
				NotEnoughCreativity.updateInventory(player);
			}
		}
		return super.slotClick(slotId, dragType, clickType, player);
	}
	
	@Override
	protected Slot addSlotToContainer(Slot slotIn) {
		if (!acceptSlots) return slotIn;
		return super.addSlotToContainer(slotIn);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn == player;
	}
	
}
