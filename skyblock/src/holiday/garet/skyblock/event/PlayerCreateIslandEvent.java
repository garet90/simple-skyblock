package holiday.garet.skyblock.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import holiday.garet.skyblock.island.SkyblockPlayer;

public class PlayerCreateIslandEvent extends Event {
	
	private SkyblockPlayer skyblockPlayer;
	private Player player;
	private boolean cancelled = false;
	private Location islandLocation;
	private boolean resetMoney;
	
	public PlayerCreateIslandEvent(SkyblockPlayer sp, Player p, Location loc, boolean rm) {
		skyblockPlayer = sp;
		player = p;
		islandLocation = loc;
		resetMoney = rm;
	}
	
	public boolean getCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean _cancelled) {
		cancelled = _cancelled;
	}
	
	public SkyblockPlayer getSkyblockPlayer() {
		return skyblockPlayer;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Location getIslandLocation() {
		return islandLocation;
	}
	
	public void setIslandLocation(Location loc) {
		islandLocation = loc;
	}
	
	public boolean getOverrideResetMoney() {
		return resetMoney;
	}
	
	public void setOverrideResetMoney(boolean rm) {
		resetMoney = rm;
	}

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}