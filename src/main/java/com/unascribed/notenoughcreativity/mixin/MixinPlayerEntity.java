package com.unascribed.notenoughcreativity.mixin;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;
import com.unascribed.notenoughcreativity.CreativePlusScreenHandler;
import com.unascribed.notenoughcreativity.NECPlayer;
import com.unascribed.notenoughcreativity.NotEnoughCreativity;
import com.unascribed.notenoughcreativity.ReachHandler;
import com.unascribed.notenoughcreativity.network.MessageOtherNoclipping;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements NECPlayer {

	@Unique
	private static final Set<PlayerEntity> nec$noclippers = Collections.newSetFromMap(new WeakHashMap<>());
	
	@Unique
	private final Set<Ability> nec$enabledAbilities = EnumSet.noneOf(Ability.class);
	@Unique
	private boolean nec$creativePlusEnabled = false;
	@Unique
	private boolean nec$noclipping = false;
	@Unique
	private boolean nec$vanillaReachExtension = false;
	@Unique
	private SimpleInventory nec$inventory = new SimpleInventory(54);
	@Unique
	private Vec3d nec$savedPosition;
	@Unique
	private Identifier nec$savedDimension;
	
	@Inject(at=@At("HEAD"), method="updateWaterSubmersionState()Z", cancellable=true)
	public void onLateTick(CallbackInfoReturnable<Boolean> ci) {
		PlayerEntity self = (PlayerEntity)(Object)this;
		if (AbilityCheck.enabled(self, Ability.NOCLIP)) {
			self.noClip = true;
			self.setOnGround(false);
			self.abilities.flying = true;
			if (nec$noclippers.add(self)) {
				new MessageOtherNoclipping(self.getEntityId(), true).sendToAllWatching(self);
			}
		} else {
			if (nec$noclippers.remove(self)) {
				new MessageOtherNoclipping(self.getEntityId(), false).sendToAllWatching(self);
			}
		}
		if (self.playerScreenHandler instanceof CreativePlusScreenHandler) {
			ReachHandler.tick(self);
			if (!self.abilities.creativeMode) {
				NotEnoughCreativity.updateInventory(self);
			} else {
				if (AbilityCheck.enabled(self, Ability.HEALTH)) {
					self.abilities.invulnerable = false;
					self.getHungerManager().setFoodLevel(15);
				} else if (!self.abilities.invulnerable) {
					self.abilities.invulnerable = true;
					self.setHealth(self.getMaxHealth());
				}
			}
		}
	}
	
	@Inject(at=@At("HEAD"), method="attack(Lnet/minecraft/entity/Entity;)V", cancellable=true)
	public void attack(Entity e, CallbackInfo ci) {
		PlayerEntity self = (PlayerEntity)(Object)this;
		if (AbilityCheck.enabled(self, Ability.ATTACK)) {
			if (e.isAttackable() && !e.handleAttack(self)) {
				DamageSource ds = DamageSource.player(self);
				((InvokerDamageSource)ds).nec$setBypassesArmor();
				((InvokerDamageSource)ds).nec$setUnblockable();
				e.damage(ds, e instanceof LivingEntity ? ((LivingEntity)e).getHealth()*2 : 200);
				self.world.playSound(null, self.getX(), self.getY(), self.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, self.getSoundCategory(), 1, 1);
				self.addCritParticles(e);
				self.addCritParticles(e);
			}
			ci.cancel();
		}
	}
	
	@Inject(at=@At("TAIL"), method="writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V")
	public void writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
		tag.putBoolean("notenoughcreativity:enabled", nec$creativePlusEnabled);
		tag.putInt("notenoughcreativity:abilities", Ability.toBits(nec$enabledAbilities));
		ListTag inv = new ListTag();
		for (int i = 0; i < nec$inventory.size(); i++) {
			ItemStack is = nec$inventory.getStack(i);
			if (is.isEmpty()) continue;
			CompoundTag entry = is.toTag(new CompoundTag());
			entry.putByte("Slot", (byte) i);
			inv.add(entry);
		}
		tag.put("notenoughcreativity:inventory", inv);
		if (nec$savedPosition != null) {
			ListTag li = new ListTag();
			li.add(DoubleTag.of(nec$savedPosition.x));
			li.add(DoubleTag.of(nec$savedPosition.y));
			li.add(DoubleTag.of(nec$savedPosition.z));
			tag.put("notenoughcreativity:saved_position", li);
			tag.putString("notenoughcreativity:saved_dimension", nec$savedDimension.toString());
		}
	}
	
	@Inject(at=@At("TAIL"), method="readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V")
	public void readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
		boolean imported = false;
		int enabledAbilitiesBits = 0;
		ListTag inv = null;
		if (tag.contains("ForgeData")) {
			CompoundTag forgeData = tag.getCompound("ForgeData");
			if (forgeData.contains("NotEnoughCreativity")) {
				// import old data from the native 1.16 Forge version or 1.12 version
				imported = true;
				nec$setCreativePlusEnabled(forgeData.getBoolean("NotEnoughCreativity"));
				enabledAbilitiesBits = forgeData.getInt("NotEnoughCreativityAbilities");
				inv = forgeData.getList("NotEnoughCreativityInventory", NbtType.COMPOUND);
				forgeData.remove("NotEnoughCreativity");
				forgeData.remove("NotEnoughCreativityAbilities");
				forgeData.remove("NotEnoughCreativityInventory");
				forgeData.remove("NotEnoughCreativityNoclipping");
			}
		}
		if (!imported) {
			nec$setCreativePlusEnabled(tag.getBoolean("notenoughcreativity:enabled"));
			enabledAbilitiesBits = tag.getInt("notenoughcreativity:abilities");
			inv = tag.getList("notenoughcreativity:inventory", NbtType.COMPOUND);
		}
		nec$inventory.clear();
		for (int i = 0; i < inv.size(); i++) {
			CompoundTag entry = inv.getCompound(i);
			nec$inventory.setStack(entry.getInt("Slot"), ItemStack.fromTag(entry));
		}
		nec$enabledAbilities.clear();
		nec$enabledAbilities.addAll(Ability.fromBits(enabledAbilitiesBits));
		if (tag.contains("notenoughcreativity:saved_position")) {
			ListTag li = tag.getList("notenoughcreativity:saved_position", NbtType.LIST);
			nec$savedPosition = new Vec3d(li.getDouble(0), li.getDouble(1), li.getDouble(2));
			nec$savedDimension = Identifier.tryParse(tag.getString("notenoughcreativity:saved_dimension"));
		} else {
			nec$savedPosition = null;
			nec$savedDimension = null;
		}
	}
	
	@Inject(at=@At("HEAD"), method="handleFallDamage(FF)Z")
	public void handleFallDamageHead(float a, float b, CallbackInfoReturnable<Boolean> ci) {
		PlayerEntity self = (PlayerEntity)(Object)this;
		if (AbilityCheck.enabled(self, Ability.HEALTH)) {
			self.abilities.allowFlying = false;
		}
	}
	
	@Inject(at=@At("RETURN"), method="handleFallDamage(FF)Z")
	public void handleFallDamageTail(float a, float b, CallbackInfoReturnable<Boolean> ci) {
		PlayerEntity self = (PlayerEntity)(Object)this;
		if (AbilityCheck.enabled(self, Ability.HEALTH)) {
			self.abilities.allowFlying = true;
		}
	}

	@Override
	public Set<Ability> nec$getEnabledAbilities() {
		return nec$enabledAbilities;
	}

	@Override
	public boolean nec$isCreativePlusEnabled() {
		return nec$creativePlusEnabled;
	}

	@Override
	public void nec$setCreativePlusEnabled(boolean enabled) {
		nec$creativePlusEnabled = enabled;
	}

	@Override
	public boolean nec$isNoclipping() {
		return nec$noclipping && ((PlayerEntity)(Object)this).isCreative();
	}

	@Override
	public void nec$setNoclipping(boolean noclipping) {
		nec$noclipping = noclipping;
	}

	@Override
	public Inventory nec$getCreativePlusInventory() {
		return nec$inventory;
	}

	@Override
	public boolean nec$isVanillaReachExtensionEnabled() {
		return nec$vanillaReachExtension;
	}

	@Override
	public void nec$setVanillaReachExtensionEnabled(boolean enabled) {
		nec$vanillaReachExtension = enabled;
	}

	@Override
	public Vec3d nec$getSavedPosition() {
		return nec$savedPosition;
	}

	@Override
	public void nec$setSavedPosition(Vec3d pos) {
		nec$savedPosition = pos;
	}

	@Override
	public Identifier nec$getSavedDimension() {
		return nec$savedDimension;
	}

	@Override
	public void nec$setSavedDimension(Identifier dim) {
		nec$savedDimension = dim;
	}
	
}
