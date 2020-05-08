package holiday.garet.GStructure.ItemTag;

import java.util.ArrayList;
import java.util.List;

import holiday.garet.GStructure.GEnchantment;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class EnchantmentTag extends ItemDataTag {
	private List<GEnchantment> enchantments;
	private List<GEnchantment> storedEnchantments;
	private int repairCost;
	
	public EnchantmentTag() {
		enchantments = new ArrayList<GEnchantment>();
		storedEnchantments = new ArrayList<GEnchantment>();
		type = 7;
	}
	
	public List<GEnchantment> getEnchantments() {
		return enchantments;
	}
	
	public List<GEnchantment> getStoredEnchantments() {
		return storedEnchantments;
	}
	
	public int getRepairCost() {
		return repairCost;
	}
	
	public void read(CompoundTag tag) {
		if (tag.containsKey("Enchantments")) {
			ListTag<CompoundTag> enchantments = tag.getListTag("Enchantments").asCompoundTagList();
			enchantments.forEach((enchantment) -> {
				this.enchantments.add(GEnchantment.readNewEnchantment(enchantment));
			});
		}
		if (tag.containsKey("StoredEnchantments")) {
			ListTag<CompoundTag> storedEnchantments = tag.getListTag("StoredEnchantments").asCompoundTagList();
			storedEnchantments.forEach((storedEnchantment) -> {
				this.storedEnchantments.add(GEnchantment.readNewEnchantment(storedEnchantment));
			});
		}
		this.repairCost = tag.getInt("RepairCost");
	}
}
