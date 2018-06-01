 package com.github.boltydawg;

import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import com.codingforcookies.armorequip.ArmorEquipEvent;

import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import io.loyloy.nicky.Nick;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

/**
 * This listener does tasks related to the 3 main classes, along with
 * a few misc things. In other words, there's a lot of stuff here.
 * I broke it up into sections in an attempt to make it more readable
 * @author BoltyDawg
 */
public class ListenerClass implements Listener {
	/**
	 * Opens up a book gui on the mage's screen containing their spells
	 * @param player that's opening the book
	 * @param page that contains the spells in the proper formatting
	 */
	private void openBook(Player player, TextComponent page) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle("Spell Menu");
        meta.setAuthor(new Nick(player).get());
        String jason = ComponentSerializer.toString(page);
        BookUtil.setPages(meta, Arrays.asList(jason));
        book.setItemMeta(meta);
        BookUtil.openBook(book, player);
    }

	/**
	 * Detects various class-related right click events
	 * @param event
	 */
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			ItemStack main = player.getInventory().getItemInMainHand();
			if(event.getHand() == EquipmentSlot.HAND) {
				//if player is holding a wand, acts upon it
				if(Material.STICK==main.getType() && CommandWand.WAND_LORE.equals(main.getItemMeta().getLore())) {
					String dName = main.getItemMeta().getDisplayName();
					//Checks if this player is a mage and that the wand is theirs
					if(player.getScoreboard().getObjective("class").getScore(player.getName()).getScore()==2 && dName!=null  && (ChatColor.stripColor(CommandWand.getName(player))).equals(dName.substring(0, dName.indexOf("\'")))) {
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
				//Manages a Fighter's Stamina whenever they swing a weapon
				else if(player.getScoreboard().getObjective("class").getScore(player.getName()).getScore()==1) {			
					String item = main.getType().name();
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
				//Makes it so only enchanters can enchant
				else if(event.getAction() == Action.RIGHT_CLICK_BLOCK && Material.ENCHANTMENT_TABLE.equals(event.getClickedBlock().getType())) {
					if(player.getScoreboard().getObjective("role").getScore(player.getName()).getScore()!=1) {
						player.sendMessage(ChatColor.RED+"The lack the required knowledge to make any use of this table");
						event.setCancelled(true);
					}
				}
			}
			//ensures that only Fighters can use shields
			ItemStack item = event.getItem();
			if(item!=null && item.getType().equals(Material.SHIELD) && (item.getItemMeta().getLore()==null || !item.getItemMeta().getLore().contains("Usable by any class"))) {
				if(player.getScoreboard().getObjective("class").getScore(player.getName()).getScore()!=1) {
					player.sendMessage(ChatColor.RED+"Only the sons and daughters of Atlas may his shields");
					event.setCancelled(true);
				}
			}
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
	
	//private static final long RC = 55l;
	//private static final long FC = 70l;
	
	
	/**
	 * Uses Borlea's code to check when a player
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
		if(event==null) { Bukkit.broadcastMessage("WHAT THE FUCK MAN"); return;}
		Player player = event.getPlayer();
		if(Main.nick) {
			Nick n = new Nick(player);
			if(n!=null && n.get()!=null)
				event.setJoinMessage(ChatColor.GOLD+n.get()+ChatColor.YELLOW+" joined the game.");
		}
		int score = player.getScoreboard().getObjective("class").getScore(player.getName()).getScore();
		//Fighters
		if(score==1) {
			for(ItemStack is: player.getInventory().getArmorContents()) {
				if(is==null || is.getItemMeta().getLore()==null) continue;
				for(String s : is.getItemMeta().getLore()) {
					if(s!=null && s.contains("Increase your Stamina by ")) {
						attributes.put(player, Integer.parseInt(s.substring(25))+attributes.getOrDefault(player, 0));
					}
				}
			}
			fregen(player);
		}
		//Mages
		else if(score==2) {
			for(ItemStack is: player.getInventory().getArmorContents()) {
				if(is==null || is.getItemMeta().getLore()==null) continue;
				for(String s : is.getItemMeta().getLore()) {
					if(s!=null && s.contains("Increase your Magicka by ")) {
						attributes.put(player, Integer.parseInt(s.substring(25))+attributes.getOrDefault(player, 0));
					}
				}
			}
		}
		//Rangers
		else if(score==3) {
			for(ItemStack is: player.getInventory().getArmorContents()) {
				if(is==null || is.getItemMeta().getLore()==null) continue;
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
	 * The following methods deal with the draining / regenerating
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
	    }.runTaskTimer(Main.instance, 55L, 5L);
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
	    }.runTaskTimer(Main.instance, 70L, 5L);
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
		ItemStack item = event.getItem();
		if(item.getType().equals(Material.POTION) && item.getItemMeta().getLore()!=null){
			List<String> lore = item.getItemMeta().getLore();
			if(lore.size()==2 && lore.get(0).charAt(0)=='+') {
				if(player.getScoreboard().getObjective("class").getScore(player.getName()).getScore()!=2) {
					player.sendMessage("The fould liquid burns your insides");
					player.damage(2);
					return;
				}
				int n = lore.get(1).charAt(0)-'0';
				if(n>1) {
					if(player.getInventory().getItemInMainHand().equals(item)) player.getInventory().setItemInMainHand(getPartialBottle(item,n));
					else player.getInventory().setItemInOffHand(getPartialBottle(item,n));
					event.setCancelled(true);
				}
				else if(n==1) {
					int score = player.getScoreboard().getObjective("Magicka").getScore(player.getName()).getScore();
					int att = attributes.getOrDefault(player, 0);
					String str = item.getItemMeta().getLore().get(0);
					int buff = Integer.parseInt(str.substring(str.indexOf('+'),str.indexOf(' ')));
					
					if(score+buff>Main.BASE_MAG+att)
						player.getScoreboard().getObjective("Magicka").getScore(player.getName()).setScore(Main.BASE_MAG+att);
					else
						player.getScoreboard().getObjective("Magicka").getScore(player.getName()).setScore(score+buff);
				}
			}
			else if(lore.get(0)!=null && lore.get(0).contains(" orbs")) {
				String str = ChatColor.stripColor(lore.get(0));
				Experience.changeExp(player,Integer.parseInt(str.substring(0,str.indexOf(' '))));
				new BukkitRunnable(){
			        @Override
			        public void run(){
			        	if(player.getInventory().getItemInMainHand().equals(new ItemStack(Material.GLASS_BOTTLE)))
			        		player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
			        	else
			        		player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
			        }
			   }.runTaskLaterAsynchronously(Main.instance, 1L);
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
	//Other, non-class related things
	@EventHandler
	public void onPlayerEditBookEvent(PlayerEditBookEvent event) {
		if(event.isSigning()&&Main.nick) {
			Player p = event.getPlayer();
			BookMeta met = event.getNewBookMeta();
			String str = new Nick(p).get();
			met.setAuthor(str);
			event.setNewBookMeta(met);
			p.sendMessage(ChatColor.GRAY + "<DISEMBODIED VOICE> Don't worry, I fixed the Author of that book for you ;)");
		}
		else {
			BookMeta prev = event.getPreviousBookMeta();
			prev.setPages(event.getNewBookMeta().getPages());
			event.setNewBookMeta(prev);	
		}
	}
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		if(true && Main.nick) { //TODO Make sure their arena score is 0
			Nick vallone = new Nick(event.getEntity().getPlayer());
			if(vallone!=null) {
				String n = new Nick(event.getEntity().getPlayer()).get();
				event.setDeathMessage(event.getDeathMessage().replace(event.getEntity().getPlayer().getName(), n));
				ItemStack skull = new ItemStack(Material.SKULL_ITEM,1,(short)3);
				SkullMeta met = (SkullMeta)skull.getItemMeta();
				met.setOwningPlayer(event.getEntity().getPlayer());
				met.setDisplayName(n);
				List<String> lore = new ArrayList<String>();
				lore.add(event.getDeathMessage().replaceAll(n+" ",""));
				met.setLore(lore);
				skull.setItemMeta(met);
				event.getEntity().getWorld() .dropItem(event.getEntity().getLocation(), skull);
			}
		}
	}
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
