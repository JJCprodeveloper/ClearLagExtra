package root;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class EntityCheckingTasks implements Runnable {
	private Main pl;
	private static boolean firstrun = true;

	public EntityCheckingTasks(Main pl) {
		this.pl = pl;
	}

	@Override
	public void run() {
		Main.removeUnusableEntities();
		for (World w : Bukkit.getWorlds()) {
			for (Entity e : w.getEntities()) {
				if (Main.cacheEntities.get(e) == null) {
					Main.cacheEntities.put(e, System.currentTimeMillis());
				}
			}
		}
		if (firstrun == true) {
			if (pl.getConfig().getBoolean("saveentities")
					&& ConfigurationHandler.getDataConfig().get("cache") != null) {
				for (String key : ConfigurationHandler.getDataConfig().getConfigurationSection("cache")
						.getKeys(false)) {
					for (Entity e : Main.cacheEntities.keySet()) {
						if (e.getCustomName() != null) {
							if (e.getCustomName().equals(
									ConfigurationHandler.getDataConfig().getString("cache." + key + ".customname"))) {
								Main.cacheEntities.put(e, System.currentTimeMillis()
										- (ConfigurationHandler.getDataConfig().getLong("cache." + key + ".time")
												* 1000));
							}
						}
					}
					ConfigurationHandler.getDataConfig().set("cache." + key, null);
					ConfigurationHandler.saveDataConfig();
				}
			}
			firstrun = false;
		}
	}
}
