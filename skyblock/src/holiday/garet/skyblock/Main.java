package holiday.garet.skyblock;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {
	FileConfiguration config = getConfig();
	
	List<String> players = new ArrayList<String>();
	List<Location> islandP1 = new ArrayList<Location>();
	List<Location> islandP2 = new ArrayList<Location>();
	List<Location> islandHome = new ArrayList<Location>();
	List<Double> balances = new ArrayList<Double>();
	List<List<Boolean>> islandSettings = new ArrayList<List<Boolean>>();
	
	String CHAT_PREFIX = ChatColor.GRAY + "[" + ChatColor.AQUA + "SKYBLOCK" + ChatColor.GRAY + "] " + ChatColor.RESET;
	int ISLAND_HEIGHT = 70;
	int ISLAND_WIDTH = 400;
	int ISLAND_DEPTH = 400;
	
	World skyWorld;
	
	int limX = 25000;
	int limZ = 25000;
	Location nextIsland;
	
	double STARTING_MONEY = 500;
	
    @Override
    public void onEnable() {
    	Bukkit.getPluginManager().registerEvents(this, this);
    	nextIsland = new Location(skyWorld, -limX, ISLAND_HEIGHT,-limZ);
    	skyWorld = getServer().getWorld("world");
    	if (config.isSet("CHAT_PREFIX")) {
    		CHAT_PREFIX = config.getString("CHAT_PREFIX");
    	}
    	if (config.isSet("ISLAND_HEIGHT")) {
    		ISLAND_HEIGHT = config.getInt("ISLAND_HEIGHT");
    	}
    	if (config.isSet("ISLAND_WIDTH")) {
    		ISLAND_WIDTH = config.getInt("ISLAND_WIDTH");
    	}
    	if (config.isSet("ISLAND_DEPTH")) {
    		ISLAND_DEPTH = config.getInt("ISLAND_DEPTH");
    	}
    	if (config.isSet("LIMIT_X")) {
    		limX = config.getInt("LIMIT_X");
    	}
    	if (config.isSet("LIMIT_Z")) {
    		limZ = config.getInt("LIMIT_Z");
    	}
    	if (config.isSet("STARTING_MONEY")) {
    		STARTING_MONEY = config.getInt("STARTING_MONEY");
    	}
    	if (config.isSet("WORLD")) {
    		skyWorld = getServer().getWorld(config.getString("WORLD"));
    	}
    	if (config.isSet("data.nextIsland")) {
    		nextIsland = new Location(skyWorld,
    				config.getInt("data.nextIsland.x"),
    				config.getInt("data.nextIsland.y"),
    				config.getInt("data.nextIsland.z"));
    	}
    	saveData(); // saves defaults (listed above). I guess it's kinda redundant if it just loaded that info, but ehhh, who cares.
    	getLogger().info("Skyblock plugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
    	getLogger().info("Saving skyblock data...");
    	saveData();
    	getLogger().info("Skyblock plugin has been disabled!");
    }
    
	@Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        if (command.getName().equalsIgnoreCase("island") || command.getName().equalsIgnoreCase("is")) {
        	if (sender instanceof Player) {
    			Player p = (Player) sender;
        		int playerId = getPlayerId(p.getUniqueId());
            	if (args.length == 0) {
	        		if (islandP1.get(playerId) == null) {
			            sender.sendMessage(CHAT_PREFIX + "Generating island...");
			            generateIsland(nextIsland, p);
			            return true;
	        		} else {
	        			sender.sendMessage(CHAT_PREFIX + "Teleporting you to your island...");
	        			p.teleport(islandHome.get(playerId));
	        			return true;
	        		}
            	} else if (args[0].equalsIgnoreCase("help")) {
            		sender.sendMessage(ChatColor.GREEN + "Island commands" + ChatColor.GRAY + ": ");
            		sender.sendMessage(ChatColor.GRAY + "usage: /is [args]\n");
            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is home" + ChatColor.RESET + ": teleports you to your island home.");
            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is sethome" + ChatColor.RESET + ": sets your island\'s home location.");
            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is reset" + ChatColor.RESET + ": resets your island.");
            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is visit <player>" + ChatColor.RESET + ": visit another player\'s island.");
            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is settings <setting | list> [true | false]" + ChatColor.RESET + ": change various island settings.");
            		return true;
            	} else if (args[0].equalsIgnoreCase("home")) {
        			sender.sendMessage(CHAT_PREFIX + "Teleporting you to your island...");
        			p.teleport(islandHome.get(playerId));
        			return true;
            	} else if (args[0].equalsIgnoreCase("sethome")) {
            		if (
            				(p.getLocation().getX() > islandP1.get(playerId).getX() && p.getLocation().getX() < islandP2.get(playerId).getX()) &&
            				(p.getLocation().getZ() > islandP1.get(playerId).getZ() && p.getLocation().getZ() < islandP2.get(playerId).getZ())) {
	            		islandHome.set(playerId, p.getLocation());
	            	    saveData();
	            		sender.sendMessage(CHAT_PREFIX + "Your island home was set to your current location.");
            		} else {
            			sender.sendMessage(ChatColor.RED + "You must be on your island to use this command!");
            		}
            		return true;
            	} else if (args[0].equalsIgnoreCase("reset")) {
            		if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
            			if (islandSettings.get(playerId).get(0) != false) {
				            generateIsland(nextIsland, p);
				            PlayerInventory inv = p.getInventory();
				            inv.clear();
				            inv.setArmorContents(new ItemStack[4]);
				            sender.sendMessage(CHAT_PREFIX + "Your island was reset successfully.");
	            			return true;
            			} else {
            				sender.sendMessage(ChatColor.RED + "To prevent abuse, you may only reset your island once at this time. Please contact a server admin if you have a problem.");
            				return true;
            			}
            		} else {
            			sender.sendMessage(CHAT_PREFIX + "Are you sure? This action is irreversable! Type \"/is reset confirm\" to continue\n" + ChatColor.RED + "YOU MAY ONLY RESET YOUR ISLAND ONE TIME!");
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("visit")) {
            		if (args.length == 2) {
            			int pId = -1;
            			String pName;
            			if (getServer().getPlayer(args[1]) == null) {
            				// they are offline
	            			@SuppressWarnings("deprecation")
	            			OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
	            			if (op.hasPlayedBefore()) {
	            				pId = getPlayerId(op.getUniqueId());
	            				pName = op.getName();
	            			} else {
	            				sender.sendMessage(ChatColor.RED + "That user doesn\'t have an island!");
	            				return true;
	            			}
            			} else {
            				// they are online
            				pId = getPlayerId(getServer().getPlayer(args[1]).getUniqueId());
            				pName = getServer().getPlayer(args[1]).getName();
            			}
            			if ((islandHome.get(pId) != null && !(p.getName().equalsIgnoreCase(args[1])) && islandSettings.get(pId).get(1)) || p.isOp()) {
                			p.teleport(islandHome.get(pId));
                			sender.sendMessage(CHAT_PREFIX + "Teleported you to " + pName + "\'s island.");
                			return true;
            			} else if (p.getName().equalsIgnoreCase(args[1])) { 
            				sender.sendMessage(ChatColor.RED + "You can\'t visit yourself!");
            				return true;
            			} else if (!(islandSettings.get(pId).get(1))) {
            				sender.sendMessage(ChatColor.RED + "That island doesn\'t allow visitors.");
            				return true;
            			} else {
            				sender.sendMessage(ChatColor.RED + "That user doesn\'t have an island!");
            				return true;
            			}
            		} else {
            			sender.sendMessage(ChatColor.RED + "Usage: /island visit [player]");
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("settings")) {
            		if (args.length > 1) {
	            		if (args[1].equalsIgnoreCase("list")) {
	            			ChatColor allowVisitors = ChatColor.RED;
	            			if (islandSettings.get(playerId).get(1)) {
	            				allowVisitors = ChatColor.GREEN;
	            			}
	            			sender.sendMessage("Island Settings: " + allowVisitors + "allow-visitors" + ChatColor.RESET);
	            			return true;
	            		} else if (args[1].equalsIgnoreCase("allow-visitors")) {
	            			if (args.length > 2) {
	            				if (args[2].equalsIgnoreCase("true")) {
	            					islandSettings.get(playerId).set(1, true);
	            					sender.sendMessage(CHAT_PREFIX + "\'allow-visitors\' was set to true.");
	            				} else if (args[2].equalsIgnoreCase("false")) {
	            					islandSettings.get(playerId).set(1, false);
	            					sender.sendMessage(CHAT_PREFIX + "\'allow-visitors\' was set to false.");
	            				} else {
		                			sender.sendMessage(ChatColor.RED + "Usage: /island settings <setting | list> [true | false]");
	            				}
	            				return true;
	            			} else {
	                			sender.sendMessage(ChatColor.RED + "Usage: /island settings <setting | list> [true | false]");
	                			return true;
	            			}
	            		} else {
                			sender.sendMessage(ChatColor.RED + "Setting not found! Type \'/island settings list\' for a list of settings.");
                			return true;
	            		}
            		} else {
            			sender.sendMessage(ChatColor.RED + "Usage: /island settings <setting | list> [true | false]");
            			return true;
            		}
            	} else {
            		sender.sendMessage(CHAT_PREFIX + "Unknown command. Type \'/is help\' for a list of commands.");
            		return true;
            	} 
        	} else {
        		sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
	            return true;
        	}
        } else if (command.getName().equalsIgnoreCase("bal") || command.getName().equalsIgnoreCase("balance")) {
        	if (sender instanceof Player) {
        		if (args.length == 0) {
	        		Player p = (Player) sender;
	        		int pId = getPlayerId(p.getUniqueId());
	        		DecimalFormat dec = new DecimalFormat("#0.00");
					sender.sendMessage("Balance: " + ChatColor.GREEN + "$" + dec.format(balances.get(pId)));
	        		return true;
        		} else {
        			Player p = getServer().getPlayer(args[0]);
        			if (p != null) {
    	        		int pId = getPlayerId(p.getUniqueId());
    	        		DecimalFormat dec = new DecimalFormat("#0.00");
    					sender.sendMessage("Balance of " + p.getName() + ": " + ChatColor.GREEN + "$" + dec.format(balances.get(pId)));
    	        		return true;
        			} else {
        				sender.sendMessage(ChatColor.RED + "The player has to be online to see their balance.");
        			}
        		}
        	} else {
        		sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
        		return true;
        	}
        } else if (command.getName().equalsIgnoreCase("pay")) {
        	if (sender instanceof Player) {
        		Player p = (Player) sender;
        		int pId = getPlayerId(p.getUniqueId());
        		if (args.length > 1) {
        			Player r = getServer().getPlayer(args[0]);
        			if (r != null) {
        				double payAmount;
        			    try {
            				payAmount = Double.valueOf(args[1]);
        			    } catch (NumberFormatException | NullPointerException nfe) {
        					sender.sendMessage(ChatColor.RED + "That\'s not a valid amount of money!");
        			        return true;
        			    }
        				if (payAmount <= balances.get(pId) && payAmount > 0) {
        					balances.set(pId, balances.get(pId) - payAmount);
        					int rId = getPlayerId(r.getUniqueId());
        					balances.set(rId, balances.get(rId) + payAmount);
        	        		DecimalFormat dec = new DecimalFormat("#0.00");
        					sender.sendMessage(ChatColor.GREEN + "Sent $" + dec.format(payAmount) + " to " + r.getName() + " successfully.");
        					r.sendMessage(ChatColor.GREEN + "Received $" + dec.format(payAmount) + " from " + p.getName() + ".");
        					saveData();
        					if (r.getName() == sender.getName()) {
        						sender.sendMessage(ChatColor.LIGHT_PURPLE + "Congratulations, you payed yourself.");
        					}
        					return true;
        				} else if (payAmount <= 0) {
        					sender.sendMessage(ChatColor.RED + "That\'s not a valid amount of money!");
        					return true;
        				} else {
        					sender.sendMessage(ChatColor.RED + "You don\'t have enough money!");
        					return true;
        				}
        			} else {
        				sender.sendMessage(ChatColor.RED + "The receiver must be online to receive money!");
        				return true;
        			}
        		} else {
        			sender.sendMessage(ChatColor.RED + "Usage: /pay <player> <amount>");
        			return true;
        		}
        	} else {
        		sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
        		return true;
        	}
        } else if (command.getName().equalsIgnoreCase("fly")) {
        	if (sender instanceof Player) {
        		Player p = (Player) sender;
        		p.setAllowFlight(!p.getAllowFlight());
        		if (p.getAllowFlight()) {
        			sender.sendMessage(ChatColor.GREEN + "Flying has been enabled.");
        		} else {
        			sender.sendMessage(ChatColor.GREEN + "Flying has been disabled.");
        		}
        		return true;
        	} else {
        		sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
        		return true;
        	}
        }
        return false;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
    	Block b = e.getBlock();
    	Player p = e.getPlayer();
    	int pId = getPlayerId(p.getUniqueId());
    	if (p != null) {
    		if (p.getWorld() == skyWorld && !(p.isOp())) {
	    		if (islandP1.get(pId) != null) {
	    			int bX = b.getLocation().getBlockX();
	    			int bZ = b.getLocation().getBlockZ();
	    			int p1X = islandP1.get(pId).getBlockX();
	    			int p1Z = islandP1.get(pId).getBlockZ();
	    			int p2X = islandP2.get(pId).getBlockX();
	    			int p2Z = islandP2.get(pId).getBlockZ();
	    			if (
	    				!((bX > p1X && bX < p2X) && (bZ > p1Z && bZ < p2Z))
	    			) {
	    				e.setCancelled(true);
	    			}
	    		} else {
	    			e.setCancelled(true);
	    		}
    		}
    	}
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
    	Block b = e.getBlock();
    	Player p = e.getPlayer();
    	int pId = getPlayerId(p.getUniqueId());
    	if (p != null) {
    		if (p.getWorld() == skyWorld && !(p.isOp())) {
	    		if (islandP1.get(pId) != null) {
	    			int bX = b.getLocation().getBlockX();
	    			int bZ = b.getLocation().getBlockZ();
	    			int p1X = islandP1.get(pId).getBlockX();
	    			int p1Z = islandP1.get(pId).getBlockZ();
	    			int p2X = islandP2.get(pId).getBlockX();
	    			int p2Z = islandP2.get(pId).getBlockZ();
	    			if (
	        				!((bX > p1X && bX < p2X) && (bZ > p1Z && bZ < p2Z))
	        			) {
	    				e.setCancelled(true);
	    			}
	    		} else {
	    			e.setCancelled(true);
	    		}
    		}
    	}
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
    	Block b = e.getClickedBlock();
    	Player p = e.getPlayer();
    	int pId = getPlayerId(p.getUniqueId());
    	if (p != null && b != null) {
    		if (p.getWorld() == skyWorld && !(p.isOp())) {
	    		if (islandP1.get(pId) != null) {
	    			int bX = b.getLocation().getBlockX();
	    			int bZ = b.getLocation().getBlockZ();
	    			int p1X = islandP1.get(pId).getBlockX();
	    			int p1Z = islandP1.get(pId).getBlockZ();
	    			int p2X = islandP2.get(pId).getBlockX();
	    			int p2Z = islandP2.get(pId).getBlockZ();
	    			if (
	        				!((bX > p1X && bX < p2X) && (bZ > p1Z && bZ < p2Z))
	        			) {
	    				e.setCancelled(true);
	    			}
	    		} else {
	    			e.setCancelled(true);
	    		}
    		}
    	}
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
    	if(e instanceof EntityDamageByEntityEvent){
	        EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent)e;
	        if (edbeEvent.getDamager() instanceof Player){
	            if (e.getEntity() instanceof Player) {
	            	if (e.getEntity().getLocation().getWorld() == skyWorld && !(edbeEvent.getDamager().isOp())) {
	            		// make it so nobody can attack anybody on an island
	            		e.setCancelled(true);
	            	}
	            } else {
	            	Player p = (Player)edbeEvent.getDamager();
	            	int pId = getPlayerId(p.getUniqueId());
	            	if (
	            			(e.getEntity().getLocation().getBlockX() < islandP1.get(pId).getBlockX() || 
	            			e.getEntity().getLocation().getBlockX() > islandP2.get(pId).getBlockX() ||
	            			e.getEntity().getLocation().getBlockZ() < islandP1.get(pId).getBlockZ() || 
	            			e.getEntity().getLocation().getBlockZ() > islandP2.get(pId).getBlockZ()) && !(edbeEvent.getDamager().isOp())) {
	            		// make it so people cant hurt mobs on other people's islands
	            		e.setCancelled(true);
	            	}
	            }
	        }
    	}
    }
    
    public void saveData() {
    	config.set("CHAT_PREFIX", CHAT_PREFIX);
    	config.set("ISLAND_HEIGHT", ISLAND_HEIGHT);
    	config.set("ISLAND_WIDTH", ISLAND_WIDTH);
    	config.set("ISLAND_DEPTH", ISLAND_DEPTH);
    	config.set("LIMIT_X", limX);
    	config.set("LIMIT_Z", limZ);
    	config.set("STARTING_MONEY", STARTING_MONEY);
    	if (!config.isSet("WORLD")) {
    		config.set("WORLD", "world");
    	}
    	for (int i = 0; i < players.size(); i++) {
	    	config.set("data.players." + players.get(i) + ".islandP1.x", islandP1.get(i).getBlockX());
	    	config.set("data.players." + players.get(i) + ".islandP1.y", islandP1.get(i).getBlockY());
	    	config.set("data.players." + players.get(i) + ".islandP1.z", islandP1.get(i).getBlockZ());
	    	config.set("data.players." + players.get(i) + ".islandP2.x", islandP2.get(i).getBlockX());
	    	config.set("data.players." + players.get(i) + ".islandP2.y", islandP2.get(i).getBlockY());
	    	config.set("data.players." + players.get(i) + ".islandP2.z", islandP2.get(i).getBlockZ());
	    	config.set("data.players." + players.get(i) + ".islandHome.x", islandHome.get(i).getX());
	    	config.set("data.players." + players.get(i) + ".islandHome.y", islandHome.get(i).getY());
	    	config.set("data.players." + players.get(i) + ".islandHome.z", islandHome.get(i).getZ());
	    	config.set("data.players." + players.get(i) + ".islandHome.pitch", (int)islandHome.get(i).getPitch());
	    	config.set("data.players." + players.get(i) + ".islandHome.yaw", (int)islandHome.get(i).getYaw());
    		config.set("data.players." + players.get(i) + ".settings.resetLeft", islandSettings.get(i).get(0));
    		config.set("data.players." + players.get(i) + ".settings.allowsVisitors", islandSettings.get(i).get(1));
    		config.set("data.players." + players.get(i) + ".balance", balances.get(i));
    	}
    	config.set("data.nextIsland.x", nextIsland.getBlockX());
    	config.set("data.nextIsland.y", nextIsland.getBlockY());
    	config.set("data.nextIsland.z", nextIsland.getBlockZ());
    	saveConfig();
    }

	public void generateIsland(Location loc, Player p) {
		int x1 = loc.getBlockX();
		int y1 = loc.getBlockY();
		int z1 = loc.getBlockZ();
		
		int length = 5;
		int width = 5;
		int height = 4;
		
		int x2 = x1 + length;
		int y2 = y1 + height;
		int z2 = z1 + width;
		
		// spawn blocks
		
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				for (int z = z1; z < z2; z++) {
					if (
							!(x == x1 && z == z1)
							&& !(x == x2 - 1 && z == z1)
							&& !(x == x1 && z == z2 - 1)
							&& !(x == x2 - 1 && z == z2 - 1)
							&& !(y < y2 - 1 && 
									(
											(z == z1+1 && x == x1)
											|| (z == z1 && x == x1+1)
											|| (z == z2-2 && x == x1)
											|| (z == z2-1 && x == x1+1)
											|| (z == z2-2 && x == x2-1)
											|| (z == z2-1 && x == x2-2)
											|| (z == z1 && x == x2-2)
											|| (z == z1+1 && x == x2-1)
											)
									)
							&& !(y < y2 - 2 &&
									(
										(z == z1 && x == x1+2)
										|| (z == z2 - 1 && x == x1+2)
										|| (z == z1+2 && x == x1)
										|| (z == z1+2 && x == x2 - 1)
										|| (z == z1+1 && x == x1+1)
										|| (z == z2-2 && x == x1+1)
										|| (z == z1+1 && x == x2-2)
										|| (z == z2-2 && x == x2-2)
										)
									)
							&& !(y < y2 - 3 &&
									(
										(z == z1+1 && x == x1+2) // (2,3)
										|| (z == z1+3 && x == x1+2) // (4,3)
										|| (z == z1+2 && x == x1+1) // (3,2)
										|| (z == z1+2 && x == x1+3) // (3,4)
										)
									)
							) {
						Block currentBlock = skyWorld.getBlockAt(x, y, z);
	    				if (y == y2 - 1) {
	    					currentBlock.setType(Material.GRASS);
	    				} else if (x == x1 + 2 && z == z1 + 2 && y > y1) {
	    					currentBlock.setType(Material.SAND);
	    				} else if (y == y1) {
	    					currentBlock.setType(Material.BEDROCK);
	    				} else if (y < y1 + 3) {
	    					int oreType = (int)Math.floor(Math.random() * 100);
	    					if (oreType < 75) {
	    						currentBlock.setType(Material.STONE);
	    					} else if (oreType < 80) {
	    						currentBlock.setType(Material.COAL_ORE);
	    					} else if (oreType < 85) {
	    						currentBlock.setType(Material.IRON_ORE);
	    					} else if (oreType < 90) {
	    						currentBlock.setType(Material.GOLD_ORE);
	    					} else if (oreType < 92.5) {
	    						currentBlock.setType(Material.DIAMOND_ORE);
	    					} else if (oreType < 95) {
								currentBlock.setType(Material.LAPIS_ORE);
							} else {
								currentBlock.setType(Material.REDSTONE_ORE);
							}
	    				} else {
	    					currentBlock.setType(Material.DIRT);
	    				}
					}
				}
			}
		}
		// spawn tree and chest
		Location treeLoc = new Location(skyWorld,x1+2,y2,z1+2);
		Location chestLoc = new Location(skyWorld,x1+2,y2,z1+1);
		Location spawnLoc = new Location(skyWorld,x1+2,y2+1,z1);
		Location homeLoc = new Location(skyWorld,x1+2.5,y2,z1+.5);
		skyWorld.generateTree(treeLoc, TreeType.TREE);
		skyWorld.getBlockAt(spawnLoc).setType(Material.AIR);
		skyWorld.getBlockAt(chestLoc).setType(Material.CHEST);
		Chest chest = (Chest) skyWorld.getBlockAt(chestLoc).getState();
	    Inventory chestinv = chest.getBlockInventory();
	    chestinv.addItem(new ItemStack(Material.LAVA_BUCKET,1));
	    chestinv.addItem(new ItemStack(Material.ICE,2));
	    chestinv.addItem(new ItemStack(Material.SUGAR_CANE,1));
	    chestinv.addItem(new ItemStack(Material.RED_MUSHROOM,1));
	    chestinv.addItem(new ItemStack(Material.BROWN_MUSHROOM,1));
	    chestinv.addItem(new ItemStack(Material.PUMPKIN_SEEDS,1));
	    chestinv.addItem(new ItemStack(Material.MELON,1));
	    chestinv.addItem(new ItemStack(Material.CACTUS,1));
	    chestinv.addItem(new ItemStack(Material.COBBLESTONE,16));;
	    
	    // teleport player
	    p.teleport(homeLoc);
	    p.setBedSpawnLocation(homeLoc, true);
	    
	    // set values
	    int pId = getPlayerId(p.getUniqueId());
	    islandP1.set(pId, new Location(skyWorld,x1-(ISLAND_WIDTH/2),0,z1-(ISLAND_DEPTH/2)));
	    islandP2.set(pId, new Location(skyWorld,x1+(ISLAND_WIDTH/2),skyWorld.getMaxHeight(),z1+(ISLAND_DEPTH/2)));
	    islandHome.set(pId, homeLoc);
	    if (islandSettings.get(pId).get(0) == null) { // resets left
	    	islandSettings.get(pId).set(0, true);
	    } else {
	    	islandSettings.get(pId).set(0, false);
	    }
	    islandSettings.get(pId).set(1, true); // allow visitors
	    balances.set(pId, STARTING_MONEY);
	    
	    // set next island location
	    if (x1 < limX) {
	    	nextIsland = new Location(skyWorld, x1+ISLAND_WIDTH, ISLAND_HEIGHT, z1);
	    } else {
	    	nextIsland = new Location(skyWorld, -limX, ISLAND_HEIGHT, z1+ISLAND_DEPTH);
	    }
	    
	    // save new set data
	    saveData();
	}

	public int getPlayerId(UUID uuid) {
		int pId = players.indexOf(uuid.toString());
	    while (pId == -1) {
	    	if (config.isSet("data.players." + uuid.toString())) {
	    		players.add(uuid.toString());
	    		islandP1.add(new Location(
	    				skyWorld,
	    				config.getInt("data.players." + uuid.toString() + ".islandP1.x"),
	    				config.getInt("data.players." + uuid.toString() + ".islandP1.y"),
	    				config.getInt("data.players." + uuid.toString() + ".islandP1.z")));
	    		islandP2.add(new Location(
	    				skyWorld,
	    				config.getInt("data.players." + uuid.toString() + ".islandP2.x"),
	    				config.getInt("data.players." + uuid.toString() + ".islandP2.y"),
	    				config.getInt("data.players." + uuid.toString() + ".islandP2.z")));
	    		islandHome.add(new Location(
	    				skyWorld,
	    				config.getDouble("data.players." + uuid.toString() + ".islandHome.x"),
	    				config.getDouble("data.players." + uuid.toString() + ".islandHome.y"),
	    				config.getDouble("data.players." + uuid.toString() + ".islandHome.z"),
	    				config.getInt("data.players." + uuid.toString() + ".islandHome.yaw"),
	    				config.getInt("data.players." + uuid.toString() + ".islandHome.pitch")));
	    		balances.add(config.getDouble("data.players." + uuid.toString() + ".balance"));
	    		islandSettings.add(new ArrayList<Boolean>());
	    		islandSettings.get(islandSettings.size()-1).add(config.getBoolean("data.players." + uuid.toString() + ".settings.resetLeft"));
	    		islandSettings.get(islandSettings.size()-1).add(config.getBoolean("data.players." + uuid.toString() + ".settings.allowsVisitors"));
	    	} else {
		    	players.add(uuid.toString());
		    	islandP1.add(null);
		    	islandP2.add(null);
		    	islandHome.add(null);
		    	balances.add(null);
	    		islandSettings.add(new ArrayList<Boolean>());
	    		islandSettings.get(islandSettings.size()-1).add(null); // resets left
	    		islandSettings.get(islandSettings.size()-1).add(null); // allows visitors
	    	}
	    	pId = getPlayerId(uuid);
	    }
		return pId;
	}
    
}
