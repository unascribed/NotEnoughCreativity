package com.unascribed.notenoughcreativity;

import com.elytradev.concrete.network.NetworkContext;
import com.unascribed.notenoughcreativity.network.MessageAbilities;
import com.unascribed.notenoughcreativity.network.MessageDeleteSlot;
import com.unascribed.notenoughcreativity.network.MessageEnabled;
import com.unascribed.notenoughcreativity.network.MessagePickBlock;
import com.unascribed.notenoughcreativity.network.MessagePickEntity;
import com.unascribed.notenoughcreativity.network.MessageSetAbility;
import com.unascribed.notenoughcreativity.network.MessageSetEnabled;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Mod(modid="notenoughcreativity", name="Not Enough Creativity", version="@VERSION@")
public class NotEnoughCreativity {

	public static NetworkContext network;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		network = NetworkContext.forChannel("!EnCre");
		network.register(MessageSetEnabled.class);
		network.register(MessageEnabled.class);
		network.register(MessageSetAbility.class);
		network.register(MessageAbilities.class);
		network.register(MessageDeleteSlot.class);
		network.register(MessagePickBlock.class);
		network.register(MessagePickEntity.class);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			NEClient.INSTANCE.preInit();
		}
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public static boolean isCreativePlus(EntityPlayer ep) {
		return ep.capabilities.isCreativeMode && ep.getEntityData().getBoolean("NotEnoughCreativity");
	}

	@SubscribeEvent
	public void onLoggedIn(PlayerLoggedInEvent e) {
		updateInventory(e.player);
	}
	
	@SubscribeEvent
	public void onDeath(LivingDeathEvent e) {
		if (e.getEntity() instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer)e.getEntity();
			if (Ability.HEALTH.isEnabled(p) && !e.getSource().canHarmInCreative()) {
				p.setHealth(0.1f);
				e.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent e) {
		if (!(e.getEntityLiving() instanceof EntityPlayer)) {
			Entity attacker = e.getEntityLiving().getAttackingEntity() ;
			if (attacker instanceof EntityPlayer) {
				EntityPlayer p = (EntityPlayer)attacker;
				if (Ability.ATTACK.isEnabled(p)) {
					e.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingExperienceDrop(LivingExperienceDropEvent e) {
		if (Ability.ATTACK.isEnabled(e.getAttackingPlayer())) {
			e.setCanceled(true);
		}
	}

	// LivingUpdate runs slightly later on players, after noClip is set to isSpectator
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent e) {
		if (e.getEntityLiving() instanceof EntityPlayer) {
			if (Ability.NOCLIP.isEnabled((EntityPlayer)e.getEntityLiving())) {
				e.getEntityLiving().noClip = true;
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		if (e.player.inventoryContainer instanceof ContainerCreativePlus) {
			if (!e.player.capabilities.isCreativeMode) {
				updateInventory(e.player);
			} else {
				if (Ability.HEALTH.isEnabled(e.player)) {
					e.player.capabilities.disableDamage = false;
					e.player.getFoodStats().setFoodLevel(15);
				} else if (!e.player.capabilities.disableDamage) {
					e.player.capabilities.disableDamage = true;
					e.player.setHealth(e.player.getMaxHealth());
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onCriticalHit(CriticalHitEvent e) {
		if (Ability.ATTACK.isEnabled(e.getEntityPlayer())) {
			e.setResult(Result.ALLOW);
			e.setDamageModifier(1000);
		}
	}
	
	@SubscribeEvent
	public void onEntityPickup(EntityItemPickupEvent e) {
		if (Ability.NOPICKUP.isEnabled(e.getEntityPlayer())) {
			e.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onChangeDimension(PlayerChangedDimensionEvent e) {
		updateInventory(e.player);
	}
	
	@SubscribeEvent
	public void onRespawn(PlayerRespawnEvent e) {
		updateInventory(e.player, true);
	}
	
	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone e) {
		NBTTagCompound from = e.getOriginal().getEntityData();
		NBTTagCompound to = e.getEntityPlayer().getEntityData();
		to.setBoolean("NotEnoughCreativity", from.getBoolean("NotEnoughCreativity"));
		to.setInteger("NotEnoughCreativityAbilities", from.getInteger("NotEnoughCreativityAbilities"));
		if (from.hasKey("NotEnoughCreativityInventory")) {
			to.setTag("NotEnoughCreativityInventory", from.getTag("NotEnoughCreativityInventory"));
		}
		updateInventory(e.getEntityPlayer(), false);
	}
	
	public static void updateInventory(EntityPlayer player) {
		updateInventory(player, true);
	}
	
	public static void updateInventory(EntityPlayer player, boolean addListener) {
		boolean enabled = isCreativePlus(player);
		Container orig = player.inventoryContainer;
		Container nw;
		if (enabled) {
			nw = new ContainerCreativePlus(player);
		} else {
			nw = new ContainerPlayer(player.inventory, !player.world.isRemote, player);
		}
		player.inventoryContainer = nw;
		if (orig == player.openContainer) {
			player.openContainer = nw;
		}
		if (!player.world.isRemote) {
			new MessageEnabled(enabled).sendTo(player);
			if (enabled) {
				new MessageAbilities(player.getEntityData().getInteger("NotEnoughCreativityAbilities")).sendTo(player);
			}
		}
		if (addListener && player instanceof IContainerListener) {
			try {
				nw.addListener((IContainerListener)player);
			} catch (IllegalArgumentException e) {
				// yeah, that's great, minecraft, it's already listening. cool. did you have to error?
			}
		}
	}

	public static void pickBlock(EntityPlayer player, RayTraceResult target, boolean exact) {
		World world = player.world;
		
		ItemStack result;
		TileEntity te = null;

		if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
			IBlockState state = world.getBlockState(target.getBlockPos());

			if (state.getBlock().isAir(state, world, target.getBlockPos())) {
				return;
			}

			if (exact && state.getBlock().hasTileEntity(state)) {
				te = world.getTileEntity(target.getBlockPos());
			}

			result = state.getBlock().getPickBlock(state, target, world, target.getBlockPos(), player);
		} else {
			if (target.typeOfHit != RayTraceResult.Type.ENTITY || target.entityHit == null) {
				return;
			}

			result = target.entityHit.getPickedResult(target);
		}

		if (result.isEmpty()) {
			return;
		}

		if (te != null) {
			storeTEInStack(result, te);
		}

		IInventory concat = new InventoryConcat("", ((ContainerCreativePlus)player.inventoryContainer).mirror, player.inventory);
		int firstAny = getSlotFor(concat, result);
		
		if (firstAny >= 54 && firstAny < 54+9) {
			player.inventory.currentItem = firstAny-54;
		} else if (firstAny == -1) {
			player.inventory.currentItem = player.inventory.getBestHotbarSlot();

			ItemStack cur = player.inventory.mainInventory.get(player.inventory.currentItem);
			player.inventory.mainInventory.set(player.inventory.currentItem, ItemStack.EMPTY);
			if (Ability.PICKSWAP.isEnabled(player) && !cur.isEmpty() && getSlotFor(concat, cur) == -1) {
				addItem(concat, cur);
			}

			player.inventory.mainInventory.set(player.inventory.currentItem, result);
		} else {
			player.inventory.currentItem = player.inventory.getBestHotbarSlot();
			ItemStack cur = player.inventory.mainInventory.get(player.inventory.currentItem);
			player.inventory.mainInventory.set(player.inventory.currentItem, concat.getStackInSlot(firstAny));
			if (Ability.PICKSWAP.isEnabled(player)) {
				concat.setInventorySlotContents(firstAny, cur);
			}
		}
		if (player instanceof EntityPlayerMP) {
			((EntityPlayerMP)player).connection.sendPacket(new SPacketHeldItemChange(player.inventory.currentItem));
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
		NBTTagCompound teNbt = te.writeToNBT(new NBTTagCompound());

		if (stack.getItem() == Items.SKULL && teNbt.hasKey("Owner")) {
			NBTTagCompound owner = teNbt.getCompoundTag("Owner");
			NBTTagCompound corrected = new NBTTagCompound();
			corrected.setTag("SkullOwner", owner);
			stack.setTagCompound(corrected);
			return stack;
		} else {
			stack.setTagInfo("BlockEntityTag", teNbt);
			NBTTagCompound display = new NBTTagCompound();
			NBTTagList lore = new NBTTagList();
			lore.appendTag(new NBTTagString("(+NBT)"));
			display.setTag("Lore", lore);
			stack.setTagInfo("display", display);
			return stack;
		}
	}
	
}
