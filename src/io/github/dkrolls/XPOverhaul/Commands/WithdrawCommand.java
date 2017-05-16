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

public class WithdrawCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player && args.length == 1){
			withdraw((Player) sender, args[0]);
		}
		else if(args.length != 1){
			sender.sendMessage(Main.prefix+"Invalid usage! "+cmd.getUsage());
		}
		else{
			sender.sendMessage(Main.prefix+"Only players can withdraw XP!");
		}
		return true;
	}
	
	private void withdraw(Player player, String arg){
		ConfigHandler.createPlayerInfo(player, player.getName()); //guarantees player info exists
		File file = ConfigHandler.getPlayerInfo(player);
		FileConfiguration info = YamlConfiguration.loadConfiguration(file);
		ExperienceManager m = new ExperienceManager(player);
		int balance = info.getInt("balance");
		int xp = m.getCurrentExp();
		try{
			int value = Integer.parseInt(arg);
			if(value > balance){
				player.sendMessage(Main.prefix+"You can't withdraw more XP than you have!");
				return;
			}
			else if(value <= 0){
				player.sendMessage(Main.prefix+"You can only withdraw positive values.");
				return;
			}
			info.set("balance", balance - value);
			try {
				ConfigHandler.updateBalance(info, file);
			} catch (IOException e) {
				Bukkit.getLogger().severe("Error saving to file!");
				e.printStackTrace();
			}
			m.changeExp(value);
			player.sendMessage(Main.prefix+"Withdrew "+ChatColor.GREEN+value+ChatColor.WHITE+
					" XP! Your current balance is "+ChatColor.GREEN+(balance - value)+ChatColor.WHITE+" XP.");
		}catch(NumberFormatException e){
			if(arg.equals("all")){
				m.setExp(balance + xp);
				player.sendMessage(Main.prefix+"Withdrew "+ChatColor.GREEN+balance+ChatColor.WHITE+
					" XP! Your current balance is "+ChatColor.GREEN+"0"+ChatColor.WHITE+" XP.");
				info.set("balance", 0);
				try {
					ConfigHandler.updateBalance(info, file);
				} catch (IOException e0) {
					Bukkit.getLogger().severe("Error saving to file!");
					e0.printStackTrace();
				}
			} //include else if for 'L' for levels
			else if(arg.charAt(arg.length() - 1) == 'l' || arg.charAt(arg.length() - 1) == 'L'){
				int levels = Integer.parseInt(arg.substring(0, arg.length() - 1));
				player.setLevel(player.getLevel() + levels);
				int diff = m.getCurrentExp() - xp;
				if(diff > balance){
					player.sendMessage(Main.prefix+"You can't withdraw more XP than you have!");
					m.setExp(xp);
					return;
				}
				player.sendMessage(Main.prefix+"Withdrew "+ChatColor.GREEN+diff+ChatColor.WHITE+
					" XP! Your current balance is "+ChatColor.GREEN+(balance - diff)+ChatColor.WHITE+" XP!");
				info.set("balance", balance - diff);
				try {
					ConfigHandler.updateBalance(info, file);
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
