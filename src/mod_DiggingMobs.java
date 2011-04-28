package net.minecraft.src;

import net.minecraft.src.modoptionsapi.*;

/**
* A mod which using the Digging Mobs AI API to make the default MC mobs dig
* 
* @author	Clinton Alexander
* @version	0.3.1
*/
public class mod_DiggingMobs extends BaseMod {
	public static final ModBooleanOption zombiesDig  = new ModBooleanOption("Zombies Dig");
	public static final ModBooleanOption creepersDig = new ModBooleanOption("Creepers Dig");
	
	public mod_DiggingMobs() {
		ModOptions mod = new ModOptions("More Deadly Mobs");
		
		mod.addOption(creepersDig);
		mod.addOption(zombiesDig);
		ModOptionsAPI.addMod(mod);
	}
	
	public String Version() {
		return "v0.3.1";
	}
}