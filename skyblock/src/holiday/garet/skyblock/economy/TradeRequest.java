package holiday.garet.skyblock.economy;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class TradeRequest {
	
	Player from;
	Player to;
	Boolean active = true;

	public TradeRequest(Player _from, Player _to, Plugin plugin) {
		from = _from;
		to = _to;
        new BukkitRunnable() {
        
            @Override
            public void run() {
            	if (isActive()) {
	            	close();
	            	if (to != null) {
	            		to.sendMessage(ChatColor.RED + "The trade request from " + from.getName() + " has expired.");
	            	}
	            	if (from != null) {
	            		from.sendMessage(ChatColor.RED + "The trade request to " + to.getName() + " has expired.");
	            	}
            	}
            }
            
        }.runTaskLater(plugin, 1200L);
        to.sendMessage(ChatColor.GREEN + "You have received a trade request from " + from.getName() + "! Type \"/trade accept " + from.getName() + "\" to trade with them. Warning: This request expires in 60 seconds!");
        from.sendMessage(ChatColor.GREEN + "You have successfully sent a trade request to " + to.getName() + ".");
	}
	
	public Boolean isActive() {
		return active;
	}
	
	public Player from() {
		return from;
	}
	
	public Player to() {
		return to;
	}
	
	public void close() {
		active = false;
	}
	
}
