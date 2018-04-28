package com.github.boltydawg;

import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import com.codingforcookies.armorequip.ArmorEquipEvent;

import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import io.loyloy.nicky.Nick;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;


public class MyListener implements Listener {
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * This listener is very long and has a lot of parts to it,
	 * so I attempted to break it up into sections so it's easier
	 * to understand.
	 * This first section deals with the activation of a Mage's wand,
	 * and showing them a book with their currently known spells
	 * in it. 
	 */
	
	/**
	 * Opens up a book gui on the mage's screen
	 * so he/she can click on what spell they
	 * want to cast
	 * @param player that's opening the book
	 * @param page that contains the spells in the proper formatting
	 */
	public void openBook(Player player, TextComponent page) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle("Choose your spell");
        meta.setAuthor("God Above");
        String jason = ComponentSerializer.toString(page);
        BookUtil.setPages(meta, Arrays.asList(jason));
        book.setItemMeta(meta);
        BookUtil.openBook(book, player);
    }
	
	/**
	 * Listener used for detecting when a Mage uses their wand,
	 * or when a fighter swings their weapon.
	 * @param event
	 */
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(event.getHand() == EquipmentSlot.HAND && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			ItemStack main = player.getInventory().getItemInMainHand();
			if(Material.STICK==main.getType() && CommandWand.WAND_LORE.equals(main.getItemMeta().getLore())) {
				String dName = main.getItemMeta().getDisplayName();
				//Checks if this player is a mage and that the wand is theirs
				if(player.getScoreboard().getObjective("class").getScore(player.getName()).getScore()==2 && dName!=null  && (ChatColor.RESET+CommandWand.getName(player)).equals(dName.substring(0, dName.indexOf("\'")))) {
					ItemStack off = player.getInventory().getItemInOffHand();
					if(off.getType() == Material.KNOWLEDGE_BOOK) {
						player.chat("/rpcast " + off.getItemMeta().getLore().get(0));
					}
					else {
						/**
						 * Accesses the file containing the player's known spells,
						 * and then once they choose a spell it has that book forcibly
						 *	placed in their left hand
						 */
						this.openBook(player,CommandTeach.getSpells(player));
					}
				}
				else {
					player.sendMessage(ChatColor.YELLOW.toString()+ChatColor.BOLD.toString()+ChatColor.ITALIC+"ZAP!"+ChatColor.GRAY+"\n*The wand denies you*");
					player.damage(2.0);
					if(player.isDead()) {
						player.sendMessage(ChatColor.DARK_BLUE.toString()+ChatColor.BOLD+"YOU ABSOLUTE FOOL");
					}
				}
			}
		}
		//This part deals with a Fighter's Stamina whenever they swing a weapon. -----------------------------------------------------------------------------------------------------------------------------------------------
		else if(event.getHand() == EquipmentSlot.HAND && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)&& player.getScoreboard().getObjective("class").getScore(player.getName()).getScore()==1) {			
			String item = player.getInventory().getItemInMainHand().getType().name();
			if(item.contains("SWORD")||item.contains("_AXE")||item.contains("TRIDENT")) {
				int s = player.getScoreboard().getObjective("Stamina").getScore(player.getName()).getScore();
				if(s>=15) {
					player.getScoreboard().getObjective("Stamina").getScore(player.getName()).setScore(s-15);
					player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(5.1);
				}
				else {
					player.getScoreboard().getObjective("Stamina").getScore(player.getName()).setScore(0);
					player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
				}
				if(regening.getOrDefault(player, false)) hitCancel.put(player, true);
				else fregen(player);
			}
			else 
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
		}
	}
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * This section deals with giving players bonuses to their
	 * max Stamina / Magicka if the player is wearing
	 * an armor piece that does so. Uses a map
	 * to store what a player's max amount is. 
	 */
	
	//Map of the current max values of everyone's attributes (Magicka / Stamina)
	private static HashMap<Player,Integer> attributes = new HashMap<Player,Integer>();
	
	private static final long RC = 55l;
	private static final long FC = 70l;
	
	
	/**
	 * Uses Borlea's code to check when a Mage
	 * puts on/takes off armor that has a trait that increases
	 * their max Magicka
	 * @param event
	 */
	@EventHandler
	public void onArmorEquipEvent(ArmorEquipEvent event) {
		Player player = event.getPlayer();
		int score = player.getScoreboard().getObjective("class").getScore(player.getName()).getScore();
		//Fighters
		if(score==1) {
			if(event.getNewArmorPiece()!=null && event.getNewArmorPiece().getItemMeta()!=null) {
				for(String s : event.getNewArmorPiece().getItemMeta().getLore()) {
					if(s==null)continue;
					else if(s.contains("Increase your Stamina by ")) {
						attributes.put(player, Integer.parseInt(s.substring(25))+attributes.getOrDefault(player, 0));
						if(!regening.getOrDefault(player, false))
							fregen(player);
					}
				}
			}
			if(event.getOldArmorPiece()!=null && event.getOldArmorPiece().getItemMeta()!=null) {
				for(String s : event.getOldArmorPiece().getItemMeta().getLore()) {
					if(s==null)continue;
					else if(s.contains("Increase your Stamina by ")) {
						int temp = Integer.parseInt(s.substring(25));
						attributes.put(player, attributes.getOrDefault(player, temp)-temp);
						if(player.getScoreboard().getObjective("Stamina").getScore(player.getName()).getScore()>Main.BASE_STAM+attributes.getOrDefault(player, 0))
							player.getScoreboard().getObjective("Stamina").getScore(player.getName()).setScore(attributes.get(player)+Main.BASE_STAM);
					}
				}
			}
		}
		//Mages
		else if(score==2) {
			if(event.getNewArmorPiece()!=null && event.getNewArmorPiece().getItemMeta()!=null) {
				for(String s : event.getNewArmorPiece().getItemMeta().getLore()) {
					if(s==null)continue;
					else if(s.contains("Increase your Magicka by ")) {
						attributes.put(player, Integer.parseInt(s.substring(25))+attributes.getOrDefault(player, 0));
						player.sendMessage("Your total Magicka is: "+(Main.BASE_MAG+attributes.getOrDefault(player, 0)));
					}
				}
			}
			if(event.getOldArmorPiece()!=null && event.getOldArmorPiece().getItemMeta()!=null) {
				for(String s : event.getOldArmorPiece().getItemMeta().getLore()) {
					if(s==null) continue;
					else if(s.contains("Increase your Magicka by ")) {
						int temp = Integer.parseInt(s.substring(25));
						attributes.put(player, attributes.getOrDefault(player, temp)-temp);
						if(player.getScoreboard().getObjective("Magicka").getScore(player.getName()).getScore()>Main.BASE_MAG+attributes.get(player))
							player.getScoreboard().getObjective("Magicka").getScore(player.getName()).setScore(attributes.get(player)+Main.BASE_MAG);
					}
				}
			}
		}
		//Rangers
		else if(score==3) {
			if(event.getNewArmorPiece()!=null && event.getNewArmorPiece().getItemMeta()!=null) {
				for(String s : event.getNewArmorPiece().getItemMeta().getLore()) {
					if(s==null)continue;
					else if(s.contains("Increase your Stamina by ")) {
						attributes.put(player, Integer.parseInt(s.substring(25))+attributes.getOrDefault(player, 0));
						if(!regening.getOrDefault(player, false))
							regen(player);
					}
				}
			}
			if(event.getOldArmorPiece()!=null && event.getOldArmorPiece().getItemMeta()!=null) {
				for(String s : event.getOldArmorPiece().getItemMeta().getLore()) {
					if(s==null)continue;
					else if(s.contains("Increase your Stamina by ")) {
						int temp = Integer.parseInt(s.substring(25));
						attributes.put(player, attributes.getOrDefault(player, temp)-temp);
						if(player.getScoreboard().getObjective("Stamina").getScore(player.getName()).getScore()>Main.BASE_STAM+attributes.getOrDefault(player, 0))
							player.getScoreboard().getObjective("Stamina").getScore(player.getName()).setScore(attributes.get(player)+Main.BASE_STAM);
					}
				}
			}
		}
	}
	/**
	 * Goes through all of the player's current armor pieces
	 * and stores any Stamina/Magicka increases they may
	 * have equipped. This way they'll still have the proper
	 * buffs when they log in after after a server restart. 
	 * @param event
	 */
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		event.setJoinMessage(ChatColor.GOLD+new Nick(player).get()+ChatColor.YELLOW+" joined the game.");
		int score = player.getScoreboard().getObjective("class").getScore(player.getName()).getScore();
		if(score==1) {
			for(ItemStack is: player.getInventory().getArmorContents()) {
				if(is==null) continue;
				for(String s : is.getItemMeta().getLore()) {
					if(s!=null && s.contains("Increase your Stamina by ")) {
						attributes.put(player, Integer.parseInt(s.substring(25))+attributes.getOrDefault(player, 0));
					}
				}
			}
			fregen(player);
		}
		else if(score==2) {
			for(ItemStack is: player.getInventory().getArmorContents()) {
				if(is==null) continue;
				for(String s : is.getItemMeta().getLore()) {
					if(s!=null && s.contains("Increase your Magicka by ")) {
						attributes.put(player, Integer.parseInt(s.substring(25))+attributes.getOrDefault(player, 0));
					}
				}
			}
		}
		else if(score==3) {
			for(ItemStack is: player.getInventory().getArmorContents()) {
				if(is==null) continue;
				for(String s : is.getItemMeta().getLore()) {
					if(s!=null && s.contains("Increase your Stamina by ")) {
						attributes.put(player, Integer.parseInt(s.substring(25))+attributes.getOrDefault(player, 0));
					}
				}
			}
			regen(player);
		}
	}
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	/**
	 * The following methods deal with the draining / regernating
	 * of a player's Stamina.
	 */
	
	/**
	 * These two maps keep track of whether or not a player is currently regening/draining
	 * their stamina, that way there aren't multiple instances of them draining / regening
	 * at the same time. 
	 */
	private HashMap<Player,Boolean> draining = new HashMap<Player,Boolean>();
	private HashMap<Player,Boolean> regening = new HashMap<Player,Boolean>();
	HashMap<Player,Boolean> hitCancel = new HashMap<Player,Boolean>();
	
	@EventHandler
	public void onPlayerToggleSprintEvent(PlayerToggleSprintEvent event) {
		Player player = event.getPlayer();
		if(player.getScoreboard().getObjective("class").getScore(player.getName()).getScore()==3) {
			if(!player.isSprinting() && player.getScoreboard().getObjective("Stamina").getScore(player.getName()).getScore()>0) {
				player.setWalkSpeed((float).3);
				if(!draining.getOrDefault(player, false))
					drain(player);
			}
			else {
				player.setWalkSpeed((float).2);
				if(!regening.getOrDefault(player, false))
					regen(player);
			}
		}
	}
	/**
	 * Drains a Ranger's Stamina while they are sprinting
	 * @param player
	 */
	private void drain(Player player) {
		draining.put(player, true);
		new BukkitRunnable()
	    {
	        @Override
	        public void run()
	        {
	        	if(!player.isSprinting()) {cancel(); draining.put(player, false); return;}
	        	else if(player.getScoreboard().getObjective("Stamina").getScore(player.getName()).getScore()<=0) {
					player.getScoreboard().getObjective("Stamina").getScore(player.getName()).setScore(0);
					cancel();
					draining.put(player, false);
					player.setWalkSpeed((float).2);
					return;
	        	}
	        	else {
	        		int sc = player.getScoreboard().getObjective("Stamina").getScore(player.getName()).getScore();
	    			player.getScoreboard().getObjective("Stamina").getScore(player.getName()).setScore(sc-2);
	        	}
	        }
	    }.runTaskTimer(Main.instance, 2L, 3L);
	}
	/**
	 * Regenerate's a player's Stamina. Can be used on either
	 * Rangers or Fighters
	 * @param player
	 */
	private void regen(Player player) {
		regening.put(player, true);
	    new BukkitRunnable()
	    {
	        @Override
	        public void run()
	        {
	        	if(player.isSprinting()) {cancel(); regening.put(player, false); return;}
	        	else {
	        		int sc = player.getScoreboard().getObjective("Stamina").getScore(player.getName()).getScore();
	        		if(sc+2>=attributes.getOrDefault(player,0)+Main.BASE_STAM) {
						player.getScoreboard().getObjective("Stamina").getScore(player.getName()).setScore(attributes.getOrDefault(player,0)+Main.BASE_STAM);
						cancel();
						regening.put(player, false);
						return;
		        	}
	        		else
	        			player.getScoreboard().getObjective("Stamina").getScore(player.getName()).setScore(sc+2);
	        	}
	        }
	    }.runTaskTimer(Main.instance, RC, 5L);
	}
	//regen but for Fighters. 
	private void fregen(Player player) {
		regening.put(player, true);
	    new BukkitRunnable()
	    {
	        @Override
	        public void run()
	        {
	        	if(hitCancel.getOrDefault(player, false)) {cancel(); hitCancel.put(player, false); fregen(player); return;}
	        	else {
	        		int sc = player.getScoreboard().getObjective("Stamina").getScore(player.getName()).getScore();
	        		if(sc+2>=attributes.getOrDefault(player,0)+Main.BASE_STAM) {
						player.getScoreboard().getObjective("Stamina").getScore(player.getName()).setScore(attributes.getOrDefault(player,0)+Main.BASE_STAM);
						cancel();
						regening.put(player, false);
						return;
		        	}
	        		else
	        			player.getScoreboard().getObjective("Stamina").getScore(player.getName()).setScore(sc+2);
	        	}
	        }
	    }.runTaskTimer(Main.instance, FC, 5L);
	}
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Checks for when a Mage drinks a Magicka potion,
	 * and adds to their Magicka stat
	 * @param event
	 */
	@EventHandler
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		if(player.getScoreboard().getObjective("class").getScore(player.getName()).getScore()==2) {
			ItemStack item = event.getItem();
			if(item.getItemMeta().getLore()==null || item.getItemMeta().getLore().size()!=2) return;
			if(item.getType().equals(Material.POTION)){
				int n = item.getItemMeta().getLore().get(1).charAt(0)-'0';
				if(n>1) {
					if(player.getInventory().getItemInMainHand().equals(item)) player.getInventory().setItemInMainHand(getPartialBottle(item,n));
					else player.getInventory().setItemInOffHand(getPartialBottle(item,n));
					event.setCancelled(true);
				}
				else if(n==1) {
					int score = player.getScoreboard().getObjective("Magicka").getScore(player.getName()).getScore();
					int att = attributes.getOrDefault(player, 0);
					String lore = item.getItemMeta().getLore().get(0);
					int buff = Integer.parseInt(lore.substring(lore.indexOf('+'),lore.indexOf(' ')));
					
					if(score+buff>Main.BASE_MAG+att)
						player.getScoreboard().getObjective("Magicka").getScore(player.getName()).setScore(Main.BASE_MAG+att);
					else
						player.getScoreboard().getObjective("Magicka").getScore(player.getName()).setScore(score+buff);
				}
			}
		}		
	}
	private ItemStack getPartialBottle(ItemStack item, int n) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(n-1+" almost there");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(meta.getLore().get(0));
		lore.add((n-1)+meta.getLore().get(1).substring(1));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Old code. System I thought of where
	 * Fighter's would gain attack damage whenever
	 * they hit something and had enough Stamina.
	 * Decided to keep it here in-case I change my mind
	 * @param event
	 */
//	@EventHandler
//	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
//		if(event.getDamager() instanceof Player && event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
//			Player p = ((Player)event.getDamager());
//			if(p.getScoreboard().getObjective("class").getScore(p.getName()).getScore()==1) {
//				int s = p.getScoreboard().getObjective("Stamina").getScore(p.getName()).getScore()+attributes.getOrDefault(p,0);
//				if(s>=10) {
//					p.getScoreboard().getObjective("Stamina").getScore(p.getName()).setScore(s-10);
//				}
//				else p.getScoreboard().getObjective("Stamina").getScore(p.getName()).setScore(0);
//				
//				String item = p.getInventory().getItemInMainHand().getType().name();
//				if(item.contains("SWORD")||item.contains("AXE")||item.contains("TRIDENT"))
//					p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(5);
//				else 
//					p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
//				
//				if(regening.getOrDefault(p, false)) hitCancel.put(p, true);
//				else fregen(p);
//				//p.sendMessage(""+event.getDamage());
//			}
//		}
//	}
}
