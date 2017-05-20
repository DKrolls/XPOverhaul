package io.github.dkrolls.XPOverhaul.Listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

import io.github.dkrolls.XPOverhaul.ConfigHandler;

public class EnchantEventListener implements Listener{
	@EventHandler
	public void BottleEnchantedEvent(EnchantItemEvent e){
		int cost = ConfigHandler.LEVELS_FOR_XP_BOTTLES;
		EnchantingInventory i = (EnchantingInventory) e.getInventory();
		if(e.getItem().getType() == Material.GLASS_BOTTLE && e.getEnchanter().getLevel() >= cost){
			int size = i.getSecondary().getAmount();
			e.getEnchanter().setLevel(e.getEnchanter().getLevel() - cost);
			if(size > 1){
				i.setSecondary(new ItemStack(Material.INK_SACK, size - 1, (short) 4));
			}
			else{
				i.setSecondary(null);
			}
			i.setItem(new ItemStack(Material.EXP_BOTTLE, 1));
		}
		if(e.getInventory() instanceof EnchantingInventory && !ConfigHandler.REQUIRE_LAPIS){
			i.setSecondary(new ItemStack(Material.INK_SACK, 64, (short) 4));
		}
	}
}
