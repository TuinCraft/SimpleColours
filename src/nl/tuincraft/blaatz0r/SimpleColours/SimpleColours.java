package nl.tuincraft.blaatz0r.SimpleColours;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Logger;

import nl.tuincraft.blaatz0r.SimpleColours.CommandExecutors.ColourExecutor;

import org.bukkit.ChatColor;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;


public class SimpleColours extends JavaPlugin {
	private final SimpleColoursPlayerListener playerListener = new SimpleColoursPlayerListener(this);
    public static Logger log;    
    private Database db;
    public String name;
    public String version;
    public HashMap<String,String> colourMap;
	public HashMap<Integer,String> colours;
	
	public void onDisable() {
        log = Logger.getLogger("Minecraft");
        log.info(name + " " + version + " disabled");
		
	}

	public void onEnable() {
		name = this.getDescription().getName();
		version = this.getDescription().getVersion();
		
        SimpleColoursSettings.initialize(getDataFolder());
		
		this.getDataFolder().mkdir();
		
		colours = new HashMap<Integer,String>();
		for (ChatColor c : ChatColor.values()){
			colours.put(c.getCode(),c.name().toLowerCase().replace('_', ' '));
		}
		
        // Register our events
		this.colourMap = new HashMap<String,String>();
		this.db = new Database(this);
        db.connect();
        
        try {
			Connection conn = this.getDb().getConnection();
			Statement stat = conn.createStatement();
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS colours (name, colour);");
			
			ResultSet rs = stat.executeQuery("SELECT * FROM colours;");
			while (rs.next()) {
				this.colourMap.put(rs.getString("name"), rs.getString("colour"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
        PluginManager pm = getServer().getPluginManager();        
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
        //pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
        
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);

        getCommand("colour").setExecutor(new ColourExecutor(this));
        
        log = Logger.getLogger("Minecraft");
        log.info(name + " " + version + " enabled");
		
	}

	public Database getDb() {
		return this.db;
	}

}
