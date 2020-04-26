package holiday.garet.skyblock.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTradeRequestExpireEvent extends Event {
	
	private Player from;
	private Player to;
	
	public PlayerTradeRequestExpireEvent(Player f, Player t) {
		from = f;
		to = t;
	}
	
	public Player getFrom() {
		return from;
	}
	
	public Player getTo() {
		return to;
	}

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}