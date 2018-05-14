package com.github.boltydawg;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * This class is what is stored in a .ser file for each mage player
 * @author BoltyDawg
 */
public class Mage implements Serializable{
	
	 static final long serialVersionUID = 137L;
	
	public ArrayList<String> spells;
	
	public Mage() {
		spells = new ArrayList<String>();
	}
	
	public Mage teach(String spell) {
		if(spell==null) return this;
		spells.add(spell);
		return this;
	}
}
