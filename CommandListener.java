package root;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {
	Main pl;

	public CommandListener(Main pl) {
		this.pl = pl;
		pl.getServer().getPluginManager().registerEvents(this, pl);
	}

	@EventHandler
	public void onPlayerClearlag(PlayerCommandPreprocessEvent e) {
		if (e.getPlayer().hasPermission("lagg.clear")) {
			if (pl.getConfig().getBoolean("commandbypassprotection")) {
				if(Main.analyseClearlagCommand(e.getMessage())){
					Main.overrideItemProtection = true;
				}
			}
		}
	}

}
