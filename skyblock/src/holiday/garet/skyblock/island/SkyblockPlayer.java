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

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SkyblockPlayer {
	
	UUID player;
	Island island;
	Location home;
	World world;
	Plugin plugin;
	FileConfiguration data;
	SkyblockPlayer visiting;
	
	public SkyblockPlayer(Player _player, Island _island, World _world, FileConfiguration _data, Plugin _plugin) {
		
		player = _player.getUniqueId();
		island = _island;
		data = _data;
		plugin = _plugin;
		world = _world;
		if (data.isSet("data.players." + player.toString() + ".home")) {
			home = new Location(world,
					data.getDouble("data.players." + player.toString() + ".home.x"),
					data.getDouble("data.players." + player.toString() + ".home.y"),
					data.getDouble("data.players." + player.toString() + ".home.z"),
					data.getInt("data.players." + player.toString() + ".home.pitch"),
					data.getInt("data.players." + player.toString() + ".home.yaw"));
		}
		
	}
	
	public SkyblockPlayer(UUID _player, Island _island, World _world, FileConfiguration _data, Plugin _plugin) {
		
		player = _player;
		island = _island;
		data = _data;
		plugin = _plugin;
		world = _world;
		if (data.isSet("data.players." + player.toString() + ".home")) {
			home = new Location(world,
					data.getDouble("data.players." + player.toString() + ".home.x"),
					data.getDouble("data.players." + player.toString() + ".home.y"),
					data.getDouble("data.players." + player.toString() + ".home.z"),
					data.getInt("data.players." + player.toString() + ".home.pitch"),
					data.getInt("data.players." + player.toString() + ".home.yaw"));
		}
		
	}
	
	public void savePlayer() {
		if (island != null) {
			data.set("data.players." + player.toString() + ".island", island.getKey());
		}
		if (home != null) {
			data.set("data.players." + player.toString() + ".home.x", home.getX());
			data.set("data.players." + player.toString() + ".home.y", home.getY());
			data.set("data.players." + player.toString() + ".home.z", home.getZ());
			data.set("data.players." + player.toString() + ".home.pitch", (int)home.getPitch());
			data.set("data.players." + player.toString() + ".home.yaw", (int)home.getYaw());
		}
	}
	
	public Player getPlayer() {
		return plugin.getServer().getPlayer(player);
	}
	
	public UUID getPlayerUUID() {
		return player;
	}
	
	public void setIsland(Island _island) {
		island = _island;
	}
	
	public Location getHome() {
		return home;
	}
	
	public void setHome(Location _home) {
		home = _home;
	}
	
	public Island getIsland() {
		return island;
	}
	
	public SkyblockPlayer getVisiting() {
		return visiting;
	}
	
	public void setVisiting(SkyblockPlayer _visiting) {
		visiting = _visiting;
	}
	
}
