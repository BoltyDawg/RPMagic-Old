package com.github.boltydawg;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandLocal implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length==0) {
			if(Main.local) {
				Main.local=false;
				Main.instance.getServer().broadcastMessage(ChatColor.YELLOW+"Chat is no longer localized");
			}
			else {
				Main.local=true;
				Main.instance.getServer().broadcastMessage(ChatColor.YELLOW+"Chat is now localized");
			}
			return true;
		}
		else
			return false;
	}

}
