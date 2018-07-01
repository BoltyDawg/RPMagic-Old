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
		Player p = ((Player)sender);
		if(Main.scoreboard.getObjective("alive").getScore(p.getName()).getScore()!=0) {p.sendMessage(ChatColor.GRAY+"Only dead people can use this."); return true;}
		else if(args==null|| args.length<2) return false;
		
		Main.mages.remove(p.getUniqueId());
		Main.leftHands.remove(p.getUniqueId());
		p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
		
		if(Main.beacons.containsKey(p.getUniqueId())) {
			for(Entity e : p.getWorld().getNearbyEntities(Main.beacons.get(p.getUniqueId()),1,1,1)) {
				if(e.getType().equals(EntityType.ARMOR_STAND)) {
					e.remove();
				}	
			}
			Main.beacons.get(p.getUniqueId()).getBlock().breakNaturally();
			Main.beacons.remove(p.getUniqueId());
		}
		Main.scoreboard.getObjective("alive").getScore(p.getName()).setScore(1);
		//TODO do all the stuff that was in the spawn chunk: mcmmo reset, give kits
		//TODO setup / reset their boss bars
		
		String name = "";
		if(Main.nick) {
			int i;
			for(i=1;i<args.length-1;i++) {
				name = name+args[i]+" ";
			}
			name = name + args[i];
			if(name.length()>24) {p.sendMessage(ChatColor.GRAY+("Character name is too long ("+name.length()+"), must be at most 24 characters")); return true;}
			Nick vallone = new Nick(p);
			vallone.set(name);
		}
		else name = p.getName();
		
		String cla = args[0];
		if(cla.equalsIgnoreCase("Fighter")) {
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+p.getName()+" parent add fighter");
			Main.scoreboard.getObjective("class").getScore(p.getName()).setScore(1);
			Main.scoreboard.getObjective("Stamina").getScore(p.getName()).setScore(Main.BASE_STAM);
			p.sendMessage(ChatColor.GRAY+"You are now a "+ChatColor.RED+"Fighter!");
		}
		else if(cla.equalsIgnoreCase("Mage")) {
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+p.getName()+" parent add mage");
			Main.scoreboard.getObjective("class").getScore(p.getName()).setScore(2);
			Main.scoreboard.getObjective("Magicka").getScore(p.getName()).setScore(Main.BASE_MAG);
			Main.mages.put(p.getUniqueId(), new ArrayList<String>());
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"getwand "+p.getName());
			p.sendMessage(ChatColor.GRAY+"You are now a "+ChatColor.BLUE+"Mage!");
		}
		else if(cla.equalsIgnoreCase("Ranger")) {
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+p.getName()+" parent add ranger");
			Main.scoreboard.getObjective("class").getScore(p.getName()).setScore(3);
			Main.scoreboard.getObjective("Stamina").getScore(p.getName()).setScore(Main.BASE_STAM);
			p.sendMessage(ChatColor.GRAY+"You are now a "+ChatColor.DARK_GREEN+"Ranger!");
		}
		else {
			p.sendMessage(ChatColor.GRAY+"Invalid class name, your options are:\n"+ChatColor.RED+"Fighter   "+ChatColor.BLUE+"Mage   "+ChatColor.DARK_GREEN+"Ranger");
			return true;
		}
		
		ItemStack journal = new ItemStack(Material.BOOK_AND_QUILL);
		BookMeta jmet = (BookMeta)journal.getItemMeta();
		jmet.addPage("");
		jmet.setDisplayName(name+"'s Journal");
		journal.setItemMeta(jmet);
		p.getInventory().addItem(journal);
		
		return true;
	}

}
