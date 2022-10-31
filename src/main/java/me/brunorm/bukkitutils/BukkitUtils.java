package me.brunorm.bukkitutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class BukkitUtils {

    public static JavaPlugin plugin = null;
    public static String debugPrefix = "";
    public static String prefix = "";
    public static boolean debug = false;

    public static void resetPlayerServer(Player player) {
        player.resetPlayerTime();
        player.resetPlayerWeather();
    }

    public static void clearPlayer(Player player, boolean sync) {
        clearPlayer(player);
        if (sync)
            resetPlayerServer(player);
    }

    public static void clearPlayer(Player player) {

        // make visible
        for (final Player players : Bukkit.getOnlinePlayers())
            players.showPlayer(player);

        // clear inventory
        player.getInventory().clear();
        player.getEquipment().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();

        // clear player
        player.setGameMode(GameMode.ADVENTURE);
        player.setExp(0);
        player.setLevel(0);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setMaxHealth(20);
        player.setFlying(false);
        player.setAllowFlight(false);
        if (player.getFireTicks() > 0)
            player.setFireTicks(0);

        // clear potion effects
        for (final PotionEffect e : player.getActivePotionEffects())
            player.removePotionEffect(e.getType());

        // synchronizes the player's time with the server time
        player.resetPlayerTime();
    }

    public static Location getCenteredLocation(Location loc) {
        return loc.clone().add(new Vector(0.5, 0, 0.5));
    }

    Location calculateClosestLocation(Location loc, ArrayList<Location> locations) {
        if (locations.size() <= 1)
            return loc;
        Location closest = locations.get(0);
        for (final Location l : locations)
            if (distance(loc.toVector(), l.toVector()) < distance(loc.toVector(), closest.toVector()))
                closest = l;
        return closest;
    }

    public static double distance(Vector vec1, Vector vec2) {
        final double dx = vec2.getX() - vec1.getX();
        final double dy = vec2.getY() - vec1.getY();
        final double dz = vec2.getZ() - vec1.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static Block getTargetBlock(Player player, int range) {
        final BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR)
                continue;
            break;
        }
        return lastBlock;
    }

    public static int getRandomSlot(Inventory inventory) {
        return (int) Math.floor(Math.random() * inventory.getSize() + 1) - 1;
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static Color getRandomColor() {
        return Color.fromRGB((int) Math.floor(Math.random() * 255), (int) Math.floor(Math.random() * 255),
                (int) Math.floor(Math.random() * 255));
    }

    public static void spawnRandomFirework(Location location) {
        if (location == null)
            return;
        final Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        final FireworkMeta meta = firework.getFireworkMeta();
        final FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.withTrail().withFlicker().with(FireworkEffect.Type.BALL_LARGE)
                .withFade(BukkitUtils.getRandomColor(), BukkitUtils.getRandomColor(), BukkitUtils.getRandomColor())
                .withColor(BukkitUtils.getRandomColor(), BukkitUtils.getRandomColor(), BukkitUtils.getRandomColor());
        meta.addEffect(builder.build());
        meta.setPower(1);
        firework.setFireworkMeta(meta);
    }

    public static boolean checkClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    public static <E> E mostFrequentElement(Iterable<E> iterable) {
        final Map<E, Integer> freqMap = new HashMap<E, Integer>();
        E mostFreq = null;
        int mostFreqCount = -1;
        for (final E e : iterable) {
            Integer count = freqMap.get(e);
            freqMap.put(e, count = count == null ? 1 : count + 1);
            // maintain the most frequent in a single pass.
            if (count > mostFreqCount) {
                mostFreq = e;
                mostFreqCount = count;
            }
        }
        return mostFreq;
    }

    public static void sendDebugMessage(String text, Object... format) {
        if (!debug)
            return;
        sendMessageWithPrefix(debugPrefix, text, format);
    }

    public static void sendDebugMessageWithPrefix(String prefix, String text, Object... format) {
        if (!debug)
            return;
        sendMessageWithPrefix(prefix, text, format);
    }

    public static void sendMessage(String text, Object... format) {
        sendMessageWithPrefix(Messager.color(prefix), Messager.colorFormat(text, format));
    }

    public static void sendMessageWithPrefix(String prefix, String text, Object... format) {
        Bukkit.getConsoleSender().sendMessage(Messager.color(prefix) + " " + Messager.colorFormat(text, format));
    }

}
