package com.unascribed.notenoughcreativity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// Fabric version. See Forge version in the "forgery" subdir that calls Forge overloads that supply
// additional context to pickblock/etc callbacks and allows custom entity pickblock.
public class PickBlockHandler {

	public static void pickBlock(PlayerEntity player, HitResult target, boolean exact) {
		World world = player.world;
		
		ItemStack result;
		BlockEntity be = null;

		if (target.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = ((BlockHitResult)target).getBlockPos();
			BlockState state = world.getBlockState(pos);

			if (state.isAir()) {
				return;
			}

			if (LoaderHandler.isFabricAPIAvailable() && FabricAPIHandler.hasIntelligentPick(state)) {
				result = FabricAPIHandler.pick(state, player, (BlockHitResult)target);
			} else {
				result = state.getBlock().getPickStack(world, pos, state);
			}
			if (exact && state.getBlock().hasBlockEntity()) {
				be = world.getBlockEntity(pos);
			}
		} else {
			if (target.getType() != HitResult.Type.ENTITY) {
				return;
			}

			// Vanilla why are you like this
			
			Entity entity = ((EntityHitResult)target).getEntity();
			if (LoaderHandler.isFabricAPIAvailable() && FabricAPIHandler.hasIntelligentPick(entity)) {
				result = FabricAPIHandler.pick(entity, player, (EntityHitResult)target);
			} else if (entity instanceof PaintingEntity) {
				result = new ItemStack(Items.PAINTING);
			} else if (entity instanceof LeashKnotEntity) {
				result = new ItemStack(Items.LEAD);
			} else if (entity instanceof ItemFrameEntity) {
				ItemFrameEntity itemFrameEntity = (ItemFrameEntity) entity;
				ItemStack itemStack4 = itemFrameEntity.getHeldItemStack();
				if (itemStack4.isEmpty()) {
					result = new ItemStack(Items.ITEM_FRAME);
				} else {
					result = itemStack4.copy();
				}
			} else if (entity instanceof AbstractMinecartEntity) {
				AbstractMinecartEntity ame = (AbstractMinecartEntity) entity;
				Item item = Items.MINECART;
				switch (ame.getMinecartType()) {
					case FURNACE: item = Items.FURNACE_MINECART; break;
					case CHEST: item = Items.CHEST_MINECART; break;
					case TNT: item = Items.TNT_MINECART; break;
					case HOPPER: item = Items.HOPPER_MINECART; break;
					case COMMAND_BLOCK: item = Items.COMMAND_BLOCK_MINECART; break;
					case RIDEABLE: case SPAWNER: item = Items.MINECART; break;
				}

				result = new ItemStack(item);
			} else if (entity instanceof BoatEntity) {
				result = new ItemStack(((BoatEntity) entity).asItem());
			} else if (entity instanceof ArmorStandEntity) {
				result = new ItemStack(Items.ARMOR_STAND);
			} else if (entity instanceof EndCrystalEntity) {
				result = new ItemStack(Items.END_CRYSTAL);
			} else {
				// SpawnEggItem.forEntity is client-only
				result = ItemStack.EMPTY;
				for (SpawnEggItem egg : SpawnEggItem.getAll()) {
					if (egg.isOfSameEntityType(null, entity.getType())) {
						result = new ItemStack(egg);
						break;
					}
				}
			}
		}

		if (result.isEmpty()) {
			return;
		}

		if (be != null) {
			storeTEInStack(result, be);
		}

		Inventory concat = new InventoryConcat(((CreativePlusScreenHandler)player.playerScreenHandler).mirror, player.inventory);
		int firstAny = getSlotFor(concat, result);
		
		if (firstAny >= 54 && firstAny < 54+9) {
			player.inventory.selectedSlot = firstAny-54;
		} else if (firstAny == -1) {
			player.inventory.selectedSlot = player.inventory.getSwappableHotbarSlot();

			ItemStack cur = player.inventory.main.get(player.inventory.selectedSlot);
			player.inventory.main.set(player.inventory.selectedSlot, ItemStack.EMPTY);
			if (AbilityCheck.enabled(player, Ability.PICKSWAP) && !cur.isEmpty() && getSlotFor(concat, cur) == -1) {
				addItem(concat, cur);
			}

			player.inventory.main.set(player.inventory.selectedSlot, result);
		} else {
			player.inventory.selectedSlot = player.inventory.getSwappableHotbarSlot();
			ItemStack cur = player.inventory.main.get(player.inventory.selectedSlot);
			player.inventory.main.set(player.inventory.selectedSlot, concat.getStack(firstAny));
			if (AbilityCheck.enabled(player, Ability.PICKSWAP)) {
				concat.setStack(firstAny, cur);
			}
		}
		if (player instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)player).networkHandler.sendPacket(new HeldItemChangeS2CPacket(player.inventory.selectedSlot));
		}
	}
	
	public static ItemStack addItem(Inventory inv, ItemStack stack) {
		ItemStack copy = stack.copy();

		for (int i = 0; i < inv.size(); ++i) {
			ItemStack cur = inv.getStack(i);

			if (cur.isEmpty()) {
				inv.setStack(i, copy);
				inv.markDirty();
				return ItemStack.EMPTY;
			}

			if (ItemStack.areItemsEqual(cur, copy)) {
				int j = Math.min(inv.getMaxCountPerStack(), cur.getMaxCount());
				int k = Math.min(copy.getCount(), j - cur.getCount());

				if (k > 0) {
					cur.increment(k);
					copy.decrement(k);

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
	
	private static int getSlotFor(Inventory inventory, ItemStack stack) {
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack is = inventory.getStack(i);
			if (!is.isEmpty() && ItemStack.areEqual(stack, is)) {
				return i;
			}
		}
		return -1;
	}

	private static ItemStack storeTEInStack(ItemStack stack, BlockEntity te) {
		CompoundTag teNbt = te.toTag(new CompoundTag());

		if (stack.getItem() instanceof SkullItem && teNbt.contains("Owner")) {
			CompoundTag owner = teNbt.getCompound("Owner");
			CompoundTag corrected = new CompoundTag();
			corrected.put("SkullOwner", owner);
			stack.setTag(corrected);
			return stack;
		} else {
			stack.putSubTag("BlockEntityTag", teNbt);
			CompoundTag display = new CompoundTag();
			ListTag lore = new ListTag();
			lore.add(StringTag.of("(+NBT)"));
			display.put("Lore", lore);
			stack.putSubTag("display", display);
			return stack;
		}
	}

	
}
