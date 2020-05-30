package holiday.garet.skyblock.world.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import holiday.garet.skyblock.event.LevelCalculatorFinishEvent;
import holiday.garet.skyblock.island.Island;

public class LevelCalculator {
	
	int pts = 0;
	List<Chunk> toCheck = new ArrayList<Chunk>();
	List<Chunk> checked = new ArrayList<Chunk>();
	int t = -9999;
	boolean finished = false;
	Player player;
	Island island;
	
	public static HashMap<Material, Integer> points = new HashMap<>();
	
	public LevelCalculator(List<Chunk> sc, Player p, Island is) {
		island = is;
		toCheck.addAll(sc);
		LevelCalculator lc = this;
		player = p;
		Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleSkyblock");
        t = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
            public void run(){
            	if (toCheck.size() > 0) {
        			tick(toCheck.get(0));
            	} else {
            		finished = true;
            		LevelCalculatorFinishEvent e = new LevelCalculatorFinishEvent(lc, player, is);
            		Bukkit.getPluginManager().callEvent(e);
            		if (t != -9999) {
            			Bukkit.getScheduler().cancelTask(t);
            		}
            	}
            }
        }, 0l, 2l);
	}
	
	public int getPts() {
		return pts;
	}
	
	public int getCheckedCount() {
		return checked.size();
	}
	
	public void tick(Chunk c) {
		boolean emptyChunk = true;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				// test if there are any blocks in column
				if (c.getBlock(x, 0, z).getLightFromSky() != 15) {
					// there are blocks in the column
					int y = 0;
					Block b;
					do {
						b = c.getBlock(x, y, z);
						if (b.getType().isSolid()) {
							emptyChunk = false;
							if (points.containsKey(b.getType())) {
								pts += points.get(b.getType());
							} else {
								pts++;
							}
						}
						y++;
					} while (b.getLightFromSky() != 15 && y < 256);
				}
			}
		}
		if (!emptyChunk) {
			// need to check surrounding chunks
			if (!checked.contains(c.getWorld().getChunkAt(c.getX() - 1, c.getZ())) && !toCheck.contains(c.getWorld().getChunkAt(c.getX() - 1, c.getZ()))) {
				toCheck.add(c.getWorld().getChunkAt(c.getX() - 1, c.getZ()));
			}
			if (!checked.contains(c.getWorld().getChunkAt(c.getX() + 1, c.getZ())) && !toCheck.contains(c.getWorld().getChunkAt(c.getX() + 1, c.getZ()))) {
				toCheck.add(c.getWorld().getChunkAt(c.getX() + 1, c.getZ()));
			}
			if (!checked.contains(c.getWorld().getChunkAt(c.getX(), c.getZ() - 1)) && !toCheck.contains(c.getWorld().getChunkAt(c.getX(), c.getZ() - 1))) {
				toCheck.add(c.getWorld().getChunkAt(c.getX(), c.getZ() - 1));
			}
			if (!checked.contains(c.getWorld().getChunkAt(c.getX(), c.getZ() + 1)) && !toCheck.contains(c.getWorld().getChunkAt(c.getX(), c.getZ() + 1))) {
				toCheck.add(c.getWorld().getChunkAt(c.getX(), c.getZ() + 1));
			}
		}
		checked.add(c);
		toCheck.remove(c);
	}
}
