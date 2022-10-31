package me.brunorm.ffa;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class FFAEvents implements Listener {

	private static final boolean COMBO = false;

	// block handling

	@EventHandler
	void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		FFAGame game = FFA.get().getGame();
		FFAPlayer ffaplayer = game.getPlayer(player);
		if (ffaplayer == null)
			return;

		// spawn protection
		if (event.getBlock().getLocation().distance(FFA.get().getGame().getSpawnLocation()) < 5) {
			event.setCancelled(true);
			return;
		}

		Block block = event.getBlock();
		ffaplayer.placedBlock(block);
		game.getBlockManager().addPlacedBlock(event.getBlockReplacedState());
	}

	@EventHandler
	void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		FFAGame game = FFA.get().getGame();
		FFAPlayer ffaplayer = game.getPlayer(player);
		if (ffaplayer == null)
			return;
		event.setCancelled(true);
	}

	// item handling

	@EventHandler
	void onPickup(PlayerPickupItemEvent event) {
		ItemMeta meta = event.getItem().getItemStack().getItemMeta();
		if (meta == null)
			return;
		List<String> lore = meta.getLore();
		if (lore == null)
			return;
		String text = lore.get(0);
		if (text.equalsIgnoreCase("testest")) {
			event.setCancelled(true);
			return;
		}
		Player player = event.getPlayer();
		FFAGame game = FFA.get().getGame();
		FFAPlayer ffaplayer = game.getPlayer(player);
		if (ffaplayer == null)
			return;
	}

	@EventHandler
	void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		FFAGame game = FFA.get().getGame();
		FFAPlayer ffaplayer = game.getPlayer(player);
		if (ffaplayer == null)
			return;
		event.setCancelled(true);
	}

	@EventHandler
	void onUse(PlayerItemDamageEvent event) {
		Player player = event.getPlayer();
		FFAGame game = FFA.get().getGame();
		FFAPlayer ffaplayer = game.getPlayer(player);
		if (ffaplayer == null)
			return;
		event.setCancelled(true);
	}

	@EventHandler
	void onHungry(FoodLevelChangeEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;
		Player player = (Player) event.getEntity();
		FFAGame game = FFA.get().getGame();
		FFAPlayer ffaplayer = game.getPlayer(player);
		if (ffaplayer == null)
			return;
		event.setCancelled(true);
	}

	@EventHandler
	void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		FFAGame game = FFA.get().getGame();
		Block clicked = event.getClickedBlock();
		if (clicked != null && clicked.getState() instanceof Sign) {
			Sign sign = (Sign) clicked.getState();
			if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase("play")) {
				game.joinPlayer(player);
				return;
			}
		}
		FFAPlayer ffaplayer = game.getPlayer(player);
		if (ffaplayer == null)
			return;
		// event.setCancelled(true);
	}

	@EventHandler
	void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		FFAGame game = FFA.get().getGame();
		FFAPlayer ffaplayer = game.getPlayer(player);
		if (ffaplayer == null)
			return;
		event.getEntity().spigot().respawn();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		FFA.get().getScoreboard().getTeam("blue").addPlayer(player);
		// FFA.get().lobby(player);
	}

	@EventHandler
	void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		FFA.get().getGame().leavePlayer(player);
	}

	@EventHandler
	void onProjectileHit(ProjectileHitEvent event) {
		Entity entity = event.getEntity();
		Projectile proj = (Projectile) entity;
		ProjectileSource source = proj.getShooter();
		if (!(source instanceof Player))
			return;
		Player player = (Player) source;
		FFAPlayer ffaplayer = FFA.get().getGame().getPlayer(player);
		if (ffaplayer == null)
			return;
		Block block = entity.getLocation().getBlock();
		this.detect(block);
		entity.remove();
	}

	void detect(Block block) {
		FFABlockManager m = FFA.get().getGame().getBlockManager();
		ArrayList<Block> blocks = new ArrayList<Block>();
		int radius = 3;
		for (int x = -radius; x <= radius; x++)
			for (int y = -radius; y <= radius; y++)
				for (int z = -radius; z <= radius; z++) {
					Block b = block.getRelative(x, y, z);
					if (b.getType().toString().toLowerCase().contains("glass"))
						blocks.add(b);
				}

		int i = 0;
		for (Block b : blocks) {
			Bukkit.getScheduler().runTaskLater(FFA.get(), new Runnable() {
				@Override
				public void run() {
					m.breakAndRegenerateBlock(b);
				}
			}, i);
			i++;
		}
	}

	HashMap<Player, BukkitTask> arrowTasks = new HashMap<Player, BukkitTask>();

	@EventHandler
	void onProjectileLaunch(ProjectileLaunchEvent event) {
		Entity entity = event.getEntity();
		Projectile proj = (Projectile) entity;
		ProjectileSource source = proj.getShooter();
		if (!(source instanceof Player))
			return;
		Player player = (Player) source;
		ItemStack arrows = player.getInventory().getItem(17);
		arrows.setAmount(arrows.getAmount() + 1);
	}

	HashMap<Player, Integer> combo = new HashMap<>();

	@EventHandler
	void onDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.ENTITY_ATTACK)
			return;
		if (event.getEntityType() != EntityType.PLAYER)
			return;

		Player player = (Player) event.getEntity();
		FFAGame game = FFA.get().getGame();
		FFAPlayer ffaplayer = game.getPlayer(player);
		if (ffaplayer == null)
			return;
		if (ffaplayer.isSpectator()) {
			event.setCancelled(true);
			return;
		}

		if (event.getCause() == DamageCause.VOID) {
			event.setCancelled(true);
			player.teleport(game.getSpawnLocation());
			game.setSpectator(ffaplayer);
		} else if (event.getFinalDamage() >= player.getHealth()) {
			event.setDamage(0);
			game.setSpectator(ffaplayer);
		}
	}

	@EventHandler
	void onDamageEntity(EntityDamageByEntityEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;
		Entity damager = event.getDamager();
		if (!(damager instanceof Player))
			return;

		Player victim = (Player) event.getEntity();
		FFAGame game = FFA.get().getGame();
		FFAPlayer victim_player = game.getPlayer(victim);
		if (victim_player == null || victim_player.isSpectator()) {
			event.setCancelled(true);
			return;
		}
		Player attacker = (Player) damager;
		FFAPlayer attacker_player = game.getPlayer(attacker);
		if (attacker_player == null || attacker_player.isSpectator()) {
			event.setCancelled(true);
			return;
		}

		Integer c = this.combo.get(attacker);
		if (c == null)
			c = 0;
		c++;
		this.combo.put(attacker, c);

		Integer c_v = this.combo.get(victim);
		if (c_v != null && c_v > 3)
			this.resetComboAndShowMessage(victim);
		this.combo.remove(victim);

		Location loc = victim.getLocation();

		if (event.getFinalDamage() >= victim.getHealth()) {
			/*
			 * for(int i = 0; i < 100; i++) { spawnRedstone(loc); }
			 */
			for (int i = 0; i < 20; i++)
				Bukkit.getScheduler().runTaskLater(FFA.get(), new Runnable() {
					@Override
					public void run() {
						FFAEvents.this.spawnRedstone(loc);
					}
				}, i);
			attacker.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 1));
			attacker.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 20, 0));
			attacker.playSound(attacker.getLocation(), Sound.NOTE_PLING, 1, 2);
			loc.getWorld().playSound(loc, Sound.CAT_HIT, 1, 1);
			this.resetComboAndShowMessage(attacker);
			event.setDamage(0);
			game.setSpectator(victim_player);
		}

		/*
		 * for(int i = 0; i < 20; i++) { Bukkit.getScheduler().runTaskLater(FFA.get(),
		 * new Runnable() {
		 *
		 * @Override public void run() { spawnRedstone(loc); } }, i); }
		 */

		victim.setMaximumNoDamageTicks(COMBO ? 0 : 20);
		victim.setNoDamageTicks(COMBO ? 0 : 20);
	}

	private void resetComboAndShowMessage(Player player) {
		Integer combo = this.combo.get(player);
		if (combo == null)
			return;
		Bukkit.getScheduler().runTaskLater(FFA.get(), new Runnable() {
			@Override
			public void run() {
				player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
			}
		}, 0);
		Bukkit.getScheduler().runTaskLater(FFA.get(), new Runnable() {
			@Override
			public void run() {
				player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1.5f);
			}
		}, 2);
		Bukkit.getScheduler().runTaskLater(FFA.get(), new Runnable() {
			@Override
			public void run() {
				player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 2);
			}
		}, 4);
		FFA.get().NMS().sendTitle(player, "", "&eCombo " + combo, 5, 5, 5);
		this.combo.remove(player);
	}

	void spawnRedstone(Location loc) {
		loc.getWorld().playSound(loc, Sound.CHICKEN_EGG_POP, 1, (float) (Math.random() * 0.5 + 0.5));
		ItemStack item = new ItemStack(Material.REDSTONE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Long.toString(Instant.now().toEpochMilli()));
		List<String> lore = new ArrayList<String>();
		lore.add("TESTEST");
		meta.setLore(lore);
		item.setItemMeta(meta);
		Item dropped = loc.getWorld().dropItem(loc, item);
		dropped.setVelocity(new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5));
		Bukkit.getScheduler().runTaskLater(FFA.get(), new Runnable() {
			@Override
			public void run() {
				dropped.remove();
			}
		}, 20L * 5);
	}
}
