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
import org.bukkit.event.player.PlayerJoinEvent;
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
	List<Integer> invites = new ArrayList<Integer>();
	List<String> islandLeader = new ArrayList<String>();
	List<List<String>> islandMembers = new ArrayList<List<String>>();
	List<String> toClear = new ArrayList<String>();
	
	// settings:
	String CHAT_PREFIX = ChatColor.GRAY + "[" + ChatColor.AQUA + "SKYBLOCK" + ChatColor.GRAY + "] " + ChatColor.RESET;
	int ISLAND_HEIGHT = 70;
	int ISLAND_WIDTH = 400;
	int ISLAND_DEPTH = 400;
	Boolean GENERATE_ORES = true;
	Boolean INFINITE_RESETS = false;
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
    	if (config.isSet("GENERATE_ORES")) {
    		GENERATE_ORES = config.getBoolean("GENERATE_ORES");
    	}
    	if (config.isSet("INFINITE_RESETS")) {
    		INFINITE_RESETS = config.getBoolean("INFINITE_RESETS");
    	}
    	if (config.isSet("CHEST_ITEMS")) {
    		CHEST_ITEMS = config.getStringList("CHEST_ITEMS");
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
            		if (args.length > 1 && args[1].equalsIgnoreCase("2")) {
                		sender.sendMessage(ChatColor.GREEN + "Island commands (2/2)" + ChatColor.GRAY + ": ");
                		sender.sendMessage(ChatColor.GRAY + "usage: /is [args]\n");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is join <player>" + ChatColor.RESET + ": join a player\'s island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is leave" + ChatColor.RESET + ": leave a co-op island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is kick <player>" + ChatColor.RESET + ": kick a member of your island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is list" + ChatColor.RESET + ": list members of your island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is makeleader <player>" + ChatColor.RESET + ": make another player leader of the island.");
            			return true;
            		} else {
                		sender.sendMessage(ChatColor.GREEN + "Island commands (1/2)" + ChatColor.GRAY + ": ");
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
	        			p.teleport(islandHome.get(playerId));
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
						            			tempP.teleport(islandHome.get(tempUID));
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
	            			@SuppressWarnings("deprecation")
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
            			String leaderUUID = config.getString("data.players." + pUUID + ".islandLeader");
            			Boolean allowsVisitors;
            			if (leaderUUID == null) {
            				allowsVisitors = islandSettings.get(pId).get(1);
            			} else {
            				allowsVisitors = config.getBoolean("data.players." + leaderUUID + ".settings.allowsVisitors");
            			}
            			if ((islandHome.get(pId) != null && !(p.getName().equalsIgnoreCase(args[1])) && allowsVisitors) || p.isOp()) {
                			p.teleport(islandHome.get(pId));
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
            			sender.sendMessage(ChatColor.RED + "You must have an island to change it\'s settings!");
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
            			if (j != null) {
            				int jId = getPlayerId(j.getUniqueId());
            				if (invites.get(jId) != null && invites.get(jId) == playerId) {
            					invites.set(jId, null);
            					islandP1.set(playerId, islandP1.get(jId));
            					islandP2.set(playerId, islandP2.get(jId));
            					islandHome.set(playerId, islandHome.get(jId));
            					islandSettings.get(playerId).set(1, islandSettings.get(jId).get(1));
            					balances.set(playerId, 0.0); // set to 0 to prevent abuse.
    				            PlayerInventory inv = p.getInventory();
    				            inv.clear();
    				            inv.setArmorContents(new ItemStack[4]);
    				            p.teleport(islandHome.get(playerId));
    				    	    p.setBedSpawnLocation(islandHome.get(playerId), true);
                    			sender.sendMessage(ChatColor.GREEN + "You have successfully joined " + j.getName() + "\'s island.");
                    			j.sendMessage(ChatColor.GREEN + sender.getName() + " has successfully joined your island.");
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
            						@SuppressWarnings("deprecation")
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
        						if (islandLeader.get(pId).equalsIgnoreCase(p.getUniqueId().toString())) {
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
        				sender.sendMessage(ChatColor.DARK_GRAY + "- " + leaderColor + leaderName);
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
	        							sender.sendMessage(ChatColor.GREEN + l.getName() + " has been made the new island leader.");
	        							l.sendMessage(ChatColor.GREEN + "You have been made the new island leader.");
	        							saveData();
	        							return true;
        							} else {
                            			sender.sendMessage(ChatColor.RED + "The a member of your island to become leader of it.");
                            			return true;
        							}
        						} else {
                        			sender.sendMessage(ChatColor.RED + "The player must be online to become leader.");
                        			return true;
        						}
        					} else {
                    			sender.sendMessage(ChatColor.RED + "Usage: /island makeleader <player>");
                    			return true;
        					}
        				} else {
                			sender.sendMessage(ChatColor.RED + "You must be the island leader to confer leadership.");
                			return true;
        				}
        			} else {
            			sender.sendMessage(ChatColor.RED + "You must have an island to make someone the leader of it!");
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
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
    	Player p = e.getPlayer();
    	if (p != null) {
    		if (toClear.indexOf(p.getUniqueId().toString()) > -1) {
    			int playerId = getPlayerId(p.getUniqueId());
	            PlayerInventory inv = p.getInventory();
	            inv.clear();
	            inv.setArmorContents(new ItemStack[4]);
	            p.teleport(islandHome.get(playerId));
	            toClear.set(toClear.indexOf(p.getUniqueId().toString()), null);
	            saveData();
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
    	config.set("GENERATE_ORES", GENERATE_ORES);
    	config.set("INFINITE_RESETS", INFINITE_RESETS);
    	config.set("CHEST_ITEMS", CHEST_ITEMS);
    	if (!config.isSet("WORLD")) {
    		config.set("WORLD", "world");
    	}
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
		    	config.set("data.players." + players.get(i) + ".islandHome.x", islandHome.get(i).getBlockX());
		    	config.set("data.players." + players.get(i) + ".islandHome.y", islandHome.get(i).getBlockY());
		    	config.set("data.players." + players.get(i) + ".islandHome.z", islandHome.get(i).getBlockZ());
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
	    		getLogger().severe("Unknown material \'" + CHEST_ITEMS.get(i).split(":")[0] + "\'!");
	    	}
	    }
	    
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
	    		islandMembers.add(config.getStringList("data.players." + uuid.toString() + ".islandMembers"));
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
	    		islandMembers.add(new ArrayList<String>());
	    	}
	    	pId = getPlayerId(uuid);
	    }
		return pId;
	}
    
}
