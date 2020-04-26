package holiday.garet.skyblock.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import holiday.garet.skyblock.island.Island;
import holiday.garet.skyblock.island.SkyblockPlayer;

public class PlayerKickIslandEvent extends Event {
	
	private Player player;
	private SkyblockPlayer skyblockPlayer;
	private Island island;
	private SkyblockPlayer kickedPlayer;
	private boolean cancelled = false;
	
	public PlayerKickIslandEvent(Player p, SkyblockPlayer sp, Island is, SkyblockPlayer kp) {
		player = p;
		skyblockPlayer = sp;
		island = is;
		kickedPlayer = kp;
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
	
	public SkyblockPlayer getKickedPlayer() {
		return kickedPlayer;
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