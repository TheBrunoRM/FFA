package me.brunorm.ffa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.brunorm.bukkitutils.BukkitUtils;

public class FFAGame {

	Location spawnLocation;
	public ArrayList<FFAPlayer> players = new ArrayList<FFAPlayer>();

	public FFAGame() {
	}

	public boolean joinPlayer(Player player) {
		if (!this.integrityCheck())
			return false;
		if (this.isJoined(player))
			return false;
		final FFAPlayer ffaplayer = new FFAPlayer(player);
		this.players.add(ffaplayer);
		spawn(ffaplayer);
		return true;
	}
	
	public boolean leavePlayer(Player player) {
		FFAPlayer ffaplayer = getPlayer(player);
		if (ffaplayer == null)
			return false;
		return leavePlayer(ffaplayer);
	}
	
	FFAPlayer getPlayer(Player player) {
		for(FFAPlayer p : players) if(p.player == player) return p;
		return null;
	}

	public boolean leavePlayer(FFAPlayer player) {
		this.players.remove(player);
		player.savedPlayer.Restore();
		return true;
	}

	private boolean integrityCheck() {
		if (this.spawnLocation == null)
			return false;
		return true;
	}

	private boolean isJoined(Player player) {
		for (final FFAPlayer p : this.players)
			if (p.player == player)
				return true;
		return false;
	}

	public void setSpawnLocation(Location loc) {
		this.spawnLocation = loc;
	}

	HashMap<FFAPlayer, BukkitTask> tasks = new HashMap<FFAPlayer, BukkitTask>();
	
	public void setSpectator(final FFAPlayer ffaplayer) {
		ffaplayer.setSpectator(true);
		final Player player = ffaplayer.player;
		BukkitUtils.clearPlayer(player, true);
		for(FFAPlayer p : players) {
			p.player.hidePlayer(player);
		}
		player.setGameMode(GameMode.ADVENTURE);
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setVelocity(new Vector(0,5,0));
		BukkitTask task = Bukkit.getScheduler().runTaskTimer(FFA.get(), new Runnable() {
			int i = 3;
			@Override
			public void run() {
				if(i <= 0) {
					respawn(ffaplayer);
					tasks.get(ffaplayer).cancel();
					FFA.get().NMS().sendTitle(player, "", "&aRespawned!", 10, 10, 10);
					player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
				} else {
					FFA.get().NMS().sendTitle(player,
							"&c&lYOU DIED!",
							"&eRespawning in " + i + " seconds...");
					i--;
				}
			}
		}, 0, 20L);
		tasks.put(ffaplayer, task);
	}

	protected void respawn(FFAPlayer ffaplayer) {
		ffaplayer.setSpectator(false);
		spawn(ffaplayer);
	}
	
	void spawn(FFAPlayer ffaplayer) {
		Player player = ffaplayer.getPlayer();
		player.teleport(spawnLocation);
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().clear();
		player.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
		ItemStack[]armor = {
			new ItemStack(Material.IRON_HELMET),
			new ItemStack(Material.IRON_CHESTPLATE),
			new ItemStack(Material.IRON_LEGGINGS),
			new ItemStack(Material.IRON_BOOTS),
		};
		player.getInventory().setArmorContents(armor);
	}
	
	public ArrayList<FFAPlayer> getAlivePlayers() {
		return this.players.stream().filter(player -> !player.isSpectator())
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public ArrayList<FFAPlayer> getPlayers() {
		return players;
	}
}
