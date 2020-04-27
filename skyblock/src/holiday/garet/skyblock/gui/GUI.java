package holiday.garet.skyblock.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import holiday.garet.skyblock.event.GUISelectItemEvent;

public class GUI implements Listener {
	
	/*
	 * Types:
	 * 0: Island selection screen
	 * 1: Island selection screen (reset)
	 */
	
	Inventory gui;
	Player player;
	String title;
	boolean active = true;
	int type;
	List<String> data;
	
	public GUI(String t, Player p, Inventory _gui, int ty) {
		gui = _gui;
		player = p;
		title = ((@Nullable InventoryView) gui).getTitle();
		player.openInventory(gui);
		type = ty;
		data = new ArrayList<String>(_gui.getSize());
		for (int i = 0; i < _gui.getSize(); i++) {
			data.add(null);
		}
	}
	
	public GUI(String t, Player p, int size, int ty) {
		gui = Bukkit.createInventory(null, size, t);
		player = p;
		title = t;
		player.openInventory(gui);
		type = ty;
		data = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			data.add(null);
		}
	}
	
	public void setSlot(int slot, ItemStack item, String d) {
		gui.setItem(slot, item);
		data.set(slot, d);
	}
	
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if ((Player)e.getWhoClicked() == player && active) {
			if (e.getSlot() >= 0 && e.getSlot() < data.size()) {
				GUISelectItemEvent ev = new GUISelectItemEvent(player, gui, title, e.getCursor(), data.get(e.getSlot()), type);
				Bukkit.getPluginManager().callEvent(ev);
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if ((Player)e.getPlayer() == player && active) {
			active = false;
		}
	}
	
}
