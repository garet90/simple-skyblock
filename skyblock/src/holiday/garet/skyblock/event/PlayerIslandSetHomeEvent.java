package holiday.garet.skyblock.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import holiday.garet.skyblock.island.Island;
import holiday.garet.skyblock.island.SkyblockPlayer;

public class PlayerIslandSetHomeEvent extends Event {
	
	private Player player;
	private SkyblockPlayer skyblockPlayer;
	private Island island;
	private Location homeLocation;
	private boolean cancelled = false;
	private boolean setSkySpawn;
	
	public PlayerIslandSetHomeEvent(Player p, SkyblockPlayer sp, Island is, Location hl, boolean sss) {
		player = p;
		skyblockPlayer = sp;
		island = is;
		homeLocation = hl;
		setSkySpawn = sss;
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
	
	public Location getHomeLocation() {
		return homeLocation;
	}
	
	public void setHomeLocation(Location hl) {
		homeLocation = hl;
	}
	
	public boolean getCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean _cancelled) {
		cancelled = _cancelled;
	}
	
	public boolean getSetSkySpawn() {
		return setSkySpawn;
	}
	
	public void setSetSkySpawn(boolean sss) {
		setSkySpawn = sss;
	}

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}