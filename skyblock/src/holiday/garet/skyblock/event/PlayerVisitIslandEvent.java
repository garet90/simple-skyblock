package holiday.garet.skyblock.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import holiday.garet.skyblock.island.SkyblockPlayer;

public class PlayerVisitIslandEvent extends Event {
	
	Player player;
	SkyblockPlayer skyblockPlayer;
	SkyblockPlayer visitingPlayer;
	boolean cancelled = false;
	
	public PlayerVisitIslandEvent(Player p, SkyblockPlayer sp, SkyblockPlayer vp) {
		player = p;
		skyblockPlayer = sp;
		visitingPlayer = vp;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public SkyblockPlayer getSkyblockPlayer() {
		return skyblockPlayer;
	}
	
	public SkyblockPlayer getVisitingPlayer() {
		return visitingPlayer;
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