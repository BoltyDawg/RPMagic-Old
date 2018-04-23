package com.github.boltydawg;

import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import com.codingforcookies.armorequip.ArmorEquipEvent;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;


public class MyListener implements Listener {
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if(event.getHand() == EquipmentSlot.HAND && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			ItemStack main = player.getInventory().getItemInMainHand();
			if(main.getType()==Material.STICK && CommandWand.WAND_LORE.equals(main.getItemMeta().getLore())) {
				//Checks if this player is a mage
				if(player.getScoreboard().getObjective("class").getScore(player.getName()).getScore()==2) {
					ItemStack off = player.getInventory().getItemInOffHand();
					if(off.getType() == Material.KNOWLEDGE_BOOK) {
						//TODO make sure this /cast is working properly
						player.chat("/rpmcast " + off.getItemMeta().getLore().get(0));
					}
					else {
						/**
						 * This accesses the file containing the player's known spells,
						 * and then once they choose a spell it has that book forcibly
						 *	placed in their left hand
						 */
						this.openBook(player,CommandTeach.getSpells(player));
					}
				}
				else player.sendMessage("You cannot use this item!");
			}
		}
	}
	/**
	 * The following two events use Borlea's code to check when a Mage
	 * puts on/takes off armor that has a trait that increases
	 * their max magicka
	 * @param event
	 */
	@EventHandler
	public void onArmorEquipEvent(ArmorEquipEvent event) {
		Player player = event.getPlayer();
		if(player.getScoreboard().getObjective("class").getScore(player.getName()).getScore()==2) {
			String[] lore = event.getNewArmorPiece().getItemMeta().getLore().toArray(new String[3]);
			for(String s : lore) {
				if(s.contains("Increase your Magicka by ")) {
					int j = Integer.parseInt(s.substring(25));
					//TODO Add j to their max Magicka
					player.sendMessage("+"+j+" Magicka");
				}
			}
		}
	}
	@EventHandler
	public void onArmorUnequipEvent(ArmorEquipEvent event) {
		Player player = event.getPlayer();
		if(player.getScoreboard().getObjective("class").getScore(player.getName()).getScore()==2) {
			String[] lore = event.getOldArmorPiece().getItemMeta().getLore().toArray(new String[3]);
			for(String s : lore) {
				if(s.contains("Increase your Magicka by ")) {
					int j = Integer.parseInt(s.substring(25));
					//TODO Subtract j from their max Magicka
					player.sendMessage("-"+j+" Magicka");
				}
			}
		}
	}
	@EventHandler
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		//TODO Look into adding custom durabilities
//		ItemStack s = new ItemStack(Material.LEATHER_CHESTPLATE);
//		short a = 6000;
//		s.setDurability(a);
//		player.getInventory().addItem(s);
		if(player.getScoreboard().getObjective("class").getScore(player.getName()).getScore()==2) {
			if(event.getItem().getItemMeta().getLore().get(0).equals("+50 Magicka"))
				event.getPlayer().sendMessage("wow this asshole is HUNGERY");
		}		
	}
	
	/**
	 * Opens up a book gui on the mage's screen
	 * so he/she can click on what spell they
	 * want to cast
	 * @param player
	 * @param page
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
}
