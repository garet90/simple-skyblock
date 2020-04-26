package holiday.garet.skyblock.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import holiday.garet.skyblock.economy.Trade;

public class PlayerTradeAcceptEvent extends Event {
	
	private Trade trade;
	private boolean cancelled = false;
	
	public PlayerTradeAcceptEvent(Trade t) {
		trade = t;
	}
	
	public Trade getTrade() {
		return trade;
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