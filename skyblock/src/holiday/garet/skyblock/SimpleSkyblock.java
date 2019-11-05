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

package holiday.garet.skyblock;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityMountEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import holiday.garet.skyblock.economy.Economy;
import holiday.garet.skyblock.economy.Trade;
import holiday.garet.skyblock.economy.TradeRequest;

@SuppressWarnings("deprecation")
public class SimpleSkyblock extends JavaPlugin implements Listener {
	FileConfiguration config = this.getConfig();
	FileConfiguration data = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "data.yml"));
	
	List<String> players = new ArrayList<String>();
	List<Location> islandP1 = new ArrayList<Location>();
	List<Location> islandP2 = new ArrayList<Location>();
	List<Location> islandHome = new ArrayList<Location>();
	List<List<Boolean>> islandSettings = new ArrayList<List<Boolean>>();
	List<Integer> invites = new ArrayList<Integer>();
	List<String> islandLeader = new ArrayList<String>();
	List<List<String>> islandMembers = new ArrayList<List<String>>();
	List<TradeRequest> tradingRequests = new ArrayList<TradeRequest>();
	List<Double> islandPts = new ArrayList<Double>();
	
	List<Trade> openTrades = new ArrayList<Trade>();
	
	List<String> toClear = new ArrayList<String>();
	
	Location nextIsland;
	
	World skyWorld;
	
    @Override
    public void onEnable() {
    	Bukkit.getPluginManager().registerEvents(this, this);
    	if (config.isSet("data")) {
    		// if the config is out of date, lets fix that.
    		getLogger().info("Updating the config file from 'v1.2.0' to 'v1.2.1'...");
    		convertConfig("1.2.0","1.2.1");
    	}
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        config = this.getConfig();
    	loadConfig();
		final String worldName;
    	if (config.isSet("WORLD")) {
    		worldName = config.getString("WORLD");
    	} else {
    		worldName = "world";
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
    	toClear = data.getStringList("data.toClear");
    	if (data.isSet("data.nextIsland")) {
    		nextIsland = new Location(skyWorld, data.getInt("data.nextIsland.x"), config.getInt("ISLAND_HEIGHT"), config.getInt("data.nextIsland.z"));
    	} else {
    		nextIsland = new Location(skyWorld, -config.getInt("LIMIT_X"), config.getInt("ISLAND_HEIGHT"),-config.getInt("LIMIT_Z"));
    	}
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
    	try {
			data.save(getDataFolder() + File.separator + "data.yml");
		} catch (IOException e) {
            Bukkit.getLogger().warning("§4Could not save data.yml file.");
		}
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
			            sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "Generating island...");
			            generateIsland(nextIsland, p);
			            return true;
	        		} else {
	        			sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "Teleporting you to your island...");
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
	        			sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "Teleporting you to your island...");
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
		            		sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "Your island home was set to your current location.");
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
		            			if (islandSettings.get(playerId).get(0) != false || config.getBoolean("INFINITE_RESETS")) {
						            generateIsland(nextIsland, p);
						            for (int i = 0; i < islandMembers.get(playerId).size(); i++) {
						            	if (islandMembers.get(playerId).get(i) != null) {
						            		UUID tempUUID = UUID.fromString(islandMembers.get(playerId).get(i));
						            		Player tempP = getServer().getPlayer(tempUUID);
						            		int tempUID = getPlayerId(tempUUID);
						            		islandP1.set(tempUID, islandP1.get(playerId));
						            		islandP2.set(tempUID, islandP2.get(playerId));
						            		islandHome.set(tempUID, islandHome.get(playerId));
						            		Economy tempEcon = new Economy(tempUUID, data);
						            		tempEcon.set(0);
						            		if (tempP == null) {
						            			toClear.add(tempUUID.toString());
						            		} else {
						            			PlayerInventory tempInv = tempP.getInventory();
						            			tempInv.clear();
						            			tempInv.setArmorContents(new ItemStack[4]);
						            			tempP.teleport(islandHome.get(tempUID), TeleportCause.PLUGIN);
						            			tempP.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "Your island was reset successfully.");
						            		}
						            	}
						            }
						            PlayerInventory inv = p.getInventory();
						            inv.clear();
						            inv.setArmorContents(new ItemStack[4]);
						            sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "Your island was reset successfully.");
			            			return true;
		            			} else {
		            				sender.sendMessage(ChatColor.RED + "To prevent abuse, you may only reset your island once at this time. Please contact a server admin if you have a problem.");
		            				return true;
		            			}
		            		} else {
		            			sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "Are you sure? This action is irreversable! This will reset your inventory, balance, and island as well as those of all the island\'s members. Type \"/is reset confirm\" to continue.");
		            			if (!config.getBoolean("INFINITE_RESETS")) {
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
                			sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "Teleported you to " + pName + "\'s island.");
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
			            					sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "\'allow-visitors\' was set to true.");
			            				} else if (args[2].equalsIgnoreCase("false")) {
			            					islandSettings.get(playerId).set(1, false);
			            					sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "\'allow-visitors\' was set to false.");
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
	            					
	            					Economy econ = new Economy(p.getUniqueId(), data);
	            					econ.set(0); // set to 0 to prevent abuse.
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
            	} else if (args[0].equalsIgnoreCase("config")) {
            		if (sender.isOp()) {
            			if (args.length > 1 && args[1].equalsIgnoreCase("reload")) {
            				loadConfig();
            				sender.sendMessage(ChatColor.GREEN + "Skyblock config has been reloaded successfully.");
            				return true;
            			} else {
                			sender.sendMessage(ChatColor.RED + "Usage: /is config <reload>");
                			return true;
            			}
            		} else {
            			sender.sendMessage(ChatColor.RED + "You must be a server operator to use this command.");
            			return true;
            		}
        		} else if (args[0].equalsIgnoreCase("allowreset")) {
        			if (sender.isOp()) {
        				if (args.length > 1) {
        					UUID tuuid;
        					Player t = getServer().getPlayer(args[1]);
        					if (t != null) {
        						tuuid = t.getUniqueId();
        					} else {
        						OfflinePlayer ot = getServer().getOfflinePlayer(args[1]);
        						tuuid = ot.getUniqueId();
        					}
        					if (tuuid != null) {
        						int tid = getPlayerId(tuuid);
        						islandSettings.get(tid).set(0,true);
                    			sender.sendMessage(ChatColor.GREEN + "The player can now reset their island again.");
                    			return true;
        					}
        				} else {
                			sender.sendMessage(ChatColor.RED + "Usage: /is allowreset <player>");
                			return true;
        				}
        			} else {
            			sender.sendMessage(ChatColor.RED + "You must be a server operator to use this command.");
            			return true;
            		}
        		} else {
            		sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "Unknown command. Type \'/is help\' for a list of commands.");
            		return true;
            	}
        	} else {
        		sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
	            return true;
        	}
        } else if (command.getName().equalsIgnoreCase("bal") || command.getName().equalsIgnoreCase("balance")) {
        	if (config.getBoolean("USE_ECONOMY")) {
	        	if (sender instanceof Player) {
	        		if (args.length == 0) {
		        		Player p = (Player) sender;
		        		Economy econ = new Economy(p.getUniqueId(), data);
		        		DecimalFormat dec = new DecimalFormat("#0.00");
						sender.sendMessage("Balance: " + ChatColor.GREEN + "$" + dec.format(econ.get()));
		        		return true;
	        		} else {
	        			Player p = getServer().getPlayer(args[0]);
		        		Economy econ;
	        			String pName;
	        			if (p != null) {
	    	        		econ = new Economy(p.getUniqueId(), data);
	    	        		pName = p.getName();
	        			} else {
	        				OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
	    	        		econ = new Economy(op.getUniqueId(), data);
	        				pName = op.getName();
	        			}
		        		DecimalFormat dec = new DecimalFormat("#0.00");
						sender.sendMessage("Balance of " + pName + ": " + ChatColor.GREEN + "$" + dec.format(econ.get()));
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
        	if (config.getBoolean("USE_ECONOMY")) {
	        	if (sender instanceof Player) {
	        		Player p = (Player) sender;
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
	        			    Economy pecon = new Economy(p.getUniqueId(), data);
	        				if (payAmount <= pecon.get() && payAmount > 0) {
	        					pecon.withdraw(payAmount);
	        					Economy recon = new Economy(r.getUniqueId(), data);
	        					recon.deposit(payAmount);
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
        	if (config.getBoolean("USE_CHATROOMS")) {
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
        	if (config.getBoolean("USE_ECONOMY")) {
	        	if (sender instanceof Player) {
	        		Player p = (Player) sender;
		        	if (args.length > 0) {
		        		int pId = getPlayerId(p.getUniqueId());
	        			Player t = getServer().getPlayer(args[0]);
	        			TradeRequest trec = getTradeRequest(p,t);
		        		if (args[0].equalsIgnoreCase("accept") || (trec != null && trec.from() == t)) {
		        			if (trec == null) {
		        				trec = getTradeRequestToPlayer(p);
		        			}
		        			if (trec != null) {
		        				t = trec.from();
		        				if (t != null && t != p) {
		        					if (p.getLocation().distance(t.getLocation()) < 10) {
			        					Trade trade = new Trade(p, t);
			        					trade.open();
			        					trec.close();
			        					openTrades.add(trade);
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
			        				TradeRequest trt = getTradeRequestToPlayer(t);
	        						if (trt == null || trt.from() != p) {
				        				TradeRequest treq = new TradeRequest(p,t,this);
				        				tradingRequests.add(treq);
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
	    			}
	    		} else {
	    			e.setCancelled(true);
	    		}
    		}
    		if (!e.isCancelled()) {
				Double addPts = 0.0;
				List<String> LEVEL_PTS = config.getStringList("LEVEL_PTS");
				for (int i = 0; i < LEVEL_PTS.size(); i++) {
    				String btype = LEVEL_PTS.get(i).split(":")[0];
    				Double bpts = Double.valueOf(LEVEL_PTS.get(i).split(":")[1]);
    				if (btype.equalsIgnoreCase("Default")) {
    					addPts = bpts;
    				} else {
    					Material bmat = XMaterial.matchXMaterial(btype).parseMaterial();
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
    		if (!e.isCancelled()) {
				Double addPts = 0.0;
				List<String> LEVEL_PTS = config.getStringList("LEVEL_PTS");
				for (int i = 0; i < LEVEL_PTS.size(); i++) {
    				String btype = LEVEL_PTS.get(i).split(":")[0];
    				Double bpts = Double.valueOf(LEVEL_PTS.get(i).split(":")[1]);
    				if (btype.equalsIgnoreCase("Default")) {
    					addPts = bpts;
    				} else {
    					Material bmat = XMaterial.matchXMaterial(btype).parseMaterial();
    					if (bmat != null && e.getBlock().getType() == bmat) {
    						addPts = bpts;
    					} else if (bmat == null) {
    						getLogger().severe("Unknown block at ADD_PTS \"" + btype + "\"");
    					}
    				}
				}
				if (islandLeader.get(pId) == null) {
					islandPts.set(pId, islandPts.get(pId)-addPts);
				} else {
					int lId = getPlayerId(UUID.fromString(islandLeader.get(pId)));
					islandPts.set(lId, islandPts.get(lId)-addPts);
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
    	if (!e.isCancelled() && config.getBoolean("BONEMEAL_DOES_MORE") && config.getBoolean("USE_CUSTOM_MECHANICS")) {
			if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getHand().equals(EquipmentSlot.HAND)) {
				if (b.getType() == XMaterial.DIRT.parseMaterial() && b.getRelative(BlockFace.UP).getType() != XMaterial.WATER.parseMaterial()) {
					if (p.getItemInHand().getType() == XMaterial.BONE_MEAL.parseMaterial()) {
						e.setCancelled(true);
						if (p.getGameMode() != GameMode.CREATIVE) {
							p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
						}
						int rand = (int) Math.round(Math.random()*31);
						if (rand == 0) {
							b.setType(XMaterial.GRASS_BLOCK.parseMaterial());
						}
						skyWorld.playEffect(b.getLocation(), Effect.VILLAGER_PLANT_GROW, 0);
					}
				} else if ((b.getType() == XMaterial.DIRT.parseMaterial() || b.getType() == XMaterial.GRASS_BLOCK.parseMaterial()) && b.getRelative(BlockFace.UP).getType() == XMaterial.WATER.parseMaterial()) {
					if (p.getItemInHand().getType() == XMaterial.BONE_MEAL.parseMaterial()) {
						Block r = b.getRelative(BlockFace.UP);
						int rand = (int) Math.round(Math.random()*7);
						if (rand == 0) {
							byte trand = (byte) Math.round(Math.random());
							ItemStack toDrop = null;
							if (trand == 0) {
								if (XMaterial.KELP.parseMaterial() != null) {
									toDrop = new ItemStack(XMaterial.KELP.parseMaterial(),1);
								}
							} else if (trand == 1) {
								if (XMaterial.SEA_PICKLE.parseMaterial() != null) {
									toDrop = new ItemStack(XMaterial.SEA_PICKLE.parseMaterial(),1);
								}
							}
							if (toDrop != null) {
								e.setCancelled(true);
								skyWorld.dropItem(r.getLocation(), toDrop);
								skyWorld.playEffect(b.getLocation(), Effect.VILLAGER_PLANT_GROW, 0);
							}
						}
					}
				} else if (b.getType() == XMaterial.GRASS_BLOCK.parseMaterial()) {
					if (p.getItemInHand().getType() == XMaterial.BONE_MEAL.parseMaterial()) {
						Block r = b.getRelative(BlockFace.UP);
						if (r.getType() == XMaterial.AIR.parseMaterial()) {
							int rand = (int) Math.round(Math.random()*7);
							if (rand == 0) {
								e.setCancelled(true);
								byte trand = (byte) Math.round(Math.random()*9);
								ItemStack toDrop = null;
								if (trand == 1) {
									toDrop = XMaterial.OAK_SAPLING.parseItem();
								} else if (trand == 2) {
									toDrop = XMaterial.JUNGLE_SAPLING.parseItem();
								} else if (trand == 3) {
									toDrop = XMaterial.DARK_OAK_SAPLING.parseItem();
								} else if (trand == 4) {
									toDrop = XMaterial.BIRCH_SAPLING.parseItem();
								} else if (trand == 5) {
									toDrop = XMaterial.ACACIA_SAPLING.parseItem();
								} else if (trand == 6) {
									toDrop = XMaterial.PUMPKIN_SEEDS.parseItem();
								} else if (trand == 7) {
									toDrop = XMaterial.MELON_SEEDS.parseItem();
								} else if (trand == 8) {
									toDrop = XMaterial.BEETROOT_SEEDS.parseItem();
								} else if (trand == 9) {
									toDrop = XMaterial.BAMBOO.parseItem();
								}
								if (toDrop != null) {
									skyWorld.dropItem(r.getLocation(), toDrop);
									skyWorld.playEffect(b.getLocation(), Effect.VILLAGER_PLANT_GROW, 0);
								}
							}
						}
					}
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
		            		if (e.getEntity() instanceof LivingEntity && config.getBoolean("USE_ECONOMY")) {
			            		if (edbeEvent.getDamage() >= ((LivingEntity)e.getEntity()).getHealth()) {
			            			double getMoney = 0;
			            			List<String> KILL_MONEY = config.getStringList("KILL_MONEY");
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
			            			if (getMoney != 0) {
			            				Economy econ = new Economy(p.getUniqueId(), data);
			            				econ.deposit(getMoney);
				            			DecimalFormat dec = new DecimalFormat("#0.00");
				            			if (getMoney > 0) {
				            				p.sendMessage(ChatColor.GREEN + "You earned $" + dec.format(getMoney) + " for killing a " + e.getEntity().getType().getName().replace("_", " ") + ".");
				            			} else {
				            				p.sendMessage(ChatColor.RED + "You lost $" + dec.format(getMoney) + " for killing a " + e.getEntity().getType().getName().replace("_", " ") + ".");
				            			}
			            			}
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
    		if (config.getBoolean("DISABLE_PLAYER_COLLISIONS")) {
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
    		int pId = getPlayerId(p.getUniqueId());
    		getLogger().info("Skyblock ID of " + p.getName() + " is " + pId);
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
	        if(type == XMaterial.LAVA.parseMaterial())
	        {
	            Block b = e.getToBlock();
	            Material toid = b.getType();
	            if (toid == XMaterial.AIR.parseMaterial()) {
	            	for (BlockFace face : faces) {
	            		Block r = b.getRelative(face, 1);
	            		if (r.getType() == XMaterial.WATER.parseMaterial()) {
	            			e.setCancelled(true);
	            			if (config.getBoolean("GENERATE_ORES")) {
								double oreType = Math.random() * 100.0;
								List<String> GENERATOR_ORES = config.getStringList("GENERATOR_ORES");
							    for(int i = 0; i < GENERATOR_ORES.size(); i++) {
							    	Material itemMat = XMaterial.matchXMaterial(GENERATOR_ORES.get(i).split(":")[0]).parseMaterial();
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
						    if (b.getType() == XMaterial.AIR.parseMaterial()) {
						    	b.setType(XMaterial.COBBLESTONE.parseMaterial());
						    }
						    
						    Location bLoc = b.getLocation();

							for (int i = 0; i < players.size(); i++) {
								int pId = i;
								Location p1 = islandP1.get(pId);
								Location p2 = islandP2.get(pId);
								if (p1.getBlockX() < bLoc.getBlockX() && bLoc.getBlockX() < p2.getBlockX() && p1.getBlockZ() < bLoc.getBlockZ() && bLoc.getBlockZ() < p2.getBlockZ()) {
									Double addPts = 0.0;
									List<String> LEVEL_PTS = config.getStringList("LEVEL_PTS");
				    				for (int ii = 0; ii < LEVEL_PTS.size(); ii++) {
			            				String btype = LEVEL_PTS.get(ii).split(":")[0];
			            				Double bpts = Double.valueOf(LEVEL_PTS.get(ii).split(":")[1]);
			            				if (btype.equalsIgnoreCase("Default")) {
			            					addPts = bpts;
			            				} else {
			            					Material bmat = XMaterial.matchXMaterial(btype).parseMaterial();
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
		if (config.getBoolean("VOID_INSTANT_DEATH") && p.getLocation().getY() < -10) {
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
			if (config.getBoolean("USE_ECONOMY")) {
				Economy econ = new Economy(p.getUniqueId(), data);
				Double loss = econ.get() / 2;
	    		DecimalFormat dec = new DecimalFormat("#0.00");
				p.sendMessage(ChatColor.RED + "You died and lost $" + dec.format(loss));
				econ.set(loss);
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
			if (config.getBoolean("USE_ECONOMY")) {
				Economy econ = new Economy(p.getUniqueId(), data);
				Double loss = econ.get() / 2;
	    		DecimalFormat dec = new DecimalFormat("#0.00");
				p.sendMessage(ChatColor.RED + "You died and lost $" + dec.format(loss));
				econ.set(loss);
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
		if (config.getBoolean("USE_CHATROOMS")) {
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
		ItemStack acceptButtonU = XMaterial.LIME_DYE.parseItem();
		ItemMeta acceptMetaU = acceptButtonU.getItemMeta();
		acceptMetaU.setDisplayName(ChatColor.GREEN + "Accept Trade");
		acceptButtonU.setItemMeta(acceptMetaU);
        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_GREEN + "Trade") && !(e.getClickedInventory() == p.getInventory()) && !(p == null)) {
            Trade activeTrade = getActiveTrade(p.getUniqueId());
    		if (((e.getSlot() % 9 >= 1 && e.getSlot() % 9 <= 3 && p == activeTrade.getPlayer2()) || (e.getSlot() % 9 >= 5 && e.getSlot() % 9 <= 7 && p == activeTrade.getPlayer1()))) {
        		e.setCancelled(true);
        	} else if (e.getCurrentItem() != null) {
	        	String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
	        	if (itemName.equalsIgnoreCase(" ")) {
	        		e.setCancelled(true);
	        	} else if (itemName.equalsIgnoreCase(ChatColor.RED + "Close Menu")) {
	        		e.setCancelled(true);
        			Inventory p1Inventory = activeTrade.getPlayer1().getInventory();
        			Inventory p2Inventory = activeTrade.getPlayer2().getInventory();
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
        			activeTrade.getPlayer1().sendMessage(ChatColor.RED + "The trade has been cancelled.");
        			activeTrade.getPlayer2().sendMessage(ChatColor.RED + "The trade has been cancelled.");
	        	} else if (e.getSlot() == 39 || e.getSlot() == 41) {
            		e.setCancelled(true);
					ItemStack acceptButton = XMaterial.GREEN_DYE.parseItem();
					ItemMeta acceptMeta = acceptButton.getItemMeta();
					acceptMeta.setDisplayName(ChatColor.GREEN + "Accepted!");
					acceptButton.setItemMeta(acceptMeta);
            		if (p == activeTrade.getPlayer1()) {
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
		                			Inventory p1Inventory = activeTrade.getPlayer1().getInventory();
		                			Inventory p2Inventory = activeTrade.getPlayer2().getInventory();
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
		        					Economy p1Econ = new Economy(activeTrade.getPlayer1().getUniqueId(), data);
		        					Economy p2Econ = new Economy(activeTrade.getPlayer2().getUniqueId(), data);
		        					p1Econ.set(p1Econ.get() + (p2Money - p1Money));
		        					p2Econ.set(p2Econ.get() + (p1Money - p2Money));
		                			closeTrade(activeTrade);
		                			activeTrade.getPlayer1().sendMessage(ChatColor.GREEN + "The trade completed successfully.");
		                			activeTrade.getPlayer2().sendMessage(ChatColor.GREEN + "The trade completed successfully.");
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
					e.setCancelled(true);
					ItemStack moneyButton = XMaterial.GOLD_INGOT.parseItem();
					ItemMeta moneyButtonMeta = moneyButton.getItemMeta();
					Economy econ = new Economy(p.getUniqueId(), data);
					double currentMoney = Double.valueOf(e.getCurrentItem().getItemMeta().getDisplayName().substring(10));
					double currentBalance = econ.get();
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
        			e.getInventory().setItem(e.getSlot(), new ItemStack(XMaterial.AIR.parseMaterial(),1));
            	}
        	}
        } else if (e.getClickedInventory() == p.getInventory() && e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_GREEN + "Trade")) {
    		Trade activeTrade = getActiveTrade(p.getUniqueId());
            Player p1 = activeTrade.getPlayer1();
            e.setCancelled(true);
    		for (int i = 9; i < 36; i++) {
    			if (p == p1) {
    				if (i % 9 >= 1 && i % 9 <= 3) {
    					if (e.getInventory().getItem(i) == null || e.getInventory().getItem(i).getType() == XMaterial.AIR.parseMaterial()) {
    						e.getInventory().setItem(i, e.getCurrentItem());
    						e.getClickedInventory().setItem(e.getSlot(), new ItemStack(XMaterial.AIR.parseMaterial(),1));
    					}
    				}
    			} else {
    				if (i % 9 >= 5 && i % 9 <= 7) {
    					if (e.getInventory().getItem(i) == null || e.getInventory().getItem(i).getType() == XMaterial.AIR.parseMaterial()) {
    						e.getInventory().setItem(i, e.getCurrentItem());
    						e.getClickedInventory().setItem(e.getSlot(), new ItemStack(XMaterial.AIR.parseMaterial(),1));
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
            Trade activeTrade = getActiveTrade(p.getUniqueId());
            if (activeTrade != null) {
	            Player p1 = activeTrade.getPlayer1();
	            Player p2 = activeTrade.getPlayer2();
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
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		if (config.getBoolean("BLAST_PROCESSING") && config.getBoolean("USE_CUSTOM_MECHANICS") && e.getLocation().getWorld() == skyWorld) {
	        for (Block b : e.blockList()) {
	            if(b.getType() == XMaterial.COBBLESTONE.parseMaterial()) {
	                b.setType(XMaterial.GRAVEL.parseMaterial());
	            } else if (b.getType() == XMaterial.GRAVEL.parseMaterial()) {
	            	b.setType(XMaterial.SAND.parseMaterial());
	            }
	        }
		}
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent e) {
		if (config.getBoolean("COBBLE_HEATING") && config.getBoolean("USE_USTOM_MECHANICS") && e.getBlock().getWorld() == skyWorld) {
			Block b = e.getBlock();
			Block c = b.getRelative(BlockFace.UP);
			if (b.getType() == XMaterial.COAL_BLOCK.parseMaterial() && c.getType() == XMaterial.COBBLESTONE.parseMaterial()) {
				e.setCancelled(true);
				b.setType(XMaterial.AIR.parseMaterial());
				double rand = (Math.random()*100);
				if (rand < 10) {
					c.setType(XMaterial.LAVA.parseMaterial());
				}
			}
		}
	}
	
	public Trade getActiveTrade(UUID playerUUID) {
		for (int i = 0; i < openTrades.size(); i++) {
			if (openTrades.get(i) != null && openTrades.get(i).hasPlayer(playerUUID)) {
				return openTrades.get(i);
			}
		}
		return null;
	}
    
    public void closeTrade(Trade activeTrade) {
    	openTrades.set(openTrades.indexOf(activeTrade), null);
    	activeTrade.close();
    }
    
    public void saveData() {
    	for (int i = 0; i < players.size(); i++) {
    		if (islandP1.get(i) != null) {
		    	data.set("data.players." + players.get(i) + ".islandP1.x", islandP1.get(i).getBlockX());
		    	data.set("data.players." + players.get(i) + ".islandP1.y", islandP1.get(i).getBlockY());
		    	data.set("data.players." + players.get(i) + ".islandP1.z", islandP1.get(i).getBlockZ());
    		} else {
		    	data.set("data.players." + players.get(i) + ".islandP1.x", null);
		    	data.set("data.players." + players.get(i) + ".islandP1.y", null);
		    	data.set("data.players." + players.get(i) + ".islandP1.z", null);
    		}
    		if (islandP2.get(i) != null) {
		    	data.set("data.players." + players.get(i) + ".islandP2.x", islandP2.get(i).getBlockX());
		    	data.set("data.players." + players.get(i) + ".islandP2.y", islandP2.get(i).getBlockY());
		    	data.set("data.players." + players.get(i) + ".islandP2.z", islandP2.get(i).getBlockZ());
    		} else {
		    	data.set("data.players." + players.get(i) + ".islandP2.x", null);
		    	data.set("data.players." + players.get(i) + ".islandP2.y", null);
		    	data.set("data.players." + players.get(i) + ".islandP2.z", null);
    		}
    		if (islandHome.get(i) != null) {
		    	data.set("data.players." + players.get(i) + ".islandHome.x", islandHome.get(i).getX());
		    	data.set("data.players." + players.get(i) + ".islandHome.y", islandHome.get(i).getY());
		    	data.set("data.players." + players.get(i) + ".islandHome.z", islandHome.get(i).getZ());
		    	data.set("data.players." + players.get(i) + ".islandHome.pitch", (int)islandHome.get(i).getPitch());
		    	data.set("data.players." + players.get(i) + ".islandHome.yaw", (int)islandHome.get(i).getYaw());
    		} else {
		    	data.set("data.players." + players.get(i) + ".islandHome.x", null);
		    	data.set("data.players." + players.get(i) + ".islandHome.y", null);
		    	data.set("data.players." + players.get(i) + ".islandHome.z", null);
		    	data.set("data.players." + players.get(i) + ".islandHome.pitch", null);
		    	data.set("data.players." + players.get(i) + ".islandHome.yaw", null);
    		}
    		if (islandSettings.get(i) != null) {
    			data.set("data.players." + players.get(i) + ".settings.resetLeft", islandSettings.get(i).get(0));
    			data.set("data.players." + players.get(i) + ".settings.allowsVisitors", islandSettings.get(i).get(1));
    		} else {
    			data.set("data.players." + players.get(i) + ".settings.resetLeft", null);
    			data.set("data.players." + players.get(i) + ".settings.allowsVisitors", null);
    		}
    		data.set("data.players." + players.get(i) + ".islandLeader", islandLeader.get(i));
    		data.set("data.players." + players.get(i) + ".islandMembers", islandMembers.get(i));
    		data.set("data.players." + players.get(i) + ".islandPts", islandPts.get(i));
    	}
    	data.set("data.nextIsland.x", nextIsland.getBlockX());
    	data.set("data.nextIsland.y", nextIsland.getBlockY());
    	data.set("data.nextIsland.z", nextIsland.getBlockZ());
    	data.set("data.toClear", toClear);
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
	    					currentBlock.setType(XMaterial.GRASS_BLOCK.parseMaterial());
	    				} else if (x == x1 + 2 && z == z1 + 2 && y > y1) {
	    					currentBlock.setType(XMaterial.SAND.parseMaterial());
	    				} else if (y == y1) {
	    					currentBlock.setType(XMaterial.BEDROCK.parseMaterial());
	    				} else if (y < y1 + 3) {
	    					if (config.getBoolean("GENERATE_ORES")) { // spice things up
	    						double oreType = Math.random() * 100.0;
	    						List<String> GENERATOR_ORES = config.getStringList("GENERATOR_ORES");
	    					    for(int i = 0; i < GENERATOR_ORES.size(); i++) {
	    					    	Material itemMat = XMaterial.matchXMaterial(GENERATOR_ORES.get(i).split(":")[0]).parseMaterial();
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
	    					    if (currentBlock.getType() == XMaterial.AIR.parseMaterial()) {
	    					    	currentBlock.setType(XMaterial.COBBLESTONE.parseMaterial());
	    					    }
	    					} else {
	    						currentBlock.setType(XMaterial.DIRT.parseMaterial());
	    					}
	    				} else {
	    					currentBlock.setType(XMaterial.DIRT.parseMaterial());
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
		skyWorld.getBlockAt(spawnLoc).setType(XMaterial.AIR.parseMaterial());
		skyWorld.getBlockAt(chestLoc).setType(XMaterial.CHEST.parseMaterial());
		Chest chest = (Chest) skyWorld.getBlockAt(chestLoc).getState();
	    Inventory chestinv = chest.getBlockInventory();
	    List<String> CHEST_ITEMS = config.getStringList("CHEST_ITEMS");
	    for(int i = 0; i < CHEST_ITEMS.size(); i++) {
	    	Material itemMat = XMaterial.matchXMaterial(CHEST_ITEMS.get(i).split(":")[0]).parseMaterial();
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
	    islandP1.set(pId, new Location(skyWorld,x1-(config.getInt("ISLAND_WIDTH")/2),0,z1-(config.getInt("ISLAND_DEPTH")/2)));
	    islandP2.set(pId, new Location(skyWorld,x1+(config.getInt("ISLAND_WIDTH")/2),skyWorld.getMaxHeight(),z1+(config.getInt("ISLAND_DEPTH")/2)));
	    islandHome.set(pId, homeLoc);
	    if (islandSettings.get(pId).get(0) == null) { // resets left
	    	islandSettings.get(pId).set(0, true);
	    } else {
	    	islandSettings.get(pId).set(0, false);
	    }
	    islandSettings.get(pId).set(1, true); // allow visitors
	    Economy econ = new Economy(p.getUniqueId(), data);
	    econ.set(config.getDouble("STARTING_MONEY"));
	    islandPts.set(pId, 100.0);
	    
	    // set next island location
	    if (x1 < config.getInt("LIMIT_X")) {
	    	nextIsland = new Location(skyWorld, x1+config.getInt("ISLAND_WIDTH"), config.getInt("ISLAND_HEIGHT"), z1);
	    } else {
	    	nextIsland = new Location(skyWorld, -config.getInt("LIMIT_X"), config.getInt("ISLAND_HEIGHT"), z1+config.getInt("ISLAND_DEPTH"));
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
	    	if (data.isSet("data.players." + uuid.toString())) {
	    		players.add(uuid.toString());
	    		if (data.isSet("data.players." + uuid.toString() + ".islandP1.x")) {
		    		islandP1.add(new Location(
		    				skyWorld,
		    				data.getInt("data.players." + uuid.toString() + ".islandP1.x"),
		    				data.getInt("data.players." + uuid.toString() + ".islandP1.y"),
		    				data.getInt("data.players." + uuid.toString() + ".islandP1.z")));
	    		} else {
	    			islandP1.add(null);
	    		}
	    		if (data.isSet("data.players." + uuid.toString() + ".islandP2.x")) {
		    		islandP2.add(new Location(
		    				skyWorld,
		    				data.getInt("data.players." + uuid.toString() + ".islandP2.x"),
		    				data.getInt("data.players." + uuid.toString() + ".islandP2.y"),
		    				data.getInt("data.players." + uuid.toString() + ".islandP2.z")));
	    		} else {
	    			islandP2.add(null);
	    		}
	    		if (data.isSet("data.players." + uuid.toString() + ".islandHome.x")) {
		    		islandHome.add(new Location(
		    				skyWorld,
		    				data.getDouble("data.players." + uuid.toString() + ".islandHome.x"),
		    				data.getDouble("data.players." + uuid.toString() + ".islandHome.y"),
		    				data.getDouble("data.players." + uuid.toString() + ".islandHome.z"),
		    				data.getInt("data.players." + uuid.toString() + ".islandHome.yaw"),
		    				data.getInt("data.players." + uuid.toString() + ".islandHome.pitch")));
	    		} else {
	    			islandHome.add(null);
	    		}
	    		islandSettings.add(new ArrayList<Boolean>());
	    		islandSettings.get(islandSettings.size()-1).add(data.getBoolean("data.players." + uuid.toString() + ".settings.resetLeft"));
	    		islandSettings.get(islandSettings.size()-1).add(data.getBoolean("data.players." + uuid.toString() + ".settings.allowsVisitors"));
	    		islandLeader.add(data.getString("data.players." + uuid.toString() + ".islandLeader"));
	    		invites.add(null);
	    		islandMembers.add(data.getStringList("data.players." + uuid.toString() + ".islandMembers"));
	    		islandPts.add(data.getDouble("data.players." + uuid.toString() + ".islandPts"));
	    	} else {
		    	players.add(uuid.toString());
		    	islandP1.add(null);
		    	islandP2.add(null);
		    	islandHome.add(null);
	    		islandSettings.add(new ArrayList<Boolean>());
	    		islandSettings.get(islandSettings.size()-1).add(null); // resets left
	    		islandSettings.get(islandSettings.size()-1).add(null); // allows visitors
	    		islandLeader.add(null);
	    		invites.add(null);
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
		List<List<Boolean>> tislandSettings = new ArrayList<List<Boolean>>();
		List<Integer> tinvites = new ArrayList<Integer>();
		List<String> tislandLeader = new ArrayList<String>();
		List<List<String>> tislandMembers = new ArrayList<List<String>>();
		for (int i = 0; i < players.size(); i++) {
			UUID tempP = UUID.fromString(players.get(i));
			if (getServer().getPlayer(tempP) != null) {
				tplayers.add(players.get(i));
				tislandP1.add(islandP1.get(i));
				tislandP2.add(islandP2.get(i));
				tislandHome.add(islandHome.get(i));
				tislandSettings.add(islandSettings.get(i));
				tinvites.add(invites.get(i));
				tislandLeader.add(islandLeader.get(i));
				tislandMembers.add(islandMembers.get(i));
			}
		}
		players = tplayers;
		islandP1 = tislandP1;
		islandP2 = tislandP2;
		islandHome = tislandHome;
		islandSettings = tislandSettings;
		invites = tinvites;
		islandLeader = tislandLeader;
		islandMembers = tislandMembers;
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
    
    public void loadConfig() {
    	this.reloadConfig();
    }
    
    public void convertConfig(String _old, String _new) {
    	if (_old == "1.2.0" && _new == "1.2.1") {
    		ConfigurationSection oldData = (ConfigurationSection) config.get("data");
    		data.set("data", oldData);
    		config.set("data", null);
    	}
		config.set("config-version", _new);
		this.saveConfig();
    	try {
			data.save(getDataFolder() + File.separator + "data.yml");
		} catch (IOException e) {
            Bukkit.getLogger().warning("§4Could not save data.yml file.");
		}
    }
    
    public TradeRequest getTradeRequestFromPlayer(Player p) {
    	for (int i = 0; i < tradingRequests.size(); i++) {
    		if (tradingRequests.get(i) != null && tradingRequests.get(i).isActive() && tradingRequests.get(i).from() == p) {
    			return tradingRequests.get(i);
    		} else if (tradingRequests.get(i) != null && !tradingRequests.get(i).isActive()) {
    			tradingRequests.set(i, null);
    		}
    	}
    	return null;
    }
    
    public TradeRequest getTradeRequestToPlayer(Player p) {
    	for (int i = tradingRequests.size() - 1; i >= 0; i--) {
    		if (tradingRequests.get(i) != null && tradingRequests.get(i).isActive() && tradingRequests.get(i).to() == p) {
    			return tradingRequests.get(i);
    		} else if (tradingRequests.get(i) != null && !tradingRequests.get(i).isActive()) {
    			tradingRequests.set(i, null);
    		}
    	}
    	return null;
    }
    
    public TradeRequest getTradeRequest(Player p, Player t) {
    	for (int i = tradingRequests.size() - 1; i >= 0; i--) {
    		if (tradingRequests.get(i) != null && tradingRequests.get(i).isActive() && ((tradingRequests.get(i).to() == p && tradingRequests.get(i).from() == t) || (tradingRequests.get(i).to() == t && tradingRequests.get(i).from() == p))) {
    			return tradingRequests.get(i);
    		} else if (tradingRequests.get(i) != null && !tradingRequests.get(i).isActive()) {
    			tradingRequests.set(i, null);
    		}
    	}
    	return null;
    }
}