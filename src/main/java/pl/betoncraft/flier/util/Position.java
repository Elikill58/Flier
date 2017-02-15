/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.util;

import org.bukkit.entity.Player;

import pl.betoncraft.flier.api.Usage;

/**
 * Utility class for checking player's position
 *
 * @author Jakub Sapalski
 */
public class Position {

	public static boolean check(Player player, Usage.Where position) {
		boolean air = player.getPlayer().isGliding();
		boolean ground = !air && Utils.getAltitude(player.getPlayer().getLocation(), 4) < 4;
		boolean fall = !ground && !air;
		switch (position) {
		case GROUND:	 return ground;
		case AIR:		 return air;
		case FALL:		 return fall;
		case NO_GROUND:	 return !ground;
		case NO_AIR:	 return !air;
		case NO_FALL:	 return !fall;
		case EVERYWHERE: return true;
		}
		return false;
	}
	
	public static Usage.Where get(Player player) {
		boolean air = player.getPlayer().isGliding();
		boolean ground = !air && Utils.getAltitude(player.getPlayer().getLocation(), 4) < 4;
		if (air) {
			return Usage.Where.AIR;
		} else if (ground) {
			return Usage.Where.GROUND;
		} else {
			return Usage.Where.FALL;
		}
	}

}
