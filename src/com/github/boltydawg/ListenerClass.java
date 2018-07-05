 package com.github.boltydawg;

import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Chest;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
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
        meta.setAuthor(Main.getName(player));
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
		boolean air = event.getAction() == Action.RIGHT_CLICK_AIR;
		boolean block = event.getAction() == Action.RIGHT_CLICK_BLOCK;
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
//		ItemMeta met = item.getItemMeta();
//		List<String> t = met.getLore();
//		t.set(0, ChatColor.GOLD+"in lucro ducere");
//		met.setLore(t);
//		item.setItemMeta(met);
		if(air || block) {
			//Makes it so only enchanters can enchant
			if(block && Material.ENCHANTMENT_TABLE.equals(event.getClickedBlock().getType())) {
				//TODO update the message and make it work for mages, not a role check
				if(Main.scoreboard.getObjective("role").getScore(player.getName()).getScore()!=1) {
//						TextComponent msg = new TextComponent();
//						msg.setText();
//						msg.setColor();
//						player.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
					
					player.sendMessage(ChatColor.RED+"The lack the required knowledge to make any use of this table");
					event.setCancelled(true);
				}
			}
			else if(item!=null && item.getType()!=Material.AIR) {
				//if player is holding a wand, acts upon it
				if(event.getHand().equals(EquipmentSlot.HAND) && Material.STICK==item.getType() && CommandWand.WAND_LORE.equals(item.getItemMeta().getLore())) {
					String dName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
					//Checks if this player is a mage and that the wand is theirs
					if(Main.scoreboard.getObjective("class").getScore(player.getName()).getScore()==2 && dName!=null  && Main.getName(player).equals(dName.substring(0, dName.indexOf("\'")))) {
						ItemStack off = player.getInventory().getItemInOffHand();
						if(off.getType() == Material.KNOWLEDGE_BOOK && off.getItemMeta().getLore()!=null) {
							Spells.cast(player, off.getItemMeta().getLore().get(0));
						}
						else {
							/**
							 * Accesses the file containing the player's known spells,
							 * and then once they choose a spell it has that book forcibly
							 *	placed in their left hand
							 */
							this.openBook(player,Spells.toText(Main.mages.getOrDefault(player.getUniqueId(),new ArrayList<String>())));
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
				//ensures that only Fighters can use shields
				else if(item.getType().equals(Material.SHIELD) && (item.getItemMeta().getLore()==null || !item.getItemMeta().getLore().contains("Usable by any class"))) {
					if(Main.scoreboard.getObjective("class").getScore(player.getName()).getScore()!=1) {
						TextComponent msg = new TextComponent();
						msg.setText("This shield is too heavy and unwieldy for you to make any use of it");
						msg.setColor(ChatColor.RED);
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
						event.setCancelled(true);
					}
				}
				//ensures that only Rangers can use certain bow
				else if(item.getType().equals(Material.BOW) && item.getItemMeta().getLore()!=null && item.getItemMeta().getLore().contains("oculis intendere")) {
					if(Main.scoreboard.getObjective("class").getScore(player.getName()).getScore()!=3) {
						TextComponent msg = new TextComponent();
						msg.setText("The bow snaps back and stings your arm");
						msg.setColor(ChatColor.RED);
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
						event.setCancelled(true);
						player.damage(1);
					}
				}
				//checks if a player is holding a Tome
				else if(item.getType().equals(Material.BOOK) && item.getItemMeta().getLore()!=null && item.getItemMeta().getLore().size()==2 && item.getItemMeta().getLore().get(0).equals("Contains the knowledge of")) {
					int mag = Main.scoreboard.getObjective("Magicka").getScore(player.getName()).getScore();
					if(mag>=20) {
						String spell = item.getItemMeta().getLore().get(1);
						Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"teach "+player.getName()+" "+spell);
						Main.scoreboard.getObjective("Magicka").getScore(player.getName()).setScore(mag-20);
					}
					else {
						TextComponent msg = new TextComponent();
						msg.setText("You need more Magicka in order to learn this spell!");
						msg.setColor(ChatColor.DARK_BLUE);
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
					}
				}
			}
		}
		else if(item!=null && (event.getAction()==Action.LEFT_CLICK_AIR || event.getAction()==Action.LEFT_CLICK_BLOCK)) {
			int cla = Main.scoreboard.getObjective("class").getScore(player.getName()).getScore();
			//Manages a Fighter's Stamina whenever they swing a weapon
			if(cla==1) {
				String name = item.getType().name();
				if(name.contains("SWORD")||name.contains("_AXE")||name.contains("TRIDENT")) {
					int s = Main.scoreboard.getObjective("Stamina").getScore(player.getName()).getScore();
					if(s>=15) {
						Main.scoreboard.getObjective("Stamina").getScore(player.getName()).setScore(s-15);
						player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(5.1);
					}
					else {
						Main.scoreboard.getObjective("Stamina").getScore(player.getName()).setScore(0);
						player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
					}
					RunnableRegen.fighterRegen(player);
				}
				else 
					player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
			}
			else if(Material.STICK==item.getType() && CommandWand.WAND_LORE.equals(item.getItemMeta().getLore())) {
				Spells.cast(player, "beam");
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

	
	/**
	 * Uses Borlea's code to check when a player
	 * puts on/takes off armor that has a trait that increases
	 * their max Magicka
	 * @param event
	 */
	@EventHandler
	public void onArmorEquipEvent(ArmorEquipEvent event) {
		Player player = event.getPlayer();
		if(player.isDead())return;
		ItemStack nap = event.getNewArmorPiece();
		ItemStack oap = event.getOldArmorPiece();
		boolean goodNap = nap!=null && nap.getType()!=Material.AIR && nap.getItemMeta().getLore()!=null;
		int score = Main.scoreboard.getObjective("class").getScore(player.getName()).getScore();
		int sub = Main.scoreboard.getObjective("subclass").getScore(player.getName()).getScore();
		if(sub==1){
			event.setCancelled(true);
			player.sendMessage(ChatColor.DARK_PURPLE+"THE ARMOR DOES NOT FIT YOU BECAUSE YOUR MUSCLES ARE TOO SWOLE");
		}
		else if(goodNap){
			ArrayList<String> lore = (ArrayList<String>)nap.getItemMeta().getLore();
			if(lore.contains("An Unstoppable Force") && sub!=4) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED+"Only a Tank can wear this armor!");
			}
			else if(lore.contains("chorus in morte") && score!=3) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED+"Only a Ranger can wear this armor!");
			}
			else if(lore.contains("fundamenta fortis") && score!=1) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED+"Only a Fighter can wear this armor!");
			}
		}
		if(!event.isCancelled()) {
			//Fighters
			if(score==1) {
				if(goodNap) {
					for(String s : nap.getItemMeta().getLore()) {
						if(s==null)continue;
						else if(s.contains(" Stamina") && s.contains("+")) {
							Main.attributes.put(player, Integer.parseInt(s.substring(1,s.indexOf(" ")))+Main.attributes.getOrDefault(player, 0));
							RunnableRegen.fighterRegen(player);
						}
					}
				}
				if(oap!=null && oap.getType()!=Material.AIR && oap.getItemMeta().getLore()!=null) {
					for(String s : oap.getItemMeta().getLore()) {
						if(s==null)continue;
						else if(s.contains(" Stamina") && s.contains("+")) {
							int temp = Integer.parseInt(s.substring(1,s.indexOf(" ")));
							Main.attributes.put(player, Main.attributes.getOrDefault(player, temp)-temp);
							if(Main.scoreboard.getObjective("Stamina").getScore(player.getName()).getScore()>Main.BASE_STAM+Main.attributes.getOrDefault(player, 0))
								Main.scoreboard.getObjective("Stamina").getScore(player.getName()).setScore(Main.attributes.get(player)+Main.BASE_STAM);
						}
					}
				}
			}
			//Mages
			else if(score==2) {
				if(goodNap) {
					for(String s : nap.getItemMeta().getLore()) {
						if(s==null)continue;
						else if(s.contains(" Magicka") && s.contains("+")) {
							Main.attributes.put(player, Integer.parseInt(s.substring(1,s.indexOf(" ")))+Main.attributes.getOrDefault(player, 0));
							RunnableRegen.mageRegen(player);
						}
					}
				}
				if(oap!=null && oap.getType()!=Material.AIR && oap.getItemMeta().getLore()!=null) {
					for(String s : oap.getItemMeta().getLore()) {
						if(s==null) continue;
						else if(s.contains(" Magicka") && s.contains("+")) {
							int temp = Integer.parseInt(s.substring(1,s.indexOf(" ")));
							Main.attributes.put(player, Main.attributes.getOrDefault(player, temp)-temp);
							if(Main.scoreboard.getObjective("Magicka").getScore(player.getName()).getScore()>Main.BASE_MAG+Main.attributes.get(player))
								Main.scoreboard.getObjective("Magicka").getScore(player.getName()).setScore(Main.attributes.get(player)+Main.BASE_MAG);
						}
					}
				}
			}
			//Rangers
			else if(score==3) {
				if(goodNap) {
					for(String s : nap.getItemMeta().getLore()) {
						if(s==null)continue;
						else if(s.contains(" Stamina") && s.contains("+")) {
							Main.attributes.put(player, Integer.parseInt(s.substring(1,s.indexOf(" ")))+Main.attributes.getOrDefault(player, 0));
							RunnableRegen.rangerRegen(player);
						}
					}
				}
				if(oap!=null && oap.getType()!=Material.AIR && oap.getItemMeta().getLore()!=null) {
					for(String s : oap.getItemMeta().getLore()) {
						if(s==null)continue;
						else if(s.contains(" Stamina") && s.contains("+")) {
							int temp = Integer.parseInt(s.substring(1,s.indexOf(" ")));
							Main.attributes.put(player, Main.attributes.getOrDefault(player, temp)-temp);
							if(Main.scoreboard.getObjective("Stamina").getScore(player.getName()).getScore()>Main.BASE_STAM+Main.attributes.getOrDefault(player, 0))
								Main.scoreboard.getObjective("Stamina").getScore(player.getName()).setScore(Main.attributes.get(player)+Main.BASE_STAM);
						}
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
		String nam = Main.getName(player);
		if(nam!=player.getName()) event.setJoinMessage(ChatColor.GOLD+nam+ChatColor.YELLOW+" joined the game.");
		int score = Main.scoreboard.getObjective("class").getScore(player.getName()).getScore();
		//Fighters
		if(score==1) {
			for(ItemStack is: player.getInventory().getArmorContents()) {
				if(is==null || is.getItemMeta().getLore()==null) continue;
				for(String s : is.getItemMeta().getLore()) {
					if(s!=null && s.contains(" Stamina") && s.contains("+")) {
						Main.attributes.put(player, Integer.parseInt(s.substring(1,s.indexOf(" ")))+Main.attributes.getOrDefault(player, 0));
					}
				}
			}
			RunnableRegen.fighterRegen(player);
		}
		//Mages
		else if(score==2) {
			for(ItemStack is: player.getInventory().getArmorContents()) {
				if(is==null || is.getItemMeta().getLore()==null) continue;
				for(String s : is.getItemMeta().getLore()) {
					if(s!=null && s.contains(" Magicka") && s.contains("+")) {
						Main.attributes.put(player, Integer.parseInt(s.substring(1,s.indexOf(" ")))+Main.attributes.getOrDefault(player, 0));
					}
				}
			}
			RunnableRegen.mageRegen(player);
		}
		//Rangers
		else if(score==3) {
			for(ItemStack is: player.getInventory().getArmorContents()) {
				if(is==null || is.getItemMeta().getLore()==null) continue;
				for(String s : is.getItemMeta().getLore()) {
					if(s!=null && s.contains(" Stamina") && s.contains("+")) {
						Main.attributes.put(player, Integer.parseInt(s.substring(1,s.indexOf(" ")))+Main.attributes.getOrDefault(player, 0));
					}
				}
			}
			RunnableRegen.rangerRegen(player);
		}
	}
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		Main.attributes.remove(p);
		draining.remove(p);
		RunnableRegen.regenerates.remove(p);
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
	private static HashMap<Player,Boolean> draining = new HashMap<Player,Boolean>();
	
	@EventHandler
	public void onPlayerToggleSprintEvent(PlayerToggleSprintEvent event) {
		Player player = event.getPlayer();
		if(player!=null && Main.scoreboard.getObjective("class").getScore(player.getName()).getScore()==3) {
			if(!player.isSprinting()) {
				player.setWalkSpeed((float).2625);
				if(!draining.getOrDefault(player, false)) 
					drain(player);
				RunnableRegen.cancelRegen(player);
			}
			else {
				player.setWalkSpeed((float).2);
				RunnableRegen.rangerRegen(player);
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
	        	if(!player.isSprinting()) {cancel(); draining.remove(player); return;}
	        	int sc = Main.scoreboard.getObjective("Stamina").getScore(player.getName()).getScore();
	        	if(sc<=0) {
					Main.scoreboard.getObjective("Stamina").getScore(player.getName()).setScore(0);
					cancel();
					draining.put(player, false);
					player.setWalkSpeed((float).2);
					return;
	        	}
	        	else {
	    			Main.scoreboard.getObjective("Stamina").getScore(player.getName()).setScore(sc-2);
	        	}
	        }
	    }.runTaskTimer(Main.instance, 1L, 4L);
	}
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	//Other, non-class related things
	@EventHandler
	public void onPlayerEditBookEvent(PlayerEditBookEvent event) {
		if(event.isSigning()&&Main.nick) {
			Player p = event.getPlayer();
			BookMeta met = event.getNewBookMeta();
			String str = Main.getName(p);
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
		Player p = event.getEntity().getPlayer();
		int alive = Main.scoreboard.getObjective("alive").getScore(p.getName()).getScore();
		if(p.getInventory().contains(Material.TOTEM)) {
			//TODO set their bed location to the hospital
			p.spigot().respawn();
			return;
		}
		if(alive==1) {
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"skillreset "+p.getName()+" all");
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"lp user "+p.getName()+" clear");
			Main.scoreboard.resetScores(p.getName());
			
			String n = Main.getName(p);
			String dm = event.getDeathMessage();
			event.setDeathMessage("");
			dm=dm.replaceAll(p.getName(), n);
			for(Player pl : p.getWorld().getPlayers()) {
				if(dm.contains(pl.getName())) {
					dm=dm.replaceAll(pl.getName(), Main.getName(pl));
				}
			}
			ItemStack skull = new ItemStack(Material.SKULL_ITEM,1,(short)3);
			SkullMeta met = (SkullMeta)skull.getItemMeta();
			met.setOwningPlayer(p);
			met.setDisplayName(ChatColor.DARK_RED+n+".");
			List<String> lore = new ArrayList<String>();
			String[] msg = dm.replaceAll(n+" ", "").trim().split(" ");
			msg[0] = msg[0].substring(0, 1).toUpperCase()+msg[0].substring(1);
			int j = 0;
			String str="";
			for(String s : msg) {
				if(j>2){
					lore.add(ChatColor.GRAY+str);
					str="";
					j=0;
				}
				str+=s+" ";
				j++;
			}
			if(str.length()>0) 
				lore.add(ChatColor.GRAY+str);
			met.setLore(lore);
			skull.setItemMeta(met);
			p.getWorld().dropItem(p.getLocation(), skull);
			
			for(Player pl : p.getWorld().getPlayers()) {
				if(pl.getLocation().distance(p.getLocation())<=100) {
					pl.sendMessage(dm);
				}
			}
		}
		else if(alive==2) {
			Main.scoreboard.getObjective("alive").getScore(p.getName()).setScore(1);
		}
	}
	@EventHandler
	public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
		if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER) && !(event.getEntity().getType()==EntityType.BLAZE || event.getEntity().getType()==EntityType.CAVE_SPIDER || event.getEntity().getType()==EntityType.SILVERFISH)) {
			event.setCancelled(true);
			double x = event.getLocation().getX();
			double y = event.getLocation().getY();
			double z = event.getLocation().getZ();
			//TODO make sure this is up to date in 1.13
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"fill "+(x+20)+" "+(y+5)+" "+(z+20)+" "+(x-20)+" "+(y-5)+" "+(z-20)+" minecraft:air 0 replace minecraft:mob_spawner");
		}
		else {
			if(event.getEntityType().equals(EntityType.ZOMBIE)){
				if(event.getEntity().getEquipment().getHelmet()==null || event.getEntity().getEquipment().getHelmet().getType().equals(Material.AIR)) {
					event.getEntity().getEquipment().setHelmet(new ItemStack(Material.SKULL_ITEM,1,(short)2));
				}
			}
		}
	}
	@EventHandler
	public void openInventory(InventoryOpenEvent event) {
		if(event.getInventory().getType().equals(InventoryType.CHEST) && event.getPlayer() instanceof Player){
			Player player = (Player)event.getPlayer();
			ItemStack item = player.getInventory().getItemInMainHand();
			//TODO make sure it's still double_plant in 1.13
			if(item.getType().equals(Material.DOUBLE_PLANT) && item.getItemMeta().getLore()!=null && item.getItemMeta().getLore().contains("lock")){
				Chest chest = (Chest)event.getInventory().getHolder();
				if(chest.isLocked())
					return;
				chest.setLock(ChatColor.GREEN+(Main.getName(player)+"'s key"));
				chest.update();
				
				TextComponent msg = new TextComponent();
				msg.setText("Only your key can open this chest now");
				msg.setColor(ChatColor.GREEN);
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
				player.sendMessage(ChatColor.GRAY+"Type /getkey to get your personal key!");
				
				event.setCancelled(true);
				item.setAmount(item.getAmount()-1);
			}
		}
	}
	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		if(event.getBlock().getType().equals(Material.CHEST) && event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
			Chest chest = ((Chest)event.getBlock().getState());
			if(chest.isLocked()) {
				ItemStack main = event.getPlayer().getInventory().getItemInMainHand();
				if(main!=null && main.getType()!=Material.AIR && main.getItemMeta().getDisplayName().equals(chest.getLock()))
					return;
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED+"You can't break a locked chest without using its key!\n"+ChatColor.GRAY+"(By hand, that is...)");
			}
		}
	}
}
