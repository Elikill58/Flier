/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Modifies score of the player.
 *
 * @author Jakub Sapalski
 */
public class ScoreAction extends DefaultAction {
	
	private final static String AMOUNT = "amount";
	
	private final int amount;

	public ScoreAction(ConfigurationSection section) throws LoadingException {
		super(section, false, false);
		amount = loader.loadInt(AMOUNT);
	}

	@Override
	public boolean act(Optional<InGamePlayer> creator, Optional<InGamePlayer> source, InGamePlayer target,
			Optional<UsableItem> item) {
		return target.getGame().modifyPoints(target.getPlayer().getUniqueId(), amount);
	}

}
