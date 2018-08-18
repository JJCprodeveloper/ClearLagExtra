package root;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurationHandler {
	static Main main = Main.getInstance();
	static File datafile = null;
	static FileConfiguration datayml = null;
    public ConfigurationHandler(){
    	setupConfig();
    	reloadDataConfig();
    	saveDataConfig();
    	saveDefaultDataConfig();
    }
    public static void setupConfig(){
        main.getConfig().options().copyDefaults(true);
        main.saveConfig();
        main.saveDefaultConfig();
    }
    public static void reloadDataConfig(){
  	  if(datafile == null){
  		  datafile = new File(main.getDataFolder(),"data.yml");
  	  }
  	  datayml = YamlConfiguration.loadConfiguration(datafile);
    }
    public static FileConfiguration getDataConfig(){
  	  if(datayml == null){
  		  reloadDataConfig();
  	  }
  	  return datayml;
    }
    public static  void saveDataConfig(){
  	  if(datafile == null || datayml == null){
  		  return;
  	  }
  	  try{
  		  getDataConfig().save(datafile);
  	  }catch (IOException e){
  		  e.printStackTrace();
  	  }
    }
    public static void saveDefaultDataConfig(){
  	  if(datafile == null){
  		  datafile = new File(main.getDataFolder(),"data.yml");
  	  }
  	  if(!datafile.exists()){
  		  main.saveResource("data.yml",false);
  	  }
    }
}
