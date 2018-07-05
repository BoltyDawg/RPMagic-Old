package com.github.boltydawg;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.block.ShulkerBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TippedArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Score;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * 1 Barbarian
 * 2 Knight TODO Tent
 * 3 Hoplite
 * 4 Tank
 * 5 Angel
 * 6 Arcane Bowman
 * 7 Assassin 
 * 8 Scout
 * 
 * @author BoltyDawg
 *
 */
public class ListenerSubclass implements Listener{
	private HashMap<Player,Boolean> cool1 = new HashMap<Player,Boolean>();
	private HashMap<Player,Boolean> cool2 = new HashMap<Player,Boolean>();
	private HashMap<Player,Location> tele = new HashMap<Player,Location>();
	
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		boolean block = event.getAction().equals(Action.RIGHT_CLICK_BLOCK);
		boolean air = event.getAction().equals(Action.RIGHT_CLICK_AIR);
		if(air || block) {
			Player p = event.getPlayer();
			ItemStack item = event.getItem();
			int sub = Main.scoreboard.getObjective("subclass").getScore(p.getName()).getScore();
			if(item!=null && item.getType()!=Material.AIR) {
				//Barbarian Rage
				if(item.getType().equals(Material.BLAZE_POWDER) && item.getItemMeta().getLore()!=null && item.getItemMeta().getLore().contains(ChatColor.DARK_RED + "GRRRAAAAGHHH")) {
					if(sub==1) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,260,2));
						p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,260,0));
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,260,1));
						p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,260,2));
						p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,260,7));
						Main.scoreboard.getObjective("damage").getScore(p.getName()).setScore(0);
						p.getInventory().removeItem(item);
						new BukkitRunnable(){
					        @Override
					        public void run(){
					        	p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,160,1));
								p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,160,1));
					        }
					   }.runTaskLater(Main.instance, 260L);
					}
					else {
						TextComponent msg = new TextComponent();
						msg.setText("IT BURNS YOUR HAND");
						msg.setColor(ChatColor.YELLOW);
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
						p.damage(1);
					}
				}
				//Barbarians equipping bone
				else if(sub==1 && item.getType().equals(Material.BONE) && item.getItemMeta().getLore()!=null) {
					if(p.getInventory().getHelmet()!=null && p.getInventory().getHelmet().getType()!=Material.AIR){
						p.sendMessage(ChatColor.DARK_PURPLE+"YOU EAT YOUR CURRENT BONE");
						p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 3.0F, 1F);
					}
					ItemStack bone = item.clone();
					bone.setAmount(1);
					p.getInventory().setHelmet(bone);
					item.setAmount(item.getAmount()-1);
				}
				//Tank shield
				else if(sub==4 && event.getHand().equals(EquipmentSlot.HAND) && item.getType().equals(Material.SHIELD) && item.getItemMeta().getLore()!=null && item.getItemMeta().getLore().contains("An Unbreakable Wall")){//TODO, make sure this lore is right
					short d = item.getDurability();
					if(d<=294 && !p.isBlocking()) {
						Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"castp "+p.getName()+" pull");
						if(!cool1.getOrDefault(p,false)) {
							event.setCancelled(true);
							item.setDurability((short)(d+42));
							cool1.put(p, true);
							new BukkitRunnable(){
						        @Override
						        public void run(){
						        	cool1.remove(p);
						        	TextComponent msg = new TextComponent();
									msg.setText("Shield recharged!");
									msg.setColor(ChatColor.LIGHT_PURPLE);
									p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
									return;
						        }
						   }.runTaskLater(Main.instance, 200L);
						}
					}
				}
				//Assassins
				else if(sub==7) {
					//Cloak
					if(item!=null && item.getType().equals(Material.WATCH) && item.getItemMeta().getLore()!=null && item.getItemMeta().getLore().contains(ChatColor.GRAY+"Was there ever any doubt?")) { //Make the lore in dark red?
						if(event.getHand().equals(EquipmentSlot.HAND)) {
							if(p.hasPotionEffect(PotionEffectType.INVISIBILITY) && p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
								return;
							int dmg = RunnableLastDamage.timeSinceLastDamage(p);
							if(dmg<=5) {
								TextComponent msg = new TextComponent();
								msg.setText("You have recently taken damage and must wait another "+(5-dmg)+" seconds");
								msg.setColor(ChatColor.AQUA);
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
								return;
							}
							Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"castp "+p.getName()+" cloak");
							if(!cool2.getOrDefault(p, false)) {	
								p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,300,0,false,true));
								event.setCancelled(true);
								cool2.put(p, true);
								new BukkitRunnable(){
							        @Override
							        public void run(){
							        	cool2.remove(p);
							        	TextComponent msg = new TextComponent();
										msg.setText("Cloak recharged!");
										msg.setColor(ChatColor.LIGHT_PURPLE);
										p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
										return;
							        }
							   }.runTaskLater(Main.instance, 900L);
							}
						}
						else {
							int dmg = RunnableLastDamage.timeSinceLastDamage(p);
							if(dmg<=4) {
								TextComponent msg = new TextComponent();
								msg.setText("You have recently taken damage and must wait another "+(4-dmg)+" seconds");
								msg.setColor(ChatColor.AQUA);
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
								return;
							}
							Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"castp "+p.getName()+" blink");
							if(!cool1.getOrDefault(p, false)) {
								event.setCancelled(true);
								cool1.put(p, true);
								new BukkitRunnable(){
							        @Override
							        public void run(){
							        	cool1.remove(p);
							        	TextComponent msg = new TextComponent();
										msg.setText("Blink recharged!");
										msg.setColor(ChatColor.LIGHT_PURPLE);
										p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
										return;
							        }
							   }.runTaskLater(Main.instance, 140L);
							}
						}
					}
				}
