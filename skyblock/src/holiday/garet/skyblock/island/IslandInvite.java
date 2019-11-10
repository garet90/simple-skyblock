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

package holiday.garet.skyblock.island;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class IslandInvite {
	
	Player player;
	Island island;
	Plugin plugin;
	String senderName;
	boolean isActive = true;

	public IslandInvite(Player _player, Island _island, Plugin _plugin) {
		player = _player;
		island = _island;
		plugin = _plugin;
		senderName = plugin.getServer().getPlayer(island.getLeader()).getName();
		player.sendMessage(ChatColor.GREEN + "You have been invited to join " + senderName + "\'s island. Type \'/is join " + senderName + "\' to join their island. This invite expires in 60 seconds! \n" + ChatColor.RED + "Warning: This will remove your old island!");
        new BukkitRunnable() {
        
            @Override
            public void run() {
            	if (isActive()) {
	            	setActive(false);
	            	if (player != null) {
	            		player.sendMessage(ChatColor.RED + "The invite from " + senderName + " has expired.");
	            	}
            	}
            }
            
        }.runTaskLater(plugin, 1200L);
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean _active) {
		isActive = _active;
	}
	
	public Player getTo() {
		return player;
	}
	
	public Island getIsland() {
		return island;
	}
	
}
