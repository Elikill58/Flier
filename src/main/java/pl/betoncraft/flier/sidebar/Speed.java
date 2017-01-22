/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.sidebar;

import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.SidebarLine;

/**
 * A sidebar line showing player's speed.
 *
 * @author Jakub Sapalski
 */
public class Speed implements SidebarLine {
	
	private InGamePlayer player;
	private double lastValue = 0;
	private String lastString;
	
	public Speed(InGamePlayer player) {
		this.player = player;
	}

	@Override
	public String getText() {
		double s = player.getPlayer().getVelocity().length() * 10;
		if (s < 1) {
			s = 0;
		}
		if (lastString == null || s != lastValue) {
			lastString = String.format("S: %.1f~", s);
			lastValue = s;
		}
		return lastString;
	}

}