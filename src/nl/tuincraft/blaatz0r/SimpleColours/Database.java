package nl.tuincraft.blaatz0r.SimpleColours;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	
	private Connection connection = null;
	private static boolean connected = false;
    private final SimpleColours plugin;
	
    public Database(SimpleColours plugin){
    	this.plugin = plugin;
    }
    
	public static boolean isConnected() {
		return connected;
	}
	
	public Connection getConnection() {
		return connection;
	}	
	
	public String getDatabasePath() {
		File db = new File(plugin.getDataFolder(),"SimpleColours.db");
		if (!db.exists()){
			try {
				db.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return db.getPath();
	}
	
	public boolean connect(){
		if (connection != null) {
			return true;
		}

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + getDatabasePath());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connected = true;

		return true;
	}
}