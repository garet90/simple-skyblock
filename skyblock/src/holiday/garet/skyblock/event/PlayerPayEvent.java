package holiday.garet.skyblock.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPayEvent extends Event {
	
	private Player payer;
	private Player payee;
	private double amount;
	private boolean cancelled = false;
	
	public PlayerPayEvent(Player p1, Player p2, double a) {
		payer = p1;
		payee = p2;
		amount = a;
	}
	
	public Player getPayer() {
		return payer;
	}
	
	public Player getPayee() {
		return payee;
	}
	
	public double getAmount() {
		return amount;
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