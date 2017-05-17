package io.github.dkrolls.XPOverhaul.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.github.dkrolls.XPOverhaul.ConfigHandler;
import io.github.dkrolls.XPOverhaul.Main;

public class ReloadCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 0){
			sender.sendMessage(Main.prefix+"Invalid usage! "+cmd.getUsage());
			return true;
		}
		sender.sendMessage(Main.prefix+"Reloading configs. This could take a while.");
		ConfigHandler.initializeConfigs();
		sender.sendMessage(Main.prefix+"Config files reloaded.");
		return true;
	}
}
