package me.brunorm.ffa;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

public class FFAEvents implements Listener {

	@EventHandler
	void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		FFAGame game = FFA.get().getGame();
		FFAPlayer ffaplayer = game.getPlayer(player);
		if(ffaplayer == null) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	void onDamage(EntityDamageEvent event) {
		if(event.getEntityType() != EntityType.PLAYER) return;
		Player player = (Player) event.getEntity();
		FFAGame game = FFA.get().getGame();
		FFAPlayer ffaplayer = game.getPlayer(player);
		if(event.getCause() == DamageCause.VOID) {
			event.setCancelled(true);
			game.setSpectator(ffaplayer);
			return;
		}
		if(event.getFinalDamage() > player.getHealth()) {
			event.setDamage(0);
			game.setSpectator(ffaplayer);
		}
	}

	@EventHandler
	void onDamageEntity(EntityDamageByEntityEvent event) {
		System.out.println("DEBUG: entity damage by entity");
	}

	@EventHandler
	void onDamageBlock(EntityDamageByBlockEvent event) {
		System.out.println("DEBUG: entity damage by block");
	}

}
