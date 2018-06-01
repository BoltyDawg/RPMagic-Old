package com.github.boltydawg;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

//TODO Barbarians don't use Stamina, have RAGE
//TODO deal with Barbarian's armor and max health. /subclass command is gonna be needed here.
/**
 * 1 Barbarian
 * 2 Knight
 * 3 Paladin
 * 4 Tank
 * 5 Arcane Wizard
 * 6 Alchemist
 * 7 Necromancer
 * 8 Pyromancer
 * 9 Angel
 * 10 Arcane Bowman
 * 11 Assassin 
 * 12 Scout
 * 
 * @author Jason
 *
 */
public class ListenerSubclass implements Listener{
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if(event.getEntityType().equals(EntityType.PLAYER)) {
			Player p = (Player)event.getEntity();
			if(event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL && p.getScoreboard().getObjective("subclass").getScore(p.getName()).getScore()==9) {
				event.setCancelled(true);
				ItemStack elytra = p.getInventory().getChestplate();
				p.getInventory().setChestplate(new ItemStack(Material.AIR));
				new BukkitRunnable(){
			        @Override
			        public void run(){
						p.getInventory().setChestplate(elytra);
			        }
			   }.runTaskLaterAsynchronously(Main.instance, 2L);
			}
		}
	}
	@EventHandler
	public void onClickEntity(PlayerInteractEntityEvent event) {
		if(event.getRightClicked().getType().equals(EntityType.HORSE) && event.getRightClicked().getCustomName().contains("Elybris")) {
			Player p = event.getPlayer();
			ItemStack is = p.getInventory().getItemInMainHand();
			ItemStack oh = p.getInventory().getItemInOffHand();
			if((is.getType().equals(Material.MONSTER_EGG) && is.getItemMeta().getDisplayName().contains("Capture")) || (oh.getType().equals(Material.MONSTER_EGG) && oh.getItemMeta().getDisplayName().contains("Capture"))){
				int s = p.getScoreboard().getObjective("subclass").getScore(p.getName()).getScore();
				if(s==12 || s==2) {
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"castp "+p.getName()+" capture");
					p.getInventory().remove(collectEgg("Steed"));
					p.getInventory().remove(collectEgg("Elybris"));
					event.setCancelled(true);
				}
				else
					p.sendMessage("Only the owner of this horse can collect it!");
			}
		}
	}
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		boolean block = event.getAction().equals(Action.RIGHT_CLICK_BLOCK);
		boolean air = event.getAction().equals(Action.RIGHT_CLICK_AIR);
		if(event.getHand().equals(EquipmentSlot.HAND)) {
			if(air || block) {
				ItemStack mh = event.getPlayer().getInventory().getItemInMainHand();
				//Scouts/Knights
				if(block && mh.getType().equals(Material.MONSTER_EGG) && mh.getItemMeta().getDisplayName()!=null) {
					if(mh.getItemMeta().getDisplayName().contains("Elybris")) {
						event.getPlayer().getInventory().addItem(collectEgg("Elybris"));
					}
					else if(mh.getItemMeta().getDisplayName().contains("Steed")) {
						event.getPlayer().getInventory().addItem(collectEgg("Steed"));
					}
				}
				else if(mh.getType().equals(Material.BLAZE_POWDER) && mh.getItemMeta().getLore()!=null && mh.getItemMeta().getLore().contains(ChatColor.DARK_RED+"BARBARIAN RAGE")) {
					Player p = event.getPlayer();
					if(p.getScoreboard().getObjective("subclass").getScore(p.getName()).getScore()==1) {
						event.setCancelled(true);
						p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,320,2));
						p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,320,0));
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,320,1));
						p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,320,2));
						p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,320,7));
						p.getScoreboard().getObjective("rage").getScore(p.getName()).setScore(0);
						p.getInventory().removeItem(mh);
						new BukkitRunnable(){
					        @Override
					        public void run(){
					        	p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,200,1));
								p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,200,1));
					        }
					   }.runTaskLater(Main.instance, 320L);
					}
					else {p.sendMessage("It burns your hand"); p.damage(1);}
				}
			}
		}
	}
	public ItemStack collectEgg(String horse) {
		ItemStack egg = new ItemStack(Material.MONSTER_EGG);
		ItemMeta met = egg.getItemMeta();
		met.setDisplayName("Capture "+horse);
		egg.setItemMeta(met);
		return egg;
	}
	
	@EventHandler
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) {
			Player p = ((Player)event.getDamager());
			if(p.getScoreboard().getObjective("subclass").getScore(p.getName()).getScore()==1 && p.getScoreboard().getObjective("rage").getScore(p.getName()).getScore() >=1500) {
				ItemStack drop = new ItemStack(Material.BLAZE_POWDER);
				ItemMeta met = drop.getItemMeta();
				met.setDisplayName(ChatColor.DARK_RED + "BOOM TIME");
				ArrayList<String> lst = new ArrayList<String>();
				lst.add(ChatColor.RED+"DROP TO ACTIVATE");
				lst.add(ChatColor.DARK_RED+"BARBARIAN RAGE");
				met.setLore(lst);
				met.addEnchant(Enchantment.BINDING_CURSE, 1, false);
				met.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				drop.setItemMeta(met);
				if(!p.getInventory().contains(drop))
					p.getInventory().addItem(drop);
				//hide scoreboard
			}
			//TODO: else: update progress to rage bar
		}
	}
}
