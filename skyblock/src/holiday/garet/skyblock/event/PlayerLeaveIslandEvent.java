package holiday.garet.skyblock.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import holiday.garet.skyblock.island.Island;
import holiday.garet.skyblock.island.SkyblockPlayer;

public class PlayerLeaveIslandEvent extends Event {
	
	private Player player;
	private SkyblockPlayer skyblockPlayer;
	private Island island;
	private boolean cancelled = false;
	
	public PlayerLeaveIslandEvent(Player p, SkyblockPlayer sp, Island is) {
		player = p;
		skyblockPlayer = sp;
		island = is;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public SkyblockPlayer getSkyblockPlayer() {
		return skyblockPlayer;
	}
	
	public Island getIsland() {
		return island;
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