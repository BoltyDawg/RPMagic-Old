package com.github.boltydawg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SpellMap implements Serializable{

	private static final long serialVersionUID = 4317006384809300373L;
	
	private HashMap<UUID,ArrayList<String>> list;
	
	public SpellMap() {
		list = new HashMap<UUID,ArrayList<String>>();
	}
	public SpellMap(HashMap<UUID,ArrayList<String>> map) {
		list=map;
	}
	public HashMap<UUID,ArrayList<String>> toMap(){
		return list;
	}

}
