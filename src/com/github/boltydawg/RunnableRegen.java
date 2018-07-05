package com.github.boltydawg;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RunnableRegen extends BukkitRunnable{
	private Player player;
	private int clazz;
	
	public static HashMap<Player,RunnableRegen> regenerates = new HashMap<Player,RunnableRegen>();
	
	public RunnableRegen(Player p) {
		player=p;
		clazz = Main.scoreboard.getObjective("class").getScore(player.getName()).getScore();
	}
	
	public void run() {
		//Mages
		if(clazz==2) {
			int sc = Main.scoreboard.getObjective("Magicka").getScore(player.getName()).getScore();
			if(sc+2>=Main.attributes.getOrDefault(player,0)+Main.BASE_MAG) {
				Main.scoreboard.getObjective("Magicka").getScore(player.getName()).setScore(Main.attributes.getOrDefault(player,0)+Main.BASE_MAG);
				regenerates.remove(player);
				cancel();
				return;
			}
			else
				Main.scoreboard.getObjective("Magicka").getScore(player.getName()).setScore(sc+2);
		}
		//Fighters and Rangers----------------------------------------------
		else {
			int sc = Main.scoreboard.getObjective("Stamina").getScore(player.getName()).getScore();
			if(sc+2>=Main.attributes.getOrDefault(player,0)+Main.BASE_STAM) {
				Main.scoreboard.getObjective("Stamina").getScore(player.getName()).setScore(Main.attributes.getOrDefault(player,0)+Main.BASE_STAM);
				regenerates.remove(player);
				cancel();
				return;
			}
			else
				Main.scoreboard.getObjective("Stamina").getScore(player.getName()).setScore(sc+2);
		}
	}
	
	public static void fighterRegen(Player player) {
		cancelRegen(player);
		
		RunnableRegen runner = new RunnableRegen(player);
		regenerates.put(player, runner);
		
		runner.runTaskTimer(Main.instance, 30L, 5L);
	}
	public static void rangerRegen(Player player) {
		cancelRegen(player);
		
		RunnableRegen runner = new RunnableRegen(player);
		regenerates.put(player, runner);
		
		runner.runTaskTimer(Main.instance, 40L, 5L);
	}
	public static void mageRegen(Player player) {
		cancelRegen(player);
		
		RunnableRegen runner = new RunnableRegen(player);
		regenerates.put(player, runner);
		
		runner.runTaskTimer(Main.instance, 80L, 6L);
	}
	
	public static void cancelRegen(Player player) {
		RunnableRegen runner = regenerates.get(player);
		if(runner!=null && !runner.isCancelled())
			runner.cancel();
		regenerates.remove(player);
	}
}