package com.github.boltydawg;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import io.loyloy.nicky.Nick;

import java.util.Arrays;
import java.util.List;

/**
 * This class gives a mage his/her wand
 * @author BoltyDawg
 */
public class CommandWand implements CommandExecutor {
	
	public static final List<String> WAND_LORE = Arrays.asList("The source through","which your power flows");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args==null || args.length!=1) return false;
		else{
			Player p = Bukkit.getPlayer(args[0]);
			String nam = getName(p);
			ItemStack item = new ItemStack(Material.STICK);
			ItemMeta meta = item.getItemMeta();
			meta.setLore(WAND_LORE);
			meta.setDisplayName(ChatColor.RESET+nam + "'s Wand");
			meta.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
			meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
			meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
			return true;
		}
	}
	public static String getName(Player p) {
		if(Main.nick==false) return p.getName();
		else {
			String n = new Nick(p).get();
			if(n==null) return p.getName();
			else return n;
		}
	}
}
