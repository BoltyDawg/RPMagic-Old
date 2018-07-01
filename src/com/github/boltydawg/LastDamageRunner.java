package com.github.boltydawg;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

public class LastDamageRunner extends BukkitRunnable{
	private Player player;
	private Objective time;
	private static final int MAX = 200;
	
	public static HashMap<Player,LastDamageRunner> damaged = new HashMap<Player,LastDamageRunner>();
	
	public LastDamageRunner(Player p) {
		player=p;
		time = Main.scoreboard.getObjective("damageTime");
	}
	
	public void run() {
		int sc = time.getScore(player.getName()).getScore();
		if(sc>=MAX) {
			cancel();
			damaged.remove(player);
			return;
		}
		time.getScore(player.getName()).setScore(sc+1);
	}
	
	public static void startCounter(Player player) {
		cancelCounter(player);
		LastDamageRunner runner = new LastDamageRunner(player);
		damaged.put(player, runner);
		
		runner.runTaskTimer(Main.instance, 1L, 1L);
		runner.time.getScore(player.getName()).setScore(0);
	}
	private static void cancelCounter(Player player) {
		LastDamageRunner runner = damaged.get(player);
		if(runner!=null && !runner.isCancelled()) {
			runner.cancel();
		}
		damaged.remove(player);
	}
	public static int timeSinceLastDamage(Player player) {
		if(damaged.get(player)==null)
			return MAX/20;
		else {
			return damaged.get(player).time.getScore(player.getName()).getScore()/20;
		}
	}
}
