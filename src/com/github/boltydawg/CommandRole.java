package com.github.boltydawg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRole implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args == null || args.length!=2) return false;
		Player player = Bukkit.getPlayer(args[0]);
		if (player == null) {sender.sendMessage("Invalid player name");return true;}
		String name = player.getName();
		if(Main.scoreboard.getObjective("role").getScore(name).getScore()!=0) {player.sendMessage(ChatColor.LIGHT_PURPLE+"You already chose your role!"); return true;}
		switch(args[1]) {
			case "blacksmith":{
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+name+" parent add blacksmith");
				Main.scoreboard.getObjective("role").getScore(name).setScore(2);
				player.sendMessage(ChatColor.GREEN+"You are now a blacksmith!");
				break;
			}
			case "enchanter":{
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+name+" parent add enchanter");
				Main.scoreboard.getObjective("role").getScore(name).setScore(2);
				player.sendMessage(ChatColor.GREEN+"You are now an enchanter/enchantress!");
				break;
			}
			case "merchant":{
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+name+" parent add merchant");
				Main.scoreboard.getObjective("role").getScore(name).setScore(3);
				player.sendMessage(ChatColor.GREEN+"You are now a merchant!");
				break;
			}
			case "researcher":{
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+name+" Alchemy 100");
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+name+" parent add researcher");
				Main.scoreboard.getObjective("role").getScore(name).setScore(4);
				player.sendMessage(ChatColor.GREEN+"You are now a researcher!");
				break;
			}
			case "sheriff":{
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+name+" parent add sheriff");
				Main.scoreboard.getObjective("role").getScore(name).setScore(5);
				player.sendMessage(ChatColor.GREEN+"You are now a sheriff!");
				break;
			}
			case "worker":{
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+name+" Herbalism 75");
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+name+" Excavation 75");
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+name+" Fishing 75");
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+name+" Mining 75");
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+name+" Woodcutting 75");
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+name+" parent add worker");
				Main.scoreboard.getObjective("role").getScore(name).setScore(6);
				player.sendMessage(ChatColor.GREEN+"You are now a worker!");
				break;
			}
		}
		return true;
	}

}
