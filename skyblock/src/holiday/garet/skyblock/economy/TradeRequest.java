/*
 * The MIT License (MIT)
 *
 * Copyright (c) Garet Halliday
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package holiday.garet.skyblock.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import holiday.garet.skyblock.event.PlayerTradeRequestExpireEvent;
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
	            	PlayerTradeRequestExpireEvent e = new PlayerTradeRequestExpireEvent(from, to);
	            	Bukkit.getPluginManager().callEvent(e);
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
