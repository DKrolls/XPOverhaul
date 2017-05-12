package io.github.dkrolls.XPOverhaul.Commands;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.dkrolls.XPOverhaul.ConfigHandler;
import io.github.dkrolls.XPOverhaul.Main;
import io.github.dkrolls.XPOverhaul.Misc.ExperienceManager;
import net.md_5.bungee.api.ChatColor;

public class DepositCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player && args.length == 1){
			deposit((Player) sender, args[0]);
		}
		else if(args.length != 1){
			sender.sendMessage(Main.prefix+"Invalid usage! "+cmd.getUsage());
		}
		else{
			sender.sendMessage(Main.prefix+"Only players can deposit XP!");
		}
		return true;
	}
	
	private void deposit(Player player, String arg){
			ConfigHandler.createPlayerInfo(player); //guarantees player info exists
			File file = ConfigHandler.getPlayerInfo(player);
			FileConfiguration info = YamlConfiguration.loadConfiguration(file);
			ExperienceManager m = new ExperienceManager(player);
			long balance = info.getLong("balance");
			int xp = m.getCurrentExp();
			try{
				int value = Integer.parseInt(arg);
				if(value > xp){
					player.sendMessage(Main.prefix+"You can't deposit more XP than you currently have!");
					return;
				}
				info.set("balance", value + balance);
				try {
					info.save(file);
				} catch (IOException e) {
					Bukkit.getLogger().severe("Error saving to file!");
					e.printStackTrace();
				}
				if(value == xp){
					m.setExp(0);
				}
				else{
					m.changeExp(-value);
				}
				player.sendMessage(Main.prefix+"Deposited "+ChatColor.GREEN+value+ChatColor.WHITE+
						" XP! Your current balance is "+ChatColor.GREEN+(balance + value)+ChatColor.WHITE+" XP.");
			}catch(NumberFormatException e){
				if(arg.equals("all")){
					m.setExp((int) 0);
					player.sendMessage(Main.prefix+"Deposited "+ChatColor.GREEN+xp+ChatColor.WHITE+
						" XP! Your current balance is "+ChatColor.GREEN+(balance + xp)+ChatColor.WHITE+" XP.");
					info.set("balance", xp + balance);
					try {
						info.save(file);
					} catch (IOException e0) {
						Bukkit.getLogger().severe("Error saving to file!");
						e0.printStackTrace();
					}
				} //include else if for 'L' for levels
				else if(arg.charAt(arg.length() - 1) == 'l' || arg.charAt(arg.length() - 1) == 'L'){
					int levels = Integer.parseInt(arg.substring(0, arg.length() - 1));
					if(player.getLevel() < levels){
						player.sendMessage(Main.prefix+"You can't deposit more XP than you currently have!");
						return;
					}
					player.setLevel(player.getLevel() - levels);
					int diff = xp - m.getCurrentExp();
					player.sendMessage(Main.prefix+"Deposited "+ChatColor.GREEN+diff+ChatColor.WHITE+
						" XP! Your current balance is "+ChatColor.GREEN+(balance + diff)+ChatColor.WHITE+" XP!");
					info.set("balance", balance + diff);
					try {
						info.save(file);
					} catch (IOException e0) {
						Bukkit.getLogger().severe("Error saving to file!");
						e0.printStackTrace();
					}
				}
				else{
					player.sendMessage(Main.prefix+"Please deposit a valid amount!");
				}
			}
	}
}
