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

	private Location spawnLocation;
	private ArrayList<FFAPlayer> players = new ArrayList<FFAPlayer>();
	HashMap<FFAPlayer, BukkitTask> tasks = new HashMap<FFAPlayer, BukkitTask>();
	private FFABlockManager blockManager;

	public FFAGame() {
		this.blockManager = new FFABlockManager();
	}

	public boolean joinPlayer(Player player) {
		if (!this.integrityCheck())
			return false;
		if (this.isJoined(player))
			return false;
		final FFAPlayer ffaplayer = new FFAPlayer(player);
		this.players.add(ffaplayer);
		this.spawn(ffaplayer);
		return true;
	}

	public boolean leavePlayer(Player player) {
		FFAPlayer ffaplayer = this.getPlayer(player);
		if (ffaplayer == null)
			return false;
		return this.leavePlayer(ffaplayer);
	}

	FFAPlayer getPlayer(Player player) {
		for (FFAPlayer p : this.players)
			if (p.player == player)
				return p;
		return null;
	}

	public boolean leavePlayer(FFAPlayer player) {
		this.getBlockManager().removeBlocksSmooth(player);
		this.players.remove(player);
		player.savedPlayer.Restore();
		// FFA.get().lobby(player.getPlayer());
		return true;
	}

	public void kickAllPlayers() {
		for (FFAPlayer p : this.players)
			p.savedPlayer.Restore();
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

	public Location getSpawnLocation() {
		return this.spawnLocation;
	}

	public void setSpectator(final FFAPlayer ffaplayer) {
		if (ffaplayer.isSpectator())
			return;
		System.out.println("setting spectator: " + ffaplayer.player.getName());
		ffaplayer.setSpectator(true);
		final Player player = ffaplayer.player;
		Bukkit.getScheduler().runTaskLater(FFA.get(), new Runnable() {
			@Override
			public void run() {
				for (FFAPlayer p : FFAGame.this.players) {
					if (p.isSpectator())
						continue;
					p.player.hidePlayer(player);
					player.setFireTicks(0);
				}
			}
		}, 1L);
		BukkitUtils.clearPlayer(player, true);
		player.setGameMode(GameMode.ADVENTURE);
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setVelocity(new Vector(0, 5, 0));
		BukkitTask task = Bukkit.getScheduler().runTaskTimer(FFA.get(), new Runnable() {
			int i = 3;

			@Override
			public void run() {
				if (this.i <= 0) {
					FFAGame.this.respawn(ffaplayer);
					FFAGame.this.tasks.get(ffaplayer).cancel();
					FFA.get().NMS().sendTitle(player, "", "&aRespawned!", 10, 10, 10);
					player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);
				} else {
					FFA.get().NMS().sendTitle(player, "&c&lYOU DIED!", "&eRespawning in " + this.i + " seconds...", 0,
							60, 0);
					this.i--;
				}
			}
		}, 0, 20L);
		this.tasks.put(ffaplayer, task);
		this.getBlockManager().removeBlocksSmooth(ffaplayer);
	}

	FFABlockManager getBlockManager() {
		return this.blockManager;
	}

	protected void respawn(FFAPlayer ffaplayer) {
		ffaplayer.setSpectator(false);
		this.spawn(ffaplayer);
	}

	void spawn(FFAPlayer ffaplayer) {
		Player player = ffaplayer.getPlayer();
		for (Player p : Bukkit.getOnlinePlayers())
			p.showPlayer(player);
		BukkitUtils.clearPlayer(player, true);
		player.setNoDamageTicks(0);
		player.teleport(this.spawnLocation);
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().clear();
		player.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
		player.getInventory().setItem(1, new ItemStack(Material.BOW));
		player.getInventory().setItem(17, new ItemStack(Material.ARROW, 3));
		player.getInventory().setItem(8, new ItemStack(Material.WOOD, 64));
		ItemStack[] armor = { new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.IRON_LEGGINGS),
				new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_HELMET), };
		player.getInventory().setArmorContents(armor);
	}

	public ArrayList<FFAPlayer> getAlivePlayers() {
		return this.players.stream().filter(player -> !player.isSpectator())
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public ArrayList<FFAPlayer> getPlayers() {
		return this.players;
	}
}
