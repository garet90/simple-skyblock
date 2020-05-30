package holiday.garet.skyblock.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import holiday.garet.skyblock.island.Island;
import holiday.garet.skyblock.world.tools.LevelCalculator;

public class LevelCalculatorFinishEvent extends Event {
	
	LevelCalculator calculator;
	Island island;
	Player player;
	
	public LevelCalculatorFinishEvent(LevelCalculator calc, Player p, Island is) {
		calculator = calc;
		player = p;
		island = is;
	}
	
	public LevelCalculator getCalculator() {
		return calculator;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Island getIsland() {
		return island;
	}
	
    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}