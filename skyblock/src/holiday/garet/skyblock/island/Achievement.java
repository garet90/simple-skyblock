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

public class Achievement {
	private FileConfiguration data;
	private net.milkbowl.vault.economy.Economy vaultEconomy;
	
	private String name;
	private ItemStack item;
	
	private List<ItemStack> requiredItems = new ArrayList<ItemStack>();
	private double requiredMoney;
	private String requiredPermission;
	
	private List<ItemStack> rewardItems = new ArrayList<ItemStack>();
	private double rewardMoney;
	
	public Achievement(String name, FileConfiguration config, FileConfiguration data, net.milkbowl.vault.economy.Economy vaultEconomy) {
		this.name = name;
		this.data = data;
		this.vaultEconomy = vaultEconomy;
		
		ConfigurationSection s = config.getConfigurationSection("ACHIEVEMENTS." + name);
		ConfigurationSection requirements = s.getConfigurationSection("requirements");
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
		if (requirements.contains("hasItems")) {
			List<String> ri = requirements.getStringList("hasItems");
			ri.forEach((rit) -> {
				try {
					String[] sp = rit.split(":");
					requiredItems.add(new ItemStack(XMaterial.matchXMaterial(sp[0]).get().parseMaterial(), Integer.valueOf(sp[1])));
				} catch (Exception e) {
					// material not found!
				}
			});
		}
		
		requiredMoney = requirements.getDouble("hasMoney");
		
		requiredPermission = requirements.getString("hasPermission");
		
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
		for (int i = 0; i < requiredItems.size(); i++) {
			if (!pInv.containsAtLeast(requiredItems.get(i), requiredItems.get(i).getAmount())) {
				return false;
			}
		}
		
		if (vaultEconomy != null) {
			if (vaultEconomy.getBalance(p) < requiredMoney) {
				return false;
			}
		} else {
			Economy e = new Economy(p.getUniqueId(), this.data);
			
			if (e.get() < requiredMoney) {
				return false;
			}
		}
		
		if (requiredPermission != null && !p.hasPermission(requiredPermission)) {
			return false;
		}
		
		return true;
	}
	
	public void awardPlayer(Player p) {
		PlayerInventory pInv = p.getInventory();
		rewardItems.forEach((i) -> {
			pInv.addItem(i);
		});
		
		if (vaultEconomy != null) {
			vaultEconomy.depositPlayer(p, rewardMoney);
		} else {
			Economy e = new Economy(p.getUniqueId(), this.data);
			
			e.deposit(rewardMoney);
		}
	}
}
