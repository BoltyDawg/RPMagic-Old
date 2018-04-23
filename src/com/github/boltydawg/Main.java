package com.github.boltydawg;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

import org.bukkit.plugin.Plugin;
import org.inventivetalent.nicknamer.api.SimpleNickManager;

//TODO get rid of the bad player messages
//TODO Incorporate the other classes and subclasses
//TODO Exp
//TODO add in spells
//TODO Stuff in the Listener
//TODO Learn reflection so I can set max durability, and have stacks of 2 potions
public class Main extends JavaPlugin{
	public static Main instance;
	public static SimpleNickManager nick;
	
	@Override
	public void onEnable() {
		instance = this;
		
		Plugin magicPlugin = Bukkit.getPluginManager().getPlugin("Magic");
		if(magicPlugin==null || !(magicPlugin.getName().equals("Magic"))) {
			Bukkit.getConsoleSender().sendMessage("[RPMagic] Magic is required to run this plugin! Shutting down..."); 
			instance = null;
			this.setEnabled(false);
			return;
		}
		else instance.getLogger().info("Found Magic");
		
		Plugin namer = Bukkit.getPluginManager().getPlugin("NickNamer");
		if(namer == null) {instance.getLogger().info("Launching without NickNamer"); nick = null;}
		else {instance.getLogger().info("Launching with NickNamer"); nick = new SimpleNickManager(namer);}
		
		getServer().getPluginManager().registerEvents(new MyListener(), this);
		
		this.getCommand("rpmcast").setExecutor(new CommandCast());
		this.getCommand("getbook").setExecutor(new CommandBook());
		this.getCommand("getwand").setExecutor(new CommandWand());
		this.getCommand("teach").setExecutor(new CommandTeach());
		this.getCommand("rpmage").setExecutor(new CommandMage());
		this.getCommand("r").setExecutor(new CommandRemember());
		
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
		
		instance.getLogger().info("RPMagic version " + instance.getDescription().getVersion() + " is now enabled!");
	}
	@Override
	public void onDisable() {
		instance.getLogger().info("RPMagic version "+instance.getDescription().getVersion() + " is now disabled");
		instance = null;
	}
}
