package me.brunorm.ffa;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.brunorm.bukkitutils.SavedPlayer;

public class FFAPlayer {

	public Player player;
	public SavedPlayer savedPlayer;
	private boolean spectator = false;
	ArrayList<Block> placedBlocks = new ArrayList<Block>();

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

	public void placedBlock(Block block) {
		placedBlocks.add(block);
	}
	
	public void removedBlock(Block block) {
		placedBlocks.remove(block);
	}

	public ArrayList<Block> getPlacedBlocks() {
		return placedBlocks;
	}

	public void resetPlacedBlocks() {
		placedBlocks.clear();
	}
}
