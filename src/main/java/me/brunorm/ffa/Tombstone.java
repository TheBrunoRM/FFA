package me.brunorm.ffa;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import me.brunorm.bukkitutils.Messager;

public class Tombstone {

	List<ArmorStand> stands = new ArrayList<ArmorStand>();

	public Tombstone(Location loc, String name) {
		ArmorStand headStand = loc.getWorld().spawn(loc.add(new Vector(0, -0.5, 0)), ArmorStand.class);
		headStand.setCustomName(Messager.color("&b&lHola bro!!"));
		headStand.setCustomNameVisible(true);
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(name);
		skull.setItemMeta(meta);
		headStand.setHelmet(skull);
		headStand.setBasePlate(false);
		headStand.setGravity(false);
		headStand.setVisible(false);
		this.stands.add(headStand);

		ArmorStand thingStand = loc.getWorld().spawn(loc.add(new Vector(0, -0.5, 0)), ArmorStand.class);
		thingStand.setHelmet(new ItemStack(Material.COBBLE_WALL));
		thingStand.setBasePlate(false);
		thingStand.setGravity(false);
		thingStand.setVisible(false);
		this.stands.add(thingStand);

		ArmorStand baseStand = loc.getWorld().spawn(loc.add(new Vector(0, -0.5, 0)), ArmorStand.class);
		baseStand.setHelmet(new ItemStack(Material.STONE));
		baseStand.setBasePlate(false);
		baseStand.setGravity(false);
		baseStand.setVisible(false);
		this.stands.add(baseStand);
	}

	public void remove() {
		for (ArmorStand s : this.stands)
			s.remove();
	}

}
