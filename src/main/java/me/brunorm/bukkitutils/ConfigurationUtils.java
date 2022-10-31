package me.brunorm.bukkitutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigurationUtils {

    public static final int DEFAULT_BUFFER_SIZE = 8192;

    public static JavaPlugin plugin;

    private static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            final byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1)
                outputStream.write(bytes, 0, read);
            outputStream.close();
        }

    }

    public static YamlConfiguration loadConfiguration(String name, String defaultFileName, String altDefaultFileName) {
        if (plugin.getResource(defaultFileName) == null)
            return loadConfiguration(name, altDefaultFileName);
        return createMissingKeys(loadConfiguration(name, defaultFileName), getDefaultConfig(altDefaultFileName));
    }

    public static YamlConfiguration loadConfiguration(String name, String defaultFileName) {
        final File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            BukkitUtils.sendDebugMessage("creating file " + name);
            copyDefaultContentsToFile(defaultFileName, file);
        }
        return createMissingKeys(YamlConfiguration.loadConfiguration(file), getDefaultConfig(defaultFileName));
    }

    public static YamlConfiguration createMissingKeys(YamlConfiguration conf, YamlConfiguration defaultConfig) {

        try {
            final ConfigurationSection section = defaultConfig.getConfigurationSection("");

            boolean modified = false;

            for (final String key : section.getKeys(true))
                if (conf.get(key) == null) {
                    BukkitUtils.sendMessage("&cWarning: key &b%s &cis not set.", key);
                    modified = true;

                    // setting the key in the configuration
                    // so it uses the default value if is not set
                    // but not saving it to prevent removing spaces and comments
                    conf.set(key, defaultConfig.get(key));
                }
            if (modified) {
                BukkitUtils.sendMessage("&cYou should not delete keys in the configuration files.");
                BukkitUtils.sendMessage("&6The plugin will use the default values for the deleted keys.");
            }
            return conf;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static YamlConfiguration getDefaultConfig(String defaultFileName) {
        try {
            final InputStream stream = plugin.getResource(defaultFileName);
            if (stream == null) {
                BukkitUtils.sendMessage("Could not get resource: " + defaultFileName);
                return null;
            }
            final Reader defaultConfigStream = new InputStreamReader(stream, "UTF-8");
            return YamlConfiguration.loadConfiguration(defaultConfigStream);
        } catch (final UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void copyDefaultContentsToFile(String defaultFileName, File file) {
        try {
            BukkitUtils.sendDebugMessage("copying default contents to file " + file.getPath());
            final File parent = new File(file.getParent());
            if (!parent.exists())
                parent.mkdir();
            if (!file.exists())
                file.createNewFile();
            final InputStream stream = plugin.getResource(defaultFileName);
            if (stream == null) {
                BukkitUtils.sendMessage("Could not get resource: " + defaultFileName);
                return;
            }
            copyInputStreamToFile(stream, file);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveConfiguration(YamlConfiguration config, String path) {
        try {
            config.save(new File(plugin.getDataFolder(), path));
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Location getLocationConfig(ConfigurationSection section, YamlConfiguration config) {
        if (section == null)
            return null;
        String worldName = section.getString("world");
        if (worldName == null)
            worldName = config.getString("worldName");
        if (worldName == null) {
            BukkitUtils.sendDebugMessage("warning, getlocationconfig: worldname is null");
            return null;
        }
        final World world = Bukkit.getWorld(worldName);
        if (world != null)
            return getLocationConfig(world, section);
        BukkitUtils.sendDebugMessage("warning, getlocationconfig: world is null");
        return null;
    }

    public static Location getLocationConfig(World world, ConfigurationSection section) {
        final Location loc = new Location(world, section.getInt("x"), section.getInt("y"), section.getInt("z"));
        return loc;
    }
}
