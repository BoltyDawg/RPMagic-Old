package com.github.boltydawg;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class ScoutBeacon {
	private Chunk chunk;
	private Location location;
	
	public ScoutBeacon(Location l) {
		location=l;
		chunk = l.getChunk();
	}
	public Chunk getChunk() {
		return chunk;
	}
	public Location getLocation() {
		return location;
	}
}
