package com.github.boltydawg;

import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import java.util.List;

/**
 * This class deals with many miscellaneous tasks when it comes
 * to spells and the spell casting process.
 * I made this so that I can organize my code more precisely
 * @author BoltyDawg
 */
public class Spells{
	
	/**
	 * a list of all currently implemented spells
	 */
	public static final String[] ALL_SPELLS = new String[] {"test","skel","boom","day","platform","missile"};
	/**
	 * The spells that corespond to each subclass
	 */
	public static final String[] ANY_SPELLS = new String[] {"test","missile"};
	public static final String[] ALC_SPELLS = new String[] {"platform"};
	public static final String[] ARC_SPELLS = new String[] {"day"};
	public static final String[] NEC_SPELLS = new String[] {"skel"};
	public static final String[] PYR_SPELLS = new String[] {"boom"};
	
	/**
	 * A list of what spells belong to what subclass
	 * @param subclass
	 * @return the list of spells that that subclass has
	 */
	public static boolean isRightSubclass(int subclass, String spell) {
		switch(subclass) {
			case 0:{
				for(int i=0;i<ANY_SPELLS.length;i++) {
					if(ANY_SPELLS[i].equals(spell)) return true;
				}
			}
			case 1:{
				for(int i=0;i<ALC_SPELLS.length;i++) {
					if(ALC_SPELLS[i].equals(spell)) return true;
				}
			}
			case 2:{
				for(int i=0;i<ARC_SPELLS.length;i++) {
					if(ARC_SPELLS[i].equals(spell)) return true;
				}
			}
			case 3:{
				for(int i=0;i<NEC_SPELLS.length;i++) {
					if(NEC_SPELLS[i].equals(spell)) return true;
				}
			}
			case 4:{
				for(int i=0;i<PYR_SPELLS.length;i++) {
					if(PYR_SPELLS[i].equals(spell)) return true;
				}
			}
			default:{
				for(int i=0;i<ANY_SPELLS.length;i++) {
					if(ANY_SPELLS[i].equals(spell)) return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * This method determines the subclass of the player and then calls the appropriate cast method
	 * Allows for better organization of code, so that not all ~40 of the spells I plan to implement are thrown
	 * into one method. It also checks to make sure they're casting a spell that their subclass has access too
	 * @param player player that is casting this spell
	 * @param spell name of the spell that is being cast 
	 */
	public static void cast(Player player, String spell) {
		//checks if the player is a Mage
		if(player.getScoreboard().getObjective("class") == null || player.getScoreboard().getObjective("class").getScore(player.getName()).getScore()!=2) {player.sendMessage("You are not a mage... how did you do this? :O"); return;}
		//checks to make sure the player has a subclass value
		if(player.getScoreboard().getObjective("subclass") == null) {player.sendMessage("Something went wrong in setting your classes"); return;}
		
		int score = player.getScoreboard().getObjective("subclass").getScore(player.getName()).getScore();
		switch(score) {
		//TODO make sure that the subclass numbers are accurate
			case 0: {
				SubSpells.cast(player, spell);
				break;
			}
			case 1:{
				SubSpells.Alch.cast(player,spell);
				break;
			}
			case 2:{
				SubSpells.Arca.cast(player,spell);
				break;
			}
			case 3:{
				SubSpells.Necro.cast(player,spell);
				break;
			}
			case 4:{
				SubSpells.Pyro.cast(player,spell);
				break;
			}
			default: player.sendMessage("Something is wrong with your subclass score!");
		}	
	}
	/**
	 * @param spell is the string that's being checked
	 * @return true if the string is a valid spell, false otherwise
	 */
	public static boolean isValid(String spell) {
		//TODO: change this to be if(ALL_SPELLS.contains(spell))
		for(int i=0;i<ALL_SPELLS.length;i++) {
			if(ALL_SPELLS[i].equals(spell)) return true;
		}
		return false;
	}
	
	/**
	 * This method takes a List<String> of spells and turns it into a text component
	 * that will have click events to cast the respective spells and also
	 * makes them c o l o r f u l. 
	 * @param spells
	 * @return
	 */
	public static TextComponent toText(List<String> spells) {
		if(spells.size()==0) return new TextComponent();
		//establishes the base text component. Can't be done in the for loop because of how the addExtra method works
		TextComponent text = new TextComponent(spells.get(0)+"\n\n");
		text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/getbook "+spells.get(0)));
		text.setColor(Spells.getColor(spells.get(0)));
		for(int i=1;i<spells.size();i++) {
			TextComponent temp = new TextComponent(spells.get(i)+"\n\n");
			temp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/getbook "+spells.get(i)));
			temp.setColor(Spells.getColor(spells.get(i)));
			text.addExtra(temp);
		}
		return text;
	}
	
	/**
	 * This method is what determines the color of each spell.
	 * Cuz we can't have a bland looking spell lists!
	 * @param spell
	 * @return
	 */
	public static ChatColor getColor(String spell) {
		switch(spell) {
			case "test": return ChatColor.GOLD;
			case "skel": return ChatColor.DARK_GRAY;
			case "missile": return ChatColor.DARK_RED;
			case "day": return ChatColor.YELLOW;
			case "platform": return ChatColor.GRAY;
			default: return ChatColor.BLACK;
		}
	}
}
