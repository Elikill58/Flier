/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.SidebarLine;
import pl.betoncraft.flier.core.defaults.DefaultGame;
import pl.betoncraft.flier.util.LangManager;

/**
 * A simple deathmatch game.
 *
 * @author Jakub Sapalski
 */
public class DeathMatchGame extends DefaultGame {
	
	protected final Map<UUID, Integer> scores = new HashMap<>();
	protected final Map<String, ChatColor> colors = new HashMap<>();
	
	protected final List<String> spawnNames;
	protected final List<Location> locations;
	protected int spawnCounter = 0;
	
	protected final List<ChatColor> usedColors;
	protected int colorCounter = 0;

	protected final int suicideScore;
	protected final int killScore;
	protected final int pointsToWin;

	public DeathMatchGame(ConfigurationSection section) throws LoadingException {
		super(section);
		suicideScore = loader.loadInt("suicide_score", 0);
		killScore = loader.loadInt("kill_score", 1);
		pointsToWin = loader.loadPositiveInt("points_to_win");
		spawnNames = section.getStringList("spawns");
		locations = new ArrayList<>(spawnNames.size());
		List<String> colors = section.getStringList("colors");
		if (colors.size() > 0) {
			usedColors = new ArrayList<>(colors.size());
			for (String color : colors) {
				try {
					usedColors.add(ChatColor.valueOf(color.toUpperCase().replace(' ', '_')));
				} catch (IllegalArgumentException e) {
					throw new LoadingException(String.format("Color '%s' does not exist.", color));
				}
			}
		} else {
			usedColors = Arrays.asList(ChatColor.values());
		}
		if (spawnNames.isEmpty()) {
			throw new LoadingException("Spawn list cannot be empty");
		}
	}
	
	private class ScoreLine implements SidebarLine {
		
		private UUID uuid;
		private int lastValue = 0;
		private String lastString;
		private String translated;
		
		public ScoreLine(InGamePlayer player) {
			uuid = player.getPlayer().getUniqueId();
			translated = LangManager.getMessage(player, "your_score");
		}

		@Override
		public String getText() {
			int a = scores.get(uuid);
			if (lastString == null || a != lastValue) {
				lastString = translated.replace("{score}", Integer.toString(a));
				lastValue = a;
			}
			return lastString;
		}
		
	}
	
	private class BestScoreLine implements SidebarLine {
		
		private int lastValue = 0;
		private String lastString;
		private String translated;
		
		public BestScoreLine(InGamePlayer player) {
			translated = LangManager.getMessage(player, "best_score");
		}

		@Override
		public String getText() {
			int a = scores.values().stream().max(Comparator.comparingInt(i -> i)).orElse(0);
			if (lastString == null || a != lastValue) {
				lastString = translated.replace("{score}", Integer.toString(a));
				lastValue = a;
			}
			return lastString;
		}
		
	}

	@Override
	public void fastTick() {}

	@Override
	public void slowTick() {}

	@Override
	public void endGame() {
		// get the winning player
		int maxPoints = scores.values().stream()
				.max((a, b) -> a - b)
				.orElse(0);
		List<UUID> winners = scores.entrySet().stream()
				.filter(e -> e.getValue() == maxPoints)
				.map(e -> e.getKey())
				.collect(Collectors.toList());
		// display message about winning
		for (InGamePlayer player : dataMap.values()) {
			UUID uuid = player.getPlayer().getUniqueId();
			String word;
			if (winners.contains(uuid)) {
				word = LangManager.getMessage(player, "win");
			} else {
				word = LangManager.getMessage(player, "lose");
			}
			String teamNames = String.join(", ", winners.stream().map(u -> dataMap.get(u).getPlayer().getName())
					.collect(Collectors.toList()));
			String win = LangManager.getMessage(player, "player_win", teamNames);
			Flier.getInstance().getFancyStuff().sendTitle(
					player.getPlayer(), win, colors.get(player.getPlayer().getName()) + word, 0, 0, 0);
			LangManager.sendMessage(player, "game_ends");
			player.getPlayer().sendMessage(win);
		}
		// end game
		lobby.endGame(this);
	}
	
	@Override
	public void removePlayer(Player player) {
		super.removePlayer(player);
		colors.remove(player.getName());
		scores.remove(player.getUniqueId());
	}

	@Override
	public void handleKill(InGamePlayer killer, InGamePlayer killed, boolean fall) {
		super.handleKill(killer, killed, fall);
		if (rounds) {
			// kills in rounded games don't increase points
			// we should check if there are any other players alive
			// and increase points if the round is finished
			List<UUID> alivePlayers = dataMap.values().stream()
					// filter teams which still have alive players
					.filter(player -> player.isPlaying())
					.map(player -> player.getPlayer().getUniqueId())
					.collect(Collectors.toList());
			if (alivePlayers.size() == 1) {
				UUID winner = alivePlayers.get(0);
				score(dataMap.get(winner), 1);
				alivePlayers.stream()
						.map(uuid -> dataMap.get(uuid))
						.forEach(player -> moveToWaitingRoom(player));
			}
			// this is for test games where only one player is playing
			if (alivePlayers.size() <= 1) {
				roundFinished = true;
			}
		} else {
			// kills in continuous games increase points
			if (killer == null || killer.equals(killed)) {
				score(killed, suicideScore);
				return;
			} else {
				score(killer, killScore);
			}
		}
	}
	
	private void score(InGamePlayer player, int score) {
		UUID uuid = player.getPlayer().getUniqueId();
		int s = scores.get(uuid) + score;
		scores.put(uuid, s);
		if (s >= pointsToWin) {
			endGame();
		}
	}

	@Override
	public void handleRespawn(InGamePlayer player) {
		super.handleRespawn(player);
		ChatColor color = colors.get(player.getPlayer().getName());
		if (color == null) {
			// player starting game
			color = usedColors.get(colorCounter++ % usedColors.size());
			colors.put(player.getPlayer().getName(), color);
			player.setColor(color);
			scores.put(player.getPlayer().getUniqueId(), 0);
			player.getLines().add(new ScoreLine(player));
			player.getLines().add(new BestScoreLine(player));
			for (InGamePlayer p : dataMap.values()) {
				p.updateColors(getColors());
			}
		}
		player.getPlayer().teleport(locations.get(spawnCounter++ % locations.size()));
	}

	@Override
	public Attitude getAttitude(InGamePlayer toThisOne, InGamePlayer ofThisOne) {
		return toThisOne.equals(ofThisOne) ? Attitude.FRIENDLY : Attitude.HOSTILE;
	}

	@Override
	public Map<String, ChatColor> getColors() {
		return colors;
	}
	
	@Override
	public void setArena(Arena arena) throws LoadingException {
		super.setArena(arena);
		locations.clear();
		for (String name : spawnNames) {
			locations.add(arena.getLocation(name));
		}
	}

}
