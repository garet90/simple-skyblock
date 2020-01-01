package holiday.garet.skyblock.world;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import holiday.garet.skyblock.XMaterial;

public class Structure {
	
	Location loc;
	Configuration bp;
	World world;
	Configuration config;
	Location pspawn;
	Logger logger;
	
	public Structure(Location _loc, Configuration _bp, World _world, Configuration _config, Logger _logger) {
		loc = _loc;
		bp = _bp;
		world = _world;
		config = _config;
		logger = _logger;
	}

    @SuppressWarnings("deprecation")
	public void generate() {
		
		logger.info("Generating structure at x:" + loc.getX() + ", y:" + loc.getY() + ", z:" + loc.getZ());
		
		int x1 = loc.getBlockX();
		int y1 = loc.getBlockY();
		int z1 = loc.getBlockZ();
		
		int length = bp.getInt("depth");
		int width = bp.getInt("width");
		int height = bp.getInt("height");
		
		int x2 = x1 + length;
		int y2 = y1 + height;
		int z2 = z1 + width;
		
		// spawn blocks
		
		Location toSetAir = null;
		
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				if (bp.isSet("biome")) {
					world.setBiome(x, z, Biome.valueOf(bp.getString("biome")));
				}
				for (int y = y1; y < y2; y++) {
					Block currentBlock = world.getBlockAt(x, y, z);
					String currentType = bp.getString("map." + String.valueOf(y-y1) + "." + String.valueOf(z-z1) + "." + String.valueOf(x-x1));
					if (currentType.equalsIgnoreCase("ore")) {
    					if (config.getBoolean("GENERATE_ORES")) { // spice things up
    						double oreType = Math.random() * 100.0;
    						List<String> GENERATOR_ORES = config.getStringList("GENERATOR_ORES");
    					    for(int i = 0; i < GENERATOR_ORES.size(); i++) {
    					    	Material itemMat = XMaterial.matchXMaterial(GENERATOR_ORES.get(i).split(":")[0]).get().parseMaterial();
    					    	Double itemChance = Double.parseDouble(GENERATOR_ORES.get(i).split(":")[1]);
    					    	if (itemMat != null) {
    					    		oreType -= itemChance;
    					    		if (oreType < 0) {
    					    			currentBlock.setType(itemMat);
    					    			break;
    					    		}
    					    	} else {
    					    		logger.severe("Unknown material \'" + GENERATOR_ORES.get(i).split(":")[0] + "\' at GENERATOR_ORES item " + (i + 1) + "!");
    					    	}
    					    }
    					    if (currentBlock.getType() == XMaterial.AIR.parseMaterial()) {
    					    	currentBlock.setType(XMaterial.COBBLESTONE.parseMaterial());
    					    }
    					} else {
    						currentBlock.setType(XMaterial.DIRT.parseMaterial());
    					}
					} else if (currentType.equalsIgnoreCase("tree")) {
						world.generateTree(new Location(world,x,y,z), TreeType.TREE);
					} else if (currentType.equalsIgnoreCase("item_chest")) {
						world.getBlockAt(new Location(world,x,y,z)).setType(XMaterial.CHEST.parseMaterial());
						Chest chest = (Chest) world.getBlockAt(new Location(world,x,y,z)).getState();
					    Inventory chestinv = chest.getBlockInventory();
					    List<String> CHEST_ITEMS = config.getStringList("CHEST_ITEMS");
					    for(int i = 0; i < CHEST_ITEMS.size(); i++) {
					    	Material itemMat = XMaterial.matchXMaterial(CHEST_ITEMS.get(i).split(":")[0]).get().parseMaterial();
					    	int itemCnt = Integer.parseInt(CHEST_ITEMS.get(i).split(":")[1]);
					    	if (itemMat != null) {
					    		chestinv.addItem(new ItemStack(itemMat,itemCnt));
					    	} else {
					    		logger.severe("Unknown material \'" + CHEST_ITEMS.get(i).split(":")[0] + "\' at CHEST_ITEMS item " + (i + 1) + "!");
					    	}
					    }
					} else if (currentType.equalsIgnoreCase("player_spawn")) {
					    pspawn = new Location(world,x+.5,y,z+.5);
					    toSetAir = new Location(world,x,y+1,z);
					} else {
						Material mat = XMaterial.matchXMaterial(currentType).get().parseMaterial();
						if (mat != null) {
							currentBlock.setType(mat);
						} else {
							logger.severe("UNKNOWN MATERIAL '" + currentType + "' AT 'structures/default.yml'!");
						}
					}
				}
			}
		}
		
		if (toSetAir != null) {
			world.getBlockAt(toSetAir).setType(XMaterial.AIR.parseMaterial());
		}
	}
	
	public Location getPlayerSpawn() {
		return pspawn;
	}
	
}
