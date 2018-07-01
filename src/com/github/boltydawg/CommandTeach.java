package com.github.boltydawg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

/**
 * This class is responsible for storing new spells
 * that a mage knows
 * @author BoltyDawg
 */
public class CommandTeach implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args == null || args.length!=2) return false;
		Player p = Bukkit.getPlayer(args[0]);
		if (p == null) {sender.sendMessage("Invalid player name"); return true;}
		else if(Main.scoreboard.getObjective("class").getScore(p.getName()).getScore()!=2) {sender.sendMessage("Only Mages can learn spells!"); return true;}
		else if(!Spells.isValid(args[1])) {sender.sendMessage("Invalid spell"); return true;}
		else if(Main.mages.get(p.getUniqueId()).contains(args[1])) {p.sendMessage(ChatColor.GRAY+"You already know this spell"); return true;}
		else {
			if(Main.mages.get(p.getUniqueId()).size()>=7) {
				p.sendMessage(ChatColor.DARK_RED+"Your head is full of too much knowledge, you must first forget one of your current spells to learn this one!"+ChatColor.GRAY+"\n(use /forget)");
				return true;
			}
			else {
				Main.mages.get(p.getUniqueId()).add(args[1]);
				p.sendMessage(ChatColor.BLUE+"You learned "+Spells.getColor(args[1])+(args[1]+"!"));
				p.getInventory().removeItem(CommandForget.getTome(args[1]));
				return true;
			}
		}
	}
}
