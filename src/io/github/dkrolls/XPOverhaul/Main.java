package io.github.dkrolls.XPOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.dkrolls.XPOverhaul.Commands.BalanceCommand;
import io.github.dkrolls.XPOverhaul.Commands.DepositCommand;
import io.github.dkrolls.XPOverhaul.Commands.ModifyCommand;
import io.github.dkrolls.XPOverhaul.Commands.ReloadCommand;
import io.github.dkrolls.XPOverhaul.Commands.SendCommand;
import io.github.dkrolls.XPOverhaul.Commands.WithdrawCommand;
import io.github.dkrolls.XPOverhaul.Listeners.EnchantEventListener;
import io.github.dkrolls.XPOverhaul.Listeners.EnchantingTableListener;

public class Main extends JavaPlugin{
	
	public static Plugin instance;
	public static final String prefix = ChatColor.GREEN+"["+ChatColor.WHITE+"XPO"+ChatColor.GREEN+"] "+ChatColor.WHITE;
	
	public void onEnable(){
		
		instance = this;
		ConfigHandler.initializeConfigs();
		this.registerCommands();
		this.registerEvents();
	}
	
	private void registerCommands(){
		this.getCommand("xpdeposit").setExecutor(new DepositCommand());
		this.getCommand("xpwithdraw").setExecutor(new WithdrawCommand());
		this.getCommand("xpbalance").setExecutor(new BalanceCommand());
		this.getCommand("xpsend").setExecutor(new SendCommand());
		this.getCommand("xpmodify").setExecutor(new ModifyCommand());
		this.getCommand("xpreload").setExecutor(new ReloadCommand());
	}
	
	private void registerEvents(){
		getServer().getPluginManager().registerEvents(new EnchantingTableListener(), this);
		getServer().getPluginManager().registerEvents(new EnchantEventListener(), this);
	}
	
	public void onDisable(){
		instance = null;
	}
}
