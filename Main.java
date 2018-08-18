package root;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.minebuilders.clearlag.Clearlag;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
	public static Main pl;
	public static Logger logger = Logger.getLogger("Minecraft");
	public static HashMap<Entity, Long> cacheEntities = new HashMap<Entity, Long>();
	public File file = new File(this.getDataFolder(), "config.yml");
	public static boolean overrideItemProtection = false;

	@Override
	public void onEnable() {
		pl = this;
		if(!file.exists()){
			getConfig().options().copyDefaults(true);
			reloadConfig();
			saveConfig();
		}
		new CommandListener(this);
		new EntityRemoveListener(this);
		startClearingTask();
		logger.info("[ClearlagExtra]Plugin by UmaruDeveloper(JJCDeveloper) has been enabled.");
		if (getClearLag() == null) {
			logger.severe("[ClearlagExtra]Clearlag is not enabled!Disabling Clearlag Extra.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		new ConfigurationHandler();
	}

	public static Main getInstance() {
		return pl;
	}

	public HashMap<Entity, Long> getEntityCache() {
		return cacheEntities;
	}

	@Override
	public void onDisable() {
		logger.info("[ClearlagExtra]Plugin by UmaruDeveloper(JJCDeveloper) has been disabled.");
		if (getConfig().getBoolean("saveentities")) {
			int x = 1;
			for (World w : Bukkit.getWorlds()) {
				for (Entity e : w.getEntities()) {
					if (cacheEntities.get(e) != null) {
						ConfigurationHandler.getDataConfig().set("cache." + x + ".time",
								(System.currentTimeMillis() - cacheEntities.get(e))/1000 );
						ConfigurationHandler.getDataConfig().set("cache." + x + ".location.world",
								e.getLocation().getWorld().getName());
						ConfigurationHandler.getDataConfig().set("cache." + x + ".location.x", e.getLocation().getX());
						ConfigurationHandler.getDataConfig().set("cache." + x + ".location.y", e.getLocation().getY());
						ConfigurationHandler.getDataConfig().set("cache." + x + ".location.z", e.getLocation().getZ());
						ConfigurationHandler.getDataConfig().set("cache." + x + ".location.yaw",
								e.getLocation().getYaw());
						ConfigurationHandler.getDataConfig().set("cache." + x + ".location.pitch",
								e.getLocation().getPitch());
						ConfigurationHandler.getDataConfig().set("cache." + x + ".type", e.getType().toString());
						e.setCustomName(String.valueOf(x));
						e.setCustomNameVisible(false);
						ConfigurationHandler.getDataConfig().set("cache." + x + ".customname", e.getCustomName());
						ConfigurationHandler.saveDataConfig();
						x++;
					}
				}
			}
		}
	}

	private void startClearingTask() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new EntityCheckingTasks(this), 0L,
				this.getConfig().getLong("checkinterval"));
	}

	public Clearlag getClearLag() {
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("ClearLag");
		if (p instanceof Clearlag) {
			return (Clearlag) p;
		}
		return null;
	}

	public static String translateColorCode(String t) {
		t = ChatColor.translateAlternateColorCodes('&', t);
		return t;
	}

	public static List<Entity> getAllEntities() {
		List<Entity> toReturn = new ArrayList<Entity>();
		for (World w : Bukkit.getWorlds()) {
			for (Entity e : w.getEntities()) {
				toReturn.add(e);
			}
		}
		return toReturn;
	}

	public static boolean checkIfDouble(String msg) {
		try {
			Double.parseDouble(msg);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean analyseClearlagCommand(String msg) {
		String[] arrays = msg.split(" ");
		if (arrays[0].equalsIgnoreCase("/lagg")||arrays[0].equalsIgnoreCase("lagg")) {
			if (arrays.length == 2) {
				if (arrays[1].equalsIgnoreCase("clear")) {
					return true;
				} else if (arrays[1].equalsIgnoreCase("killmobs")) {
					return true;
				}
			} else if (arrays.length == 3) {
				if (arrays[1].equalsIgnoreCase("area") && checkIfDouble(arrays[2])) {
					return true;
				}
			}
		}
		return false;
	}

	public static void removeUnusableEntities() {
		Iterator<Entity> keys = cacheEntities.keySet().iterator();
		List<Entity> entitytoremove = new ArrayList<Entity>();
		while (keys.hasNext()) {
			Entity key = keys.next();
			for (World w : Bukkit.getWorlds()) {
				boolean found = false;
				for (Entity e : w.getEntities()) {
					if (e == key) {
						if(!e.isValid()){
							entitytoremove.add(key);
						}
						found = true;
					}
				}
				if (found = false) {
					entitytoremove.add(key);
				}
			}
		}
		for (Entity e : entitytoremove) {
			Main.cacheEntities.remove(e);
		}
	}
}
