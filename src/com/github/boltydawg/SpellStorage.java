package com.github.boltydawg;

import org.bukkit.entity.Player;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

/**
 * This class stores an ArrayList of left hand ItemStacks and their
 * respected owners. This class is here because you can't do tuples
 * in Java, and I need to make sure that if two or more mages are casting
 * spells at the same time they all get the right items back
 * @author BoltyDawg
 *
 */

public class SpellStorage {
	
	private static ArrayList<SpellStorage> leftHands = new ArrayList<SpellStorage>();
	
	public Player player;
	public ItemStack offHand;
	
	public SpellStorage(Player p, ItemStack l) {
		player = p;
		offHand = l;
	}
	public static void store(SpellStorage ss) {
		leftHands.add(ss);
	}
	public static ItemStack getOffHand(Player p) {
		for(int i=0;i<leftHands.size();i++) {
			if(leftHands.get(i).player.equals(p)) return leftHands.get(i).offHand;
		}
		return new ItemStack(Material.AIR);
	}
}
