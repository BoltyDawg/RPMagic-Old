package com.github.boltydawg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import java.util.Timer;  
import java.util.TimerTask;


/**
 * This interface splits up all the cast commands into the
 * mage's respective class.
 * @author BoltyDawg
 *
 */
public interface SubSpells {
	//TODO Remove cool down reductions?
	static void cast(Player player, String spell) {
		switch(spell) {
			case"test":{
				player.chat(ChatColor.GRAY+ChatColor.ITALIC.toString()+"Testistimus!");
				player.sendMessage("Your subclass score is "+player.getScoreboard().getObjective("subclass").getScore(player.getName()).getScore());
				break;
			}
			case"missile":{
				Timer t = new Timer();
				player.chat(ChatColor.GRAY+ChatColor.ITALIC.toString()+"Destuncion!");
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"castp "+player.getName()+" missile range 12 cooldown_reduction 1");
				for(int i=1;i<4;i++) {
					t.schedule(  
						    new TimerTask() {
						        @Override
						        public void run() {
						        	Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"castp "+player.getName()+" missile range 12 cooldown_reduction 1");
						        }
						    },
						    i*1000
						);
				}
				break;
			}
			default: player.sendMessage("You can't cast this spell! ");
		}
		player.getInventory().setItemInOffHand(Main.leftHands.get(player));
	}
	
	public class Alch implements SubSpells{
		public static void cast(Player player, String spell) {
			switch(spell) {
				case "platform":{
					player.chat(ChatColor.GRAY+ChatColor.ITALIC.toString()+"Extonda!");
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"castp "+player.getName()+" platform range 5 cooldown_reduction 1");
					break;
				}
				default: SubSpells.cast(player, spell);
			}
			player.getInventory().setItemInOffHand(Main.leftHands.get(player));
		}
	}
	
	public class Arca implements SubSpells{
		public static void cast(Player player, String spell) {
			switch(spell) {
				case "day":{
					player.chat(ChatColor.GRAY+ChatColor.ITALIC.toString()+"DIVONLA!");
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"castp "+player.getName()+" day cooldown_reduction 1");
					break;
				}
				default: SubSpells.cast(player, spell);
			}
			player.getInventory().setItemInOffHand(Main.leftHands.get(player));
		}
	}
	
	public class Necro implements SubSpells{
		@SuppressWarnings("deprecation")
		public static void cast(Player player, String spell) {
			switch(spell) {
				case"skel":{
				//player.getWorld().spawnEntity(player.getLocation(), EntityType.SKELETON);
					player.chat(ChatColor.GRAY+ChatColor.ITALIC.toString()+"Remedorus!");
					player.getScoreboard().getPlayerTeam(player).addPlayer(player);
					ConsoleCommandSender cons = Bukkit.getServer().getConsoleSender();
					String cmd1 = "execute "+player.getName()+" ~ ~ ~ /summon skeleton ~ ~1 ~ {CustomName:\""+player.getName()+"'s Minion\",CustomNameVisible:1,HandItems:[{id:\"minecraft:bow\",Count:1b,tag:{ench:[{id:48,lvl:2}]}},{}],HandDropChances:[0.0F,0.085F]}";
					//String cmd1 = "execute "+player.getName()+" ~ ~ ~ /summon wolf ~ ~1 ~ {Angry:1,CollarColor:0,Sitting:0}";
					String cmd2 = "execute "+player.getName()+" ~ ~ ~ /scoreboard teams join "+player.getScoreboard().getPlayerTeam(player).getName()+" @e[r=1,type=Skeleton]";
					Bukkit.dispatchCommand(cons, cmd1);
					Bukkit.dispatchCommand(cons, cmd2);
					break;
				}
			default: SubSpells.cast(player, spell);
			}
			player.getInventory().setItemInOffHand(Main.leftHands.get(player));
		}
	}
	
	public class Pyro implements SubSpells{
		public static void cast(Player player, String spell) {
			switch(spell) {
				case"boom":{
					player.chat(ChatColor.GRAY+ChatColor.ITALIC.toString()+"Expulstiva!");
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"castp "+player.getName()+" boom cooldown_reduction 1");
					break;
				}
				default: SubSpells.cast(player, spell);
			}
			player.getInventory().setItemInOffHand(Main.leftHands.get(player));
		}
	}
}
