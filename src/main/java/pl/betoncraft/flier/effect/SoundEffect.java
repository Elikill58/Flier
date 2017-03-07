/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.effect;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.core.defaults.DefaultEffect;

/**
 * Plays a sound to a single player.
 *
 * @author Jakub Sapalski
 */
public class SoundEffect extends DefaultEffect {

	private static final String SOUND = "sound";
	private static final String VOLUME = "volume";
	private static final String PITCH = "pitch";
	
	private Sound sound;
	private float volume;
	private float pitch;

	public SoundEffect(ConfigurationSection section) throws LoadingException {
		super(section);
		playerOnly();
		sound = loader.loadEnum(SOUND, Sound.class);
		volume = (float) loader.loadPositiveDouble(VOLUME, 1.0);
		pitch = (float) loader.loadPositiveDouble(PITCH, 1.0);
	}

	@Override
	public void fire(InGamePlayer player) {
		player.getPlayer().playSound(player.getPlayer().getLocation(), sound, volume, pitch);
	}

}
