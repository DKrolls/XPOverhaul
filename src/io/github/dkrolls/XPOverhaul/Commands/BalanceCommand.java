package io.github.dkrolls.XPOverhaul.Commands;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.dkrolls.XPOverhaul.ConfigHandler;
import io.github.dkrolls.XPOverhaul.Main;
import io.github.dkrolls.XPOverhaul.Misc.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;

public class BalanceCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length > 1){
			sender.sendMessage(Main.prefix+"Invalid usage! "+cmd.getUsage());
		}
		else if(args.length == 1){
			UUIDFetcher f = new UUIDFetcher(Arrays.asList(args[0]));
			try {
				Map<String, UUID> uuids = f.call();
				UUID uuid = uuids.get(args[0]);
				if(uuid == null){
					sender.sendMessage(Main.prefix+args[0]+" has no account on file!");
					return true;
				}
				OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(uuid);
				balance(sender, player, args[0]);
			} catch (Exception e) {
				Bukkit.getLogger().severe("Error fetching UUID!");
				e.printStackTrace();
			}
		}
		else if(args.length == 0){
			if(sender instanceof Player){
				Player player = (Player) sender;
				balance(sender, player, player.getName());
			}
			else{
				sender.sendMessage(Main.prefix+"You must be a player to lookup your own balance!");
			}
		}
		return true;
	}
	
	private void balance(CommandSender sender, OfflinePlayer player, String name){
		File file = ConfigHandler.getPlayerInfo(player);
		if(file == null || !file.exists()){
			sender.sendMessage(Main.prefix+name+" has no account on file!"); //player.getName() is null sometimes 
			return;
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		long balance = config.getLong("balance");
		sender.sendMessage(Main.prefix+name+"'s balance is "+ChatColor.GREEN+balance+".");
	}
}
