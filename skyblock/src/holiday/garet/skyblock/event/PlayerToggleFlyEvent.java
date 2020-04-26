package holiday.garet.skyblock.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerToggleFlyEvent extends Event {
	
	Player player;
	boolean value;
	boolean cancelled = false;
	
	public PlayerToggleFlyEvent(Player p, boolean v) {
		player = p;
		value = v;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean getValue() {
		return value;
	}
	
	public void setValue(boolean v) {
		value = v;
	}
	
	public boolean getCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean _cancelled) {
		cancelled = _cancelled;
	}

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}