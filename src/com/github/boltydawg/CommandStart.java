package com.github.boltydawg;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;
import io.loyloy.nicky.Nick;
import net.md_5.bungee.api.ChatColor;

public class CommandStart implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub 
		if(!(sender instanceof Player)) {sender.sendMessage("this command is only for players, you machine"); return true;}
		Player p = ((Player)sender);
		Score score = p.getScoreboard().getObjective("class").getScore(p.getName());
		if(score.getScore()!=0) {p.sendMessage(ChatColor.GRAY+"Only dead people can use this."); return true;}
		else if(args==null|| args.length<2) return false;
		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "rpmage delete "+p.getName());
		
		if(Main.nick) {
			String name = "";
			int i;
			for(i=1;i<args.length-1;i++) {
				name = name+args[i]+" ";
			}
			name = name + args[i];
			if(name.length()>24) {p.sendMessage(ChatColor.GRAY+("Character name is too long ("+name.length()+"), must be at most 24 characters")); return true;}
			Nick vallone = new Nick(p);
			vallone.set(name);
		}
		
		String cla = args[0];
		if(cla.equalsIgnoreCase("Fighter")) {
			score.setScore(1);
			p.sendMessage(ChatColor.GRAY+"You are now a "+ChatColor.RED+"Fighter!");
		}
		else if(cla.equalsIgnoreCase("Mage")) {
			score.setScore(2);
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "rpmage create "+p.getName());
			p.sendMessage(ChatColor.GRAY+"You are now a "+ChatColor.BLUE+"Mage!");
		}
		else if(cla.equalsIgnoreCase("Ranger")) {
			score.setScore(3);
			p.sendMessage(ChatColor.GRAY+"You are now a "+ChatColor.DARK_GREEN+"Ranger!");
		}
		else {
			p.sendMessage(ChatColor.GRAY+"Invalid class name, your options are:\n"+ChatColor.RED+"Fighter   "+ChatColor.BLUE+"Mage   "+ChatColor.DARK_GREEN+"Ranger");
			return true;
		}
		//do all the stuff that was in the spawn chunk: permissions, mcmmo reset, give items
		return true;
	}

}
