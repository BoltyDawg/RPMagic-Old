package com.github.boltydawg;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.HashMap;

import org.bukkit.plugin.Plugin;
//TODO Player death event: usual shit, clear them from the attributes map
//TODO get rid of the bad player messages
//TODO Incorporate the other classes and subclasses
//TODO Exp
//TODO add more spells
//TODO Make it so that they have to drink the potions twice? - give them a half empty after drinking first time
//TODO Change /mage command and add in /newchar
	//TODO in /newchar, change their nickname to class color + their current nickname. However, this interfers with getting their name, you'd have to clear the formatting first. A lot of extra work that's maybe not worth it in the end
//TODO Think of a system that makes it so that player's can't set their own nickname whenever?
	//TODO a /start command? Can be: /start <class> <name of your character (including spaces)>. it'll only run if your class score is 0, a.k.a. you've died.
//TODO decide what commands I want to keep/remove from the plugin.yml
public class Main extends JavaPlugin{
	public static Main instance;
	public static boolean nick;
	public static final int BASE_STAM = 100;
	public static final int BASE_MAG = 100;
	public static HashMap<Player,ItemStack> leftHands;
	
	@Override
	public void onEnable() {
		instance = this;
		leftHands = new HashMap<Player,ItemStack>();
		
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
		
		getServer().getPluginManager().registerEvents(new MyListener(), this);
		
		this.getCommand("rpcast").setExecutor(new CommandCast());
		this.getCommand("getbook").setExecutor(new CommandBook());
		this.getCommand("getwand").setExecutor(new CommandWand());
		this.getCommand("teach").setExecutor(new CommandTeach());
		this.getCommand("rpmage").setExecutor(new CommandMage());
		this.getCommand("r").setExecutor(new CommandRemember());
		this.getCommand("start").setExecutor(new CommandStart());
		
		File f = new File("plugins\\RPMagic");
		f.mkdirs();
		
		//Initializes the scoreboard objectives if this is the first time starting
		try {Bukkit.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("class", "dummy");}
		catch(Exception e) {;}
		try {Bukkit.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("subclass", "dummy");}
		catch(Exception e) {;}
		try {Bukkit.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("role", "dummy");}
		catch(Exception e) {;}
		try {Bukkit.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("Magicka", "dummy");}
		catch(Exception e) {;}
		try {Bukkit.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("Stamina", "dummy");}
		catch(Exception e) {;}
		
		instance.getLogger().info("RPMagic version " + instance.getDescription().getVersion() + " is now enabled!");
	}
	@Override
	public void onDisable() {
		instance.getLogger().info("RPMagic version "+instance.getDescription().getVersion() + " is now disabled");
		instance = null;
		leftHands=null;
	}
}
