package me.brunorm.ffa;

import org.bukkit.entity.Player;

public class FFAPlayer {

	public Player player;
	public SavedPlayer savedPlayer;
	private boolean spectator = false;

	public FFAPlayer(Player player) {
		this.savedPlayer = new SavedPlayer(player);
		this.player = player;
	}

	public boolean isSpectator() {
		return spectator;
	}

	public void setSpectator(boolean spectator) {
		this.spectator = spectator;
	}

	public Player getPlayer() {
		return player;
	}
}
