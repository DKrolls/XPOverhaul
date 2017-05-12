package io.github.dkrolls.XPOverhaul.Listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

import io.github.dkrolls.XPOverhaul.BottleEnchantment;

public class EnchantingTableListener implements Listener{
	
	@EventHandler
	public void EnchantInteract(PrepareItemEnchantEvent e){
		e.setCancelled(false);
		Player player = e.getEnchanter();
		if(player.hasPermission("XPO.bottle") && e.getItem().getType() == Material.GLASS_BOTTLE){
			e.setCancelled(false);
			e.getOffers()[0] = new EnchantmentOffer(new BottleEnchantment(-1), 1, 1);
			e.getOffers()[1] = null;
			e.getOffers()[2] = null;
		}
	}	
}