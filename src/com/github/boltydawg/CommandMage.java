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
		if(args[0].equals("create")) {
			if(p==null) {sender.sendMessage("Invalid player name"); return true;}
			else {
				File f = new File("plugins\\RPMagic\\"+p.getUniqueId().toString()+".ser");
				try{f.createNewFile();}
				catch(IOException o) {o.printStackTrace();}
				
				try {
					Mage mag = new Mage(p.getScoreboard().getObjective("subclass").getScore(args[1]).getScore());
					FileOutputStream fos = new FileOutputStream("plugins\\RPMagic\\"+p.getUniqueId().toString()+".ser");
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.reset();
					oos.writeObject(mag);
					oos.close();
					p.getScoreboard().getObjective("class").getScore(args[1]).setScore(2);
					sender.sendMessage("Mage created!");
					return true;
				}
				catch(FileNotFoundException e){
					Bukkit.broadcastMessage("File not found in MageCommand!");
					e.printStackTrace();
				}
				catch(IOException e) {
					Bukkit.broadcastMessage("IOException in MageCommand!");
					e.printStackTrace();
				}
				return true;
			}
		}
		else if(args[0].equals("delete")) {
			if(p==null) {sender.sendMessage("Invalid player name"); return true;}
			else {
				File f = new File("plugins\\RPMagic\\"+p.getUniqueId().toString()+".ser");
				f.delete();
				sender.sendMessage("Mage deleted!");
				return true;
			}
		}
		return false;
	}

}
