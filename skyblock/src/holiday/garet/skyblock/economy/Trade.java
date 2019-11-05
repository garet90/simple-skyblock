package holiday.garet.skyblock.economy;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import holiday.garet.skyblock.XMaterial;

public class Trade {
	
	Player player1;
	Player player2;
	Inventory menu;
	
	public Trade(Player _player1, Player _player2) {
		player1 = _player1;
		player2 = _player2;
		createMenu();
	}
	
	public void open() {
		player1.openInventory(menu);
		player2.openInventory(menu);
	}
	
	public void createMenu() {
		menu = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "Trade");
		// Close menu button
		ItemStack closeMenu = XMaterial.BARRIER.parseItem();
		ItemMeta closeMeta = closeMenu.getItemMeta();
		closeMeta.setDisplayName(ChatColor.RED + "Close Menu");
		closeMenu.setItemMeta(closeMeta);
		menu.setItem(49, closeMenu);
		// Grey glass borders
		ItemStack glassBorder = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
		ItemMeta glassBorderMeta = glassBorder.getItemMeta();
		glassBorderMeta.setDisplayName(" ");
		glassBorder.setItemMeta(glassBorderMeta);
		for (int i = 0; i < 54; i++) {
			if (i != 49 && ((i >= 0 && i <= 8) || (i % 9 == 0 || i % 9 == 8 || i % 9 == 4) || (i >= 45))) {
				menu.setItem(i, glassBorder);
			}
		}
		// Accept buttons
		ItemStack acceptButton = XMaterial.LIME_DYE.parseItem();
		ItemMeta acceptButtonMeta = acceptButton.getItemMeta();
		acceptButtonMeta.setDisplayName(ChatColor.GREEN + "Accept Trade");
		acceptButton.setItemMeta(acceptButtonMeta);
		menu.setItem(39, acceptButton);
		menu.setItem(41, acceptButton);
		// Gold buttons
		ItemStack moneyButton = XMaterial.GOLD_INGOT.parseItem();
		ItemMeta moneyButtonMeta = moneyButton.getItemMeta();
		moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $0.00");
		List<String> lore = Arrays.asList(ChatColor.GRAY + "Shift left click: +$100", ChatColor.GRAY + "Shift right click: -$100", ChatColor.GRAY + "Left click: +$1", ChatColor.GRAY + "Right click: -$1");
		moneyButtonMeta.setLore(lore);
		moneyButton.setItemMeta(moneyButtonMeta);
		menu.setItem(37, moneyButton);
		menu.setItem(43, moneyButton);
	}
	
	public boolean hasPlayer(UUID _player) {
		if (player1.getUniqueId() == _player || player2.getUniqueId() == _player) {
			return true;
		}
		return false;
	}
	
	public boolean hasPlayer(Player _player) {
		if (player1 == _player || player2 == _player) {
			return true;
		}
		return false;
	}
	
	public void close() {
		player1.closeInventory();
		player2.closeInventory();
	}
	
	public Player getPlayer1() {
		return player1;
	}
	
	public Player getPlayer2() {
		return player2;
	}

}
