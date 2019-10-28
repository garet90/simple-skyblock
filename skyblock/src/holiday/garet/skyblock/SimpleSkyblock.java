package holiday.garet.skyblock;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityMountEvent;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class SimpleSkyblock extends JavaPlugin implements Listener {
	FileConfiguration config = getConfig();
	
	List<String> players = new ArrayList<String>();
	List<Location> islandP1 = new ArrayList<Location>();
	List<Location> islandP2 = new ArrayList<Location>();
	List<Location> islandHome = new ArrayList<Location>();
	List<Double> balances = new ArrayList<Double>();
	List<List<Boolean>> islandSettings = new ArrayList<List<Boolean>>();
	List<Integer> invites = new ArrayList<Integer>();
	List<String> islandLeader = new ArrayList<String>();
	List<List<String>> islandMembers = new ArrayList<List<String>>();
	List<String> tradingRequests = new ArrayList<String>();
	List<Double> islandPts = new ArrayList<Double>();
	
	List<String> openTrades = new ArrayList<String>();
	
	List<String> toClear = new ArrayList<String>();
	
	// settings:
	String CHAT_PREFIX = ChatColor.GRAY + "[" + ChatColor.AQUA + "SKYBLOCK" + ChatColor.GRAY + "] " + ChatColor.RESET;
	int ISLAND_HEIGHT = 70;
	int ISLAND_WIDTH = 400;
	int ISLAND_DEPTH = 400;
	Boolean GENERATE_ORES = true;
	Boolean INFINITE_RESETS = false;
	Boolean USE_CHATROOMS = true;
	Boolean VOID_INSTANT_DEATH = true;
	Boolean USE_ECONOMY = true;
	Boolean DISABLE_PLAYER_COLLISIONS = true;
	List<String> CHEST_ITEMS = new ArrayList<String>(){/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	{
        add("LAVA_BUCKET:1");
        add("ICE:2");
        add("SUGAR_CANE:1");
        add("RED_MUSHROOM:1");
        add("BROWN_MUSHROOM:1");
        add("PUMPKIN_SEEDS:1");
        add("MELON:1");
        add("CACTUS:1");
        add("COBBLESTONE:16");
          }};
    List<String> GENERATOR_ORES = new ArrayList<String>(){/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	{
        add("COAL_ORE:2.5");
        add("IRON_ORE:2.5");
        add("GOLD_ORE:.2");
        add("DIAMOND_ORE:.2");
        add("LAPIS_ORE:.2");
        add("REDSTONE_ORE:1.5");
        add("EMERALD_ORE:.1");
        add("OBSIDIAN:.05");
        add("STONE:45.875");
        add("COBBLESTONE:45.875");
          }};
  	List<String> KILL_MONEY = new ArrayList<String>(){/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	{
		add("Default:0");
        add("Monster:3");
        add("Animals:1");
        add("CREEPER:5");
        add("ENDERMAN:10");
        add("ZOMBIE:2");
          }};

	List<String> LEVEL_PTS = new ArrayList<String>(){/**
	 	 * 
		 */
		private static final long serialVersionUID = 1L;

	{
      add("Default:1");
      add("COBBLESTONE:0.1");
      add("SAPLING:50");
        }};
	
	World skyWorld;
	
	int limX = 25000;
	int limZ = 25000;
	Location nextIsland;
	
	double STARTING_MONEY = 500;
	
    @Override
    public void onEnable() {
    	Bukkit.getPluginManager().registerEvents(this, this);
    	toClear = config.getStringList("data.toClear");
    	nextIsland = new Location(skyWorld, -limX, ISLAND_HEIGHT,-limZ);
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
    	if (config.isSet("data.nextIsland")) {
    		nextIsland = new Location(skyWorld,
    				config.getInt("data.nextIsland.x"),
    				config.getInt("data.nextIsland.y"),
    				config.getInt("data.nextIsland.z"));
    	}
    	if (config.isSet("GENERATE_ORES")) {
    		GENERATE_ORES = config.getBoolean("GENERATE_ORES");
    	}
    	if (config.isSet("INFINITE_RESETS")) {
    		INFINITE_RESETS = config.getBoolean("INFINITE_RESETS");
    	}
    	if (config.isSet("CHEST_ITEMS")) {
    		CHEST_ITEMS = config.getStringList("CHEST_ITEMS");
    	}
    	if (config.isSet("KILL_MONEY")) {
    		KILL_MONEY = config.getStringList("KILL_MONEY");
    	}
    	if (config.isSet("GENERATOR_ORES")) {
    		GENERATOR_ORES = config.getStringList("GENERATOR_ORES");
    	}
    	if (config.isSet("LEVEL_PTS")) {
    		LEVEL_PTS = config.getStringList("LEVEL_PTS");
    	}
		final String worldName;
    	if (config.isSet("WORLD")) {
    		worldName = config.getString("WORLD");
    	} else {
    		worldName = "world";
    	}
    	if (!config.isSet("BIOME")) {
    		config.set("BIOME","PLAINS");
    	}
    	if (config.isSet("USE_CHATROOMS")) {
    		USE_CHATROOMS = config.getBoolean("USE_CHATROOMS");
    	}
    	if (config.isSet("USE_ECONOMY")) {
    		USE_ECONOMY = config.getBoolean("USE_ECONOMY");
    	}
    	if (config.isSet("VOID_INSTANT_DEATH")) {
    		VOID_INSTANT_DEATH = config.getBoolean("VOID_INSTANT_DEATH");
    	}
    	if (config.isSet("DISABLE_PLAYER_COLLISIONS")) {
    		DISABLE_PLAYER_COLLISIONS = config.getBoolean("DISABLE_PLAYER_COLLISIONS");
    	}
    	skyWorld = getServer().getWorld(worldName);
    	if (skyWorld == null) { // If we don't have a world generated yet
            BukkitScheduler scheduler = getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                	WorldCreator wc = new WorldCreator(worldName);
	                wc.generator(new EmptyWorldGenerator());
	                wc.createWorld();
	                skyWorld = getServer().getWorld(worldName);
                }
            }, 0L);
    	}
    	saveData(); // saves defaults (listed above). I guess it's kinda redundant if it just loaded that info, but ehhh, who cares.
    	getLogger().info("SimpleSkyblock has been enabled!");
    	try {
	        getLogger().info("Checking for updates...");
	        UpdateChecker updater = new UpdateChecker(this, 72100);
	        if(updater.checkForUpdates()) {
		        getLogger().info(ChatColor.GREEN + "There is an update for SimpleSkyblock! Go to https://www.spigotmc.org/resources/simple-skyblock.72100/ to download it.");
	        }else{
		        getLogger().info("There are no updates for SimpleSkyblock at this time.");
	        }
	    } catch(Exception e) {
	        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not proceed update-checking, plugin disabled!");
	        Bukkit.getPluginManager().disablePlugin(this);
	    }
    }
    
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
    	return new EmptyWorldGenerator();
    }
    
    @Override
    public void onDisable() {
    	getLogger().info("Saving skyblock data...");
    	saveData();
    	getLogger().info("SimpleSkyblock has been disabled!");
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
	        			p.teleport(islandHome.get(playerId), TeleportCause.PLUGIN);
	            		p.setBedSpawnLocation(p.getLocation(), true);
	        			p.setFallDistance(0);
	        			return true;
	        		}
            	} else if (args[0].equalsIgnoreCase("help")) {
            		if (args.length > 1 && args[1].equalsIgnoreCase("2")) {
                		sender.sendMessage(ChatColor.GREEN + "Island Commands (2/2)" + ChatColor.GRAY + ": ");
                		sender.sendMessage(ChatColor.GRAY + "usage: /is [args]\n");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is join <player>" + ChatColor.RESET + ": join a player\'s island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is leave" + ChatColor.RESET + ": leave a co-op island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is kick <player>" + ChatColor.RESET + ": kick a member of your island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is list" + ChatColor.RESET + ": list members of your island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is makeleader <player>" + ChatColor.RESET + ": make another player leader of the island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is level" + ChatColor.RESET + ": check what level your island is.");
            			return true;
            		} else {
                		sender.sendMessage(ChatColor.GREEN + "Island Commands (1/2)" + ChatColor.GRAY + ": ");
                		sender.sendMessage(ChatColor.GRAY + "usage: /is [args]\n");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is help [n]" + ChatColor.RESET + ": view page [n] of /island help.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is home" + ChatColor.RESET + ": teleports you to your island home.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is sethome" + ChatColor.RESET + ": sets your island\'s home location.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is reset" + ChatColor.RESET + ": resets your island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is visit <player>" + ChatColor.RESET + ": visit another player\'s island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is settings <setting | list> [true | false]" + ChatColor.RESET + ": change various island settings.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is invite <player>" + ChatColor.RESET + ": invite a player to join your island.");
	            		return true;
            		}
            	} else if (args[0].equalsIgnoreCase("home")) {
            		if (islandHome.get(playerId) != null) {
	        			sender.sendMessage(CHAT_PREFIX + "Teleporting you to your island...");
	        			p.teleport(islandHome.get(playerId), TeleportCause.PLUGIN);
	            		p.setBedSpawnLocation(p.getLocation(), true);
	        			p.setFallDistance(0);
	        			return true;
            		} else {
            			sender.sendMessage(ChatColor.RED + "You must have an island to teleport to it!");
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("sethome")) {
            		if (islandP1.get(playerId) != null) {
	            		if (
	            				(p.getLocation().getX() > islandP1.get(playerId).getX() && p.getLocation().getX() < islandP2.get(playerId).getX()) &&
	            				(p.getLocation().getZ() > islandP1.get(playerId).getZ() && p.getLocation().getZ() < islandP2.get(playerId).getZ())) {
		            		islandHome.set(playerId, p.getLocation());
		            		p.setBedSpawnLocation(p.getLocation(), true);
		            	    saveData();
		            		sender.sendMessage(CHAT_PREFIX + "Your island home was set to your current location.");
	            		} else {
	            			sender.sendMessage(ChatColor.RED + "You must be on your island to use this command!");
	            		}
	            		return true;
            		} else {
            			sender.sendMessage(ChatColor.RED + "You must have an island to set home!");
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("reset")) {
            		if (islandP1.get(playerId) != null) {
	            		if (islandLeader.get(playerId) == null) {
		            		if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
		            			if (islandSettings.get(playerId).get(0) != false || INFINITE_RESETS) {
						            generateIsland(nextIsland, p);
						            for (int i = 0; i < islandMembers.get(playerId).size(); i++) {
						            	if (islandMembers.get(playerId).get(i) != null) {
						            		UUID tempUUID = UUID.fromString(islandMembers.get(playerId).get(i));
						            		Player tempP = getServer().getPlayer(tempUUID);
						            		int tempUID = getPlayerId(tempUUID);
						            		islandP1.set(tempUID, islandP1.get(playerId));
						            		islandP2.set(tempUID, islandP2.get(playerId));
						            		islandHome.set(tempUID, islandHome.get(playerId));
						            		balances.set(tempUID, balances.get(playerId));
						            		if (tempP == null) {
						            			toClear.add(tempUUID.toString());
						            		} else {
						            			PlayerInventory tempInv = tempP.getInventory();
						            			tempInv.clear();
						            			tempInv.setArmorContents(new ItemStack[4]);
						            			tempP.teleport(islandHome.get(tempUID), TeleportCause.PLUGIN);
						            			tempP.sendMessage(CHAT_PREFIX + "Your island was reset successfully.");
						            		}
						            	}
						            }
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
		            			sender.sendMessage(CHAT_PREFIX + "Are you sure? This action is irreversable! This will reset your inventory, balance, and island as well as those of all the island\'s members. Type \"/is reset confirm\" to continue.");
		            			if (!INFINITE_RESETS) {
		            				sender.sendMessage(ChatColor.RED + "YOU MAY ONLY RESET YOUR ISLAND ONE TIME!");
		            			}
		            			return true;
		            		}
	            		} else {
	            			sender.sendMessage(ChatColor.RED + "You must be island leader to use this command!");
	            			return true;
	            		}
            		} else {
            			sender.sendMessage(ChatColor.RED + "You must have an island to reset it!");
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("visit")) {
            		if (args.length == 2) {
            			int pId = -1;
            			String pName;
            			String pUUID;
            			if (getServer().getPlayer(args[1]) == null) {
            				// they are offline
	            			OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
	            			if (op.hasPlayedBefore()) {
	            				pId = getPlayerId(op.getUniqueId());
	            				pName = op.getName();
	            				pUUID = op.getUniqueId().toString();
	            			} else {
	            				sender.sendMessage(ChatColor.RED + "That user doesn\'t have an island!");
	            				return true;
	            			}
            			} else {
            				// they are online
            				pId = getPlayerId(getServer().getPlayer(args[1]).getUniqueId());
            				pName = getServer().getPlayer(args[1]).getName();
            				pUUID = getServer().getPlayer(args[1]).getUniqueId().toString();
            			}
            			String leaderUUID;
            			if (config.getString("data.players." + pUUID + ".islandLeader") != null) {
            				leaderUUID = config.getString("data.players." + pUUID + ".islandLeader");
            			} else {
            				leaderUUID = pUUID;
            			}
            			Boolean allowsVisitors;
            			if (leaderUUID == null) {
            				allowsVisitors = islandSettings.get(pId).get(1);
            			} else {
            				allowsVisitors = config.getBoolean("data.players." + leaderUUID + ".settings.allowsVisitors");
            			}
            			if (leaderUUID.equalsIgnoreCase(islandLeader.get(playerId)) || leaderUUID.equalsIgnoreCase(p.getUniqueId().toString())) {
            				sender.sendMessage(ChatColor.RED + "You can\'t visit your own island!");
            				return true;
            			}
            			if ((islandHome.get(pId) != null && !(p.getName().equalsIgnoreCase(args[1])) && allowsVisitors) || p.isOp()) {
                			p.teleport(islandHome.get(pId), TeleportCause.PLUGIN);
		            		p.setBedSpawnLocation(p.getLocation(), true);
		            		p.setBedSpawnLocation(p.getLocation(), true);
		        			p.setFallDistance(0);
                			sender.sendMessage(CHAT_PREFIX + "Teleported you to " + pName + "\'s island.");
                			return true;
            			} else if (p.getName().equalsIgnoreCase(args[1])) { 
            				sender.sendMessage(ChatColor.RED + "You can\'t visit yourself!");
            				return true;
            			} else if (!(allowsVisitors)) {
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
            		if (islandP1.get(playerId) != null) {
	            		if (islandLeader.get(playerId) == null) {
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
			            				saveData();
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
	            			sender.sendMessage(ChatColor.RED + "You must be island leader to use this command!");
	            			return true;
	            		}
            		} else {
            			sender.sendMessage(ChatColor.RED + "You must have an island to change its settings!");
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("invite")) {
            		if (islandP1.get(playerId) != null) {
	            		if (islandLeader.get(playerId) == null) {
		            		if (args.length > 1) {
		            			Player r = getServer().getPlayer(args[1]);
		            			if (r != null) {
		            				int rId = getPlayerId(r.getUniqueId());
			            			if (islandLeader.get(rId) == null || !islandLeader.get(rId).equalsIgnoreCase(p.getUniqueId().toString())) {
			            				r.sendMessage(ChatColor.GREEN + "You have been invited to join " + sender.getName() + "\'s island. Type \'/is join " + sender.getName() + "\' to join their island. \n" + ChatColor.RED + "Warning: This will remove your old island!");
			            				sender.sendMessage(ChatColor.GREEN + "You have successfully invited " + r.getName() + " to join your island.");
			            				invites.set(playerId, rId);
			            				return true;
		            				} else {
		            					p.sendMessage(ChatColor.RED + "This player is already a member of your island!");
		            					return true;
		            				}
		            			} else {
		                			sender.sendMessage(ChatColor.RED + "Player must be online to receive invite!");
		                			return true;
		            			}
		            		} else {
		            			sender.sendMessage(ChatColor.RED + "Usage: /island invite <player>");
		            			return true;
		            		}
	            		} else {
	            			sender.sendMessage(ChatColor.RED + "You must be island leader to use this command!");
	            			return true;
	            		}
            		} else {
            			sender.sendMessage(ChatColor.RED + "You must have an island to invite people to it!");
            			return true;
            		}
        	    } else if (args[0].equalsIgnoreCase("join")) {
        	    	if (args.length > 1) {
            			Player j = getServer().getPlayer(args[1]);
            			if (islandMembers.get(playerId) == null || getNotNullLength(islandMembers.get(playerId)) == 0) {
	            			if (j != null) {
	            				int jId = getPlayerId(j.getUniqueId());
	            				if (invites.get(jId) != null && invites.get(jId) == playerId) {
	            					if (islandLeader.get(playerId) != null) {
		    	        				Player l = getServer().getPlayer(UUID.fromString(islandLeader.get(playerId)));
		    	        				UUID lUUID = UUID.fromString(islandLeader.get(playerId));
		    	        				if (l != null) {
		    	        					l.sendMessage(ChatColor.GREEN + p.getName() + " has left your island.");
		    	        				}
		    	        				int lId = getPlayerId(lUUID);
		    	        				for (int i = 0; i < islandMembers.get(lId).size(); i++) {
	        	        					if (islandMembers.get(lId).get(i) != null) {
			    	        					Player toTell = getServer().getPlayer(UUID.fromString(islandMembers.get(lId).get(i)));
			    	        					if (!islandMembers.get(lId).get(i).equalsIgnoreCase(p.getUniqueId().toString()) && toTell != null) {
			    	        						toTell.sendMessage(ChatColor.GREEN + p.getName() + " has left your island.");
			    	        					}
	        	        					}
		    	        				}
		    	        				islandMembers.get(lId).set(islandMembers.get(lId).indexOf(p.getUniqueId().toString()), null);
	            					}
	            					invites.set(jId, null);
	            					islandP1.set(playerId, islandP1.get(jId));
	            					islandP2.set(playerId, islandP2.get(jId));
	            					islandHome.set(playerId, islandHome.get(jId));
	            					islandSettings.get(playerId).set(1, islandSettings.get(jId).get(1));
	            					balances.set(playerId, 0.0); // set to 0 to prevent abuse.
	    				            PlayerInventory inv = p.getInventory();
	    				            inv.clear();
	    				            inv.setArmorContents(new ItemStack[4]);
	    				            p.teleport(islandHome.get(playerId), TeleportCause.PLUGIN);
	    				            p.setFallDistance(0);
	    				    	    p.setBedSpawnLocation(islandHome.get(playerId), true);
	                    			sender.sendMessage(ChatColor.GREEN + "You have successfully joined " + j.getName() + "\'s island.");
	                    			j.sendMessage(ChatColor.GREEN + sender.getName() + " has joined your island.");
	    	        				for (int i = 0; i < islandMembers.get(jId).size(); i++) {
        	        					if (islandMembers.get(jId).get(i) != null) {
		    	        					Player toTell = getServer().getPlayer(UUID.fromString(islandMembers.get(jId).get(i)));
		    	        					if (!islandMembers.get(jId).get(i).equalsIgnoreCase(p.getUniqueId().toString()) && toTell != null) {
		    	        						toTell.sendMessage(ChatColor.GREEN + p.getName() + " has joined your island.");
		    	        					}
        	        					}
	    	        				}
	                    			islandLeader.set(playerId, j.getUniqueId().toString());
	                    			islandMembers.get(jId).add(p.getUniqueId().toString());
	                    			saveData();
	    				            return true;
	            				} else {
	                    			sender.sendMessage(ChatColor.RED + "You have not been invited by that player! Ask them to send another invite.");
	                    			return true;
	            				}
	            			} else {
	                			sender.sendMessage(ChatColor.RED + "Player must be online to join their island!");
	                			return true;
	            			}
            			} else {
                			sender.sendMessage(ChatColor.RED + "You can\'t be an island leader and join another island! Use \"/island makeleader <player>\" to transfer leadership.");
                			return true;
            			}
        	    	} else {
            			sender.sendMessage(ChatColor.RED + "Usage: /island join <player>");
            			return true;
        	    	}
        		} else if (args[0].equalsIgnoreCase("leave")) {
        			if (islandP1.get(playerId) != null) {
	        			if (islandLeader.get(playerId) != null) {
	        				Player l = getServer().getPlayer(UUID.fromString(islandLeader.get(playerId)));
	        				UUID lUUID = UUID.fromString(islandLeader.get(playerId));
	        				if (l != null) {
	        					l.sendMessage(ChatColor.GREEN + p.getName() + " has left your island.");
	        				}
	        				int lId = getPlayerId(lUUID);
	        				for (int i = 0; i < islandMembers.get(lId).size(); i++) {
	        					if (islandMembers.get(lId).get(i) != null) {
		        					Player toTell = getServer().getPlayer(UUID.fromString(islandMembers.get(lId).get(i)));
		        					if (!islandMembers.get(lId).get(i).equalsIgnoreCase(p.getUniqueId().toString()) && toTell != null) {
		        						toTell.sendMessage(ChatColor.GREEN + p.getName() + " has left your island.");
		        					}
	        					}
	        				}
	        				islandP1.set(playerId, null);
	        				islandP2.set(playerId, null);
	        				islandHome.set(playerId, null);
	        				islandLeader.set(playerId, null);
	        				islandSettings.get(playerId).set(1, null);
                			islandMembers.get(getPlayerId(lUUID)).set(islandMembers.get(getPlayerId(lUUID)).indexOf(p.getUniqueId().toString()),null);
	        				saveData();
	            			sender.sendMessage(ChatColor.GREEN + "You have successfully left your island.");
	        				return true;
	        			} else {
	            			sender.sendMessage(ChatColor.RED + "You cannot leave while you are island leader!");
	            			return true;
	        			}
        			} else {
            			sender.sendMessage(ChatColor.RED + "You must be a member of an island to leave it!");
            			return true;
        			}
        		} else if (args[0].equalsIgnoreCase("kick")) {
            		if (islandP1.get(playerId) != null) {
            			if (islandLeader.get(playerId) == null) {
            				if (args.length > 1) {
            					Player k = getServer().getPlayer(args[1]);
            					String kName;
            					UUID pUUID;
            					if (k == null) {
            						OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
            						pUUID = op.getUniqueId();
            						kName = op.getName();
            					} else {
            						pUUID = k.getUniqueId();
            						kName = k.getName();
                					if (k.getUniqueId() == p.getUniqueId()) {
                						sender.sendMessage(ChatColor.RED + "You can\'t kick yourself!");
                						return true;
                					}
            					}
        						int pId = getPlayerId(pUUID);
        						if (islandLeader.get(pId) != null && islandLeader.get(pId).equalsIgnoreCase(p.getUniqueId().toString())) {
        							islandP1.set(pId, null);
        							islandP2.set(pId, null);
        							islandHome.set(pId, null);
        							islandSettings.get(pId).set(1, null);
        							islandLeader.set(pId, null);
        							islandMembers.get(playerId).set(islandMembers.get(playerId).indexOf(pUUID.toString()), null);
        							saveData();
            		        		sender.sendMessage(ChatColor.GREEN + kName+ " has been kicked from your island.");
            		        		if (k != null) {
            		        			k.sendMessage(ChatColor.RED + "You have been kicked from your island.");
            		        		}
        	        				for (int i = 0; i < islandMembers.get(playerId).size(); i++) {
        	        					if (islandMembers.get(playerId).get(i) != null) {
	        	        					Player toTell = getServer().getPlayer(UUID.fromString(islandMembers.get(playerId).get(i)));
	        	        					if (toTell != null && !islandMembers.get(playerId).get(i).equalsIgnoreCase(p.getUniqueId().toString())) {
	        	        						toTell.sendMessage(ChatColor.GREEN + kName + " has left your island.");
	        	        					}
        	        					}
        	        				}
            		        		return true;
        						} else {
                					sender.sendMessage(ChatColor.RED + "This user is not a member of your island!");
                					return true;
        						}
            				} else {
            					sender.sendMessage(ChatColor.RED + "Usage: /island kick <player>");
            					return true;
            				}
            			} else {
                			sender.sendMessage(ChatColor.RED + "You must be the island leader to kick someone!");
                			return true;
            			}
            		} else {
            			sender.sendMessage(ChatColor.RED + "You must be a member of an island to kick someone!");
            			return true;
            		}
        		} else if (args[0].equalsIgnoreCase("list")) {
        			if (islandP1.get(playerId) != null) {
        				UUID leaderUUID;
        				if (islandLeader.get(playerId) == null) {
        					// you are the leader
        					leaderUUID = p.getUniqueId();
        				} else {
        					// you are not the leader
        					leaderUUID = UUID.fromString(islandLeader.get(playerId));
        				}
        				int lId = getPlayerId(leaderUUID);
        				Player l = getServer().getPlayer(leaderUUID);
        				String leaderName;
        				ChatColor leaderColor;
        				if (l != null) {
        					leaderName = l.getName();
        					leaderColor = ChatColor.GREEN;
        				} else {
							OfflinePlayer op = Bukkit.getOfflinePlayer(leaderUUID);
    						leaderName = op.getName();
    						leaderColor = ChatColor.RED;
        				}
        				sender.sendMessage(ChatColor.AQUA + "Island Members" + ChatColor.GRAY + ":");
        				sender.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.GOLD + "[LEADER] " + leaderColor + leaderName);
        				for (int i = 0; i < islandMembers.get(lId).size(); i++) {
        					if (islandMembers.get(lId).get(i) != null) {
        						UUID tempUUID = UUID.fromString(islandMembers.get(lId).get(i));
        						Player tempP = getServer().getPlayer(tempUUID);
        						if (tempP != null) {
            						sender.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.GREEN + tempP.getName());
        						} else {
        							OfflinePlayer tempOP = Bukkit.getOfflinePlayer(tempUUID);
        							sender.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.RED + tempOP.getName());
        						}
        					}
        				}
        				return true;
        			} else {
            			sender.sendMessage(ChatColor.RED + "You must be a member of an island to see the members of it!");
            			return true;
        			}
            	} else if (args[0].equalsIgnoreCase("makeleader")) {
        			if (islandP1.get(playerId) != null) {
        				if (islandLeader.get(playerId) == null) {
        					if (args.length > 1) {
        						Player l = getServer().getPlayer(args[1]);
        						if (l != null) {
            						if (l.getUniqueId() != p.getUniqueId()) {
	        							int lId = getPlayerId(l.getUniqueId());
	        							if (islandLeader.get(lId).equalsIgnoreCase(p.getUniqueId().toString())) {
		        							islandLeader.set(lId, null);
		        							islandLeader.set(playerId, l.getUniqueId().toString());
		        							for (int i = 0; i < islandMembers.get(playerId).size(); i++) {
		        								if (islandMembers.get(playerId).get(i) != null && !islandMembers.get(playerId).get(i).equalsIgnoreCase(l.getUniqueId().toString())) {
		        									int tempId = getPlayerId(UUID.fromString(islandMembers.get(playerId).get(i)));
		        									islandLeader.set(tempId, l.getUniqueId().toString());
		        								}
		        							}
		        							islandMembers.set(lId, islandMembers.get(playerId));
		        							islandMembers.set(playerId, null);
		        							islandMembers.get(lId).add(p.getUniqueId().toString());
		        							islandMembers.get(lId).set(islandMembers.get(lId).indexOf(l.getUniqueId().toString()), null);
		        							islandSettings.set(lId, islandSettings.get(playerId));
		        							invites.set(playerId, null);
		        							islandMembers.set(playerId, null);
		        							sender.sendMessage(ChatColor.GREEN + l.getName() + " has been made the new island leader.");
		        							l.sendMessage(ChatColor.GREEN + "You have been made the new island leader.");
		        							saveData();
		        							return true;
	        							} else {
	                            			sender.sendMessage(ChatColor.RED + "The player must be a member of your island to become leader of it!");
	                            			return true;
	        							}
            						} else {
            							sender.sendMessage(ChatColor.RED + "You are already the leader of your island!");
            							return true;
            						}
        						} else {
                        			sender.sendMessage(ChatColor.RED + "The player must be online to become leader!");
                        			return true;
        						}
        					} else {
                    			sender.sendMessage(ChatColor.RED + "Usage: /island makeleader <player>");
                    			return true;
        					}
        				} else {
                			sender.sendMessage(ChatColor.RED + "You must be the island leader to confer leadership!");
                			return true;
        				}
        			} else {
            			sender.sendMessage(ChatColor.RED + "You must have an island to make someone the leader of it!");
            			return true;
        			}
            	} else if (args[0].equalsIgnoreCase("level")) {
            		if (islandP1.get(playerId) != null) {
            			int isLevel = 0;
            			if (islandLeader.get(playerId) == null) {
            				isLevel = (int)Math.floor(Math.sqrt(islandPts.get(playerId))-9);
            			} else {
            				int lId = getPlayerId(UUID.fromString(islandLeader.get(playerId)));
            				isLevel = (int)Math.floor(Math.sqrt(islandPts.get(lId))-9);
            			}
            			if (isLevel < 0) {
            				isLevel = 0;
            			}
            			sender.sendMessage(ChatColor.GREEN + "Your island is level " + isLevel + ".");
	            		return true;
            		} else {
            			sender.sendMessage(ChatColor.RED + "You must have an island to check the level of it!");
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
        	if (USE_ECONOMY) {
	        	if (sender instanceof Player) {
	        		if (args.length == 0) {
		        		Player p = (Player) sender;
		        		int pId = getPlayerId(p.getUniqueId());
		        		DecimalFormat dec = new DecimalFormat("#0.00");
						sender.sendMessage("Balance: " + ChatColor.GREEN + "$" + dec.format(balances.get(pId)));
		        		return true;
	        		} else {
	        			Player p = getServer().getPlayer(args[0]);
	        			int pId;
	        			String pName;
	        			if (p != null) {
	    	        		pId = getPlayerId(p.getUniqueId());
	    	        		pName = p.getName();
	        			} else {
	        				OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
	        				pId = getPlayerId(op.getUniqueId());
	        				pName = op.getName();
	        			}
		        		DecimalFormat dec = new DecimalFormat("#0.00");
		        		Double balance = balances.get(pId);
		        		if (balance == null) {
		        			balance = 0.0;
		        		}
						sender.sendMessage("Balance of " + pName + ": " + ChatColor.GREEN + "$" + dec.format(balance));
	        			return true;
	        		}
	        	} else {
	        		sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
	        		return true;
	        	}
        	} else {
        		sender.sendMessage(ChatColor.RED + "Skyblock Economy is disabled!");
        		return true;
        	}
        } else if (command.getName().equalsIgnoreCase("pay")) {
        	if (USE_ECONOMY) {
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
        	} else {
        		sender.sendMessage(ChatColor.RED + "Skyblock Economy is disabled!");
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
        } else if (command.getName().equalsIgnoreCase("shout")) {
        	if (USE_CHATROOMS) {
	        	if (args.length > 0) {
	        		String m = "";
	        		for (int i = 0; i < args.length; i++) {
	        			m += args[i] + " ";
	        		}
	        		for (Player p: getServer().getOnlinePlayers()) {
	    				p.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "World" + ChatColor.GRAY + "] " + ChatColor.WHITE + sender.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + m);
	        		}
	        		getLogger().info("[World]" + sender.getName() + ": " + m);
	        		return true;
	        	} else {
	        		sender.sendMessage(ChatColor.RED + "Usage: /shout <message>");
	        		return true;
	        	}
        	} else {
        		sender.sendMessage(ChatColor.RED + "Chatrooms are disabled.");
        	}
        } else if (command.getName().equalsIgnoreCase("trade")) {
        	if (USE_ECONOMY) {
	        	if (sender instanceof Player) {
	        		Player p = (Player) sender;
		        	if (args.length > 0) {
		        		int pId = getPlayerId(p.getUniqueId());
	        			Player t = getServer().getPlayer(args[0]);
		        		if (args[0].equalsIgnoreCase("accept") || (t != null && t.getUniqueId().toString().equalsIgnoreCase(tradingRequests.get(pId)))) {
		        			String r = tradingRequests.get(pId);
		        			if (r != null) {
		        				t = getServer().getPlayer(UUID.fromString(r));
		        				if (t != null && t != p) {
		        					if (p.getLocation().distance(t.getLocation()) < 10) {
			        					tradingRequests.set(pId, null);
			        					Inventory tradeMenu = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "Trade");
			        					// Close menu button
			        					ItemStack closeMenu = new ItemStack(Material.BARRIER, 1);
			        					ItemMeta closeMeta = closeMenu.getItemMeta();
			        					closeMeta.setDisplayName(ChatColor.RED + "Close Menu");
			        					closeMenu.setItemMeta(closeMeta);
			        					tradeMenu.setItem(49, closeMenu);
			        					// Grey glass borders
			        					Material glassMat;
			        					if (Material.matchMaterial("160") == null) {
			        						glassMat = Material.getMaterial("STAINED_GLASS_PANE");
			        					} else {
			        						glassMat = Material.matchMaterial("160");
			        					}
			        					// all done for the sake of backwards compatibility :D
			        					ItemStack glassBorder = new ItemStack(glassMat, 1, (short)7);
			        					ItemMeta glassBorderMeta = glassBorder.getItemMeta();
			        					glassBorderMeta.setDisplayName(" ");
			        					glassBorder.setItemMeta(glassBorderMeta);
			        					for (int i = 0; i < 54; i++) {
			        						if (i != 49 && ((i >= 0 && i <= 8) || (i % 9 == 0 || i % 9 == 8 || i % 9 == 4) || (i >= 45))) {
			        							tradeMenu.setItem(i, glassBorder);
			        						}
			        					}
			        					// Accept buttons
			        					Material acceptMat;
			        					if (Material.matchMaterial("351") == null) {
			        						acceptMat = Material.getMaterial("INK_SACK");
			        					} else {
			        						acceptMat = Material.matchMaterial("351");
			        					}
			        					ItemStack acceptButton = new ItemStack(acceptMat, 1, (short)10);
			        					ItemMeta acceptButtonMeta = acceptButton.getItemMeta();
			        					acceptButtonMeta.setDisplayName(ChatColor.GREEN + "Accept Trade");
			        					acceptButton.setItemMeta(acceptButtonMeta);
			        					tradeMenu.setItem(39, acceptButton);
			        					tradeMenu.setItem(41, acceptButton);
			        					// Gold buttons
			        					Material moneyMat;
			        					if (Material.matchMaterial("266") == null) {
			        						moneyMat = Material.getMaterial("GOLD_INGOT");
			        					} else {
			        						moneyMat = Material.matchMaterial("266");
			        					}
			        					ItemStack moneyButton = new ItemStack(moneyMat, 1);
			        					ItemMeta moneyButtonMeta = moneyButton.getItemMeta();
			        					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $0.00");
			                    		List<String> lore = Arrays.asList(ChatColor.GRAY + "Shift left click: +$100", ChatColor.GRAY + "Shift right click: -$100", ChatColor.GRAY + "Left click: +$1", ChatColor.GRAY + "Right click: -$1");
			                    		moneyButtonMeta.setLore(lore);
			        					moneyButton.setItemMeta(moneyButtonMeta);
			        					tradeMenu.setItem(37, moneyButton);
			        					tradeMenu.setItem(43, moneyButton);
			        					p.openInventory(tradeMenu);
			        					t.openInventory(tradeMenu);
			        					openTrades.add(t.getUniqueId().toString() + ":" + p.getUniqueId().toString());
			        					return true;
		        					} else {
			        					sender.sendMessage(ChatColor.RED + "You aren't close enough to trade!");
			        					return true;
		        					}
		        				} else if (t == p) {
		        					sender.sendMessage(ChatColor.RED + "You can't trade with yourself!");
		        					tradingRequests.set(pId, null);
		        					return true;
		        				} else {
		        					sender.sendMessage(ChatColor.RED + "This player is no longer online.");
		        					tradingRequests.set(pId, null);
		        					return true;
		        				}
		        			} else {
		        				sender.sendMessage(ChatColor.RED + "You don't have any trade requests.");
		        				return true;
		        			}
		        		} else {
		        			if (t != null && t != p) {
	        					if (p.getLocation().distance(t.getLocation()) < 10) {
			        				int tId = getPlayerId(t.getUniqueId());
	        						if (tradingRequests.get(tId) == null || !tradingRequests.get(tId).equalsIgnoreCase(p.getUniqueId().toString())) {
				        				t.sendMessage(ChatColor.GREEN + sender.getName() + " has requested to trade with you. Type \"/trade accept\" to trade with them.");
				        				sender.sendMessage(ChatColor.GREEN + "You have successfully requested to trade with " + t.getName() + ".");
				        				tradingRequests.set(tId, p.getUniqueId().toString()); 
				        				return true;
	        						} else {
	        							sender.sendMessage(ChatColor.RED + "You have already requested to trade with this player!");
	        							return true;
	        						}
	        					} else {
		        					sender.sendMessage(ChatColor.RED + "You aren't close enough to trade!");
		        					return true;
	        					}
		        			} else if (t == p) {
	        					sender.sendMessage(ChatColor.RED + "You can't trade with yourself!");
	        					return true;
		        			} else {
		        				sender.sendMessage(ChatColor.RED + "The player must be online to trade!");
		        				return true;
		        			}
		        		}
		        	} else {
		        		sender.sendMessage(ChatColor.RED + "Usage: /trade <player>");
		        		return true;
		        	}
	        	} else {
	        		sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
	        		return true;
	        	}
        	} else {
        		sender.sendMessage(ChatColor.RED + "Skyblock Economy is disabled!");
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
	    			} else {
	    				Double addPts = 0.0;
	    				for (int i = 0; i < LEVEL_PTS.size(); i++) {
            				String btype = LEVEL_PTS.get(i).split(":")[0];
            				Double bpts = Double.valueOf(LEVEL_PTS.get(i).split(":")[1]);
            				if (btype.equalsIgnoreCase("Default")) {
            					addPts = bpts;
            				} else {
            					Material bmat = Material.getMaterial(btype);
            					if (bmat != null && e.getBlock().getType() == bmat) {
            						addPts = bpts;
            					} else if (bmat == null) {
            						getLogger().severe("Unknown block at ADD_PTS \"" + btype + "\"");
            					}
            				}
	    				}
	    				if (islandLeader.get(pId) == null) {
	    					islandPts.set(pId, islandPts.get(pId)+addPts);
	    				} else {
	    					int lId = getPlayerId(UUID.fromString(islandLeader.get(pId)));
	    					islandPts.set(lId, islandPts.get(lId)+addPts);
	    				}
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
	    			} else {
	    				Double addPts = 0.0;
	    				for (int i = 0; i < LEVEL_PTS.size(); i++) {
            				String btype = LEVEL_PTS.get(i).split(":")[0];
            				Double bpts = Double.valueOf(LEVEL_PTS.get(i).split(":")[1]);
            				if (btype.equalsIgnoreCase("Default")) {
            					addPts = bpts;
            				} else {
            					Material bmat = Material.getMaterial(btype);
            					if (bmat != null && e.getBlock().getType() == bmat) {
            						addPts = bpts;
            					} else if (bmat == null) {
            						getLogger().severe("Unknown block at ADD_PTS \"" + btype + "\"");
            					}
            				}
	    				}
	    				if (islandLeader.get(pId) == null) {
	    					islandPts.set(pId, islandPts.get(pId)+addPts);
	    				} else {
	    					int lId = getPlayerId(UUID.fromString(islandLeader.get(pId)));
	    					islandPts.set(lId, islandPts.get(lId)+addPts);
	    				}
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
	            	if (p != null) {
		            	if (
		            			(e.getEntity().getLocation().getBlockX() < islandP1.get(pId).getBlockX() || 
		            			e.getEntity().getLocation().getBlockX() > islandP2.get(pId).getBlockX() ||
		            			e.getEntity().getLocation().getBlockZ() < islandP1.get(pId).getBlockZ() || 
		            			e.getEntity().getLocation().getBlockZ() > islandP2.get(pId).getBlockZ()) && e.getEntity().getLocation().getWorld() == skyWorld && !(edbeEvent.getDamager().isOp())) {
		            		// make it so people cant hurt mobs on other people's islands
		            		e.setCancelled(true);
		            	} else {
		            		if (e.getEntity() instanceof LivingEntity && USE_ECONOMY) {
			            		if (edbeEvent.getDamage() >= ((LivingEntity)e.getEntity()).getHealth()) {
			            			double getMoney = 0;
			            			for (int i = 0; i < KILL_MONEY.size(); i++) {
			            				String etype = KILL_MONEY.get(i).split(":")[0];
			            				Double emoney = Double.valueOf(KILL_MONEY.get(i).split(":")[1]);
			            				if (etype.equalsIgnoreCase("Default")) {
			            					getMoney = emoney;
			            				} else if (etype.equalsIgnoreCase("Monster")) {
			            					if (e.getEntity() instanceof Monster) {
			            						getMoney = emoney;
			            					}
			            				} else if (etype.equalsIgnoreCase("Animals")) {
			            					if (e.getEntity() instanceof Animals) {
			            						getMoney = emoney;
			            					}
			            				} else {
			            					EntityType eT = EntityType.fromName(etype);
			            					if (eT != null && e.getEntityType() == eT) {
			            						getMoney = emoney;
			            					} else if (eT == null) {
			            						getLogger().severe("Unknown entity at KILL_MONEY \"" + etype + "\"");
			            					}
			            				}
			            			}
				            		balances.set(pId, balances.get(pId)+getMoney);
				    	    		DecimalFormat dec = new DecimalFormat("#0.00");
				            		p.sendMessage(ChatColor.GREEN + "You earned $" + dec.format(getMoney) + " for killing a " + e.getEntity().getType().getName().replace("_", " ") + ".");
			            		}
			            	}
		            	}
	            	}
	            }
	        }
    	}
    	if (e.getEntity() instanceof Player) {
    		Player p = (Player) e.getEntity();
        	int pId = getPlayerId(p.getUniqueId());
        	if (
        			(p.getLocation().getBlockX() < islandP1.get(pId).getBlockX() || 
        			p.getLocation().getBlockX() > islandP2.get(pId).getBlockX() ||
        			p.getLocation().getBlockZ() < islandP1.get(pId).getBlockZ() || 
        			p.getLocation().getBlockZ() > islandP2.get(pId).getBlockZ()) && e.getEntity().getLocation().getWorld() == skyWorld && !(p.isOp())) {
        		// make it so people are invincible on other people's islands
        		e.setCancelled(true);
        	}
    	}
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
    	Player p = e.getPlayer();
    	if (p != null) {
    		if (DISABLE_PLAYER_COLLISIONS) {
    			noCollideAddPlayer(p);
    		}
    		if (toClear.indexOf(p.getUniqueId().toString()) > -1) {
    			int playerId = getPlayerId(p.getUniqueId());
	            PlayerInventory inv = p.getInventory();
	            inv.clear();
	            inv.setArmorContents(new ItemStack[4]);
	            p.teleport(islandHome.get(playerId), TeleportCause.PLUGIN);
	            toClear.set(toClear.indexOf(p.getUniqueId().toString()), null);
	            saveData();
    		}
    	}
    }
    
    @EventHandler
    public void onFromTo(BlockFromToEvent e)
    {
    	if (e.getBlock().getWorld() == skyWorld) {
	    	BlockFace[] faces = new BlockFace[]
		        {
		            BlockFace.NORTH,
		            BlockFace.EAST,
		            BlockFace.SOUTH,
		            BlockFace.WEST
		        };
	        Material type = e.getBlock().getType();
	        if(type == Material.LAVA)
	        {
	            Block b = e.getToBlock();
	            Material toid = b.getType();
	            if (toid == Material.AIR) {
	            	for (BlockFace face : faces) {
	            		Block r = b.getRelative(face, 1);
	            		if (r.getType() == Material.WATER) {
	            			e.setCancelled(true);
	            			if (GENERATE_ORES) {
								double oreType = Math.random() * 100.0;
							    for(int i = 0; i < GENERATOR_ORES.size(); i++) {
							    	Material itemMat = Material.getMaterial(GENERATOR_ORES.get(i).split(":")[0]);
							    	Double itemChance = Double.parseDouble(GENERATOR_ORES.get(i).split(":")[1]);
							    	if (itemMat != null) {
							    		oreType -= itemChance;
							    		if (oreType < 0) {
							    			b.setType(itemMat);
							    			break;
							    		}
							    	} else {
							    		getLogger().severe("Unknown material \'" + GENERATOR_ORES.get(i).split(":")[0] + "\' at GENERATOR_ORES item " + (i + 1) + "!");
							    	}
							    }
	            			}
						    if (b.getType() == Material.AIR) {
						    	b.setType(Material.COBBLESTONE);
						    }
						    
						    Location bLoc = b.getLocation();

							for (int i = 0; i < players.size(); i++) {
								int pId = i;
								Location p1 = islandP1.get(pId);
								Location p2 = islandP2.get(pId);
								if (p1.getBlockX() < bLoc.getBlockX() && bLoc.getBlockX() < p2.getBlockX() && p1.getBlockZ() < bLoc.getBlockZ() && bLoc.getBlockZ() < p2.getBlockZ()) {
									Double addPts = 0.0;
				    				for (int ii = 0; ii < LEVEL_PTS.size(); ii++) {
			            				String btype = LEVEL_PTS.get(ii).split(":")[0];
			            				Double bpts = Double.valueOf(LEVEL_PTS.get(ii).split(":")[1]);
			            				if (btype.equalsIgnoreCase("Default")) {
			            					addPts = bpts;
			            				} else {
			            					Material bmat = Material.getMaterial(btype);
			            					if (bmat != null && b.getType() == bmat) {
			            						addPts = bpts;
			            					} else if (bmat == null) {
			            						getLogger().severe("Unknown block at ADD_PTS \"" + btype + "\"");
			            					}
			            				}
				    				}
									if (islandLeader.get(pId) == null) {
										islandPts.set(pId, islandPts.get(pId)+addPts);
									} else {
										int lId = getPlayerId(UUID.fromString(islandLeader.get(pId)));
										islandPts.set(lId, islandPts.get(lId)+addPts);
									}
									break;
								}
							}
	            		}
	            	}
	            }
	        }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
    	Player p = e.getPlayer();
    	int pId = getPlayerId(p.getUniqueId());
    	if (
    			(p.getLocation().getBlockX() < islandP1.get(pId).getBlockX() || 
    			p.getLocation().getBlockX() > islandP2.get(pId).getBlockX() ||
    			p.getLocation().getBlockZ() < islandP1.get(pId).getBlockZ() || 
    			p.getLocation().getBlockZ() > islandP2.get(pId).getBlockZ()) && e.getPlayer().getLocation().getWorld() == skyWorld && !(p.isOp())) {
    		e.setCancelled(true);
    	}
    }
    
	@EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
    	Player p = e.getPlayer();
    	int pId = getPlayerId(p.getUniqueId());
    	if (
    			(p.getLocation().getBlockX() < islandP1.get(pId).getBlockX() || 
    			p.getLocation().getBlockX() > islandP2.get(pId).getBlockX() ||
    			p.getLocation().getBlockZ() < islandP1.get(pId).getBlockZ() || 
    			p.getLocation().getBlockZ() > islandP2.get(pId).getBlockZ()) && e.getPlayer().getLocation().getWorld() == skyWorld && !(p.isOp())) {
    		e.setCancelled(true);
    	}
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (VOID_INSTANT_DEATH && p.getLocation().getY() < -10) {
			p.sendMessage(ChatColor.RED + "You fell into the void.");
			if (p.getBedSpawnLocation() != null) {
				p.teleport(p.getBedSpawnLocation(), TeleportCause.PLUGIN);
			} else {
				int pId = getPlayerId(p.getUniqueId());
				if (islandHome.get(pId) != null) {
					p.teleport(islandHome.get(pId), TeleportCause.PLUGIN);
				} else {
					p.teleport(skyWorld.getSpawnLocation(), TeleportCause.PLUGIN);
				}
			}
			p.setVelocity(new Vector(0,0,0));
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
			p.setFallDistance(0);
			int pId = getPlayerId(p.getUniqueId());
			if (balances.get(pId) != null && USE_ECONOMY) {
				Double loss = balances.get(pId) / 2;
	    		DecimalFormat dec = new DecimalFormat("#0.00");
				p.sendMessage(ChatColor.RED + "You died and lost $" + dec.format(loss));
				balances.set(pId, loss);
			}
			if (skyWorld.getGameRuleValue("keepInventory") != "true") {
				p.getInventory().clear();
				p.getInventory().setArmorContents(new ItemStack[4]);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Entity ent = e.getEntity();
		if (ent instanceof Player) {
			Player p = (Player) ent;
			int pId = getPlayerId(p.getUniqueId());
			if (balances.get(pId) != null && USE_ECONOMY) {
				Double loss = balances.get(pId) / 2;
	    		DecimalFormat dec = new DecimalFormat("#0.00");
				p.sendMessage(ChatColor.RED + "You died and lost $" + dec.format(loss));
				balances.set(pId, loss);
			}
		}
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		ProjectileSource ent = e.getEntity().getShooter();
		if (ent instanceof Player) {
			Player p = (Player) ent;
	    	int pId = getPlayerId(p.getUniqueId());
	    	if (
	    			(p.getLocation().getBlockX() < islandP1.get(pId).getBlockX() || 
	    			p.getLocation().getBlockX() > islandP2.get(pId).getBlockX() ||
	    			p.getLocation().getBlockZ() < islandP1.get(pId).getBlockZ() || 
	    			p.getLocation().getBlockZ() > islandP2.get(pId).getBlockZ()) && e.getEntity().getLocation().getWorld() == skyWorld && !(p.isOp())) {
	    		e.setCancelled(true);
	    	}
			
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (USE_CHATROOMS) {
			Player p = e.getPlayer();
			int pId = getPlayerId(p.getUniqueId());
			if (islandP1.get(pId) != null) {
				e.setCancelled(true);
				if (islandLeader.get(pId) == null) {
					for (int i = 0; i < islandMembers.get(pId).size(); i++) {
						Player isPlayer = getServer().getPlayer(UUID.fromString(islandMembers.get(pId).get(i)));
						if (isPlayer != null) {
							isPlayer.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "Island" + ChatColor.GRAY + "] " + ChatColor.WHITE + p.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + e.getMessage());
						}
					}
					p.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "Island" + ChatColor.GRAY + "] " + ChatColor.WHITE + p.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + e.getMessage());
				} else {
					UUID lUUID = UUID.fromString(islandLeader.get(pId));
					int lId = getPlayerId(lUUID);
					for (int i = 0; i < islandMembers.get(lId).size(); i++) {
						Player isPlayer = getServer().getPlayer(UUID.fromString(islandMembers.get(lId).get(i)));
						if (isPlayer != null) {
							isPlayer.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "Island" + ChatColor.GRAY + "] " + ChatColor.WHITE + p.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + e.getMessage());
						}
					}
					Player l = getServer().getPlayer(lUUID);
					if (l != null) {
						l.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "Island" + ChatColor.GRAY + "] " + ChatColor.WHITE + p.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + e.getMessage());
					}
				}
				getLogger().info(p.getName() + ": " + e.getMessage());
			} else {
				p.sendMessage(ChatColor.RED + "You must have an island to chat. Use \"/shout\" to chat with the whole server.");
			}
		}
	}
	
	@EventHandler
	private void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
		Material acceptMat;
		if (Material.matchMaterial("351") == null) {
			acceptMat = Material.getMaterial("INK_SACK");
		} else {
			acceptMat = Material.matchMaterial("351");
		}
		ItemStack acceptButtonU = new ItemStack(acceptMat, 1, (short)10);
		ItemMeta acceptMetaU = acceptButtonU.getItemMeta();
		acceptMetaU.setDisplayName(ChatColor.GREEN + "Accept Trade");
		acceptButtonU.setItemMeta(acceptMetaU);
        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_GREEN + "Trade") && !(e.getClickedInventory() == p.getInventory()) && !(p == null)) {
            String activeTrade = getActiveTrade(p.getUniqueId());
            Player p1 = getServer().getPlayer(UUID.fromString(activeTrade.split(":")[0]));
            Player p2 = getServer().getPlayer(UUID.fromString(activeTrade.split(":")[1]));
    		if (((e.getSlot() % 9 >= 1 && e.getSlot() % 9 <= 3 && p == p2) || (e.getSlot() % 9 >= 5 && e.getSlot() % 9 <= 7 && p == p1))) {
        		e.setCancelled(true);
        	} else if (e.getCurrentItem() != null) {
	        	String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
	        	if (itemName.equalsIgnoreCase(" ")) {
	        		e.setCancelled(true);
	        	} else if (itemName.equalsIgnoreCase(ChatColor.RED + "Close Menu")) {
	        		e.setCancelled(true);
        			Inventory p1Inventory = p1.getInventory();
        			Inventory p2Inventory = p2.getInventory();
        			for (int i = 0; i < 45; i++) {
        				if (i % 9 >= 1 && i % 9 <= 3 && i > 9 && i < 36 && i != 39) {
        					if (e.getInventory().getItem(i) != null) {
        						p1Inventory.addItem(e.getInventory().getItem(i));
        					}
        				}
        				if (i % 9 >= 5 && i % 9 <= 7 && i > 9 && i < 36 && i != 41) {
        					if (e.getInventory().getItem(i) != null) {
        						p2Inventory.addItem(e.getInventory().getItem(i));
        					}
        				}
        			}
        			closeTrade(activeTrade);
        			p1.closeInventory();
        			p2.closeInventory();
        			p1.sendMessage(ChatColor.RED + "The trade has been cancelled.");
        			p2.sendMessage(ChatColor.RED + "The trade has been cancelled.");
	        	} else if (e.getSlot() == 39 || e.getSlot() == 41) {
            		e.setCancelled(true);
            		Material aM;
					if (Material.matchMaterial("351") == null) {
						aM = Material.getMaterial("INK_SACK");
					} else {
						aM = Material.matchMaterial("351");
					}
					ItemStack acceptButton = new ItemStack(aM, 1, (short)2);
					ItemMeta acceptMeta = acceptButton.getItemMeta();
					acceptMeta.setDisplayName(ChatColor.GREEN + "Accepted!");
					acceptButton.setItemMeta(acceptMeta);
            		if (p == p1) {
            			if (itemName.equalsIgnoreCase(ChatColor.GREEN + "Accept Trade")) {
            				e.getInventory().setItem(39, acceptButton);
            			} else {
            				e.getInventory().setItem(39, acceptButtonU);
            			}
            		} else {
            			if (itemName.equalsIgnoreCase(ChatColor.GREEN + "Accept Trade")) {
            				e.getInventory().setItem(41, acceptButton);
            			} else {
            				e.getInventory().setItem(41, acceptButtonU);
            			}
            		}
            		if (e.getInventory().getItem(39).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Accepted!") && e.getInventory().getItem(41).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Accepted!")) {
            			e.getInventory().getItem(39).setAmount(3);
            			e.getInventory().getItem(41).setAmount(3);
            			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		    				public void run() {
		    					if (e.getInventory().getItem(39).getAmount() == 1 && e.getInventory().getItem(41).getAmount() == 1 && e.getInventory().getItem(39).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Accepted!") && e.getInventory().getItem(41).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Accepted!") && getActiveTrade(p.getUniqueId()) != null) {
		                			Inventory p1Inventory = p1.getInventory();
		                			Inventory p2Inventory = p2.getInventory();
		                			for (int i = 0; i < 45; i++) {
		                				if (i % 9 >= 1 && i % 9 <= 3 && i > 9 && i < 36 && i != 39) {
		                					if (e.getInventory().getItem(i) != null) {
		                						p2Inventory.addItem(e.getInventory().getItem(i));
		                					}
		                				}
		                				if (i % 9 >= 5 && i % 9 <= 7 && i > 9 && i < 36 && i != 41) {
		                					if (e.getInventory().getItem(i) != null) {
		                						p1Inventory.addItem(e.getInventory().getItem(i));
		                					}
		                				}
		                			}
		        					double p1Money = Double.valueOf(e.getInventory().getItem(37).getItemMeta().getDisplayName().substring(10));
		        					double p2Money = Double.valueOf(e.getInventory().getItem(43).getItemMeta().getDisplayName().substring(10));
		        					int p1Id = getPlayerId(p1.getUniqueId());
		        					int p2Id = getPlayerId(p2.getUniqueId());
		        					balances.set(p1Id, balances.get(p1Id) + (p2Money - p1Money));
		        					balances.set(p2Id, balances.get(p2Id) + (p1Money - p2Money));
		                			closeTrade(activeTrade);
		                			p1.closeInventory();
		                			p2.closeInventory();
		                			p1.sendMessage(ChatColor.GREEN + "The trade completed successfully.");
		                			p2.sendMessage(ChatColor.GREEN + "The trade completed successfully.");
		    					} else {
		    						
		    					}
							}
            			}, 60);
            			for (int i = 1; i < 3; i++) {
            				final int count = i;
	            		    this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
	            		    	public void run() {
	            		    		if (e.getInventory().getItem(39).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Accepted!") && e.getInventory().getItem(41).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Accepted!")) {
	            		    			e.getInventory().getItem(39).setAmount(3 - count);
	            		    			e.getInventory().getItem(41).setAmount(3 - count);
	            		    		}
	            		    	}
	            		    }, (20*(i)));
            			}
            		}
            	} else if (e.getSlot() == 37 || e.getSlot() == 43) {
					Material moneyMat;
					e.setCancelled(true);
					if (Material.matchMaterial("266") == null) {
						moneyMat = Material.getMaterial("GOLD_INGOT");
					} else {
						moneyMat = Material.matchMaterial("266");
					}
					ItemStack moneyButton = new ItemStack(moneyMat, 1);
					ItemMeta moneyButtonMeta = moneyButton.getItemMeta();
					int pId = getPlayerId(p.getUniqueId());
					double currentMoney = Double.valueOf(e.getCurrentItem().getItemMeta().getDisplayName().substring(10));
					double currentBalance = balances.get(pId);
		    		DecimalFormat dec = new DecimalFormat("#0.00");
            		if (e.isLeftClick()) {
            			if (e.isShiftClick()) {
            				if ((currentMoney + 100) <= currentBalance) {
            					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $" + dec.format((int)(currentMoney + 100)));
            				} else {
            					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $" + dec.format(Math.floor(currentBalance)));
            				}
            			} else {
            				if ((currentMoney + 1) <= currentBalance) {
            					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $" + dec.format((int)(currentMoney + 1)));
            				} else {
            					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $" + dec.format(Math.floor(currentBalance)));
            				}
            			}
            		} else if (e.isRightClick()) {
            			if (e.isShiftClick()) {
            				if (currentMoney > 100) {
            					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $" + dec.format((int)(currentMoney - 100)));
            				} else {
            					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $0.00");
            				}
            			} else {
            				if (currentMoney > 0) {
            					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $" + dec.format((int)(currentMoney - 1)));
            				} else {
            					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $0.00");
            				}
            			}
            		}
            		List<String> lore = Arrays.asList(ChatColor.GRAY + "Shift left click: +$100", ChatColor.GRAY + "Shift right click: -$100", ChatColor.GRAY + "Left click: +$1", ChatColor.GRAY + "Right click: -$1");
            		moneyButtonMeta.setLore(lore);
					moneyButton.setItemMeta(moneyButtonMeta);
					e.getInventory().setItem(e.getSlot(), moneyButton);
					e.getInventory().setItem(41, acceptButtonU);
					e.getInventory().setItem(39, acceptButtonU);
        		} else {
            		e.setCancelled(true);
        			e.getInventory().setItem(41, acceptButtonU);
        			e.getInventory().setItem(39, acceptButtonU);
        			p.getInventory().addItem(e.getCurrentItem());
        			e.getInventory().setItem(e.getSlot(), new ItemStack(Material.AIR,1));
            	}
        	}
        } else if (e.getClickedInventory() == p.getInventory() && e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_GREEN + "Trade")) {
    		String activeTrade = getActiveTrade(p.getUniqueId());
            Player p1 = getServer().getPlayer(UUID.fromString(activeTrade.split(":")[0]));
            e.setCancelled(true);
    		for (int i = 9; i < 36; i++) {
    			if (p == p1) {
    				if (i % 9 >= 1 && i % 9 <= 3) {
    					if (e.getInventory().getItem(i) == null || e.getInventory().getItem(i).getType() == Material.AIR) {
    						e.getInventory().setItem(i, e.getCurrentItem());
    						e.getClickedInventory().setItem(e.getSlot(), new ItemStack(Material.AIR,1));
    					}
    				}
    			} else {
    				if (i % 9 >= 5 && i % 9 <= 7) {
    					if (e.getInventory().getItem(i) == null || e.getInventory().getItem(i).getType() == Material.AIR) {
    						e.getInventory().setItem(i, e.getCurrentItem());
    						e.getClickedInventory().setItem(e.getSlot(), new ItemStack(Material.AIR,1));
    					}
    				}
    			}
    		}
			e.getInventory().setItem(41, acceptButtonU);
			e.getInventory().setItem(39, acceptButtonU);
    	}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_GREEN + "Trade") && !(p == null)) {
            String activeTrade = getActiveTrade(p.getUniqueId());
            if (activeTrade != null) {
	            Player p1 = getServer().getPlayer(UUID.fromString(activeTrade.split(":")[0]));
	            Player p2 = getServer().getPlayer(UUID.fromString(activeTrade.split(":")[1]));
				Inventory p1Inventory = p1.getInventory();
				Inventory p2Inventory = p2.getInventory();
				for (int i = 0; i < 45; i++) {
					if (i % 9 >= 1 && i % 9 <= 3 && i > 9 && i < 36 && i != 39) {
						if (e.getInventory().getItem(i) != null) {
							p1Inventory.addItem(e.getInventory().getItem(i));
						}
					}
					if (i % 9 >= 5 && i % 9 <= 7 && i > 9 && i < 36 && i != 41) {
						if (e.getInventory().getItem(i) != null) {
							p2Inventory.addItem(e.getInventory().getItem(i));
						}
					}
				}
				closeTrade(activeTrade);
				p1.closeInventory();
				p2.closeInventory();
    			p1.sendMessage(ChatColor.RED + "The trade has been cancelled.");
    			p2.sendMessage(ChatColor.RED + "The trade has been cancelled.");
            }
        }
	}
	
	@EventHandler
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
		Entity ent = e.getTarget();
		if (ent instanceof Player) {
			Player p = (Player) ent;
	    	int pId = getPlayerId(p.getUniqueId());
	    	if (
	    			(p.getLocation().getBlockX() < islandP1.get(pId).getBlockX() || 
	    			p.getLocation().getBlockX() > islandP2.get(pId).getBlockX() ||
	    			p.getLocation().getBlockZ() < islandP1.get(pId).getBlockZ() || 
	    			p.getLocation().getBlockZ() > islandP2.get(pId).getBlockZ()) && e.getEntity().getLocation().getWorld() == skyWorld && !(p.isOp())) {
	    		e.setCancelled(true);
	    	}
			
		}
	}
	
	@EventHandler
	public void onEntityMount(EntityMountEvent e) {
		Entity ent = e.getEntity();
		if (ent instanceof Player) {
			Player p = (Player) ent;
	    	int pId = getPlayerId(p.getUniqueId());
	    	if (
	    			(p.getLocation().getBlockX() < islandP1.get(pId).getBlockX() || 
	    			p.getLocation().getBlockX() > islandP2.get(pId).getBlockX() ||
	    			p.getLocation().getBlockZ() < islandP1.get(pId).getBlockZ() || 
	    			p.getLocation().getBlockZ() > islandP2.get(pId).getBlockZ()) && e.getEntity().getLocation().getWorld() == skyWorld && !(p.isOp())) {
	    		e.setCancelled(true);
	    	}
			
		}
	}
	
	public String getActiveTrade(UUID playerUUID) {
		for (int i = 0; i < openTrades.size(); i++) {
			if (openTrades.get(i) != null && openTrades.get(i).contains(playerUUID.toString())) {
				return openTrades.get(i);
			}
		}
		return null;
	}
    
    public void closeTrade(String _key) {
    	openTrades.set(openTrades.indexOf(_key), null);
    }
    
    public void saveData() {
    	if (!config.isSet("WORLD")) {
    		config.set("WORLD", "world");
    	}
    	config.set("CHAT_PREFIX", CHAT_PREFIX);
    	config.set("ISLAND_HEIGHT", ISLAND_HEIGHT);
    	config.set("ISLAND_WIDTH", ISLAND_WIDTH);
    	config.set("ISLAND_DEPTH", ISLAND_DEPTH);
    	config.set("LIMIT_X", limX);
    	config.set("LIMIT_Z", limZ);
    	config.set("STARTING_MONEY", STARTING_MONEY);
    	config.set("GENERATE_ORES", GENERATE_ORES);
    	config.set("INFINITE_RESETS", INFINITE_RESETS);
    	config.set("USE_CHATROOMS", USE_CHATROOMS);
    	config.set("USE_ECONOMY", USE_ECONOMY);
    	config.set("VOID_INSTANT_DEATH", VOID_INSTANT_DEATH);
    	config.set("DISABLE_PLAYER_COLLISIONS", DISABLE_PLAYER_COLLISIONS);
    	config.set("CHEST_ITEMS", CHEST_ITEMS);
    	config.set("GENERATOR_ORES", GENERATOR_ORES);
    	config.set("KILL_MONEY", KILL_MONEY);
    	config.set("LEVEL_PTS", LEVEL_PTS);
    	for (int i = 0; i < players.size(); i++) {
    		if (islandP1.get(i) != null) {
		    	config.set("data.players." + players.get(i) + ".islandP1.x", islandP1.get(i).getBlockX());
		    	config.set("data.players." + players.get(i) + ".islandP1.y", islandP1.get(i).getBlockY());
		    	config.set("data.players." + players.get(i) + ".islandP1.z", islandP1.get(i).getBlockZ());
    		} else {
		    	config.set("data.players." + players.get(i) + ".islandP1.x", null);
		    	config.set("data.players." + players.get(i) + ".islandP1.y", null);
		    	config.set("data.players." + players.get(i) + ".islandP1.z", null);
    		}
    		if (islandP2.get(i) != null) {
		    	config.set("data.players." + players.get(i) + ".islandP2.x", islandP2.get(i).getBlockX());
		    	config.set("data.players." + players.get(i) + ".islandP2.y", islandP2.get(i).getBlockY());
		    	config.set("data.players." + players.get(i) + ".islandP2.z", islandP2.get(i).getBlockZ());
    		} else {
		    	config.set("data.players." + players.get(i) + ".islandP2.x", null);
		    	config.set("data.players." + players.get(i) + ".islandP2.y", null);
		    	config.set("data.players." + players.get(i) + ".islandP2.z", null);
    		}
    		if (islandHome.get(i) != null) {
		    	config.set("data.players." + players.get(i) + ".islandHome.x", islandHome.get(i).getX());
		    	config.set("data.players." + players.get(i) + ".islandHome.y", islandHome.get(i).getY());
		    	config.set("data.players." + players.get(i) + ".islandHome.z", islandHome.get(i).getZ());
		    	config.set("data.players." + players.get(i) + ".islandHome.pitch", (int)islandHome.get(i).getPitch());
		    	config.set("data.players." + players.get(i) + ".islandHome.yaw", (int)islandHome.get(i).getYaw());
    		} else {
		    	config.set("data.players." + players.get(i) + ".islandHome.x", null);
		    	config.set("data.players." + players.get(i) + ".islandHome.y", null);
		    	config.set("data.players." + players.get(i) + ".islandHome.z", null);
		    	config.set("data.players." + players.get(i) + ".islandHome.pitch", null);
		    	config.set("data.players." + players.get(i) + ".islandHome.yaw", null);
    		}
    		if (islandSettings.get(i) != null) {
    			config.set("data.players." + players.get(i) + ".settings.resetLeft", islandSettings.get(i).get(0));
    			config.set("data.players." + players.get(i) + ".settings.allowsVisitors", islandSettings.get(i).get(1));
    		} else {
    			config.set("data.players." + players.get(i) + ".settings.resetLeft", null);
    			config.set("data.players." + players.get(i) + ".settings.allowsVisitors", null);
    		}
    		config.set("data.players." + players.get(i) + ".balance", balances.get(i));
    		config.set("data.players." + players.get(i) + ".islandLeader", islandLeader.get(i));
    		config.set("data.players." + players.get(i) + ".islandMembers", islandMembers.get(i));
    		config.set("data.players." + players.get(i) + ".islandPts", islandPts.get(i));
    	}
    	config.set("data.nextIsland.x", nextIsland.getBlockX());
    	config.set("data.nextIsland.y", nextIsland.getBlockY());
    	config.set("data.nextIsland.z", nextIsland.getBlockZ());
    	config.set("data.toClear", toClear);
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
	    					if (GENERATE_ORES) { // spice things up
	    						double oreType = Math.random() * 100.0;
	    					    for(int i = 0; i < GENERATOR_ORES.size(); i++) {
	    					    	Material itemMat = Material.getMaterial(GENERATOR_ORES.get(i).split(":")[0]);
	    					    	Double itemChance = Double.parseDouble(GENERATOR_ORES.get(i).split(":")[1]);
	    					    	if (itemMat != null) {
	    					    		oreType -= itemChance;
	    					    		if (oreType < 0) {
	    					    			currentBlock.setType(itemMat);
	    					    			break;
	    					    		}
	    					    	} else {
	    					    		getLogger().severe("Unknown material \'" + GENERATOR_ORES.get(i).split(":")[0] + "\' at GENERATOR_ORES item " + (i + 1) + "!");
	    					    	}
	    					    }
	    					    if (currentBlock.getType() == Material.AIR) {
	    					    	currentBlock.setType(Material.COBBLESTONE);
	    					    }
	    					} else {
	    						currentBlock.setType(Material.DIRT);
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
	    for(int i = 0; i < CHEST_ITEMS.size(); i++) {
	    	Material itemMat = Material.getMaterial(CHEST_ITEMS.get(i).split(":")[0]);
	    	int itemCnt = Integer.parseInt(CHEST_ITEMS.get(i).split(":")[1]);
	    	if (itemMat != null) {
	    		chestinv.addItem(new ItemStack(itemMat,itemCnt));
	    	} else {
	    		getLogger().severe("Unknown material \'" + CHEST_ITEMS.get(i).split(":")[0] + "\' at CHEST_ITEMS item " + (i + 1) + "!");
	    	}
	    }
	    
	    // teleport player
	    p.setFallDistance(0);
	    p.teleport(homeLoc, TeleportCause.PLUGIN);
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
	    islandPts.set(pId, 100.0);
	    
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
		// run garbage collector if there is enough garbage in memory
		if (Bukkit.getOnlinePlayers().size() > players.size()*1.25) {
			garbageCollector();
		}
		
		int pId = players.indexOf(uuid.toString());
	    while (pId == -1) {
	    	if (config.isSet("data.players." + uuid.toString())) {
	    		players.add(uuid.toString());
	    		if (config.isSet("data.players." + uuid.toString() + ".islandP1.x")) {
		    		islandP1.add(new Location(
		    				skyWorld,
		    				config.getInt("data.players." + uuid.toString() + ".islandP1.x"),
		    				config.getInt("data.players." + uuid.toString() + ".islandP1.y"),
		    				config.getInt("data.players." + uuid.toString() + ".islandP1.z")));
	    		} else {
	    			islandP1.add(null);
	    		}
	    		if (config.isSet("data.players." + uuid.toString() + ".islandP2.x")) {
		    		islandP2.add(new Location(
		    				skyWorld,
		    				config.getInt("data.players." + uuid.toString() + ".islandP2.x"),
		    				config.getInt("data.players." + uuid.toString() + ".islandP2.y"),
		    				config.getInt("data.players." + uuid.toString() + ".islandP2.z")));
	    		} else {
	    			islandP2.add(null);
	    		}
	    		if (config.isSet("data.players." + uuid.toString() + ".islandHome.x")) {
		    		islandHome.add(new Location(
		    				skyWorld,
		    				config.getDouble("data.players." + uuid.toString() + ".islandHome.x"),
		    				config.getDouble("data.players." + uuid.toString() + ".islandHome.y"),
		    				config.getDouble("data.players." + uuid.toString() + ".islandHome.z"),
		    				config.getInt("data.players." + uuid.toString() + ".islandHome.yaw"),
		    				config.getInt("data.players." + uuid.toString() + ".islandHome.pitch")));
	    		} else {
	    			islandHome.add(null);
	    		}
	    		balances.add(config.getDouble("data.players." + uuid.toString() + ".balance"));
	    		islandSettings.add(new ArrayList<Boolean>());
	    		islandSettings.get(islandSettings.size()-1).add(config.getBoolean("data.players." + uuid.toString() + ".settings.resetLeft"));
	    		islandSettings.get(islandSettings.size()-1).add(config.getBoolean("data.players." + uuid.toString() + ".settings.allowsVisitors"));
	    		islandLeader.add(config.getString("data.players." + uuid.toString() + ".islandLeader"));
	    		invites.add(null);
	    		tradingRequests.add(null);
	    		islandMembers.add(config.getStringList("data.players." + uuid.toString() + ".islandMembers"));
	    		islandPts.add(config.getDouble("data.players." + uuid.toString() + ".islandPts"));
	    	} else {
		    	players.add(uuid.toString());
		    	islandP1.add(null);
		    	islandP2.add(null);
		    	islandHome.add(null);
		    	balances.add(null);
	    		islandSettings.add(new ArrayList<Boolean>());
	    		islandSettings.get(islandSettings.size()-1).add(null); // resets left
	    		islandSettings.get(islandSettings.size()-1).add(null); // allows visitors
	    		islandLeader.add(null);
	    		invites.add(null);
	    		tradingRequests.add(null);
	    		islandMembers.add(new ArrayList<String>());
	    		islandPts.add(0.0);
	    	}
	    	pId = getPlayerId(uuid);
	    }
		return pId;
	}
	
	public void garbageCollector() {
		saveData();
		List<String> tplayers = new ArrayList<String>();
		List<Location> tislandP1 = new ArrayList<Location>();
		List<Location> tislandP2 = new ArrayList<Location>();
		List<Location> tislandHome = new ArrayList<Location>();
		List<Double> tbalances = new ArrayList<Double>();
		List<List<Boolean>> tislandSettings = new ArrayList<List<Boolean>>();
		List<Integer> tinvites = new ArrayList<Integer>();
		List<String> tislandLeader = new ArrayList<String>();
		List<List<String>> tislandMembers = new ArrayList<List<String>>();
		List<String> ttradingRequests = new ArrayList<String>();
		for (int i = 0; i < players.size(); i++) {
			UUID tempP = UUID.fromString(players.get(i));
			if (getServer().getPlayer(tempP) != null) {
				tplayers.add(players.get(i));
				tislandP1.add(islandP1.get(i));
				tislandP2.add(islandP2.get(i));
				tislandHome.add(islandHome.get(i));
				tbalances.add(balances.get(i));
				tislandSettings.add(islandSettings.get(i));
				tinvites.add(invites.get(i));
				tislandLeader.add(islandLeader.get(i));
				tislandMembers.add(islandMembers.get(i));
				ttradingRequests.add(tradingRequests.get(i));
			}
		}
		players = tplayers;
		islandP1 = tislandP1;
		islandP2 = tislandP2;
		islandHome = tislandHome;
		balances = tbalances;
		islandSettings = tislandSettings;
		invites = tinvites;
		islandLeader = tislandLeader;
		islandMembers = tislandMembers;
		tradingRequests = ttradingRequests;
	}
	
    public static void noCollideAddPlayer(Player p) {
        ScoreboardManager sm = Bukkit.getScoreboardManager();
        Scoreboard board = sm.getNewScoreboard();
        String rand = (""+Math.random()).substring(0, 3);
        Team team = board.registerNewTeam("no_collision"+rand);
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.addEntry(p.getName());
        p.setScoreboard(board);
    }
    
    public static int getNotNullLength(List<String> _arr) {
    	int actualLength = 0;
    	for (int i = 0; i < _arr.size(); i++) {
    		if (_arr.get(i) != null) {
    			actualLength++;
    		}
    	}
    	return actualLength;
    }
}