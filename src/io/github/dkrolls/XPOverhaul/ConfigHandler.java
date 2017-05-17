package io.github.dkrolls.XPOverhaul;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.dkrolls.XPOverhaul.Misc.NameFetcher;

public class ConfigHandler {
	//balances contains File objects nesting FileConfigurations
	private static HashMap<UUID, File> balances = new HashMap<UUID, File>();
	private static XPAccount[] topAccounts;
	
	public static int DEFAULT_STARTING_BALANCE;
	public static boolean ALLOW_BOTTLE_ENCHANTING;
	public static boolean ALLOW_VIEWING_OTHER_BALANCES;
	public static int TOP_BALANCES_TO_SHOW;
	
	/**
	 * Refreshes HashMap mapping UUIDs to player balance files.
	 * Also will reload values stored in config.yml.
	 */
	public static void initializeConfigs(){
		File file = new File(Main.instance.getDataFolder(), "config.yml");
		if(!file.exists()){
			Main.instance.saveResource("config.yml", false);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		DEFAULT_STARTING_BALANCE = config.getInt("balances.initial-balance"); //could break
		ALLOW_VIEWING_OTHER_BALANCES = config.getBoolean("balances.allow-viewing-other-balances");
		TOP_BALANCES_TO_SHOW = config.getInt("balances.xptop-number");
		ALLOW_BOTTLE_ENCHANTING = config.getBoolean("enchanting.allow-bottle-enchanting");
		File balanceFolder = new File(Main.instance.getDataFolder(), "balances");
		if(!balanceFolder.exists()){
			balanceFolder.mkdirs();
		}
		initializeTables();
	}
	/**
	 * Creates player balance file if it doesn't already exist.
	 * Call this before a method to prevent NPEs.
	 * @param player
	 * @param name
	 */
	public static void createPlayerInfo(OfflinePlayer player, String name){
		UUID uuid = player.getUniqueId();
		File balancesFolder = new File(Main.instance.getDataFolder(), "balances");
		File file = new File(balancesFolder, uuid.toString()+".yml");
		if(!file.exists()){ //optimize using balances HashMap here
			FileConfiguration info = YamlConfiguration.loadConfiguration(file);
			info.set("username", name);
			info.set("balance", DEFAULT_STARTING_BALANCE);
			try {
				info.save(file);
				balances.put(player.getUniqueId(), file);
			} catch (IOException e) {
				Bukkit.getLogger().severe("Error saving to file!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets a File pointing to a player's balance information.
	 * @param player OfflinePlayer to get balance of
	 * @return File that can be opened as a FileConfiguration
	 */
	public static File getPlayerInfo(OfflinePlayer player){
		return balances.get(player.getUniqueId());
	}
	
	/**
	 * Saves config to file.
	 * @param config The FileConfiguration to be saved to File file
	 * @param file The file where config is to be saved
	 * @throws IOException 
	 */
	public static void updateBalance(FileConfiguration config, File file) throws IOException{
		UUID uuid = UUID.fromString(file.getName().substring(0, file.getName().indexOf('.')));
		try {
			config.set("username", NameFetcher.getName(uuid));
			config.save(file);
		} catch (Exception e) {}
	}
	
	public static XPAccount[] getTopAccounts(){
		return topAccounts;
	}
	
	/**
	 * This pulls all balances stored in the "balances" folder and stores them in a HashMap for quick reference.
	 * This pulls all balances stored in the "balances" folder and stores them in a HashMap to provide /xpt abilities.
	 * Avoid using this as it is a costly I/O operation.
	 */
	private static void initializeTables(){
		File balanceFolder = new File(Main.instance.getDataFolder(), "balances");
		List<XPAccount> accountList = new ArrayList<XPAccount>();
		for(File file : balanceFolder.listFiles()){
			String uuidString = file.getName().substring(0, file.getName().indexOf('.'));
			UUID uuid = UUID.fromString(uuidString);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			int balance = config.getInt("balance");
			accountList.add(new XPAccount(balance, uuid));
			balances.put(uuid, file);
		}
		topAccounts = accountList.toArray(new XPAccount[accountList.size()]);
		Arrays.sort(topAccounts);
	}
}
