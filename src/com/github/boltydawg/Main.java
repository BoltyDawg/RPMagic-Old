package com.github.boltydawg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import com.nisovin.shopkeepers.api.ShopkeepersPlugin;

import io.loyloy.nicky.Nick;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.plugin.Plugin;

/**OPTIONAL STUFF TODO
 * Buy mcmmo... eh
 * improve Mage casting system: maybe let them chose a spell that they can bind to left mouse, rather than it always being beam?
 * Look up some pre-made 1.13 cmd block stuff that can be used as spells? Like the black hole
 * look into Magic autmatica
 * make public stuff protected instead?. OPh
 */

public class Main extends JavaPlugin{
	public static Main instance;
	public static boolean nick;
	public static final int BASE_STAM = 100;
	public static final int BASE_MAG = 100;
	public static final int RAGE = 400;
	public static Scoreboard scoreboard;
	public static boolean local = false;
	
	public static HashMap<UUID,ItemStack> leftHands;
	public static HashMap<UUID,ArrayList<String>> mages;
	public static HashMap<UUID,Location> beacons;
	public static HashMap<Player,Integer> attributes;
	public static HashMap<UUID,BossBar> bars;
	public static HashMap<UUID,BossBar> rageBars;
	
	public static ShopkeepersPlugin shopkeepers;
	@Override
	public void onEnable() {
		instance = this;
		mages = new HashMap<UUID,ArrayList<String>>();
		leftHands = new HashMap<UUID,ItemStack>();
		beacons = new HashMap<UUID,Location>();
		scoreboard=this.getServer().getScoreboardManager().getMainScoreboard();
		attributes = new HashMap<Player,Integer>();
		bars = new HashMap<UUID,BossBar>();
		rageBars = new HashMap<UUID,BossBar>();
		
//		Plugin magicPlugin = Bukkit.getPluginManager().getPlugin("Magic");
//		if(magicPlugin==null || !(magicPlugin.getName().equals("Magic"))) {
//			Bukkit.getConsoleSender().sendMessage("[RPMagic] Magic is required to run this plugin! Shutting down..."); 
//			instance = null;
//			this.setEnabled(false);
//			return;
//		}
//		else instance.getLogger().info("Found Magic");
		
		for(Plugin plugin:Bukkit.getPluginManager().getPlugins()) {
			if(plugin instanceof ShopkeepersPlugin)
				shopkeepers=(ShopkeepersPlugin)plugin;
			else if(plugin.getName().equalsIgnoreCase("Nicky")) {
				instance.getLogger().info("Found Nicky"); 
				nick = true;
			}
		}
		if(!nick) {
			instance.getLogger().info("Launching without Nicky"); 
			nick = false;
		}
		
		getServer().getPluginManager().registerEvents(new ListenerClass(), this);
		getServer().getPluginManager().registerEvents(new ListenerSubclass(), this);
		getServer().getPluginManager().registerEvents(new ListenerRole(), this);
		
		ArrayList<String> block = new ArrayList<String>();
		block.add(Material.PLAYER_HEAD.name());
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
		this.getCommand("role").setExecutor(new CommandRole());
		this.getCommand("local").setExecutor(new CommandLocal());
		this.getCommand("openinv").setExecutor(new CommandOpenInv());
		this.getCommand("dungeons").setExecutor(new CommandDungeons());
		this.getCommand("rename").setExecutor(new CommandRename());
		
		File f = new File("plugins\\RPMagic");
		f.mkdirs();
		
		//Initializes the scoreboard objectives if this is the first time starting
		try {scoreboard.registerNewObjective("class", "dummy","Class");}
		catch(Exception e) {;}
		try {scoreboard.registerNewObjective("subclass", "dummy","Subclass");}
		catch(Exception e) {;}
		try {scoreboard.registerNewObjective("role", "dummy","Role");}
		catch(Exception e) {;}
		try {scoreboard.registerNewObjective("Magicka", "dummy","Magicka");}
		catch(Exception e) {;}
		try {scoreboard.registerNewObjective("Stamina", "dummy","Stamina");}
		catch(Exception e) {;}
		try {scoreboard.registerNewObjective("damage", "dummy","Damage");}
		catch(Exception e) {;}
		try {scoreboard.registerNewObjective("alive", "dummy","Alive");}
		catch(Exception e) {;}
		try {scoreboard.registerNewObjective("damageTime", "dummy","Damage Time");}
		catch(Exception e) {;}
		try {scoreboard.registerNewObjective("settlement", "dummy","Settlement");}
		catch(Exception e) {;}
		try {scoreboard.registerNewObjective("pillars", "dummy","Settlement");}
		catch(Exception e) {;}
		try {scoreboard.registerNewObjective("zombie", "dummy","Settlement");}
		catch(Exception e) {;}
		try {scoreboard.registerNewObjective("water", "dummy","Settlement");}
		catch(Exception e) {;}
		
		//Creates the files necessary for storage
		SerUtil.createFiles();
		//Reads from those files to instantiate the objects
		SerUtil.loadValues();
		
		instance.getLogger().info("RPMagic version " + instance.getDescription().getVersion() + " is now enabled!");
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					player.setCompassTarget(player.getLocation().add(0,0,-100));
				}
			}
		}.runTaskTimer(this, 30L, 30L);
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
	public static BossBar staminaBar(Player player) {
		BossBar bar = Bukkit.createBossBar(ChatColor.GREEN+"Stamina", BarColor.GREEN, BarStyle.SOLID);
		bar.addPlayer(player);
		double d = ((double)Main.scoreboard.getObjective("Stamina").getScore(player.getName()).getScore())/(Main.BASE_STAM+Main.attributes.getOrDefault(player,0));
		if(d>1)
			bar.setProgress(1.0);
		else if(d>=0)
			bar.setProgress(d);
		else
			bar.setProgress(0);
		return bar;
	}
	public static BossBar magickaBar(Player player) {
		BossBar bar = Bukkit.createBossBar(ChatColor.BLUE+"Magicka", BarColor.BLUE, BarStyle.SOLID);
		bar.addPlayer(player);
		double d = ((double)Main.scoreboard.getObjective("Magicka").getScore(player.getName()).getScore())/(Main.BASE_MAG+Main.attributes.getOrDefault(player,0));
		if(d>1)
			bar.setProgress(1.0);
		else if(d>=0)
			bar.setProgress(d);
		else
			bar.setProgress(0);
		return bar;
	}
	public static BossBar rageBar(Player player) {
		BossBar bar = Bukkit.createBossBar(ChatColor.DARK_RED+"RAGE", BarColor.RED, BarStyle.SOLID);
		bar.addPlayer(player);
		int dmg = Main.scoreboard.getObjective("damage").getScore(player.getName()).getScore();
		if(dmg>=Main.RAGE) {
			bar.setProgress(1.0);
			ListenerSubclass.giveRageDrop(player);
		}
		else if(dmg>=0)
			bar.setProgress(((double)dmg)/Main.RAGE);
		else
			bar.setProgress(0);
		return bar;
	}
}
