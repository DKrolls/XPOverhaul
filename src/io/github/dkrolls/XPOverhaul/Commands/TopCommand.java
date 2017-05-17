package io.github.dkrolls.XPOverhaul.Commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.github.dkrolls.XPOverhaul.ConfigHandler;
import io.github.dkrolls.XPOverhaul.Main;
import io.github.dkrolls.XPOverhaul.XPAccount;
import io.github.dkrolls.XPOverhaul.Misc.NameFetcher;
import net.md_5.bungee.api.ChatColor;

public class TopCommand implements CommandExecutor{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 0){
			sender.sendMessage(Main.prefix+"Incorrect usage! "+cmd.getUsage());
			return true;
		}
		XPAccount[] topAccounts = ConfigHandler.getTopAccounts();
		int num = topAccounts.length;
		if(num > ConfigHandler.TOP_BALANCES_TO_SHOW){
			num = ConfigHandler.TOP_BALANCES_TO_SHOW;
		}
		sender.sendMessage(Main.prefix+"Top "+ChatColor.GREEN+num+" XP account balances:");
		for(int i = 0; i < num; i++){
			int balance = topAccounts[topAccounts.length - 1 - i].getBalance();
			UUID uuid = topAccounts[topAccounts.length - 1 - i].getUUID();
			String name;
			try {
				name = NameFetcher.getName(uuid);
			} catch (Exception e) {
				e.printStackTrace();
				name = uuid.toString();
			}
			sender.sendMessage(Main.prefix+ChatColor.GREEN+"#"+(i + 1)+": "+ChatColor.WHITE+name+
				": "+ChatColor.GREEN+balance+ChatColor.WHITE+" XP.");
		}
		return true;
	}
}
