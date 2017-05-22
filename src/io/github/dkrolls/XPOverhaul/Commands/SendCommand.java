package io.github.dkrolls.XPOverhaul.Commands;

import java.io.File;
import java.io.IOException;

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
import io.github.dkrolls.XPOverhaul.Misc.ExperienceManager;
import io.github.dkrolls.XPOverhaul.Misc.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;

public class SendCommand implements CommandExecutor{

	@Override //transfer [destination] (source) [amount]
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length > 3 || args.length < 2){
			sender.sendMessage(Main.prefix+"Incorrect usage! "+cmd.getUsage());
			return true;
		}
		if(args.length == 2){ //transfer [destination] [amount]
			if(!(sender instanceof Player)){
				sender.sendMessage(Main.prefix+"Please specify a source and destination account.");
				return true;
			}
			Player src = (Player) sender;
			ExperienceManager m = new ExperienceManager(src);
			ConfigHandler.createPlayerInfo(src, src.getName());
			File file = ConfigHandler.getPlayerInfo(src);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			int balance = config.getInt("balance");
			OfflinePlayer dst = null;
			try {
				dst = Bukkit.getServer().getOfflinePlayer(UUIDFetcher.getUUIDOf(args[0]));
			} catch (Exception e1) {
				sender.sendMessage(Main.prefix+"Invalid account. Please try again.");
				return true;
			}
			try{
				int value = Integer.parseInt(args[1]);
				transfer(sender, value, src, dst, src.getName(), args[0]);
			}catch(NumberFormatException e){
				if(args[1].equals("all")){
					transfer(sender, balance, src, dst, src.getName(), args[0]);
					return true;
				}
				else if(args[1].charAt(args[1].length() - 1) == 'l' || args[1].charAt(args[1].length() - 1) == 'L'){
					String trimmed = args[1].substring(0, args[1].length() - 1);
					int temp = m.getCurrentExp(); //this is so janky
					m.setExp(0); //involves storing XP, giving levels to see how much XP
					src.setLevel(Integer.parseInt(trimmed)); //corresponds to level then transfer
					int value = m.getCurrentExp();
					m.setExp(temp);
					transfer(sender, value, src, dst, src.getName(), args[0]);
					return true;
				}
				src.sendMessage(Main.prefix+"Please enter a valid amount to transfer.");
				return true;
			}
		}
		else if(args.length == 3){ //transfer [src] [dst] [amount]
			if(!sender.hasPermission("XPO.send.other")){
				sender.sendMessage(Main.prefix+"You can only transfer balance from your own account.");
				return true;
			}
			OfflinePlayer src = null;
			OfflinePlayer dst = null;
			try {
				src = Bukkit.getServer().getOfflinePlayer(UUIDFetcher.getUUIDOf(args[0]));
				dst = Bukkit.getServer().getOfflinePlayer(UUIDFetcher.getUUIDOf(args[1]));
			}catch (Exception e1) {
				sender.sendMessage(Main.prefix+"Invalid account. Please try again.");
				return true;
			}
			try{
				int value = Integer.parseInt(args[2]);
				transfer(sender, value, src, dst, args[0], args[1]);
			}catch(NumberFormatException e){
				if(args[2].equals("all")){
					ConfigHandler.createPlayerInfo(src, args[0]);
					File srcFile = ConfigHandler.getPlayerInfo(src);
					FileConfiguration srcConfig = YamlConfiguration.loadConfiguration(srcFile);
					int srcBalance = srcConfig.getInt("balance");
					transfer(sender, srcBalance, src, dst, args[0], args[1]);
				}
				else if(args[2].charAt(args[2].length() - 1) == 'l' || args[2].charAt(args[2].length() - 1) == 'L'){
					String trimmed = args[2].substring(0, args[2].length() - 1);
					try{
						int levels = Integer.parseInt(trimmed);
						int value = ExperienceManager.getXpForLevel(levels);
						Bukkit.getServer().broadcastMessage("xp at level "+levels+": "+value);
						transfer(sender, value, src, dst, args[0], args[1]);
					}catch(NumberFormatException e1){
						sender.sendMessage(Main.prefix+"Please enter a valid amount to transfer.");
						return true;
					}
				}
				else{
					sender.sendMessage(Main.prefix+"Please enter a valid amount to transfer.");
					return true;
				}
			}
		}
		return true;
	}
	private void transfer(CommandSender sender, int value, OfflinePlayer src, OfflinePlayer dst, String srcName, String dstName){
		if(src == null || dst == null){
			sender.sendMessage(Main.prefix+"Invalid account. Please try again.");
			return;
		}
		else if(src.equals(dst)){
			sender.sendMessage(Main.prefix+"Source and destination accounts must differ.");
			return;
		}
		else if(value <= 0){
			sender.sendMessage(Main.prefix+"You can only transfer positive values.");
			return;
		}
		ConfigHandler.createPlayerInfo(src, srcName);
		ConfigHandler.createPlayerInfo(dst, dstName);
		File srcFile = ConfigHandler.getPlayerInfo(src);
		File dstFile = ConfigHandler.getPlayerInfo(dst);
		FileConfiguration srcConfig = YamlConfiguration.loadConfiguration(srcFile);
		FileConfiguration dstConfig = YamlConfiguration.loadConfiguration(dstFile);
		int srcBalance = srcConfig.getInt("balance");
		int dstBalance = dstConfig.getInt("balance");
		if(srcBalance < value){
			sender.sendMessage(Main.prefix+"Transfer failed! "+srcName+" only has "+ChatColor.GREEN+srcBalance+ChatColor.WHITE+" XP in their account!");
			return;
		}
		dstConfig.set("balance", dstBalance + value);
		srcConfig.set("balance", srcBalance - value);
		try {
			ConfigHandler.updateBalance(srcConfig, srcFile);
			ConfigHandler.updateBalance(dstConfig, dstFile);
			sender.sendMessage(Main.prefix+"Successfully transfered "+ChatColor.GREEN+value+ChatColor.WHITE+" XP from "+srcName+" to "+dstName+".");
			if(dst.getPlayer() != null){
				dst.getPlayer().sendMessage(Main.prefix+"You received "+ChatColor.GREEN+value+ChatColor.WHITE+" XP from "
					+srcName+". Your current balance is "+ChatColor.GREEN+(dstBalance + value)+ChatColor.WHITE+" XP.");
			}
			if(src.getPlayer() != null){
				src.getPlayer().sendMessage(Main.prefix+"You sent "+ChatColor.GREEN+value+ChatColor.WHITE+" XP to "
					+dstName+". Your current balance is "+ChatColor.GREEN+(srcBalance - value)+ChatColor.WHITE+" XP.");
			}
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe("Error saving a file after transfer!");
			e.printStackTrace();
		}
		
	}
}
