package com.github.boltydawg;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.HashMap;

import org.bukkit.plugin.Plugin;
//TODO Rework the spell storage system. Add in a /spells command that allows them to chose what spells come up when they click their wand
				// will need two maps (NOT MAGES), one for all known spells and one for selected spells. Try to do JSON or YML or something so it doesn't unload chunks 
//TODO Player death event: reset scoreboard scores, clear them from the maps, remove their Stamina / Magicka bar
//TODO Do the subclasses, multiple listeners
//TODO Exp
//TODO add all the spells
//TODO do the spawn chunk stuff in the start command
//TODO Clean up and format player messages
//TODO Change from using serializable to JSON or another format, do research
//TODO fix /r so that it uses the nickname and has a proper color format?
//TODO AFTER UPDATE: set up the boss bars on join/leave/death, make sure all the commands still work
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
