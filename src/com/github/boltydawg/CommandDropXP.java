package com.github.boltydawg;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import java.util.ArrayList;
import io.loyloy.nicky.Nick;

public class CommandDropXP implements CommandExecutor {

	//TODO gonna have to make them drop a custom potion that has their xp amount in it
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player p = ((Player) sender);
		int xp = Experience.getExp(p)-7;
		if(xp>0) {
			ItemStack bottle = new ItemStack(Material.POTION);
			PotionMeta met = (PotionMeta)bottle.getItemMeta();
			ArrayList<String> l = new ArrayList<String>(1);
			l.add(ChatColor.YELLOW.toString()+xp+ChatColor.YELLOW+" orbs");
			met.setLore(l);
			met.setColor(Color.GREEN);
			met.setDisplayName(ChatColor.GREEN+new Nick(p).get()+ChatColor.GREEN+"'s XP");
			met.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			bottle.setItemMeta(met);
			p.getInventory().addItem(bottle);
			Experience.changeExp(p, -xp);
			return true;
		}
		p.sendMessage("You need at least over 1 level of xp");
		return true;
	}
}
