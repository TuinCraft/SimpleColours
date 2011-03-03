package nl.tuincraft.blaatz0r.SimpleColours;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerListener;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class SimpleColoursPlayerListener extends PlayerListener  {
	

    private final SimpleColours plugin;
    
    
	public SimpleColoursPlayerListener(SimpleColours simpleColours) {
		this.plugin = simpleColours;
	}

	public void onPlayerChat(PlayerChatEvent event) {
		String p = event.getPlayer().getName();
		if (plugin.colourMap.containsKey(p)) {
			String prefix = plugin.colourMap.get(p);
	        event.setFormat(event.getFormat().replace("%1$s", prefix + "%1$s" + ChatColor.WHITE));
		}
	}
	
	public void onPlayerJoin(PlayerEvent event) {
		try {
			Player p = event.getPlayer();
			Connection conn = plugin.getDb().getConnection();
			Statement stat = conn.createStatement();
			
			ResultSet rs = stat.executeQuery("SELECT * FROM colours WHERE name = '" + p.getName()+ "';");
			if (rs.next()) {
				String c = rs.getString("colour");
				if (!plugin.colourMap.containsKey(p.getName())) {
					plugin.colourMap.put(p.getName(), c.toString());
				}
		        Logger log = Logger.getLogger("Minecraft");
		        log.info("Colour set to " + c + " for " + p.getName());
			}
			
	
		} catch (SQLException e) {
			e.printStackTrace();
		}

    }
	
	public void onPlayerQuit(PlayerEvent event) {
		String p = event.getPlayer().getName();
		if (plugin.colourMap.containsKey(p))
			plugin.colourMap.remove(p);
	}
	
    
	
	
}
