package com.github.boltydawg;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.loyloy.nicky.Nick;

public class CommandSubclass implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args == null || args.length!=2) return false;
		Player p = Bukkit.getPlayer(args[0]);
		if (p == null) {sender.sendMessage("Invalid player name");return true;}
		if(Main.scoreboard.getObjective("subclass").getScore(p.getName()).getScore()!=0) {p.sendMessage(ChatColor.LIGHT_PURPLE+"You already chose your subclass!");}
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
		
		switch(args[1]) {
			case "barbarian":{
				if(Main.scoreboard.getObjective("class").getScore(p.getName()).getScore()!=1) {p.sendMessage(ChatColor.DARK_GRAY+"Only a Fighter can choose to be a BARBARIAN"); return true;}
				ItemStack[] conts = p.getInventory().getArmorContents();
				boolean t = false;
				if(conts!=null && conts.length>0) {
					for(ItemStack is: conts) {
						if(is==null) continue;
						else if(!is.getType().equals(Material.AIR)){
							t = true;
							p.getInventory().addItem(is);
						}
					}
					if(t)
						p.sendMessage(ChatColor.DARK_PURPLE+"YOUR MUSCLES BECOME SO LARGE THAT YOUR ARMOR POPS OFF");
				}
				new Nick(p).set(Main.getName(p).toUpperCase());
				p.getInventory().setArmorContents(new ItemStack[] {null,null,null,ListenerSubclass.barbarianBone(Main.getName(p))});
				p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
				p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(8);
				p.setHealth(40);
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+p.getName()+" Unarmed 150");
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+p.getName()+" parent add barbarian");
				Main.scoreboard.getObjective("subclass").getScore(p.getName()).setScore(1);
				p.sendMessage(ChatColor.DARK_AQUA+"YOU ARE NOW A BARBARIAN");
				break;
			}
			case "knight":{
				if(Main.scoreboard.getObjective("class").getScore(p.getName()).getScore()!=1) {p.sendMessage(ChatColor.DARK_GRAY+"Only a Fighter can choose to be a Knight"); return true;}
				
				//TODO update to 1.13
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"give "+p.getName()+" spawn_egg 1 0 {EntityTag:{id:\"minecraft:horse\",Variant:1028,CustomName:\""+Main.getName(p)+"'s Steed\",Attributes:[{Name:\"generic.movementSpeed\",Base:0.280f},{Name:\"horse.jumpStrength\",Base:0.75f},{Name:\"generic.maxHealth\",Base:60}],Health:60,SaddleItem:{id:saddle,Count:1},Age:0,Tame:1,Tags:[\""+Main.getName(p)+" steed\"]}}");
				ItemStack is = new ItemStack(Material.RED_SHULKER_BOX);
				ItemMeta met = is.getItemMeta();
				met.setDisplayName("Red Backpack");
				is.setItemMeta(met);
				p.getInventory().addItem(is);
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+p.getName()+" Taming 100");
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+p.getName()+" parent add knight");
				Main.scoreboard.getObjective("subclass").getScore(p.getName()).setScore(2);
				p.sendMessage(ChatColor.DARK_AQUA+"You are now a Knight!");
				break;
			}
			//TODO, makka roo
			case "hoplite":{
				if(Main.scoreboard.getObjective("class").getScore(p.getName()).getScore()!=1) {p.sendMessage(ChatColor.DARK_GRAY+"Only a Fighter can choose to be a Paladin"); return true;}
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+p.getName()+" Swords 100");
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+p.getName()+" parent add hoplite");
				Main.scoreboard.getObjective("subclass").getScore(p.getName()).setScore(3);
				p.sendMessage(ChatColor.DARK_AQUA+"You are now a Hoplite!");
				break;
			}
			case"tank":{
				if(Main.scoreboard.getObjective("class").getScore(p.getName()).getScore()!=1) {p.sendMessage(ChatColor.DARK_GRAY+"Only a Fighter can choose to be a Tank"); return true;}
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+p.getName()+" Axes 75");
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+p.getName()+" parent add tank");
				Main.scoreboard.getObjective("subclass").getScore(p.getName()).setScore(4);
				p.sendMessage(ChatColor.DARK_AQUA+"You are now a Tank!");
				break;
			}
			case"angel":{
				if(Main.scoreboard.getObjective("class").getScore(p.getName()).getScore()!=3) {p.sendMessage(ChatColor.DARK_GRAY+"Only a Ranger can choose to be an Angel"); return true;}
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+p.getName()+" Acrobatics 75");
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+p.getName()+" parent add angel");
				Main.scoreboard.getObjective("subclass").getScore(p.getName()).setScore(5);
				p.sendMessage(ChatColor.DARK_AQUA+"You are now an Angel!");
				break;
			}
			case"bowman":{
				if(Main.scoreboard.getObjective("class").getScore(p.getName()).getScore()!=3) {p.sendMessage(ChatColor.DARK_GRAY+"Only a Ranger can choose to be an Arcane Bowman"); return true;}
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+p.getName()+" Archery 75");
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+p.getName()+" parent add bowman");
				Main.scoreboard.getObjective("subclass").getScore(p.getName()).setScore(6);
				p.sendMessage(ChatColor.DARK_AQUA+"You are now an Arcane Bowman!");
				break;
			}
			case"assassin":{
				if(Main.scoreboard.getObjective("class").getScore(p.getName()).getScore()!=3) {p.sendMessage(ChatColor.DARK_GRAY+"Only a Ranger can choose to be an Assassin"); return true;}
				p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(16);
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"give "+p.getName()+" minecraft:golden_sword 1 0 {repairCost:\"200\",display:{Name:\"Assassin's Dagger\",Lore:[\"...the blade is a promise\"]},AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Slot:\"mainhand\",Amount:10,Operation:0,UUIDMost:68455,UUIDLeast:118238},{AttributeName:\"generic.attackSpeed\",Name:\"generic.attackSpeed\",Slot:\"mainhand\",Amount:-4.0,Operation:0,UUIDMost:85443,UUIDLeast:178550}],HideFlags:2}");
				ItemStack clock = new ItemStack(Material.WATCH);
				ItemMeta met = clock.getItemMeta();
				met.setDisplayName(ChatColor.AQUA+"Pocket Watch");
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(ChatColor.GRAY+"Was there ever any doubt?");
				met.setLore(lore);
				clock.setItemMeta(met);
				p.getInventory().addItem(clock);
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+p.getName()+" parent add assassin");
				Main.scoreboard.getObjective("subclass").getScore(p.getName()).setScore(7);
				p.sendMessage(ChatColor.DARK_AQUA+"You are now an Assassin!");
				break;
			}
			case"scout":{
				if(Main.scoreboard.getObjective("class").getScore(p.getName()).getScore()!=3) {p.sendMessage(ChatColor.DARK_GRAY+"Only a Ranger can choose to be a Scout"); return true;}
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+p.getName()+" Taming 100");
				
				//TODO update to 1.13
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"give "+p.getName()+" spawn_egg 1 0 {EntityTag:{id:\"minecraft:horse\",Variant:0,CustomName:\""+Main.getName(p)+"'s Elybris\",Attributes:[{Name:\"generic.movementSpeed\",Base:0.33295f},{Name:\"horse.jumpStrength\",Base:0.75f},{Name:\"generic.maxHealth\",Base:24}],Health:24,SaddleItem:{id:saddle,Count:1},Age:0,Tame:1,Tags:[\""+Main.getName(p)+" elybris\"]}}");
				ItemStack is = new ItemStack(Material.BROWN_SHULKER_BOX);
				ItemMeta met = is.getItemMeta();
				//TODO rename these
				met.setDisplayName("Brown Backpack");
				is.setItemMeta(met);
				p.getInventory().addItem(is);
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+p.getName()+" parent add scout");
				Main.scoreboard.getObjective("subclass").getScore(p.getName()).setScore(8);
				p.sendMessage(ChatColor.DARK_AQUA+"You are now a Scout!");
				break;
			}
			default:{
				p.sendMessage(ChatColor.GRAY+"Invalid subclass name! (everything is in lower case)");
			}
		}
		return true;
	}

}
