package com.github.boltydawg;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.md_5.bungee.api.chat.TextComponent;
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
		else if(p.getScoreboard().getObjective("class").getScore(p.getName()).getScore()!=2) {sender.sendMessage("Only Mages can learn spells!"); return true;}
		else if(!Spells.isValid(args[1])) {sender.sendMessage("Invalid spell"); return true;}
		else {write(Bukkit.getPlayer(args[0]),args[1]); return true;}
	}
	
	private void write(Player player, String spell) {
		if(!Spells.isRightSubclass(player.getScoreboard().getObjective("subclass").getScore(player.getName()).getScore(), spell)) {
			player.sendMessage("You can't learn this spell.");
			return;
		}
		try {
			FileInputStream fis = new FileInputStream("plugins\\RPMagic\\"+player.getUniqueId().toString()+".ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			Mage result = (Mage)ois.readObject();
			ois.close();
			
			FileOutputStream fos = new FileOutputStream("plugins\\RPMagic\\"+player.getUniqueId().toString()+".ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			if(Spells.isValid(spell)) {
				if(result.spells.contains(spell)) player.sendMessage("You already know this spell!");
				else {result = result.teach(spell); player.sendMessage("You learned a spell!");}
			}
			else Bukkit.broadcastMessage("This message should never be sent, it's just a safety precaution. Please tell Jason if you see this! He'll be amazed");
			
			oos.reset();
			oos.writeObject(result);
			oos.close();
		}
		catch(FileNotFoundException e){
			Bukkit.broadcastMessage("File not found!");
			e.printStackTrace();
		}
		catch(IOException e) {
			Bukkit.broadcastMessage("IOException!");
			e.printStackTrace();
		}
		catch(ClassNotFoundException e) {
			Bukkit.broadcastMessage("ClassNotFoundException!");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param player
	 * @return a TextComponent of the player's known spells
	 */	
	public static TextComponent getSpells(Player player){
		try {
			FileInputStream fis = new FileInputStream("plugins\\RPMagic\\"+player.getUniqueId().toString()+".ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			Mage result = (Mage)ois.readObject();
			ois.close();
			return Spells.toText(result.spells);
		}
		catch(FileNotFoundException e){
			Main.instance.getLogger().warning("Could not find "+player.getName()+"'s file!");
			return null;
		}
		catch(IOException e) {
			Main.instance.getLogger().warning("IOException in "+player.getName()+"'s file!");
			return null;
		}
		catch(ClassNotFoundException e) {
			Main.instance.getLogger().warning("Class not found in "+player.getName()+"'s file!");
			return null;
		}
	}
}
