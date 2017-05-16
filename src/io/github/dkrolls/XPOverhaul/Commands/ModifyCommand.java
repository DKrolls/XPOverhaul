package io.github.dkrolls.XPOverhaul.Commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.dkrolls.XPOverhaul.ConfigHandler;
import io.github.dkrolls.XPOverhaul.Main;
import io.github.dkrolls.XPOverhaul.Misc.ExperienceManager;
import io.github.dkrolls.XPOverhaul.Misc.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;

public class ModifyCommand implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 2){
			sender.sendMessage(Main.prefix+"Incorrect usage! "+cmd.getUsage());
			return true;
		}
		try{
			OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(UUIDFetcher.getUUIDOf(args[0]));
			try{
				int value = Integer.parseInt(args[1]);
				modify(sender, player, args[0], value);
			}catch(NumberFormatException e){
				if(args[1].charAt(args[1].length() - 1) == 'l' || args[1].charAt(args[1].length() - 1) == 'L'){
					try{
						int levels = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
						int value = ExperienceManager.getXpForLevel(Math.abs(levels));
						if(levels < 0){
							modify(sender, player, args[0], -value);
						}
						else{
							modify(sender, player, args[0], value);
						}
						return true;
					}catch(NumberFormatException e1){
						sender.sendMessage(Main.prefix+"Please enter a valid amount to transfer.");
						return true;
					}
				}
				sender.sendMessage(Main.prefix+"Please enter a valid amount to transfer.");
				return true;
			}
		}catch(Exception e){
			sender.sendMessage(Main.prefix+"Invalid account. Please try again.");
			return true;
		}
		return true;
	}
	
	public void modify(CommandSender sender, OfflinePlayer player, String name, int value){
		if(player == null){
			sender.sendMessage(Main.prefix+"Invalid account. Please try again.");
			return;
		}
		else if(value == 0){
			sender.sendMessage(Main.prefix+"Please choose either a positive or negative change in balance.");
			return;
		}
		try{
			ConfigHandler.createPlayerInfo(player, name);
			File file = ConfigHandler.getPlayerInfo(player);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			int balance = config.getInt("balance");
			config.set("balance", balance + value);
			ConfigHandler.updateBalance(config, file);
			if(value < 0){
				sender.sendMessage(Main.prefix+"Deducted "+ChatColor.GREEN+(-value)+ChatColor.WHITE+
					" XP from "+name+"'s balance. Their remaining balance is "+ChatColor.GREEN+(balance + value)+".");
				if(player.getPlayer() != null){
					player.getPlayer().sendMessage(Main.prefix+ChatColor.GREEN+(-value)+ChatColor.WHITE+
						" XP has been deducted from your account.");
				}
				return;
			}
			sender.sendMessage(Main.prefix+"Added "+ChatColor.GREEN+(value)+ChatColor.WHITE+
				" XP to "+name+"'s balance. Their remaining balance is "+ChatColor.GREEN+(balance + value)+".");
			if(player.getPlayer() != null){
				player.getPlayer().sendMessage(Main.prefix+ChatColor.GREEN+(value)+ChatColor.WHITE+
					" XP has been added to your account.");
			}
			return;
		}catch(Exception e){
			sender.sendMessage(Main.prefix+"Invalid account. Please try again.");
			return;
		}
	}
}
