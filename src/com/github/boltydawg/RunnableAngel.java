package com.github.boltydawg;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class RunnableAngel extends BukkitRunnable{
	private static ArrayList<Player> angels = new ArrayList<Player>();
	
	public RunnableAngel() {
		
	}
	@Override
	public void run() {
		if(angels.size()==0) {
			cancel();
			return;
		}
		for(Player player : angels) {
			if(!player.isOnline())
				angels.remove(player);
			else if(player.getInventory().getItemInMainHand().getType()==Material.ELYTRA)
				player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,30,0));
		}
	}
	public static void activate(Player player) {
		if(angels.size()==0) 
			new RunnableAngel().runTaskTimer(Main.instance, 10L, 10L);
		angels.add(player);
	}

}
