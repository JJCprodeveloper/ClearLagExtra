package root;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.minebuilders.clearlag.events.EntityRemoveEvent;

public class EntityRemoveListener implements Listener {
	Main pl;

	public EntityRemoveListener(Main pl) {
		this.pl = pl;
		pl.getServer().getPluginManager().registerEvents(this, pl);
	}

	@EventHandler
	public void onClearlagRemoveEntities(EntityRemoveEvent e) {
		Iterator<Entity> entitys = e.getEntityList().iterator();
		List<Entity> toremove = new ArrayList<Entity>();
		List<Entity> toadd = new ArrayList<Entity>();
		new EntityCheckingTasks(pl).run();
		if (Main.overrideItemProtection) {
			Main.overrideItemProtection = false;
			return;
		}
		int counter = 0;
		while (entitys.hasNext()) {
			Entity entity = entitys.next();
			if (Main.cacheEntities.get(entity) != null) {
				int entityexistance = pl.getConfig().getInt("maxexistance");
				for (String key : pl.getConfig().getConfigurationSection("otherexistance").getKeys(false)) {
					if (key.equalsIgnoreCase(entity.getType().toString())) {
						entityexistance = pl.getConfig().getInt("otherexistance." + key + ".maxexistance");
					}
				}
				if (((System.currentTimeMillis() - Main.cacheEntities.get(entity)) / 1000) >= entityexistance) {
					Main.cacheEntities.remove(entity);
					toadd.add(entity);

				} else {
					toremove.add(entity);
				}
			}
		}
		for (Entity entity : toremove) {
			e.removeEntity(entity);
			counter++;
		}
		for (Entity entity : toadd) {
			e.addEntity(entity);
		}
		if (counter > 0) {
			if (pl.getConfig().getBoolean("removalmessage.enabled") == true) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(Main.translateColorCode(pl.getConfig().getString("removalmessage.format")
							.replaceAll("<entities>", String.valueOf(counter))));
				}
			}
		}

	}
}
