package com.github.boltydawg;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import io.loyloy.nicky.Nick;
import net.md_5.bungee.api.ChatColor;

public class CommandStart implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {sender.sendMessage("this command is only for players, you machine"); return true;}
		Player player = ((Player)sender);
		if(Main.scoreboard.getObjective("alive").getScore(player.getName()).getScore()!=0) {player.sendMessage(ChatColor.GRAY+"Only dead people can use this."); return true;}
		else if(args==null|| args.length<2) return false;
		
		Main.mages.remove(player.getUniqueId());
		Main.leftHands.remove(player.getUniqueId());
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
		
		if(Main.beacons.containsKey(player.getUniqueId())) {
			for(Entity e : player.getWorld().getNearbyEntities(Main.beacons.get(player.getUniqueId()),1,1,1)) {
				if(e.getType().equals(EntityType.ARMOR_STAND)) {
					e.remove();
				}	
			}
			Main.beacons.get(player.getUniqueId()).getBlock().breakNaturally();
			Main.beacons.remove(player.getUniqueId());
		}
		Main.scoreboard.getObjective("alive").getScore(player.getName()).setScore(1);
		//TODO do all the stuff that was in the spawn chunk: mcmmo reset, give kits
		//TODO setup / reset their boss bars
		
		String name = "";
		if(Main.nick) {
			int i;
			for(i=1;i<args.length-1;i++) {
				name = name+args[i]+" ";
			}
			name = name + args[i];
			if(name.length()>16) {player.sendMessage(ChatColor.GRAY+("Character name is too long ("+name.length()+"), must be at most 16 characters")); return true;}
			Nick vallone = new Nick(player);
			vallone.set(name);
		}
		else name = player.getName();
		
		String cla = args[0];
		if(cla.equalsIgnoreCase("Fighter")) {
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+player.getName()+" parent add fighter");
			Main.scoreboard.getObjective("class").getScore(player.getName()).setScore(1);
			Main.scoreboard.getObjective("Stamina").getScore(player.getName()).setScore(Main.BASE_STAM);
			
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"title "+player.getName()+" title \""+ChatColor.RED+("Welcome, "+name)+"\"");
			player.sendMessage(ChatColor.GRAY+"You are now a "+ChatColor.RED+"Fighter!");
		}
		else if(cla.equalsIgnoreCase("Mage")) {
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+player.getName()+" parent add mage");
			Main.scoreboard.getObjective("class").getScore(player.getName()).setScore(2);
			Main.scoreboard.getObjective("Magicka").getScore(player.getName()).setScore(Main.BASE_MAG);
			Main.mages.put(player.getUniqueId(), new ArrayList<String>());
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"getwand "+player.getName());
			
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"title "+player.getName()+" title \""+ChatColor.BLUE+("Welcome, "+name)+"\"");
			player.sendMessage(ChatColor.GRAY+"You are now a "+ChatColor.BLUE+"Mage!");
		}
		else if(cla.equalsIgnoreCase("Ranger")) {
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+player.getName()+" parent add ranger");
			Main.scoreboard.getObjective("class").getScore(player.getName()).setScore(3);
			Main.scoreboard.getObjective("Stamina").getScore(player.getName()).setScore(Main.BASE_STAM);
			
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"title "+player.getName()+" title \""+ChatColor.DARK_GREEN+("Welcome, "+name)+"\"");
			player.sendMessage(ChatColor.GRAY+"You are now a "+ChatColor.DARK_GREEN+"Ranger!");
		}
		else {
			player.sendMessage(ChatColor.GRAY+"Invalid class name, your options are:\n"+ChatColor.RED+"Fighter   "+ChatColor.BLUE+"Mage   "+ChatColor.DARK_GREEN+"Ranger");
			return true;
		}
		
		ItemStack journal = new ItemStack(Material.BOOK_AND_QUILL);
		BookMeta jmet = (BookMeta)journal.getItemMeta();
		jmet.addPage("");
		jmet.setDisplayName(name+"'s Journal");
		journal.setItemMeta(jmet);
		player.getInventory().addItem(journal);
		
		return true;
	}

}
