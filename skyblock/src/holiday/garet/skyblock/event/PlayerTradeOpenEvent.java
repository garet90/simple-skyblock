package holiday.garet.skyblock.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTradeOpenEvent extends Event {
	
	private Player from;
	private Player to;
	private boolean cancelled = false;
	
	public PlayerTradeOpenEvent(Player f, Player t) {
		from = f;
		to = t;
	}
	
	public Player getFrom() {
		return from;
	}
	
	public Player getTo() {
		return to;
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