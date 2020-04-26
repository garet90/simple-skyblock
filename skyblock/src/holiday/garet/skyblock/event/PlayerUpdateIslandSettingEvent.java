package holiday.garet.skyblock.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import holiday.garet.skyblock.island.Island;
import holiday.garet.skyblock.island.SkyblockPlayer;

public class PlayerUpdateIslandSettingEvent extends Event {
	
	private Player player;
	private SkyblockPlayer skyblockPlayer;
	private Island island;
	private String setting;
	private Boolean value;
	private boolean cancelled = false;
	
	public PlayerUpdateIslandSettingEvent(Player p, SkyblockPlayer sp, Island is, String s, Boolean v) {
		player = p;
		skyblockPlayer = sp;
		island = is;
		setting = s;
		value = v;
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
	
	public String getSetting() {
		return setting;
	}
	
	public Boolean getValue() {
		return value;
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