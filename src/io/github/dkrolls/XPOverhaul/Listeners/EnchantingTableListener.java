package io.github.dkrolls.XPOverhaul.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

import io.github.dkrolls.XPOverhaul.BottleEnchantment;
import io.github.dkrolls.XPOverhaul.ConfigHandler;

public class EnchantingTableListener implements Listener{
	
	private static List<EnchantingInventory> inUse = new ArrayList<EnchantingInventory>();
	
	@EventHandler
	public void EnchantInteract(PrepareItemEnchantEvent e){
		int cost = ConfigHandler.LEVELS_FOR_XP_BOTTLES;
		if(ConfigHandler.ALLOW_BOTTLE_ENCHANTING){
			e.setCancelled(false);
			Player player = e.getEnchanter();
			if(player.hasPermission("XPO.bottle") && e.getItem().getType() == Material.GLASS_BOTTLE){
				e.setCancelled(false);
				e.getOffers()[0] = new EnchantmentOffer(new BottleEnchantment(-1), 1, cost);
				e.getOffers()[1] = null;
				e.getOffers()[2] = null;
			}
		}
	}
	
	@EventHandler
	public void enchantOpenEvent(InventoryOpenEvent e){
		if(e.getInventory() instanceof EnchantingInventory && !ConfigHandler.REQUIRE_LAPIS){
			EnchantingInventory i = (EnchantingInventory) e.getInventory();
			i.setSecondary(new ItemStack(Material.INK_SACK, 64, (short) 4));
			inUse.add(i);
		}
	}
	
	@EventHandler
	public void lapisClicked(InventoryClickEvent e){
		if(e.getInventory() instanceof EnchantingInventory){
			if(!ConfigHandler.REQUIRE_LAPIS && e.getClickedInventory() instanceof EnchantingInventory && e.getSlot() == 1){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void enchantingTableClosed(InventoryCloseEvent e){
		if(e.getInventory() instanceof EnchantingInventory && !ConfigHandler.REQUIRE_LAPIS){
			e.getInventory().setItem(1, null);
			EnchantingInventory i = (EnchantingInventory) e.getInventory();
			inUse.remove(i);
		}
	}
	/**
	 * This purges all open EnchantingInventories of lapis so none drops on the ground if the server restarts
	 */
	public static void purgeUseList(){
		for(EnchantingInventory i : inUse){
			i.setSecondary(null);
		}
	}
}