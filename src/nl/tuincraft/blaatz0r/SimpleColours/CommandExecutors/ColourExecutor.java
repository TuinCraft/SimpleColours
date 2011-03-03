package nl.tuincraft.blaatz0r.SimpleColours.CommandExecutors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import nl.tuincraft.blaatz0r.SimpleColours.SimpleColours;
import nl.tuincraft.blaatz0r.SimpleColours.SimpleColoursSettings;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;


public class ColourExecutor implements CommandExecutor {
	
	private final SimpleColours plugin;
	
	public ColourExecutor(SimpleColours p) {
		this.plugin = p;	
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] sects) {
		// TODO Auto-generated method stub
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (sects.length == 1) {
				String arg = sects[0];
				if (arg.equalsIgnoreCase("help")) {
					this.sendHelp(player);
				} else if (arg.equalsIgnoreCase("reset") && (player.isOp() || SimpleColoursSettings.usersCanChange)){
					this.removePlayer(player);							
				} else {
					// /colour blue
					try {
						ChatColor c = ChatColor.valueOf(arg.replace(' ','_').toUpperCase()); 
						if (player.isOp() || SimpleColoursSettings.usersCanChange) {
							if (player.isOp() || SimpleColoursSettings.allowDoubles || colourAvailable(c)) {
								this.setColour(player, c);
							} else {
								player.sendMessage("That colour is already taken! Double colours are not allowed.");
							}
						} else {
							player.sendMessage("Players are not allowed to change colour.");
						}
					} catch (Exception ex) {
						return false;
					}
				}
				return true;
			} else if (sects.length == 2) {
				String arg = sects[0];
				String rest = sects[1];
				
				// /colour blaatz0r
				if (player.isOp() && (isOnlinePlayer(arg) || inDatabase(arg))) {
					// /colour blaatz0r reset
					if (rest.equalsIgnoreCase("reset")){
						this.removePlayer(plugin.getServer().getPlayer(arg));
					} 
					
					// /colour blaatz0r red
					else {
						ChatColor c;
						try {
							c = ChatColor.valueOf(rest.replace(' ','_').toUpperCase());
							this.setColour(plugin.getServer().getPlayer(arg), c);
						} catch (Exception ex) {
							return false;
						}
						return true;

					}
				} 
				
				// /colour dark gray
				else {
					String newColour = arg + ' ' + rest;
					ChatColor c;
					try {
						c = ChatColor.valueOf(newColour.replace(' ','_').toUpperCase()); 
						if (player.isOp() || SimpleColoursSettings.usersCanChange) {
							if (SimpleColoursSettings.allowDoubles || colourAvailable(c)) {
								this.setColour(player, c);
							} else {
								player.sendMessage("That colour is already taken! Double colours are not allowed.");
							}
						} else {
							player.sendMessage("Players are not allowed to change colour.");
						}
					} catch (Exception ex) {
						return false;
					}
				}
				return true;
			} 
			
			// /colour blaatz0r dark blue
			else if (sects.length == 3) {
				String arg = sects[0];
				if (player.isOp() && (isOnlinePlayer(arg) || inDatabase(arg))) {
					String rest = sects[1] + ' ' + sects[2];
					ChatColor c;
					try {
						c = ChatColor.valueOf(rest.replace(' ','_').toUpperCase());
						this.setColour(plugin.getServer().getPlayer(arg), c);
					} catch (Exception ex) {
						return false;
					}
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
			
		} else {
			return false;			
		}
	}
	

    private boolean colourAvailable(ChatColor c) {

		String colour = c.toString();
		try {
			Connection conn = plugin.getDb().getConnection();
			PreparedStatement stat = conn.prepareStatement("SELECT * FROM colours WHERE colour = ?;");
			stat.setString(1,colour);
			ResultSet rs = stat.executeQuery();
			return !rs.next();			
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;	
	}

	private boolean inDatabase(String arg) {
    	
		try {
			Connection conn = plugin.getDb().getConnection();
			PreparedStatement stat = conn.prepareStatement("SELECT * FROM colours WHERE name = ?;");
			stat.setString(1,arg);
			ResultSet rs = stat.executeQuery();
			return rs.next();			
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isOnlinePlayer(String s) {
		boolean found = false;
		
    	for (Player p : plugin.getServer().getOnlinePlayers()) {
    		if (p.getName().equals(s)) {
    			found = true;
    		}
    	}
    	return found;
    }
    
    private void sendHelp(Player player) {
		player.sendMessage("Use /colour [colour] to set your player colour (e.g.: /colour red), available colours:");
		String colourString = "";
		for (String s : plugin.colours.values()) {
			ChatColor c = ChatColor.valueOf(s.replace(' ','_').toUpperCase());
			colourString += c + s + ChatColor.WHITE + ", ";
		}
		colourString = colourString.substring(0,colourString.length()-2) + ".";
		player.sendMessage(colourString);
		player.sendMessage("/colour reset - resets back to white");
		
	}

	private void removePlayer(Player player) {
		plugin.colourMap.remove(player.getName());
		
		if (inDatabase(player.getName())) {
			try {
				Connection conn = plugin.getDb().getConnection();
				Statement stat = conn.createStatement();
				stat.execute("DELETE FROM colours WHERE name = '" + player.getName() + "'");
		
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
    
	public void setColour(Player p, ChatColor c) {
		try {
			String colour = c.toString();
			Connection conn = plugin.getDb().getConnection();
			Statement stat = conn.createStatement();
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS colours (name, colour);");
			
			ResultSet rs = stat.executeQuery("SELECT * FROM colours WHERE name = '" + p.getName()+ "';");
			if (rs.next()) {
				stat.execute("UPDATE colours SET colour = '" + colour + "' WHERE name = '" + p.getName() + "';");
				
			} else {
				PreparedStatement prep = conn.prepareStatement(
			      "INSERT INTO colours VALUES (?, ?);");
				prep.setString(1, p.getName());
				prep.setString(2, colour);
				prep.execute();
			}
			
			plugin.colourMap.put(p.getName(), c.toString());
	        Logger log = Logger.getLogger("Minecraft");
	        log.info("Colour set to " + c + " for " + p.getName());
	    	p.sendMessage("Color set to " + c + plugin.colours.get(c.getCode()) + ChatColor.WHITE + ".");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
