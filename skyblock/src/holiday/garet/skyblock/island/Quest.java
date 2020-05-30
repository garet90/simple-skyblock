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

public class Quest {
	private FileConfiguration data;
	private net.milkbowl.vault.economy.Economy vaultEconomy;
	
	private String name;
	private ItemStack item;
	
	private List<ItemStack> costItems = new ArrayList<ItemStack>();
	private double costMoney;
	private String costPermission;
	
	private List<ItemStack> rewardItems = new ArrayList<ItemStack>();
	private double rewardMoney;
	
	public Quest(String name, FileConfiguration config, FileConfiguration data, net.milkbowl.vault.economy.Economy vaultEconomy) {
		this.name = name;
		this.data = data;
		this.vaultEconomy = vaultEconomy;
		
		ConfigurationSection s = config.getConfigurationSection("QUESTS." + name);
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
		
		// set rewards
		if (rewards.contains("items")) {
			List<String> rei = rewards.getStringList("items");
			rei.forEach((rit) -> {
				try {
					String[] sp = rit.split(":");
					rewardItems.add(new ItemStack(XMaterial.matchXMaterial(sp[0]).get().parseMaterial(), Integer.valueOf(sp[1])));
				} catch (Exception e) {
					// material not found!
				}
			});
		}
		
		rewardMoney = rewards.getDouble("money");
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public boolean checkCompletion(Player p) {
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
		
		return true;
	}
	
	public void awardPlayer(Player p) {
		PlayerInventory pInv = p.getInventory();
		
		costItems.forEach((i) -> {
			pInv.removeItem(i);
		});
		
		rewardItems.forEach((i) -> {
			pInv.addItem(i);
		});
		
		if (vaultEconomy != null) {
			vaultEconomy.withdrawPlayer(p, costMoney);
			vaultEconomy.depositPlayer(p, rewardMoney);
		} else {
			Economy e = new Economy(p.getUniqueId(), this.data);
			
			e.withdraw(costMoney);
			e.deposit(rewardMoney);
		}
	}
}
