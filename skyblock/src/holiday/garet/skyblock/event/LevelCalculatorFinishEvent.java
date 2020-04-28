package holiday.garet.skyblock.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import holiday.garet.skyblock.world.tools.LevelCalculator;

public class LevelCalculatorFinishEvent extends Event {
	
	LevelCalculator calculator;
	Player player;
	
	public LevelCalculatorFinishEvent(LevelCalculator calc, Player p) {
		calculator = calc;
		player = p;
	}
	
	public LevelCalculator getCalculator() {
		return calculator;
	}
	
	public Player getPlayer() {
		return player;
	}
	
    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}