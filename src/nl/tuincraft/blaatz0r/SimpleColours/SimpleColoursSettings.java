package nl.tuincraft.blaatz0r.SimpleColours;
import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.util.LinkedHashMap;
	import java.util.Map;
	import org.bukkit.util.config.Configuration;
	
public class SimpleColoursSettings {
	private static final String settingsFile = "SimpleColours.yml";
		
	public static LinkedHashMap<String,String> defaults;

	public static boolean allowDoubles;
	public static boolean usersCanChange;
    
    public static void initialize(File dataFolder) {
    	
    	defaults = new LinkedHashMap<String,String>();
        defaults.put("allow-doubles", "true");
        defaults.put("users-can-change", "true");
        
        if(!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File configFile = new File(dataFolder, settingsFile);
        if(!configFile.exists()) {
            createSettingsFile(configFile);
        }
        
        Configuration config = new Configuration(configFile);
        config.load();
        allowDoubles		= config.getBoolean("allow-doubles", true);
        usersCanChange	= config.getBoolean("users-can-change", true);
    }
	        
    private static void createSettingsFile(File configFile) {
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            configFile.createNewFile();
            fwriter = new FileWriter(configFile, true);
            bwriter = new BufferedWriter(fwriter);
            
            for (Map.Entry<String, String> e : defaults.entrySet()) {
            	
            	bwriter.write(e.getKey() + ": " + e.getValue());
            	bwriter.newLine();
            }
            bwriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.close();
                }
                if (fwriter != null)
                    fwriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	

}
