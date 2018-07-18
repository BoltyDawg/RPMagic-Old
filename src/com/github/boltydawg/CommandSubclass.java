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
		Player player = Bukkit.getPlayer(args[0]);
		if (player == null) {sender.sendMessage("Invalid player name");return true;}
		if(Main.scoreboard.getObjective("subclass").getScore(player.getName()).getScore()!=0) {player.sendMessage(ChatColor.LIGHT_PURPLE+"You already chose your subclass!"); return true;}
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
		
		switch(args[1]) {
			case "barbarian":{
				if(Main.scoreboard.getObjective("class").getScore(player.getName()).getScore()!=1) {player.sendMessage(ChatColor.DARK_GRAY+"Only a Fighter can choose to be a BARBARIAN"); return true;}
				ItemStack[] conts = player.getInventory().getArmorContents();
				boolean t = false;
				if(conts!=null && conts.length>0) {
					for(ItemStack is: conts) {
						if(is==null) continue;
						else if(!is.getType().equals(Material.AIR)){
							t = true;
							player.getInventory().addItem(is);
						}
					}
					if(t)
						player.sendMessage(ChatColor.DARK_PURPLE+"YOUR MUSCLES BECOME SO LARGE THAT YOUR ARMOR POPS OFF");
				}
				Nick vallone = new Nick(player);
				vallone.set(vallone.get().toUpperCase());
				ItemStack bone = new ItemStack(Material.BONE);
				ItemMeta met = bone.getItemMeta();
				met.setDisplayName(Main.getName(player)+" FIRST BONE");
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("IT TASTE LIKE MOTHER");
				met.setLore(lore);
				bone.setItemMeta(met);
				player.getInventory().setArmorContents(new ItemStack[] {null,null,null,bone});
				for(ItemStack s : player.getInventory().getContents()) {
					if(s!=null && s.getType()==Material.BOOK_AND_QUILL) {
						String display = s.getItemMeta().getDisplayName();
						if(display.equalsIgnoreCase(Main.getName(player)+"'s Journal"))
							s.getItemMeta().setDisplayName(display.toUpperCase());
					}
				}
				player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
				player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(8);
				player.setHealth(40);
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+player.getName()+" Unarmed 100");
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+player.getName()+" parent add barbarian");
				Main.scoreboard.getObjective("subclass").getScore(player.getName()).setScore(1);
				player.sendMessage(ChatColor.DARK_AQUA+"YOU ARE NOW A BARBARIAN");
				break;
			}
			case "knight":{
				if(Main.scoreboard.getObjective("class").getScore(player.getName()).getScore()!=1) {player.sendMessage(ChatColor.DARK_GRAY+"Only a Fighter can choose to be a Knight"); return true;}
				
				//TODO update to 1.13
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"give "+player.getName()+" spawn_egg 1 0 {EntityTag:{id:\"minecraft:horse\",Variant:1028,CustomName:\""+Main.getName(player)+"'s Steed\",Attributes:[{Name:\"generic.movementSpeed\",Base:0.280f},{Name:\"horse.jumpStrength\",Base:0.75f},{Name:\"generic.maxHealth\",Base:60}],Health:60,SaddleItem:{id:saddle,Count:1},Age:0,Tame:1,Tags:[\""+Main.getName(player)+" steed\"]}}");
				ItemStack is = new ItemStack(Material.RED_SHULKER_BOX);
				ItemMeta met = is.getItemMeta();
				met.setDisplayName("Red Backpack");
				is.setItemMeta(met);
				player.getInventory().addItem(is);
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+player.getName()+" Taming 100");
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+player.getName()+" parent add knight");
				Main.scoreboard.getObjective("subclass").getScore(player.getName()).setScore(2);
				player.sendMessage(ChatColor.DARK_AQUA+"You are now a Knight!");
				break;
			}
			//TODO, makka roo
			case "hoplite":{
				if(Main.scoreboard.getObjective("class").getScore(player.getName()).getScore()!=1) {player.sendMessage(ChatColor.DARK_GRAY+"Only a Fighter can choose to be a Paladin"); return true;}
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+player.getName()+" Swords 100");
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+player.getName()+" parent add hoplite");
				Main.scoreboard.getObjective("subclass").getScore(player.getName()).setScore(3);
				player.sendMessage(ChatColor.DARK_AQUA+"You are now a Hoplite!");
				break;
			}
			case"tank":{
				if(Main.scoreboard.getObjective("class").getScore(player.getName()).getScore()!=1) {player.sendMessage(ChatColor.DARK_GRAY+"Only a Fighter can choose to be a Tank"); return true;}
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+player.getName()+" Axes 75");
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+player.getName()+" parent add tank");
				Main.scoreboard.getObjective("subclass").getScore(player.getName()).setScore(4);
				player.sendMessage(ChatColor.DARK_AQUA+"You are now a Tank!");
				break;
			}
			case"angel":{
				if(Main.scoreboard.getObjective("class").getScore(player.getName()).getScore()!=3) {player.sendMessage(ChatColor.DARK_GRAY+"Only a Ranger can choose to be an Angel"); return true;}
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+player.getName()+" Acrobatics 75");
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+player.getName()+" parent add angel");
				Main.scoreboard.getObjective("subclass").getScore(player.getName()).setScore(5);
				player.sendMessage(ChatColor.DARK_AQUA+"You are now an Angel!");
				break;
			}
			case"bowman":{
				if(Main.scoreboard.getObjective("class").getScore(player.getName()).getScore()!=3) {player.sendMessage(ChatColor.DARK_GRAY+"Only a Ranger can choose to be an Arcane Bowman"); return true;}
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+player.getName()+" Archery 75");
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+player.getName()+" parent add bowman");
				Main.scoreboard.getObjective("subclass").getScore(player.getName()).setScore(6);
				player.sendMessage(ChatColor.DARK_AQUA+"You are now an Arcane Bowman!");
				break;
			}
			case"assassin":{
				if(Main.scoreboard.getObjective("class").getScore(player.getName()).getScore()!=3) {player.sendMessage(ChatColor.DARK_GRAY+"Only a Ranger can choose to be an Assassin"); return true;}
				player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(16);
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"give "+player.getName()+" minecraft:golden_sword 1 0 {repairCost:\"200\",display:{Name:\"Assassin's Dagger\",Lore:[\"...the blade is a promise\"]},AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Slot:\"mainhand\",Amount:10,Operation:0,UUIDMost:68455,UUIDLeast:118238},{AttributeName:\"generic.attackSpeed\",Name:\"generic.attackSpeed\",Slot:\"mainhand\",Amount:-4.0,Operation:0,UUIDMost:85443,UUIDLeast:178550}],HideFlags:2}");
				ItemStack clock = new ItemStack(Material.WATCH);
				ItemMeta met = clock.getItemMeta();
				met.setDisplayName(ChatColor.AQUA+"Pocket Watch");
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(ChatColor.GRAY+"Was there ever any doubt?");
				met.setLore(lore);
				clock.setItemMeta(met);
				player.getInventory().addItem(clock);
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+player.getName()+" parent add assassin");
				Main.scoreboard.getObjective("subclass").getScore(player.getName()).setScore(7);
				player.sendMessage(ChatColor.DARK_AQUA+"You are now an Assassin!");
				break;
			}
			case"scout":{
				if(Main.scoreboard.getObjective("class").getScore(player.getName()).getScore()!=3) {player.sendMessage(ChatColor.DARK_GRAY+"Only a Ranger can choose to be a Scout"); return true;}
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"mmoedit "+player.getName()+" Taming 100");
				
				//TODO update to 1.13
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"give "+player.getName()+" spawn_egg 1 0 {EntityTag:{id:\"minecraft:horse\",Variant:0,CustomName:\""+Main.getName(player)+"'s Elybris\",Attributes:[{Name:\"generic.movementSpeed\",Base:0.33295f},{Name:\"horse.jumpStrength\",Base:0.75f},{Name:\"generic.maxHealth\",Base:24}],Health:24,SaddleItem:{id:saddle,Count:1},Age:0,Tame:1,Tags:[\""+Main.getName(player)+" elybris\"]}}");
				ItemStack is = new ItemStack(Material.BROWN_SHULKER_BOX);
				ItemMeta met = is.getItemMeta();
				//TODO rename these
				met.setDisplayName("Brown Backpack");
				is.setItemMeta(met);
				player.getInventory().addItem(is);
				
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+player.getName()+" parent add scout");
				Main.scoreboard.getObjective("subclass").getScore(player.getName()).setScore(8);
				player.sendMessage(ChatColor.DARK_AQUA+"You are now a Scout!");
				break;
			}
			default:{
				player.sendMessage(ChatColor.GRAY+"Invalid subclass name! (everything is in lower case)");
			}
		}
		return true;
	}

}
