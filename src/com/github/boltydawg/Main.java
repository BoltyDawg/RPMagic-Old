package com.github.boltydawg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.anjocaido.groupmanager.GroupManager;

import io.loyloy.nicky.Nick;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.plugin.Plugin;

//TODO make sheriffs. Figure out how to make it work with Faction jails? Wanted posters
	//Aren't reliant on Townee. Hit someone with baton and it updates their arrested score then forces them to be mounted on the sheriff and puts them in adventure mode. Right clicking the baton/ string or whatever 
	//releases them and gives them extreme slowness for a bit so they can't run off, or permanently? It could be like they're hogtied
//TODO locks and keys recipes for blacksmiths
// TODO Finish all the subclasses, AND DO THEIR CRAFTING, AND THEIR MANUALS. Go through each subclass individually, focus in on it.
//TODO Skim through subclasses and see if there are any more damage cooldowns I should add
//TODO Buy mcmmo... eh
//TODO improve Mage casting system: maybe let them chose a spell that they can bind to left mouse, rather than it always being beam?
// TODO Look up some pre-made 1.13 cmd block stuff that can be used as spells? Like the black hole
//TODO look into Magic autmatica
//TODO make public stuff protected instead. OPh
// TODO fix /r so that it uses the nickname and has a proper color format? or.... delete it? :(
//TODO Store a map of placed heads with their location as the key and the SkullMeta as the object, so that when a player breaks one of these it gives them the skull with the same meta?
/**TODO: AFTER UPDATE
 * on death: remove their Stamina / Magicka bar
 * set up the boss bars on join/leave/death, make sure to replace barbarian stamina with rage
 * make sure all the commands still work
 * Make Hoplites and knight spears
 */
public class Main extends JavaPlugin{
	public static Main instance;
	public static GroupManager perms;
	public static boolean nick;
	public static final int BASE_STAM = 100;
	public static final int BASE_MAG = 100;
	public static Scoreboard scoreboard;
	
	public static HashMap<UUID,ItemStack> leftHands;
	public static HashMap<UUID,ArrayList<String>> mages;
	public static HashMap<UUID,Location> beacons;
	public static HashMap<Player,Integer> attributes;
	@Override
	public void onEnable() {
		instance = this;
		mages = new HashMap<UUID,ArrayList<String>>();
		leftHands = new HashMap<UUID,ItemStack>();
		beacons = new HashMap<UUID,Location>();
		scoreboard=this.getServer().getScoreboardManager().getMainScoreboard();
		attributes = new HashMap<Player,Integer>();
		
		Plugin magicPlugin = Bukkit.getPluginManager().getPlugin("Magic");
		if(magicPlugin==null || !(magicPlugin.getName().equals("Magic"))) {
			Bukkit.getConsoleSender().sendMessage("[RPMagic] Magic is required to run this plugin! Shutting down..."); 
			instance = null;
			this.setEnabled(false);
			return;
		}
		else instance.getLogger().info("Found Magic");
		
		Plugin namer = Bukkit.getPluginManager().getPlugin("Nicky");
		if(namer == null) {instance.getLogger().info("Launching without Nicky"); nick = false;}
		else {instance.getLogger().info("Launching with Nicky"); nick = true;}
		
		//perms = GroupManager.BukkitPermissions;//(GroupManager)Bukkit.getPluginManager().getPlugin("GroupManager");
		
		getServer().getPluginManager().registerEvents(new ListenerClass(), this);
		getServer().getPluginManager().registerEvents(new ListenerSubclass(), this);
		ArrayList<String> block = new ArrayList<String>();
		block.add(Material.SKULL.name());
		block.add(Material.SKULL_ITEM.name());
		getServer().getPluginManager().registerEvents(new ArmorListener(block), this);
		
		this.getCommand("rpcast").setExecutor(new CommandCast());
		this.getCommand("getbook").setExecutor(new CommandBook());
		this.getCommand("getwand").setExecutor(new CommandWand());
		this.getCommand("teach").setExecutor(new CommandTeach());
		this.getCommand("r").setExecutor(new CommandRemember());
		this.getCommand("start").setExecutor(new CommandStart());
		this.getCommand("dropxp").setExecutor(new CommandDropXP());
		this.getCommand("forget").setExecutor(new CommandForget());
		this.getCommand("subclass").setExecutor(new CommandSubclass());
		this.getCommand("spellinfo").setExecutor(new CommandSpellInfo());
		this.getCommand("getkey").setExecutor(new CommandKey());
		
		File f = new File("plugins\\RPMagic");
		f.mkdirs();
		
		//Initializes the scoreboard objectives if this is the first time starting
		try {this.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("class", "dummy");}
		catch(Exception e) {;}
		try {this.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("subclass", "dummy");}
		catch(Exception e) {;}
		try {this.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("role", "dummy");}
		catch(Exception e) {;}
		try {this.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("Magicka", "dummy");}
		catch(Exception e) {;}
		try {this.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("Stamina", "dummy");}
		catch(Exception e) {;}
		try {this.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("damage", "stat.damageDealt").setDisplayName("RAGE");}
		catch(Exception e) {;}
		try {this.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("alive", "dummy");}
		catch(Exception e) {;}
		try {this.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("damageTime", "dummy");}
		catch(Exception e) {;}
		
		//Creates the files necessary for storage
		SerUtil.createFiles();
		//Reads from those files to instantiate the objects
		SerUtil.loadValues();
		
		instance.getLogger().info("RPMagic version " + instance.getDescription().getVersion() + " is now enabled!");
	}
	@Override
	public void onDisable() {
		SerUtil.storeValues();
		
		instance.getLogger().info("RPMagic version "+instance.getDescription().getVersion() + " is now disabled");
		instance = null;
	}
	
	public static String getName(Player p) {
		if(Main.nick==false) return p.getName();
		
		String n = new Nick(p).get();
		if(n==null) return p.getName();
		else return ChatColor.stripColor(n);
	}
}
