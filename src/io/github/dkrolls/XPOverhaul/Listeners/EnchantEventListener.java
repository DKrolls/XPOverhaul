package io.github.dkrolls.XPOverhaul.Listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

public class EnchantEventListener implements Listener{
	@EventHandler
	public void BottleEnchantedEvent(EnchantItemEvent e){
		if(e.getItem().getType() == Material.GLASS_BOTTLE && e.getEnchanter().getLevel() > 0){
			EnchantingInventory i = (EnchantingInventory) e.getInventory();
			int size = i.getSecondary().getAmount();
			e.getEnchanter().setLevel(e.getEnchanter().getLevel() - 1);
			if(size > 1){
				i.setSecondary(new ItemStack(Material.INK_SACK, size - 1, (short) 4));
			}
			else{
				i.setSecondary(null);
			}
			i.setItem(new ItemStack(Material.EXP_BOTTLE, 1));
		}
	}
}