//				//Angel Rocket
//				else if(sub==5) {
//					if(item.getType().equals(Material.FIREBALL) && item.getItemMeta().getLore()!=null && item.getItemMeta().getLore().contains("Take flight!")) {
//						event.setCancelled(true);
//						if(p.isGliding()) {
//							Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"castp "+p.getName()+" fling|3");
//							item.setAmount(1);
//							p.getInventory().removeItem(item);
//					}
//						else 
//							p.sendMessage("You must be flying to use this item!");
//					}
//					else {
//						ItemStack elytra = p.getInventory().getItemInOffHand();
//						if(elytra!=null && elytra.getType().equals(Material.ELYTRA)) {
//							event.setCancelled(true);
//							short dur = (short)(elytra.getDurability()+10);
//							if(dur>=432)
//								elytra.setDurability((short)432);
//							else {
//								elytra.setDurability(dur);
//								p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,120,2));
//							}
//						}
//					}
//				}
			}
		}
	}
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) {
			Player p = ((Player)event.getDamager());
			int sub = Main.scoreboard.getObjective("subclass").getScore(p.getName()).getScore();
			//Barbarian Rage
			if(sub==1 && Main.scoreboard.getObjective("damage").getScore(p.getName()).getScore() >=1500) {
				ItemStack drop = new ItemStack(Material.BLAZE_POWDER);
				ItemMeta met = drop.getItemMeta();
				met.setDisplayName(ChatColor.RED+"BARBARIAN, "+ChatColor.DARK_RED+"RAGE");
				ArrayList<String> lst = new ArrayList<String>();
				lst.add(ChatColor.DARK_RED + "GRRRAAAAGHHH");
				met.setLore(lst);
				met.addEnchant(Enchantment.BINDING_CURSE, 1, false);
				met.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				drop.setItemMeta(met);
				if(!p.getInventory().contains(drop))
					p.getInventory().addItem(drop);
				//hide scoreboard
				//TODO: else: update progress to rage bar
			}
			//Knight Saturation
			else if(sub==2) {
				int f = p.getFoodLevel();
				if(f<19 && Main.scoreboard.getObjective("damage").getScore(p.getName()).getScore()>=200) {
					p.setFoodLevel(f+1);
					Main.scoreboard.getObjective("damage").getScore(p.getName()).setScore(0);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player p = (Player)event.getEntity();
			
			if(event.getCause()!=DamageCause.FALL)
				RunnableLastDamage.startCounter(p);
			
			int sub = Main.scoreboard.getObjective("subclass").getScore(p.getName()).getScore();
			//Angels
			if(sub==5) {
				if(event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL) {
					boolean b=false;;
					for(Entity e : p.getNearbyEntities(5, 5, 5)) {
						if(e instanceof Damageable) {
							Damageable d = (Damageable) e;
							b=true;
							if(e instanceof Player) 
								d.damage(event.getDamage()/2);
							else
								d.damage(event.getDamage());
						}
					}
					if(b) {
						p.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, p.getLocation(), 20);
					}
					event.setCancelled(true);
					p.setGliding(false);
				}
				else if(event.getCause() == EntityDamageEvent.DamageCause.FALL) {
					boolean b = false;
					for(Entity e : p.getNearbyEntities(5, 5, 5)) {
						if(e instanceof Damageable) {
							b = true;
							Damageable d = (Damageable) e;
							if(e instanceof Player) {
								d.damage(event.getDamage()/2);
								((Player)d).playSound(p.getLocation(), Sound.ENTITY_GENERIC_BIG_FALL, 3.0F, .5F);
							}
							else
								d.damage(event.getDamage());
						}
					}
					if(b) {
						p.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, p.getLocation(), 20);
					}
					event.setDamage(event.getDamage()*.66666);
				}
			}
			//Barbarians
			else if(sub==1) {
				Score s = Main.scoreboard.getObjective("damage").getScore(p.getName());
				s.setScore(((int)event.getDamage()+s.getScore()));
			}
			//Tank shield regen
			else if(sub==4) {
				ItemStack shield = p.getInventory().getItemInOffHand();
				//TODO: make sure the shield lore is right, and make sure the amount of durability we subtract is accurate
				if(shield!=null && shield.getType().equals(Material.SHIELD) && shield.getItemMeta().getLore()!=null && shield.getItemMeta().getLore().contains("An Unbreakable Wall")) {
					short s = (short)(shield.getDurability()-(1.5*event.getDamage()));
					if(s>=0)
						shield.setDurability(s);
					else
						shield.setDurability((short)0);
				}
			}
		}
	}
	//Angels--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	@EventHandler
	public void onToggleSneakEvent(PlayerToggleSneakEvent event) {
		if(event.getPlayer().isGliding()) 
			event.getPlayer().setGliding(false);
	}
	//Knights and Scouts---------------------------------------------------------------------------------------------------------------------------------------------------------------------
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if(event.getEntityType().equals(EntityType.HORSE) && event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)) {
			new BukkitRunnable(){
		        @Override
		        public void run(){
		        	for(Entity e : event.getEntity().getNearbyEntities(6, 6, 6)) {
						if(e instanceof Player) {
							Player p = (Player)e;
							String name = Main.getName(p);
							if(event.getEntity().getScoreboardTags().contains(name+" steed")) {
								p.getInventory().addItem(collectEgg("Steed"));
								return;
							}
							else if(event.getEntity().getScoreboardTags().contains(name+" elybris")) {
								p.getInventory().addItem(collectEgg("Elybris"));
								return;
							}
						}
		        	}
		        }
		   }.runTaskLater(Main.instance, 1L);
			
		}
	}
	@EventHandler
	public void onClickEntity(PlayerInteractEntityEvent event) {
		if(event.getRightClicked().getType().equals(EntityType.HORSE) && event.getRightClicked().getScoreboardTags()!=null) {
			Player p = event.getPlayer();
			if(event.getRightClicked().getScoreboardTags().contains(Main.getName(p)+" elybris") && (p.getInventory().getItemInMainHand().equals(collectEgg("Elybris")) || p.getInventory().getItemInOffHand().equals(collectEgg("Elybris")))) {
				if(Main.scoreboard.getObjective("subclass").getScore(p.getName()).getScore()==8) {
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"castp "+p.getName()+" capture");
					p.getInventory().remove(collectEgg("Elybris"));
					event.setCancelled(true);
				}
				else {
					TextComponent msg = new TextComponent();
					msg.setText("This is not your horse!");
					msg.setColor(ChatColor.YELLOW);
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
				}
			}
			else if(event.getRightClicked().getScoreboardTags().contains(Main.getName(p)+" steed") && (p.getInventory().getItemInMainHand().equals(collectEgg("Steed")) || p.getInventory().getItemInOffHand().equals(collectEgg("Steed")))) {
				if(Main.scoreboard.getObjective("subclass").getScore(p.getName()).getScore()==2) {
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"castp "+p.getName()+" capture");
					p.getInventory().remove(collectEgg("Steed"));
					event.setCancelled(true);
				}
				else {
					TextComponent msg = new TextComponent();
					msg.setText("This is not your horse!");
					msg.setColor(ChatColor.YELLOW);
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
				}
			}
		}
		else if(event.getRightClicked() instanceof Player) {
			((Player)event.getRightClicked()).addPassenger(event.getPlayer());
		}
	}
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Material mat = event.getBlockPlaced().getType();
		if(Main.scoreboard.getObjective("subclass").getScore(p.getName()).getScore()==8) {
			if(mat.equals(Material.SEA_LANTERN)) {
				if(p.getWorld().getHighestBlockYAt(event.getBlockPlaced().getLocation())-1<=event.getBlockPlaced().getLocation().getY()) {
					if(Main.beacons.containsKey(p.getUniqueId())) {
						for(Entity e : p.getWorld().getNearbyEntities(Main.beacons.get(p.getUniqueId()),1,1,1)) {
							if(e.getType().equals(EntityType.ARMOR_STAND)) 
								e.remove();
						}
						Main.beacons.get(p.getUniqueId()).getBlock().breakNaturally();
					}
					Main.beacons.put(p.getUniqueId(), event.getBlockPlaced().getLocation());
					event.getBlockPlaced().setType(Material.END_GATEWAY);
					ArmorStand as = ((ArmorStand)p.getWorld().spawnEntity(event.getBlockPlaced().getLocation().add(.5,0,.5), EntityType.ARMOR_STAND));
					as.setCustomName(Main.getName(p)+"'s Waypoint");
					as.setCustomNameVisible(true);
					as.setHelmet(new ItemStack(Material.BEACON));
					as.setSmall(true);
				}
				else {
					event.setCancelled(true);
					TextComponent msg = new TextComponent();
					msg.setText("There can't be any blocks above your beacon!");
					msg.setColor(ChatColor.YELLOW);
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
				}
			}
			else if(mat.equals(Material.BEDROCK)) {
				if(Main.beacons.containsKey(p.getUniqueId())) {
					if(p.getWorld().getBlockAt(Main.beacons.get(p.getUniqueId())).getType().equals(Material.END_GATEWAY)) {
						if(p.getWorld().getHighestBlockYAt(event.getBlockPlaced().getLocation())-1<=event.getBlockPlaced().getLocation().getY()) {
							int dmg = RunnableLastDamage.timeSinceLastDamage(p);
							if(dmg<10) {
								TextComponent msg = new TextComponent();
								msg.setText("You have recently taken damage and must wait another "+(10-dmg)+" seconds");
								msg.setColor(ChatColor.AQUA);
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
								return;
							}
							if(tele.containsKey(p))
								tele.get(p).getBlock().breakNaturally();
							event.getBlockPlaced().setType(Material.PORTAL);
							tele.put(p, event.getBlockPlaced().getLocation());
							new BukkitRunnable(){
						        @Override
						        public void run(){
						        	event.getBlockPlaced().breakNaturally();
						        }
						   }.runTaskLater(Main.instance, 600L);
						}
						else {
							event.setCancelled(true);
							TextComponent msg = new TextComponent();
							msg.setText("There can't be any blocks above this teleporter");
							msg.setColor(ChatColor.YELLOW);
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
						}
					}
					else {
						event.setCancelled(true);
						TextComponent msg = new TextComponent();
						msg.setText("Your beacon is either missing or destroyed");
						msg.setColor(ChatColor.YELLOW);
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
					}
				}
				else {
					TextComponent msg = new TextComponent();
					msg.setText("You haven't placed a beacon yet");
					msg.setColor(ChatColor.YELLOW);
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
				}
			}
		}
		else if(mat.equals(Material.RED_SHULKER_BOX)) {
			if(Main.scoreboard.getObjective("subclass").getScore(p.getName()).getScore()!=2){
				ItemStack[] is =((ShulkerBox)event.getBlockPlaced().getState()).getInventory().getStorageContents();
				for(int i=0;i<is.length;i++) {
					if(is[i]!=null)
						event.getBlockPlaced().getWorld().dropItem(event.getBlockPlaced().getLocation(), is[i]);
				}
				event.setCancelled(true);
	        	event.getPlayer().getInventory().remove(Material.RED_SHULKER_BOX);
	        	event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_SHIELD_BREAK, 3.0F, .5F);
	        	TextComponent msg = new TextComponent();
				msg.setText("The box broke without its owner");
				msg.setColor(ChatColor.YELLOW);
				event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
			}
		}
		else if(mat.equals(Material.BROWN_SHULKER_BOX)) {
			if(Main.scoreboard.getObjective("subclass").getScore(p.getName()).getScore()!=8){
				ItemStack[] is =((ShulkerBox)event.getBlockPlaced().getState()).getInventory().getStorageContents();
				for(int i=0;i<is.length;i++) {
					if(is[i]!=null)
						event.getBlockPlaced().getWorld().dropItem(event.getBlockPlaced().getLocation(), is[i]);
				}
				event.setCancelled(true);
	        	event.getPlayer().getInventory().remove(Material.BROWN_SHULKER_BOX);
	        	event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_SHIELD_BREAK, 3.0F, .5F);
	        	TextComponent msg = new TextComponent();
				msg.setText("The box broke without its owner");
				msg.setColor(ChatColor.YELLOW);
				event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
			}
		}
	}
	@EventHandler
	public void onEntityPortalEnter(EntityPortalEnterEvent event) {
		if(event.getEntity() instanceof Player) {
			Player p = ((Player)event.getEntity());
			if(tele.containsValue(event.getLocation())) {
				if(event.getLocation().equals(tele.get(p))) {
					p.teleport(Main.beacons.get(p.getUniqueId()));
					tele.get(p).getBlock().breakNaturally();
					tele.remove(p);
				}
				else {
					TextComponent msg = new TextComponent();
					msg.setText("You are denied access!");
					msg.setColor(ChatColor.YELLOW);
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
					event.getLocation().getBlock().breakNaturally();
				}
			}
		}
	}
	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {
		if(event.getEntityType().equals(EntityType.ARMOR_STAND) && event.getEntity().getLocation().getBlock().getType().equals(Material.END_GATEWAY)) {
			event.getEntity().getLocation().getBlock().breakNaturally();
			event.getDrops().clear();
		}
	}
	@EventHandler
	public void onMountEvent(EntityMountEvent event) {
		if(event.getEntity() instanceof Player && event.getMount().getType().equals(EntityType.HORSE)) {
			Player p = ((Player)event.getEntity());
			for(String s : event.getMount().getScoreboardTags()) {
				if(s.contains( "steed")) {
					if(!((Main.getName(p)+" steed").equals(s))) {
						event.setCancelled(true);
						p.sendMessage(ChatColor.YELLOW+"The horse thrusts you off, in recognition that you aren't its master!");
					}
				}
				else if(s.contains(" elybris")) {
					if(!((Main.getName(p)+" elybris").equals(s))) {
						event.setCancelled(true);
						p.sendMessage(ChatColor.YELLOW+"The horse runs away before you can mount it!");
					}
				}
			}
			if(event.getMount().getScoreboardTags().contains(Main.getName(p)+" steed") && Main.scoreboard.getObjective("subclass").getScore(p.getName()).getScore()==2 && p.getInventory().firstEmpty()!=-1) {
				//TODO update this to 1.13
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"give "+p.getName()+" iron_hoe 1 0 {display:{Name:\"Knight Lance\",Lore:[\"Used on Horseback\"]},AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:12,Operation:0,UUIDMost:210155,UUIDLeast:259571,Slot:\"mainhand\"},{AttributeName:\"generic.attackSpeed\",Name:\"generic.attackSpeed\",Amount:-3.5,Operation:0,UUIDMost:215044,UUIDLeast:421056,Slot:\"mainhand\"}],ench:[{id:71,lvl:1}], HideFlags:3,Unbreakable:1}");
			}
		}
	}
	@EventHandler
	public void onEntityDismountEvent(EntityDismountEvent event) {
		if(event.getEntity() instanceof Player && event.getDismounted().getType().equals(EntityType.HORSE)) {
			Player p = ((Player)event.getEntity());
			if(event.getDismounted().getScoreboardTags().contains(Main.getName(p)+" steed") && Main.scoreboard.getObjective("subclass").getScore(p.getName()).getScore()==2) {
				//TODO update this to 1.13
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"clear "+p.getName()+" iron_hoe 0 5 {display:{Name:\"Knight Lance\",Lore:[\"Used on Horseback\"]},AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:12,Operation:0,UUIDMost:210155,UUIDLeast:259571,Slot:\"mainhand\"},{AttributeName:\"generic.attackSpeed\",Name:\"generic.attackSpeed\",Amount:-3.5,Operation:0,UUIDMost:215044,UUIDLeast:421056,Slot:\"mainhand\"}],ench:[{id:71,lvl:1}],HideFlags:3,Unbreakable:1}");
			}
		}
	}
	
	private ItemStack collectEgg(String horse) {
		ItemStack egg = new ItemStack(Material.MONSTER_EGG);
		ItemMeta met = egg.getItemMeta();
		met.setDisplayName("Capture "+horse);
		met.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
		egg.setItemMeta(met);
		return egg;
	}
	//Barbarians-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	@EventHandler
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
		Player p = event.getPlayer();
		//Barbarians eat food differently than regular players. They get buffs for eating raw meat, and can't eat cooked meat.
		if(Main.scoreboard.getObjective("subclass").getScore(p.getName()).getScore()==1){
			Material mat = event.getItem().getType();
			if(mat.equals(Material.RAW_BEEF) || mat.equals(Material.PORK)) {
				int h = p.getFoodLevel()+8;
				if(h>=20) {
					p.setFoodLevel(20);
					p.setSaturation(p.getSaturation()+12.8f);
				}
				else {
					p.setFoodLevel(h);
				}
				p.getInventory().removeItem(new ItemStack(event.getItem().getType(),1));
				event.setCancelled(true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,320,2));
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,320,0));
			}
			else if(mat.equals(Material.RAW_CHICKEN) || mat.equals(Material.MUTTON) || mat.equals(Material.RABBIT)) {
				int h = p.getFoodLevel()+6;
				if(h>=20) {
					p.setFoodLevel(20);
					p.setSaturation(p.getSaturation()+7.2f);
				}
				else {
					p.setFoodLevel(h);
				}
				p.getInventory().removeItem(new ItemStack(event.getItem().getType(),1));
				event.setCancelled(true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,320,2));
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,320,0));
			}
			else if(mat.equals(Material.COOKED_BEEF) || mat.equals(Material.COOKED_CHICKEN) || mat.equals(Material.COOKED_MUTTON) || mat.equals(Material.COOKED_RABBIT) || mat.equals(Material.GRILLED_PORK)) {
				TextComponent msg = new TextComponent();
				msg.setText("THE CHARRED MEAT MAKES YOU FEEL SICK");
				msg.setColor(ChatColor.DARK_BLUE);
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
				p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,100,0));
				p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,100,2));
				p.getInventory().removeItem(new ItemStack(event.getItem().getType(),1));
				event.setCancelled(true);
			}
		}
	}
	//ARCANE BOWMEN------------------------------------------------------------------------------------------------------------------------------------
	@EventHandler
	public void shootBow(EntityShootBowEvent event) {
		if(event.getEntity() instanceof Player) {// && event.getBow().getItemMeta().getLore()!=null && event.getBow().getItemMeta().getLore().contains("blah")) {
			Player p = (Player)event.getEntity();
			if(Main.scoreboard.getObjective("subclass").getScore(p.getName()).getScore()==6) {
				if(event.getProjectile() instanceof TippedArrow) {
					TippedArrow arrow = ((TippedArrow)event.getProjectile());		                
					String effect = arrow.getBasePotionData().getType().name();
					p.sendMessage(effect);
					//These are in all caps
					if(effect.equals("NIGHT_VISION")) {
						
					}
				}
			}
		}
	}
	@EventHandler
	public void projectileHit(ProjectileHitEvent event) {
		if(event.getHitBlock()!=null) {
			
		}
	}
}
