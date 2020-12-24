package com.unascribed.notenoughcreativity;

import java.util.Arrays;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public enum Ability {
	NOPICKUP(0, () -> SoundEvents.BLOCK_NOTE_SNARE),
	ATTACK(0, () -> SoundEvents.BLOCK_NOTE_XYLOPHONE),
	HEALTH(0, () -> SoundEvents.BLOCK_NOTE_HAT),
	INSTABREAK(0, () -> SoundEvents.BLOCK_NOTE_BASEDRUM),
	PICKSWAP(0, () -> SoundEvents.BLOCK_NOTE_BASS),
	NIGHTVISION(0, () -> SoundEvents.BLOCK_END_PORTAL_FRAME_FILL),
	NOCLIP(0, () -> SoundEvents.EVOCATION_ILLAGER_CAST_SPELL),
	DARKMODE(-1, () -> SoundEvents.BLOCK_NOTE_BELL),
	LONGREACH(0, () -> SoundEvents.BLOCK_NOTE_FLUTE),
	;
	public static final ImmutableList<Ability> VALUES = ImmutableList.copyOf(values());
	public static final ImmutableList<Ability> VALUES_SORTED = ImmutableList.sortedCopyOf((a, b) -> Integer.compare(a.weight, b.weight), Arrays.asList(values()));
	
	private final int weight;
	private final Supplier<SoundEvent> sound;
	
	private int index = -1;
	
	private Ability(int weight, Supplier<SoundEvent> sound) {
		this.weight = weight;
		this.sound = sound;
	}
	
	public SoundEvent getSound() {
		return sound.get();
	}
	
	public int getWeight() {
		return weight;
	}
	
	public int index() {
		if (index == -1) {
			index = VALUES_SORTED.indexOf(this);
		}
		return index;
	}
	
	public boolean isEnabled(EntityPlayer player) {
		if (player == null) return false;
		return NotEnoughCreativity.isCreativePlus(player) && (player.getEntityData().getInteger("NotEnoughCreativityAbilities") & (1 << ordinal())) != 0;
	}
}
