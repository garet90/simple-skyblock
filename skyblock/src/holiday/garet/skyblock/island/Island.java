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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Island {
	
	int islandKey;
	World world;
	FileConfiguration data;
	Location p1;
	Location p2;
	String leader;
	List<String> members;
	List<String> trusts;
	Boolean allowVisitors = true;
	Boolean canReset = true;
	int resetsLeft;
	Boolean visitorsCanRideMobs = false;
	Boolean visitorsCanPortal = true;
	Boolean nether;
	String schematic;
	int passiveMobs = 0;
	Plugin plugin;

	public Island(int _islandKey, World _world, FileConfiguration _data, Plugin _plugin) {
		islandKey = _islandKey;
		data = _data;
		world = _world;
		plugin = _plugin;
		if (data.isSet("data.islands." + islandKey)) {
			p1 = new Location(world,
					data.getInt("data.islands." + islandKey + ".p1.x"),
					data.getInt("data.islands." + islandKey + ".p1.y"),
					data.getInt("data.islands." + islandKey + ".p1.z"));
			p2 = new Location(world,
					data.getInt("data.islands." + islandKey + ".p2.x"),
					data.getInt("data.islands." + islandKey + ".p2.y"),
					data.getInt("data.islands." + islandKey + ".p2.z"));
			leader = data.getString("data.islands." + islandKey + ".leader");
			members = data.getStringList("data.islands." + islandKey + ".members");
			allowVisitors = data.getBoolean("data.islands." + islandKey + ".allowVisitors");
			visitorsCanRideMobs = data.getBoolean("data.islands." + islandKey + ".visitorsCanRideMobs");
			visitorsCanPortal = data.getBoolean("data.islands." + islandKey + ".visitorsCanPortal");
			canReset = data.getBoolean("data.islands." + islandKey + ".canReset");
			resetsLeft = data.getInt("data.islands." + islandKey + ".resetsLeft");
			trusts = data.getStringList("data.islands." + islandKey + ".trusts");
			passiveMobs = data.getInt("data.islands." + islandKey + ".passiveMobs");
			if (trusts == null) {
				trusts = new ArrayList<String>();
			}
			if (data.isSet("data.islands." + String.valueOf(islandKey) + ".nether")) {
				nether = data.getBoolean("data.islands." + String.valueOf(islandKey) + ".nether");
			} else {
				nether = false;
			}
		}
	}
	
	public Island(Location _p1, Location _p2, double _pts, int _islandKey, Player _leader, World _world, int _resetsLeft, String s, FileConfiguration _data, Plugin _plugin) {
		p1 = _p1;
		p2 = _p2;
		islandKey = _islandKey;
		leader = _leader.getUniqueId().toString();
		world = _world;
		plugin = _plugin;
		data = _data;
		resetsLeft = _resetsLeft;
		members = new ArrayList<String>();
		nether = false;
		trusts = new ArrayList<String>();
		schematic = s;
	}
	
	public Island(World _world, Location _p1, Location _p2, Player _leader, FileConfiguration _data) {
		p1 = _p1;
		p2 = _p2;
		data = _data;
		leader = _leader.getUniqueId().toString();
	}
	
	public Location getP1() {
		return p1;
	}
	
	public Location getP2() {
		return p2;
	}
	
	public Boolean inBounds(Location _location) {
		if (_location.getX() >= p1.getX() && p2.getX() >= _location.getX() && _location.getZ() >= p1.getZ() && p2.getZ() >= _location.getZ()) {
			return true;
		}
		return false;
	}
	
	public void saveIsland() {
		if (p1 != null) {
			data.set("data.islands." + String.valueOf(islandKey) + ".p1.x", p1.getBlockX());
			data.set("data.islands." + String.valueOf(islandKey) + ".p1.y", p1.getBlockY());
			data.set("data.islands." + String.valueOf(islandKey) + ".p1.z", p1.getBlockZ());
		}
		if (p2 != null) {
			data.set("data.islands." + String.valueOf(islandKey) + ".p2.x", p2.getBlockX());
			data.set("data.islands." + String.valueOf(islandKey) + ".p2.y", p2.getBlockY());
			data.set("data.islands." + String.valueOf(islandKey) + ".p2.z", p2.getBlockZ());
		}
		data.set("data.islands." + String.valueOf(islandKey) + ".leader", leader);
		data.set("data.islands." + String.valueOf(islandKey) + ".members", members);
		data.set("data.islands." + String.valueOf(islandKey) + ".allowVisitors", allowVisitors);
		data.set("data.islands." + String.valueOf(islandKey) + ".visitorsCanRideMobs", visitorsCanRideMobs);
		data.set("data.islands." + String.valueOf(islandKey) + ".visitorsCanPortal", visitorsCanPortal);
		data.set("data.islands." + String.valueOf(islandKey) + ".canReset", canReset);
		data.set("data.islands." + String.valueOf(islandKey) + ".nether", nether);
		data.set("data.islands." + String.valueOf(islandKey) + ".resetsLeft", resetsLeft);
		data.set("data.islands." + String.valueOf(islandKey) + ".trusts", trusts);
		data.set("data.islands." + String.valueOf(islandKey) + ".passiveMobs", passiveMobs);
	}
	
	public boolean hasPlayer(Player p) {
		if (p != null) {
			String playerUUID = p.getUniqueId().toString();
			if (leader.equalsIgnoreCase(playerUUID) || members.contains(playerUUID)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasPlayer(UUID pUUID) {
		String playerUUID = pUUID.toString();
		if (leader.equalsIgnoreCase(playerUUID) || members.contains(playerUUID)) {
			return true;
		}
		return false;
	}
	
	public UUID getLeader() {
		return UUID.fromString(leader);
	}
	
	public boolean canReset() {
		return canReset;
	}
	
	public List<UUID> getMembers() {
		List<UUID> newList = new ArrayList<UUID>();
		for (int i = 0; i < members.size(); i++) {
			if (members.get(i) != null) {
				newList.add(UUID.fromString(members.get(i)));
			}
		}
		return newList;
	}
	
	public boolean allowsVisitors() {
		return allowVisitors;
	}
	
	public void setAllowsVisitors(boolean _value) {
		allowVisitors = _value;
	}
	
	public void leaveIsland(Player p) {
		for (int i = 0; i < members.size(); i++) {
			if (members.get(i) != null && members.get(i).equalsIgnoreCase(p.getUniqueId().toString())) {
				members.set(i, null);
			}
		}
	}
	
	public void leaveIsland(UUID uuid) {
		for (int i = 0; i < members.size(); i++) {
			if (members.get(i) != null && members.get(i).equalsIgnoreCase(uuid.toString())) {
				members.set(i, null);
			}
		}
		if (leader.equalsIgnoreCase(uuid.toString())) {
			leader = null;
		}
	}
	
	public void messageAllMembers(String message, Player except) {
		Player l = plugin.getServer().getPlayer(UUID.fromString(leader));
		if (l != null && l != except) {
			l.sendMessage(message);
		}
		for (int i = 0; i < members.size(); i++) {
			if (members.get(i) != null) {
				Player t = plugin.getServer().getPlayer(UUID.fromString(members.get(i)));
				if (t != null && t != except) {
					t.sendMessage(message);
				}
			}
		}
	}
	
	public void messageAllMembers(String message) {
		Player l = plugin.getServer().getPlayer(UUID.fromString(leader));
		if (l != null) {
			l.sendMessage(message);
		}
		for (int i = 0; i < members.size(); i++) {
			if (members.get(i) != null) {
				Player t = plugin.getServer().getPlayer(UUID.fromString(members.get(i)));
				if (t != null) {
					t.sendMessage(message);
				}
			}
		}
	}
	
	public void addPlayer(Player p) {
		members.add(p.getUniqueId().toString());
	}
	
	public int getKey() {
		return islandKey;
	}
	
	public void makeLeader(Player p) {
		members.add(leader);
		for (int i = 0; i < members.size(); i++) {
			if (members.get(i) != null && members.get(i).equalsIgnoreCase(p.getUniqueId().toString())) {
				members.set(i, null);
				leader = p.getUniqueId().toString();
			}
		}
	}
	
	public void setCanReset(boolean _canReset) {
		canReset = _canReset;
	}
	
	public void setP1(Location _p1) {
		p1 = _p1;
	}
	
	public void setP2(Location _p2) {
		p2 = _p2;
	}
	
	public Boolean visitorsCanRideMobs() {
		return visitorsCanRideMobs;
	}
	
	public void setVisitorsCanRideMobs(Boolean _value) {
		visitorsCanRideMobs = _value;
	}
	
	public Boolean getNether() {
		return nether;
	}
	
	public void setNether(Boolean _nether) {
		nether = _nether;
	}
	
	public Boolean visitorsCanPortal() {
		return visitorsCanPortal;
	}
	
	public void setVisitorsCanPortal(Boolean _value) {
		visitorsCanPortal = _value;
	}
	
	public void removeReset() {
		resetsLeft -= 1;
		if (resetsLeft == 0) {
			setCanReset(false);
		}
	}
	
	public int getResetsLeft() {
		return resetsLeft;
	}
	
	public Boolean trustContains(UUID _uuid) {
		for (int i = 0; i < trusts.size(); i++) {
			if (trusts.get(i).equalsIgnoreCase(String.valueOf(_uuid))) {
				return true;
			}
		}
		return false;
	}
	
	public void trustPlayer(UUID _uuid) {
		if (!trustContains(_uuid)) {
			trusts.add(String.valueOf(_uuid));
		}
	}

	public void removeTrust(UUID _uuid) {
		trusts.remove(String.valueOf(_uuid));
	}
	
	public String getSchematic() {
		return schematic;
	}
	
	public void setSchematic(String s) {
		schematic = s;
	}
	
	public int getPassiveMobs() {
		return passiveMobs;
	}
	
	public void setPassiveMobs(int pm) {
		passiveMobs = pm;
	}
	
}
