package holiday.garet.skyblock.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import holiday.garet.skyblock.island.Island;
import holiday.garet.skyblock.island.SkyblockPlayer;

public class PlayerResetIslandEvent extends Event {
	
	private Player player;
	private SkyblockPlayer skyblockPlayer;
	private Island island;
	private Double resetCost;
	private boolean cancelled = false;
	private Location location;
	
	public PlayerResetIslandEvent(Player p, SkyblockPlayer sp, Island is, Double rc, Location loc) {
		player = p;
		skyblockPlayer = sp;
		island = is;
		resetCost = rc;
		location = loc;
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
	
	public Double getResetCost() {
		return resetCost;
	}
	
	public void setResetCost(double rc) {
		resetCost = rc;
	}
	
	public boolean getCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean _cancelled) {
		cancelled = _cancelled;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location loc) {
		location = loc;
	}

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}