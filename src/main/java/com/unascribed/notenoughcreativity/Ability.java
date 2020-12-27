package com.unascribed.notenoughcreativity;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;

public enum Ability {
	NOPICKUP(0),
	ATTACK(0),
	HEALTH(0),
	INSTABREAK(0),
	PICKSWAP(0),
	NIGHTVISION(0),
	NOCLIP(0),
	DARKMODE(-1),
	LONGREACH(0),
	;
	public static final ImmutableList<Ability> VALUES = ImmutableList.copyOf(values());
	public static final ImmutableList<Ability> VALUES_SORTED = ImmutableList.sortedCopyOf((a, b) -> Integer.compare(a.weight, b.weight), Arrays.asList(values()));
	
	private final int weight;
	
	private int index = -1;
	
	private Ability(int weight) {
		this.weight = weight;
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
	
	public int bit() {
		return 1 << ordinal();
	}

	public static Set<Ability> fromBits(int bits) {
		EnumSet<Ability> set = EnumSet.noneOf(Ability.class);
		for (Ability a : Ability.VALUES) {
			if ((bits & a.bit()) != 0) {
				set.add(a);
			}
		}
		return set;
	}
	
	public static int toBits(Set<Ability> set) {
		int i = 0;
		for (Ability a : set) {
			i |= a.bit();
		}
		return i;
	}
	
}
