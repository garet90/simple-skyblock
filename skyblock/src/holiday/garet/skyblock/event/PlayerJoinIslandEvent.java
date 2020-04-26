package holiday.garet.skyblock.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import holiday.garet.skyblock.island.Island;
import holiday.garet.skyblock.island.SkyblockPlayer;

public class PlayerJoinIslandEvent extends Event {
	
	private Player player;
	private SkyblockPlayer skyblockPlayer;
	private Island oldIsland;
	private Island newIsland;
	private boolean cancelled = false;
	
	public PlayerJoinIslandEvent(Player p, SkyblockPlayer sp, Island oi, Island ni) {
		player = p;
		skyblockPlayer = sp;
		oldIsland = oi;
		newIsland = ni;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public SkyblockPlayer getSkyblockPlayer() {
		return skyblockPlayer;
	}
	
	public Island getOldIsland() {
		return oldIsland;
	}
	
	public Island getNewIsland() {
		return newIsland;
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