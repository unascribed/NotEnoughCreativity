package com.unascribed.notenoughcreativity;

import com.unascribed.notenoughcreativity.network.MessageAbilities;
import com.unascribed.notenoughcreativity.network.MessageDeleteSlot;
import com.unascribed.notenoughcreativity.network.MessageEnabled;
import com.unascribed.notenoughcreativity.network.MessagePickBlock;
import com.unascribed.notenoughcreativity.network.MessagePickEntity;
import com.unascribed.notenoughcreativity.network.MessageSetAbility;
import com.unascribed.notenoughcreativity.network.MessageSetEnabled;
import com.unascribed.notenoughcreativity.repackage.com.elytradev.concrete.network.NetworkContext;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("notenoughcreativity")
public class NotEnoughCreativity {

	public static NetworkContext network;
	
	public NotEnoughCreativity() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
	}
	
	public void onSetup(FMLCommonSetupEvent e) {
		network = NetworkContext.forChannel(new ResourceLocation("notenoughcreativity", "main"));
		network.register(MessageSetEnabled.class);
		network.register(MessageEnabled.class);
		network.register(MessageSetAbility.class);
		network.register(MessageAbilities.class);
		network.register(MessageDeleteSlot.class);
		network.register(MessagePickBlock.class);
		network.register(MessagePickEntity.class);
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> NEClientSpringboard::setup);
		MinecraftForge.EVENT_BUS.addListener(this::onLoggedIn);
		MinecraftForge.EVENT_BUS.addListener(this::onDeath);
		MinecraftForge.EVENT_BUS.addListener(this::onLivingDrops);
		MinecraftForge.EVENT_BUS.addListener(this::onLivingExperienceDrop);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerTick);
		MinecraftForge.EVENT_BUS.addListener(this::onCriticalHit);
		MinecraftForge.EVENT_BUS.addListener(this::onEntityPickup);
		MinecraftForge.EVENT_BUS.addListener(this::onChangeDimension);
		MinecraftForge.EVENT_BUS.addListener(this::onRespawn);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerClone);
		MinecraftForge.EVENT_BUS.addListener(this::onLivingUpdate);
	}
	
	public static boolean isCreativePlus(PlayerEntity ep) {
		return ep != null && ep.abilities.isCreativeMode && ep.getPersistentData().getBoolean("NotEnoughCreativity");
	}

	public void onLoggedIn(PlayerLoggedInEvent e) {
		updateInventory(e.getPlayer());
	}
	
	public void onDeath(LivingDeathEvent e) {
		if (e.getEntity() instanceof PlayerEntity) {
			PlayerEntity p = (PlayerEntity)e.getEntity();
			if (Ability.HEALTH.isEnabled(p) && !e.getSource().canHarmInCreative()) {
				p.setHealth(0.1f);
				e.setCanceled(true);
			}
		}
	}
	
	public void onLivingDrops(LivingDropsEvent e) {
		if (!(e.getEntityLiving() instanceof PlayerEntity)) {
			Entity attacker = e.getEntityLiving().getAttackingEntity() ;
			if (attacker instanceof PlayerEntity) {
				PlayerEntity p = (PlayerEntity)attacker;
				if (Ability.ATTACK.isEnabled(p)) {
					e.setCanceled(true);
				}
			}
		}
	}
	
	public void onLivingExperienceDrop(LivingExperienceDropEvent e) {
		if (Ability.ATTACK.isEnabled(e.getAttackingPlayer())) {
			e.setCanceled(true);
		}
	}
	
	// LivingUpdate runs slightly later on players, after noClip is set to isSpectator
	public void onLivingUpdate(LivingUpdateEvent e) {
		if (e.getEntityLiving() instanceof PlayerEntity) {
			if (Ability.NOCLIP.isEnabled((PlayerEntity)e.getEntityLiving())) {
				e.getEntityLiving().noClip = true;
			}
		}
	}
	
	private static final AttributeModifier REACH_MODIFIER = new AttributeModifier("Not Enough Creativity Long Reach ability", 8, Operation.ADDITION);
	
	public void onPlayerTick(PlayerTickEvent e) {
		if (e.phase != Phase.START) return;
		if (e.player.container instanceof ContainerCreativePlus) {
			ModifiableAttributeInstance reach = e.player.getAttributeManager().createInstanceIfAbsent(ForgeMod.REACH_DISTANCE.get());
			if (!e.player.abilities.isCreativeMode) {
				if (reach.hasModifier(REACH_MODIFIER)) {
					reach.removeModifier(REACH_MODIFIER);
				}
				updateInventory(e.player);
			} else {
				if (Ability.LONGREACH.isEnabled(e.player)) {
					if (!reach.hasModifier(REACH_MODIFIER)) {
						reach.applyNonPersistentModifier(REACH_MODIFIER);
					}
				} else {
					if (reach.hasModifier(REACH_MODIFIER)) {
						reach.removeModifier(REACH_MODIFIER);
					}
				}
				if (Ability.HEALTH.isEnabled(e.player)) {
					e.player.abilities.disableDamage = false;
					e.player.getFoodStats().setFoodLevel(15);
				} else if (!e.player.abilities.disableDamage) {
					e.player.abilities.disableDamage = true;
					e.player.setHealth(e.player.getMaxHealth());
				}
			}
		}
	}
	
	public void onCriticalHit(CriticalHitEvent e) {
		if (Ability.ATTACK.isEnabled(e.getPlayer())) {
			e.setResult(Result.ALLOW);
			e.setDamageModifier(1000);
		}
	}
	
	public void onEntityPickup(EntityItemPickupEvent e) {
		if (Ability.NOPICKUP.isEnabled(e.getPlayer())) {
			e.setCanceled(true);
		}
	}
	
	public void onChangeDimension(PlayerChangedDimensionEvent e) {
		updateInventory(e.getPlayer());
	}
	
	public void onRespawn(PlayerRespawnEvent e) {
		updateInventory(e.getPlayer());
	}
	
	public void onPlayerClone(PlayerEvent.Clone e) {
		CompoundNBT from = e.getOriginal().getPersistentData();
		CompoundNBT to = e.getPlayer().getPersistentData();
		to.putBoolean("NotEnoughCreativity", from.getBoolean("NotEnoughCreativity"));
		to.putInt("NotEnoughCreativityAbilities", from.getInt("NotEnoughCreativityAbilities"));
		if (from.contains("NotEnoughCreativityInventory")) {
			to.put("NotEnoughCreativityInventory", from.get("NotEnoughCreativityInventory"));
		}
		updateInventory(e.getPlayer());
	}
	
	public static void updateInventory(PlayerEntity player) {
		boolean enabled = isCreativePlus(player);
		PlayerContainer orig = player.container;
		PlayerContainer nw;
		if (enabled) {
			nw = new ContainerCreativePlus(player);
		} else {
			nw = new PlayerContainer(player.inventory, !player.world.isRemote, player);
		}
		if (!player.world.isRemote) {
			new MessageEnabled(enabled).sendTo(player);
			if (enabled) {
				new MessageAbilities(player.getPersistentData().getInt("NotEnoughCreativityAbilities")).sendTo(player);
			}
		}
		player.container = nw;
		if (orig == player.openContainer) {
			player.openContainer = nw;
		}
		if (player instanceof IContainerListener) {
			nw.addListener((IContainerListener)player);
		}
	}

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

		IInventory concat = new InventoryConcat("", ((ContainerCreativePlus)player.container).mirror, player.inventory);
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
