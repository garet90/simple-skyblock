package holiday.garet.skyblock.world;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import holiday.garet.skyblock.XMaterial;
import holiday.garet.skyblock.GMaterial;
import holiday.garet.skyblock.world.schematic.Schematic;

public class Structure {
	
	Location loc;
	Schematic bp;
	World world;
	Configuration config;
	Location pspawn;
	Logger logger;
	
	public Structure(Location _loc, Schematic _bp, World _world, FileConfiguration _config, Logger _logger) {
		loc = _loc;
		bp = _bp;
		world = _world;
		config = _config;
		logger = _logger;
	}

    public void generate() {
		
		logger.info("Generating structure at x:" + loc.getX() + ", y:" + loc.getY() + ", z:" + loc.getZ());
		
		int x1 = loc.getBlockX();
		int y1 = loc.getBlockY();
		int z1 = loc.getBlockZ();
		
		int length = bp.getLength();
		int width = bp.getWidth();
		int height = bp.getHeight();
		
		logger.info("Structure length: " + length);
		logger.info("Structure width: " + width);
		logger.info("Structure height: " + height);
		
		// spawn blocks
		
		for (int x = 0; x < length; x++) {
			for (int z = 0; z < width; z++) {
				for (int y = 0; y < height; y++) {
					Block currentBlock = world.getBlockAt(x + x1, y + y1, z + z1);
					int index = (y * length + z) * width + x;
					int typeId = bp.getBlocks()[index];
					byte data = bp.getData()[index];
					try {
						XMaterial currentType = GMaterial.getMaterial(typeId, data);
						if (currentType == XMaterial.RED_STAINED_GLASS) {
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
						} else if (currentType == XMaterial.GREEN_STAINED_GLASS) {
							world.getBlockAt(new Location(world,x+x1,y+y1,z+z1)).setType(XMaterial.CHEST.parseMaterial());
							Chest chest = (Chest) world.getBlockAt(new Location(world,x+x1,y+y1,z+z1)).getState();
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
						} else if (currentType == XMaterial.YELLOW_STAINED_GLASS) {
						    pspawn = new Location(world,x + x1 +.5,y+y1,z + z1 +.5);
						} else {
							currentBlock.setType(currentType.parseMaterial());
						}
					} catch (Exception e) {
						logger.severe("Block placing failed at (" + (x + x1) + "," + (y + y1) + "," + (z + z1) + ")!");
						logger.severe("Index: " + index);
						logger.severe("Type ID: " + typeId);
						logger.severe("Data: " + data);
					}
				}
			}
		}
	}
	
	public Location getPlayerSpawn() {
		return pspawn;
	}
	
}
