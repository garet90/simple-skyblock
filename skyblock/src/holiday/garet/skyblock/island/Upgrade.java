package holiday.garet.skyblock.island;

import java.util.ArrayList;
import java.util.List;

import holiday.garet.skyblock.XMaterial;
import holiday.garet.skyblock.economy.Economy;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class Upgrade {
	private FileConfiguration data;
	private net.milkbowl.vault.economy.Economy vaultEconomy;
	
	private String name;
	private ItemStack item;
	
	private List<ItemStack> costItems = new ArrayList<ItemStack>();
	private double costMoney;
	private String costPermission;
	private int level;
	private List<String> prereqs = new ArrayList<String>();
	
	private List<String> rewardGeneratorOres = new ArrayList<String>();
	
	public Upgrade(String name, FileConfiguration config, FileConfiguration data, net.milkbowl.vault.economy.Economy vaultEconomy) {
		this.name = name;
		this.data = data;
		this.vaultEconomy = vaultEconomy;
		
		ConfigurationSection s = config.getConfigurationSection("UPGRADES." + name);
		ConfigurationSection costs = s.getConfigurationSection("costs");
		ConfigurationSection rewards = s.getConfigurationSection("rewards");
		
		// set item
		this.item = XMaterial.matchXMaterial(s.getString("item")).get().parseItem();
		ItemMeta im = this.item.getItemMeta();
		im.setDisplayName(s.getString("name").replaceAll("&", "§"));
		List<String> lore = s.getStringList("lore");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, lore.get(i).replaceAll("&", "§"));
		}
		im.setLore(lore);
		this.item.setItemMeta(im);
		
		if (costs != null) {
			// set requirements
			if (costs.contains("items")) {
				List<String> ri = costs.getStringList("items");
				ri.forEach((rit) -> {
					try {
						String[] sp = rit.split(":");
						costItems.add(new ItemStack(XMaterial.matchXMaterial(sp[0]).get().parseMaterial(), Integer.valueOf(sp[1])));
					} catch (Exception e) {
						// material not found!
					}
				});
			}
			
			costMoney = costs.getDouble("money");
			
			costPermission = costs.getString("permission");
			
			level = costs.getInt("level");
			
			prereqs = costs.getStringList("pre");
		}
		
		if (rewards != null) {
			// set rewards
			rewardGeneratorOres = rewards.getStringList("generator_ores");
		}
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public boolean checkCompletion(Player p, Island is) {
		PlayerInventory pInv = p.getInventory();
		for (int i = 0; i < costItems.size(); i++) {
			if (!pInv.containsAtLeast(costItems.get(i), costItems.get(i).getAmount())) {
				return false;
			}
		}
		
		if (vaultEconomy != null) {
			if (vaultEconomy.getBalance(p) < costMoney) {
				return false;
			}
		} else {
			Economy e = new Economy(p.getUniqueId(), this.data);
			
			if (e.get() < costMoney) {
				return false;
			}
		}
		
		if (costPermission != null && !p.hasPermission(costPermission)) {
			return false;
		}
		
		if (level != 0 && is.getLevel() < level) {
			return false;
		}
		
		for (int i = 0; i < prereqs.size(); i++) {
			if (!is.hasUpgrade(prereqs.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	public void awardPlayer(Player p, Island is) {
		PlayerInventory pInv = p.getInventory();
		
		costItems.forEach((i) -> {
			pInv.removeItem(i);
		});
		
		if (vaultEconomy != null) {
			vaultEconomy.withdrawPlayer(p, costMoney);
		} else {
			Economy e = new Economy(p.getUniqueId(), this.data);
			
			e.withdraw(costMoney);
		}
		
		is.setGeneratorOres(rewardGeneratorOres);
	}
}
