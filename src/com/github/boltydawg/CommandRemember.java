package com.github.boltydawg;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CommandRemember implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player)sender;
		String message = ChatColor.DARK_GREEN+("* "+Main.getName(player)+" will remember that *");
		for(Entity e: player.getNearbyEntities(100, 50, 100)) {
			if(e instanceof Player)
				((Player)e).sendMessage(message);
		}
		player.sendMessage(message);
		return true;
	}

}
