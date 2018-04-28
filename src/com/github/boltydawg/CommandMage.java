package com.github.boltydawg;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.bukkit.entity.Player;

/**
 * This class deals with the creation
 * and deletion of mage.ser files
 * @author BoltyDawg
 */
public class CommandMage implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args == null || args.length!=2) return false;
		Player p = Bukkit.getPlayer(args[1]);
		if(p==null) {sender.sendMessage("Invalid player name"); return true;}
		if(args[0].equalsIgnoreCase("create")) {
			File f = new File("plugins\\RPMagic\\"+p.getUniqueId().toString()+".ser");
			try{f.createNewFile();}
			catch(IOException o) {o.printStackTrace();}
			
			try {
				FileOutputStream fos = new FileOutputStream("plugins\\RPMagic\\"+p.getUniqueId().toString()+".ser");
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.reset();
				oos.writeObject(new Mage());
				oos.close();
				p.getScoreboard().getObjective("class").getScore(args[1]).setScore(2);
				Main.instance.getLogger().info("Mage created");
				return true;
			}
			catch(FileNotFoundException e){
				sender.sendMessage("File not found in MageCommand!");
				e.printStackTrace();
			}
			catch(IOException e) {
				sender.sendMessage("IOException in MageCommand!");
				e.printStackTrace();
			}
			return true;
		}
		else if(args[0].equals("delete")) {
			File f = new File("plugins\\RPMagic\\"+p.getUniqueId().toString()+".ser");
			f.delete();
			Main.instance.getLogger().info("Mage deleted");
			return true;
		}
		return false;
	}
}
