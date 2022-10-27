package me.brunorm.ffa;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.brunorm.bukkitutils.BukkitReflection;

public class FFA extends JavaPlugin {

	private static FFA plugin;

	private String packageName;
	private String serverPackageVersion;
	private BukkitReflection nmsHandler;

	FFAGame game;

	@Override
	public void onEnable() {
		plugin = this;

		this.game = new FFAGame();
		this.game.setSpawnLocation(this.getLocationFromConfig("spawn"));

		this.packageName = this.getServer().getClass().getPackage().getName();
		this.serverPackageVersion = this.packageName.substring(this.packageName.lastIndexOf('.') + 1);
		this.nmsHandler = new BukkitReflection();
		
		this.getCommand("ffa").setExecutor(new FFACommand());
		Bukkit.getPluginManager().registerEvents(new FFAEvents(), this);
		Bukkit.getConsoleSender().sendMessage("FFA has been enabled.");
	}

	private Location getLocationFromConfig(String string) {
		final FileConfiguration config = this.getConfig();
		final String name = config.getString(string + ".world");
		if (name == null)
			return null;
		final World world = Bukkit.getWorld(name);
		if (world == null)
			return null;
		return new Location(world, //
				config.getDouble(string + ".x"), //
				config.getDouble(string + ".y"), //
				config.getDouble(string + ".z"));
	}

	public void saveLocationConfig(String string, Location location) {
		final FileConfiguration config = this.getConfig();
		config.set(string + ".x", location.getX());
		config.set(string + ".y", location.getY());
		config.set(string + ".z", location.getZ());
		config.set(string + ".world", location.getWorld().getName());
		try {
			config.save(new File(this.getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable() {
		for(FFAPlayer p : getGame().getPlayers()) getGame().leavePlayer(p);
		Bukkit.getConsoleSender().sendMessage("FFA has been disabled.");
	}

	public static FFA get() {
		return plugin;
	}

	public BukkitReflection NMS() {
		return this.nmsHandler;
	}

	public String getServerPackageVersion() {
		return this.serverPackageVersion;
	}

	public FFAGame getGame() {
		return this.game;
	}
}
