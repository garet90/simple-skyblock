package holiday.garet.skyblock.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUISelectItemEvent extends Event {

	Player player;
	Inventory inventory;
	ItemStack item;
	String data;
	String title;
	int type;
	
	public GUISelectItemEvent(Player p, Inventory in, String t, ItemStack it, String d, int ty) {
		inventory = in;
		item = it;
		player = p;
		title = t;
		type = ty;
		data = d;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getType() {
		return type;
	}
	
	public String getData() {
		return data;
	}
	
    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}