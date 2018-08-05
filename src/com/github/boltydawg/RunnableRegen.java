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
		if(!Main.bars.containsKey(player)) {
			cancel();
			return;
		}
		//Mages
		if(clazz==2) {
			int sc = Main.scoreboard.getObjective("Magicka").getScore(player.getName()).getScore();
			if(sc+1>=Main.attributes.getOrDefault(player,0)+Main.BASE_MAG) {
				Main.scoreboard.getObjective("Magicka").getScore(player.getName()).setScore(Main.attributes.getOrDefault(player,0)+Main.BASE_MAG);
				regenerates.remove(player);
				Main.bars.get(player).setProgress(1.0);
				new BukkitRunnable(){
			        @Override
			        public void run(){
			        	if(Main.bars.containsKey(player) && Main.bars.get(player).getProgress()==1.0)
			        		Main.bars.get(player).setVisible(false);
			        }
			   }.runTaskLater(Main.instance, 70);
				cancel();
				return;
			}
			else {
				Main.scoreboard.getObjective("Magicka").getScore(player.getName()).setScore(sc+1);
				Main.bars.get(player).setProgress(((double)sc+1)/(Main.BASE_MAG+Main.attributes.getOrDefault(player,0)));
			}
				
		}
		//Fighters and Rangers----------------------------------------------
		else {
			int sc = Main.scoreboard.getObjective("Stamina").getScore(player.getName()).getScore();
			if(sc+1>=Main.attributes.getOrDefault(player,0)+Main.BASE_STAM) {
				Main.scoreboard.getObjective("Stamina").getScore(player.getName()).setScore(Main.attributes.getOrDefault(player,0)+Main.BASE_STAM);
				regenerates.remove(player);
				Main.bars.get(player).setProgress(1.0);
				new BukkitRunnable(){
			        @Override
			        public void run(){
			        	if(Main.bars.containsKey(player) && Main.bars.get(player).getProgress()==1.0)
			        		Main.bars.get(player).setVisible(false);
			        }
			   }.runTaskLater(Main.instance, 70);
				cancel();
				return;
			}
			else {
				Main.scoreboard.getObjective("Stamina").getScore(player.getName()).setScore(sc+1);
				Main.bars.get(player).setProgress(((double)sc+1)/(Main.BASE_STAM+Main.attributes.getOrDefault(player,0)));
			}
				
		}
	}
	//11.5 seconds to regen 100 Stamina
	public static void fighterRegen(Player player) {
		cancelRegen(player);
		
		RunnableRegen runner = new RunnableRegen(player);
		regenerates.put(player, runner);
		
		Main.bars.get(player).setProgress(((double)Main.scoreboard.getObjective("Stamina").getScore(player.getName()).getScore())/(Main.BASE_STAM+Main.attributes.getOrDefault(player,0)));
		
		runner.runTaskTimer(Main.instance, 30L, 2L);
	}
	//12.2 seconds to regen 100 Stamina
	public static void rangerRegen(Player player) {
		cancelRegen(player);
		
		RunnableRegen runner = new RunnableRegen(player);
		regenerates.put(player, runner);
		
		Main.bars.get(player).setProgress(((double)Main.scoreboard.getObjective("Stamina").getScore(player.getName()).getScore())/(Main.BASE_STAM+Main.attributes.getOrDefault(player,0)));
		
		runner.runTaskTimer(Main.instance, 1L, 3L);
	}
	public static void sprintingRegen(Player player) {
		cancelRegen(player);
		
		RunnableRegen runner = new RunnableRegen(player);
		regenerates.put(player, runner);
		
		Main.bars.get(player).setProgress(((double)Main.scoreboard.getObjective("Stamina").getScore(player.getName()).getScore())/(Main.BASE_STAM+Main.attributes.getOrDefault(player,0)));
		
		runner.runTaskTimer(Main.instance, 40L, 5L);
	}
	//takes 35 seconds to regen 100 Magicka
	public static void mageRegen(Player player) {
		cancelRegen(player);
		
		RunnableRegen runner = new RunnableRegen(player);
		regenerates.put(player, runner);
		
		Main.bars.get(player).setProgress(((double)Main.scoreboard.getObjective("Magicka").getScore(player.getName()).getScore())/(Main.BASE_MAG+Main.attributes.getOrDefault(player,0)));
		
		runner.runTaskTimer(Main.instance, 100L, 6L);
	}
	
	public static void cancelRegen(Player player) {
		if(!Main.bars.containsKey(player)) {
			return;
		}
		if(!Main.bars.get(player).isVisible())
			Main.bars.get(player).setVisible(true);
		
		RunnableRegen runner = regenerates.get(player);
		if(runner!=null && !runner.isCancelled())
			runner.cancel();
		regenerates.remove(player);
	}
}