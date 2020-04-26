package holiday.garet.skyblock.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerShoutEvent extends Event {
	
	private CommandSender sender;
	private String message;
	private boolean cancelled = false;
	
	public PlayerShoutEvent(CommandSender s, String m) {
		sender = s;
		message = m;
	}
	
	public CommandSender getSender() {
		return sender;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String m) {
		message = m;
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