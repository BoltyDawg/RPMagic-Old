package com.github.boltydawg;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandForget implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player p = (Player)sender;
		if(Main.scoreboard.getObjective("class").getScore(p.getName()).getScore()!=2) {p.sendMessage(ChatColor.GRAY+"Only Mages can forget spells"); return true;}
		ArrayList<String> lst = Main.mages.getOrDefault(p.getUniqueId(),new ArrayList<String>());
		if(lst.size()==0) {p.sendMessage(ChatColor.GRAY+"You don't know any spells to forget!"); return true;}
		else if(!lst.contains(args[0])) {p.sendMessage("You don't know this spell"); return true;}
		else {
			lst.remove(args[0]);
			Main.mages.replace(p.getUniqueId(), lst);
			p.sendMessage(ChatColor.GRAY+"Suddenly, you can't remember how to cast "+args[0]);
			
			p.getInventory().addItem(getTome(args[0]));
			return true;
		}
	}
	
	public static ItemStack getTome(String spell) {
		ItemStack tome = new ItemStack(Material.BOOK);
		ItemMeta met = tome.getItemMeta();
		met.setDisplayName("Spell Tome");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Contains the knowledge of");
		lore.add(spell);
		met.setLore(lore);
		met.addEnchant(Enchantment.BINDING_CURSE, 1, false);
		met.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		tome.setItemMeta(met);
		return tome;
	}
}
