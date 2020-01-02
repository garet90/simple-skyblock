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
	World world_nether;
	Plugin plugin;
	FileConfiguration data;
	SkyblockPlayer visiting;
	Boolean wasVisiting = false;
	
	public SkyblockPlayer(Player _player, Island _island, World _world, World _world_nether, FileConfiguration _data, Plugin _plugin) {
		
		player = _player.getUniqueId();
		island = _island;
		data = _data;
		plugin = _plugin;
		world = _world;
		world_nether = _world_nether;
		if (data.isSet("data.players." + player.toString() + ".home")) {
			World homeWorld = world;
			if (data.getBoolean("data.players." + player.toString() + ".home.nether")) {
				homeWorld = world_nether;
			}
			home = new Location(homeWorld,
					data.getDouble("data.players." + player.toString() + ".home.x"),
					data.getDouble("data.players." + player.toString() + ".home.y"),
					data.getDouble("data.players." + player.toString() + ".home.z"),
					data.getInt("data.players." + player.toString() + ".home.yaw"),
					data.getInt("data.players." + player.toString() + ".home.pitch"));
		}
		if (data.isSet("data.players." + player.toString() + ".visiting")) {
			if (home != null) {
				_player.teleport(home);
				_player.setBedSpawnLocation(home, false);
			} else {
				_player.teleport(_world.getSpawnLocation());
				_player.setBedSpawnLocation(_world.getSpawnLocation(), false);
			}
		}
	}
	
	public SkyblockPlayer(UUID _player, Island _island, World _world, World _world_nether, FileConfiguration _data, Plugin _plugin) {
		
		player = _player;
		island = _island;
		data = _data;
		plugin = _plugin;
		world = _world;
		world_nether = _world_nether;
		if (data.isSet("data.players." + player.toString() + ".home")) {
			World homeWorld = world;
			if (data.getBoolean("data.players." + player.toString() + ".home.nether")) {
				homeWorld = world_nether;
			}
			home = new Location(homeWorld,
					data.getDouble("data.players." + player.toString() + ".home.x"),
					data.getDouble("data.players." + player.toString() + ".home.y"),
					data.getDouble("data.players." + player.toString() + ".home.z"),
					data.getInt("data.players." + player.toString() + ".home.yaw"),
					data.getInt("data.players." + player.toString() + ".home.pitch"));
		}
		if (data.isSet("data.players." + player.toString() + ".visiting")) {
			wasVisiting = true;
		}
	}
	
	public void savePlayer() {
		if (island != null) {
			data.set("data.players." + player.toString() + ".island", island.getKey());
		}
		if (home != null) {
			if (home.getWorld() == world_nether) {
				data.set("data.players." + player.toString() + ".home.nether", true);
			} else {
				data.set("data.players." + player.toString() + ".home.nether", false);
			}
			data.set("data.players." + player.toString() + ".home.x", home.getX());
			data.set("data.players." + player.toString() + ".home.y", home.getY());
			data.set("data.players." + player.toString() + ".home.z", home.getZ());
			data.set("data.players." + player.toString() + ".home.pitch", (int)home.getPitch());
			data.set("data.players." + player.toString() + ".home.yaw", (int)home.getYaw());
		}
		if (visiting != null) {
			data.set("data.players." + player.toString() + ".visiting", visiting.getPlayerUUID().toString());
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
	
	public void goHome(Player p) {
		if (home != null) {
			p.teleport(home);
			p.setBedSpawnLocation(home, false);
		} else {
			p.teleport(world.getSpawnLocation());
			p.setBedSpawnLocation(world.getSpawnLocation(), false);
		}
		wasVisiting = false;
		visiting = null;
	}
	
	public Boolean wasVisiting() {
		return wasVisiting;
	}
	
}
