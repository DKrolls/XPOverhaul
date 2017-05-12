package io.github.dkrolls.XPOverhaul;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class BottleEnchantment extends Enchantment{

	public BottleEnchantment(int id) {
		super(id);
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		if(item.getType() == Material.GLASS_BOTTLE){
			return true;
		}
		return false;
	}

	@Override
	public boolean conflictsWith(Enchantment e) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ALL;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public String getName() {
		return "Enchanted Bottle";
	}

	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}
	
}

