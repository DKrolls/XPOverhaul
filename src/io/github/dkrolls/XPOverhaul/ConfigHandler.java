package io.github.dkrolls.XPOverhaul;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigHandler {
	//balances contains File objects nesting FileConfigurations
	static HashMap<String, File> balances = new HashMap<String, File>();
	
	public static long DEFAULT_STARTING_BALANCE;
	public static boolean ALLOW_BOTTLE_ENCHANTING;
	public static boolean ALLOW_VIEWING_OTHER_BALANCES;
	
	public static void initializeConfigs(){
		File file = new File(Main.instance.getDataFolder(), "config.yml");
		if(!file.exists()){
			Main.instance.saveResource("config.yml", false);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		DEFAULT_STARTING_BALANCE = config.getLong("balances.initial-balance"); //could break
		ALLOW_VIEWING_OTHER_BALANCES = config.getBoolean("balances.allow-viewing-other-balances");
		ALLOW_BOTTLE_ENCHANTING = config.getBoolean("enchanting.allow-bottle-enchanting");
		File balanceFolder = new File(Main.instance.getDataFolder(), "balances");
		if(!balanceFolder.exists()){
			balanceFolder.mkdirs();
		}
		balances = getBalances(); // ~N initialization, constant lookup and insert
	}
	
	public static void createPlayerInfo(OfflinePlayer player, String name){
		String uuid = player.getUniqueId().toString();
		File balancesFolder = new File(Main.instance.getDataFolder(), "balances");
		File file = new File(balancesFolder, uuid+".yml");
		if(!file.exists()){ //optimize using balances HashMap here
			FileConfiguration info = YamlConfiguration.loadConfiguration(file);
			info.set("username", name);
			info.set("balance", DEFAULT_STARTING_BALANCE);
			try {
				info.save(file);
				balances.put(player.getUniqueId().toString(), file);
			} catch (IOException e) {
				Bukkit.getLogger().severe("Error saving to file!");
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Returns null if hasn't played before
	 */
	public static File getPlayerInfo(OfflinePlayer player){
		return balances.get(player.getUniqueId().toString());
	}
	
	/*
	 * Avoid using this as it pulls the entire list
	 */
	private static HashMap<String, File> getBalances(){
		File balanceFolder = new File(Main.instance.getDataFolder(), "balances");
		HashMap<String, File> balances = new HashMap<String, File>();
		for(File f : balanceFolder.listFiles()){
			String uuid = f.getName().substring(0, f.getName().indexOf('.'));
			balances.put(uuid, f);
		}
		return balances;
	}
}
