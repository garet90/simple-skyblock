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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import holiday.garet.skyblock.economy.Economy;
import holiday.garet.skyblock.economy.Trade;
import holiday.garet.skyblock.economy.TradeRequest;
import holiday.garet.skyblock.island.SkyblockPlayer;
import holiday.garet.skyblock.island.Island;
import holiday.garet.skyblock.island.IslandInvite;

@SuppressWarnings("deprecation")
public class SimpleSkyblock extends JavaPlugin implements Listener {
	FileConfiguration config = this.getConfig();
	FileConfiguration data = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "data.yml"));

	List<SkyblockPlayer> players = new ArrayList<SkyblockPlayer>();
	List<Island> islands = new ArrayList<Island>();
	List<TradeRequest> tradingRequests = new ArrayList<TradeRequest>();
	List<IslandInvite> islandInvites = new ArrayList<IslandInvite>();
	
	int nextIslandKey = 0;
	
	List<String> toClear = new ArrayList<String>();
	
	Location nextIsland;
	
	World skyWorld;
	World skyNether;
	
    @Override
    public void onEnable() {
    	Bukkit.getPluginManager().registerEvents(this, this);
    	if (config.isSet("data")) {
    		// if the config is out of date, lets fix that.
    		convertConfig("1.2.0","1.2.1");
    	} else if (config.getString("config-version").equalsIgnoreCase("1.2.1")) {
    		convertConfig("1.2.1","1.2.2");
    	}
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        config = this.getConfig();
    	reloadConfig();
		final String worldName;
    	if (config.isSet("WORLD")) {
    		worldName = config.getString("WORLD");
    	} else {
    		worldName = "world";
    	}
    	if (worldName == "mrtest") {
    		getLogger().info("OOGA BOOGA BOOGA!");
    	}
    	skyWorld = getServer().getWorld(worldName);
    	skyNether = getServer().getWorld(worldName + "_nether");
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
    	if (skyNether == null && config.getBoolean("USE_NETHER")) { // If we don't have a world generated yet
            BukkitScheduler scheduler = getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                	WorldCreator wc = new WorldCreator(worldName + "_nether");
	                wc.generator(new EmptyWorldGenerator());
	                wc.createWorld();
	                skyNether = getServer().getWorld(worldName + "_nether");
                }
            }, 0L);
    	}
    	toClear = data.getStringList("data.toClear");
    	if (data.isSet("data.nextIsland")) {
    		nextIsland = new Location(skyWorld, data.getInt("data.nextIsland.x"), config.getInt("ISLAND_HEIGHT"), config.getInt("data.nextIsland.z"));
    	} else {
    		nextIsland = new Location(skyWorld, -config.getInt("LIMIT_X"), config.getInt("ISLAND_HEIGHT"),-config.getInt("LIMIT_Z"));
    	}
    	if (data.isSet("data.nextIsland.key")) {
    		nextIslandKey = data.getInt("data.nextIsland.key");
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
    	for (Player p: getServer().getOnlinePlayers()) {
        	if (p != null) {
	    		Island playerIsland = getPlayerIsland(p);
	    		if (playerIsland == null) {
	    			String playerIslandDataLocation = "data.players." + p.getUniqueId().toString() + ".island";
	    			if (data.isSet(playerIslandDataLocation)) {
	    				playerIsland = new Island(data.getInt(playerIslandDataLocation), skyWorld, data, this);
	    				islands.add(playerIsland);
	    			}
	    		}
    			players.add(new SkyblockPlayer(p, playerIsland, skyWorld, data, this));
        		SkyblockPlayer sp = getSkyblockPlayer(p);
        		if (config.getBoolean("DISABLE_PLAYER_COLLISIONS")) {
        			noCollideAddPlayer(p);
        		}
        		if (toClear.contains(p.getUniqueId().toString())) {
        			PlayerInventory inv = p.getInventory();
    	            inv.clear();
    	            inv.setArmorContents(new ItemStack[4]);
    	            p.teleport(sp.getHome(), TeleportCause.PLUGIN);
    	            toClear.set(toClear.indexOf(p.getUniqueId().toString()), null);
        		}
        	}
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
        		Island playerIsland = getPlayerIsland(p);
    			SkyblockPlayer sp = getSkyblockPlayer(p);
            	if (args.length == 0) {
	        		if (playerIsland == null) {
			            sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "Generating island...");
			            generateIsland(nextIsland, p, null);
			            return true;
	        		} else {
	        			sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "Teleporting you to your island...");
	        			p.teleport(sp.getHome(), TeleportCause.PLUGIN);
	            		p.setBedSpawnLocation(p.getLocation(), true);
	        			p.setFallDistance(0);
	        			sp.setVisiting(null);
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
            		if (sp.getHome() != null) {
	        			sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "Teleporting you to your island...");
	        			p.teleport(sp.getHome(), TeleportCause.PLUGIN);
	            		p.setBedSpawnLocation(p.getLocation(), true);
	        			p.setFallDistance(0);
	        			sp.setVisiting(null);
	        			return true;
            		} else {
            			sender.sendMessage(ChatColor.RED + "You must have an island to teleport to it!");
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("sethome")) {
            		if (playerIsland != null) {
	            		if (playerIsland.inBounds(p.getLocation())) {
		            		sp.setHome(p.getLocation());
		            		p.setBedSpawnLocation(p.getLocation(), true);
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
            		if (playerIsland != null) {
	            		if (playerIsland.getLeader().toString().equalsIgnoreCase(p.getUniqueId().toString())) {
		            		if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
		            			if (playerIsland.canReset() || config.getBoolean("INFINITE_RESETS")) {
						            generateIsland(nextIsland, p, playerIsland);
						            List<UUID> islandMembers = playerIsland.getMembers();
						            for (int i = 0; i < islandMembers.size(); i++) {
						            	if (islandMembers.get(i) != null) {
						            		Player tempP = getServer().getPlayer(islandMembers.get(i));
						            		SkyblockPlayer tempSP = getSkyblockPlayer(tempP);
						            		tempSP.setHome(sp.getHome());
						            		Economy tempEcon = new Economy(islandMembers.get(i), data);
						            		tempEcon.set(0);
						            		if (tempP == null) {
						            			toClear.add(islandMembers.get(i).toString());
						            		} else {
						            			PlayerInventory tempInv = tempP.getInventory();
						            			tempInv.clear();
						            			tempInv.setArmorContents(new ItemStack[4]);
						            			tempP.teleport(sp.getHome(), TeleportCause.PLUGIN);
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
            			String pName;
            			SkyblockPlayer sz;
            			if (getServer().getPlayer(args[1]) == null) {
            				// they are offline
	            			OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
	            			if (op.hasPlayedBefore()) {
	            				pName = op.getName();
	            				sz = getSkyblockPlayer(op);
	            			} else {
	            				sender.sendMessage(ChatColor.RED + "That user doesn\'t have an island!");
	            				return true;
	            			}
            			} else {
            				pName = getServer().getPlayer(args[1]).getName();
            				getServer().getPlayer(args[1]).getUniqueId().toString();
            				sz = getSkyblockPlayer(getServer().getPlayer(args[1]));
            			}
            			Boolean allowsVisitors = sz.getIsland().allowsVisitors();
            			if (playerIsland != null && sz.getIsland() == playerIsland) {
            				sender.sendMessage(ChatColor.RED + "You can\'t visit your own island!");
            				return true;
            			}
            			if ((sz.getHome() != null && !(p.getName().equalsIgnoreCase(args[1])) && allowsVisitors) || p.hasPermission("skyblock.admin")) {
                			p.teleport(sz.getHome(), TeleportCause.PLUGIN);
		            		p.setBedSpawnLocation(p.getLocation(), true);
		        			p.setFallDistance(0);
		        			sp.setVisiting(sz);
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
            		if (playerIsland != null) {
	            		if (playerIsland.getLeader().toString().equalsIgnoreCase(p.getUniqueId().toString())) {
		            		if (args.length > 1) {
			            		if (args[1].equalsIgnoreCase("list")) {
			            			ChatColor allowVisitors = ChatColor.RED;
			            			if (playerIsland.allowsVisitors()) {
			            				allowVisitors = ChatColor.GREEN;
			            			}
			            			ChatColor visitorsCanRideMobs = ChatColor.RED;
			            			if (playerIsland.visitorsCanRideMobs()) {
			            				visitorsCanRideMobs = ChatColor.GREEN;
			            			}
			            			sender.sendMessage("Island Settings: " + allowVisitors + "allow-visitors" + ChatColor.RESET + ", " + visitorsCanRideMobs + "visitors-can-ride-mobs");
			            			return true;
			            		} else if (args[1].equalsIgnoreCase("allow-visitors")) {
			            			if (args.length > 2) {
			            				if (args[2].equalsIgnoreCase("true")) {
			            					playerIsland.setAllowsVisitors(true);
			            					sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "\'allow-visitors\' was set to true.");
			            				} else if (args[2].equalsIgnoreCase("false")) {
			            					playerIsland.setAllowsVisitors(false);
			            					sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "\'allow-visitors\' was set to false.");
			            				} else {
				                			sender.sendMessage(ChatColor.RED + "Usage: /island settings <setting | list> [true | false]");
			            				}
			            				return true;
			            			} else {
			                			sender.sendMessage(ChatColor.RED + "Usage: /island settings <setting | list> [true | false]");
			                			return true;
			            			}
			            		} else if (args[1].equalsIgnoreCase("visitors-can-ride-mobs")) {
			            			if (args.length > 2) {
			            				if (args[2].equalsIgnoreCase("true")) {
			            					playerIsland.setVisitorsCanRideMobs(true);
			            					sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "\'visitors-can-ride-mobs\' was set to true.");
			            				} else if (args[2].equalsIgnoreCase("false")) {
			            					playerIsland.setVisitorsCanRideMobs(false);
			            					sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + "\'visitors-can-ride-mobs\' was set to false.");
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
	            			sender.sendMessage(ChatColor.RED + "You must be island leader to use this command!");
	            			return true;
	            		}
            		} else {
            			sender.sendMessage(ChatColor.RED + "You must have an island to change its settings!");
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("invite")) {
            		if (playerIsland != null) {
	            		if (playerIsland.getLeader().toString().equalsIgnoreCase(p.getUniqueId().toString())) {
		            		if (args.length > 1) {
		            			Player r = getServer().getPlayer(args[1]);
		            			if (r != null) {
		            				if (getIslandInvite(playerIsland, r) == null) {
				            			Island rIsland = getPlayerIsland(r);
				            			if (rIsland != playerIsland) {
				            				sender.sendMessage(ChatColor.GREEN + "You have successfully invited " + r.getName() + " to join your island.");
				            				islandInvites.add(new IslandInvite(r, playerIsland, this));
				            				return true;
			            				} else {
			            					p.sendMessage(ChatColor.RED + "This player is already a member of your island!");
			            					return true;
			            				}
		            				} else {
			                			sender.sendMessage(ChatColor.RED + "You have already sent this player an invite!");
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
            			if (playerIsland == null || (playerIsland.getMembers() == null || getNotNullLength(playerIsland.getMembers()) == 0)) {
	            			if (j != null) {
                    			Island toJoin = getPlayerIsland(j);
	            				IslandInvite ii = getIslandInvite(toJoin, p);
	            				SkyblockPlayer sj = getSkyblockPlayer(j);
	            				if (ii != null && ii.isActive()) {
	            					if (playerIsland != null) {
	            						playerIsland.messageAllMembers(ChatColor.GREEN + p.getName() + " has left your island.", p);
		    	        				playerIsland.leaveIsland(p);
	            					}
	            					ii.setActive(false);
	            					sp.setHome(sj.getHome());
	            					
	            					Economy econ = new Economy(p.getUniqueId(), data);
	            					econ.set(0); // set to 0 to prevent abuse.
	    				            PlayerInventory inv = p.getInventory();
	    				            inv.clear();
	    				            inv.setArmorContents(new ItemStack[4]);
	    				            p.teleport(sj.getHome(), TeleportCause.PLUGIN);
	    				            p.setFallDistance(0);
	    		        			sp.setVisiting(null);
	    				    	    p.setBedSpawnLocation(sj.getHome(), true);
	                    			toJoin.addPlayer(p);
	                    			sp.setIsland(toJoin);
	                    			toJoin.messageAllMembers(ChatColor.GREEN + p.getName() + " has joined your island.", p);
	                    			p.sendMessage(ChatColor.GREEN + "You have successfully joined " + j.getName() + "\'s island.");
	                    			
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
        			if (playerIsland != null) {
	        			if (!playerIsland.getLeader().toString().equalsIgnoreCase(p.getUniqueId().toString())) {
	        				playerIsland.messageAllMembers(ChatColor.GREEN + p.getName() + " has left your island.", p);
	        				playerIsland.leaveIsland(p);
	        				sp.setHome(null);
	        				sp.setIsland(null);
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
            		if (playerIsland != null) {
            			if (playerIsland.getLeader().toString().equalsIgnoreCase(p.getUniqueId().toString())) {
            				if (args.length > 1) {
            					Player k = getServer().getPlayer(args[1]);
            					String kName;
            					UUID pUUID;
            					SkyblockPlayer ps;
            					if (k == null) {
            						OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
            						pUUID = op.getUniqueId();
            						kName = op.getName();
            						ps = getSkyblockPlayer(op);
            					} else {
            						pUUID = k.getUniqueId();
            						kName = k.getName();
                					if (k.getUniqueId() == p.getUniqueId()) {
                						sender.sendMessage(ChatColor.RED + "You can\'t kick yourself!");
                						return true;
                					}
                					ps = getSkyblockPlayer(k);
            					}
        						if (playerIsland.hasPlayer(pUUID)) {
        							ps.setHome(null);
        							ps.setIsland(null);
        							playerIsland.leaveIsland(pUUID);
            		        		sender.sendMessage(ChatColor.GREEN + kName+ " has been kicked from your island.");
            		        		if (k != null) {
            		        			k.sendMessage(ChatColor.RED + "You have been kicked from your island.");
            		        		}
            		        		playerIsland.messageAllMembers(ChatColor.GREEN + kName + " has left your island.", p);
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
        			if (playerIsland != null) {
        				Player l = getServer().getPlayer(playerIsland.getLeader());
        				String leaderName;
        				ChatColor leaderColor;
        				if (l != null) {
        					leaderName = l.getName();
        					leaderColor = ChatColor.GREEN;
        				} else {
							OfflinePlayer op = Bukkit.getOfflinePlayer(playerIsland.getLeader());
    						leaderName = op.getName();
    						leaderColor = ChatColor.RED;
        				}
        				sender.sendMessage(ChatColor.AQUA + "Island Members" + ChatColor.GRAY + ":");
        				sender.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.GOLD + "[LEADER] " + leaderColor + leaderName);
        				for (int i = 0; i < playerIsland.getMembers().size(); i++) {
        					if (playerIsland.getMembers().get(i) != null) {
        						UUID tempUUID = playerIsland.getMembers().get(i);
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
        			if (playerIsland != null) {
        				if (playerIsland.getLeader().toString().equalsIgnoreCase(p.getUniqueId().toString())) {
        					if (args.length > 1) {
        						Player l = getServer().getPlayer(args[1]);
        						if (l != null) {
            						if (l.getUniqueId() != p.getUniqueId()) {
	        							if (playerIsland.hasPlayer(l.getUniqueId())) {
		        							playerIsland.makeLeader(l);
		        							sender.sendMessage(ChatColor.GREEN + l.getName() + " has been made the new island leader.");
		        							l.sendMessage(ChatColor.GREEN + "You have been made the new island leader.");
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
            		if (playerIsland != null) {
            			int isLevel = 0;
            			isLevel = (int)Math.floor(Math.sqrt(playerIsland.getPoints())-9);
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
            				reloadConfig();
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
        						Island tis = getPlayerIsland(t);
        						tis.setCanReset(true);
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
	        		getLogger().info("[World] " + sender.getName() + ": " + m);
	        		return true;
	        	} else {
	        		sender.sendMessage(ChatColor.RED + "Usage: /shout <message>");
	        		return true;
	        	}
        	} else {
        		sender.sendMessage(ChatColor.RED + "Chatrooms are disabled.");
        		return true;
        	}
        } else if (command.getName().equalsIgnoreCase("trade")) {
        	if (config.getBoolean("USE_ECONOMY")) {
	        	if (sender instanceof Player) {
	        		Player p = (Player) sender;
		        	if (args.length > 0) {
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
			        					Trade trade = new Trade(t, p, this, new Economy(t.getUniqueId(), data), new Economy(p.getUniqueId(), data));
			        					this.getServer().getPluginManager().registerEvents(trade, this);
			        					trade.open();
			        					trec.close();
			        					return true;
		        					} else {
			        					sender.sendMessage(ChatColor.RED + "You aren't close enough to trade!");
			        					return true;
		        					}
		        				} else if (t == p) {
		        					sender.sendMessage(ChatColor.RED + "You can't trade with yourself!");
		        					return true;
		        				} else {
		        					sender.sendMessage(ChatColor.RED + "This player is no longer online.");
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
    	Island playerIsland = getPlayerIsland(p);
    	if (p != null) {
    		if ((p.getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getWorld() == skyNether)) && !(p.hasPermission("skyblock.admin"))) {
	    		if (playerIsland != null) {
	    			if (!(playerIsland.inBounds(b.getLocation()))) {
	    				e.setCancelled(true);
	    			}
	    		} else {
	    			e.setCancelled(true);
	    		}
    		}
    		if (!e.isCancelled() && (p.getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getWorld() == skyNether))) {
    			if (playerIsland != null) {
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
					playerIsland.setPoints(playerIsland.getPoints() + addPts);
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
    	Block b = e.getBlock();
    	Player p = e.getPlayer();
    	Island playerIsland = getPlayerIsland(p);
    	if (p != null) {
    		if ((p.getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getWorld() == skyNether)) && !(p.hasPermission("skyblock.admin"))) {
	    		if (playerIsland != null) {
	    			if (!(playerIsland.inBounds(b.getLocation()))) {
	    				e.setCancelled(true);
	    			}
	    		} else {
	    			e.setCancelled(true);
	    		}
    		}
    		if (!e.isCancelled() && (p.getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getWorld() == skyNether))) {
    			if (playerIsland != null) {
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
					playerIsland.setPoints(playerIsland.getPoints()-addPts);
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
    	Block b = e.getClickedBlock();
    	Player p = e.getPlayer();
    	Island playerIsland = getPlayerIsland(p);
    	if (p != null && b != null) {
    		if ((p.getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getWorld() == skyNether)) && !(p.hasPermission("skyblock.admin"))) {
	    		if (playerIsland != null) {
	    			if (!(playerIsland.inBounds(b.getLocation()))) {
	    				e.setCancelled(true);
	    			}
	    		} else {
	    			e.setCancelled(true);
	    		}
    		}
    	}
    	if (!e.isCancelled() && config.getBoolean("BONEMEAL_DOES_MORE") && config.getBoolean("USE_CUSTOM_MECHANICS") && (p.getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getWorld() == skyNether))) {
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
								byte trand = (byte) Math.round(Math.random()*10);
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
								} else if (trand == 10) {
									toDrop = XMaterial.SWEET_BERRIES.parseItem();
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
    	if(e instanceof EntityDamageByEntityEvent && (e.getEntity().getLocation().getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && e.getEntity().getLocation().getWorld() == skyNether))){
	        EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent)e;
	        if (edbeEvent.getDamager() instanceof Player){
	            if (e.getEntity() instanceof Player) {
	            	if ((e.getEntity().getLocation().getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && e.getEntity().getLocation().getWorld() == skyNether)) && !(edbeEvent.getDamager().isOp())) {
	            		// make it so nobody can attack anybody on an island
	            		e.setCancelled(true);
	            	}
	            } else {
	            	Player p = (Player)edbeEvent.getDamager();
	            	Island playerIsland = getPlayerIsland(p);
	            	if (p != null) {
		            	if ((playerIsland == null || !(playerIsland.inBounds(e.getEntity().getLocation()))) && !(edbeEvent.getDamager().isOp())) {
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
        	Island playerIsland = getPlayerIsland(p);
        	if ((playerIsland == null || !(playerIsland.inBounds(e.getEntity().getLocation()))) && !(p.hasPermission("skyblock.admin"))) {
        		// make it so people are invincible on other people's islands
        		e.setCancelled(true);
        	}
    	}
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
    	Player p = e.getPlayer();
    	SkyblockPlayer sp = getSkyblockPlayer(p);
    	if (p != null) {
    		if (sp == null) {
	    		Island playerIsland = getPlayerIsland(p);
	    		if (playerIsland == null) {
	    			String playerIslandDataLocation = "data.players." + p.getUniqueId().toString() + ".island";
	    			if (data.isSet(playerIslandDataLocation)) {
	    				playerIsland = new Island(data.getInt(playerIslandDataLocation), skyWorld, data, this);
	    				islands.add(playerIsland);
	    			}
	    		}
    			players.add(new SkyblockPlayer(p, playerIsland, skyWorld, data, this));
    		}
    		sp = getSkyblockPlayer(p);
    		if (config.getBoolean("DISABLE_PLAYER_COLLISIONS")) {
    			noCollideAddPlayer(p);
    		}
    		if (toClear.contains(p.getUniqueId().toString())) {
    			PlayerInventory inv = p.getInventory();
	            inv.clear();
	            inv.setArmorContents(new ItemStack[4]);
	            p.teleport(sp.getHome(), TeleportCause.PLUGIN);
	            toClear.set(toClear.indexOf(p.getUniqueId().toString()), null);
    		}
    	}
    }
    
    public void onPlayerQuit(PlayerQuitEvent e) {
    	Player p = e.getPlayer();
    	SkyblockPlayer sp = getSkyblockPlayer(p);
    	if (sp != null) {
    		sp.savePlayer();
    	}
    }
    
    @EventHandler
    public void onFromTo(BlockFromToEvent e)
    {
    	if ((e.getBlock().getLocation().getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && e.getBlock().getLocation().getWorld() == skyNether))) {
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
								SkyblockPlayer player = players.get(i);
								Island playerIsland = getPlayerIsland(player.getPlayer());
								if (playerIsland != null && playerIsland.inBounds(bLoc)) {
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
									playerIsland.setPoints(playerIsland.getPoints() + addPts);
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
    	Island playerIsland = getPlayerIsland(p);
    	if ((p.getLocation().getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getLocation().getWorld() == skyNether)) && ((playerIsland == null || !(playerIsland.inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin")))) {
    		e.setCancelled(true);
    	}
    }
    
	@EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
    	Player p = e.getPlayer();
    	Island playerIsland = getPlayerIsland(p);
    	if ((p.getLocation().getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getLocation().getWorld() == skyNether)) && ((playerIsland == null || !(playerIsland.inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin")))) {
    		e.setCancelled(true);
    	}
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SkyblockPlayer sp = getSkyblockPlayer(p);
		if (config.getBoolean("VOID_INSTANT_DEATH") && p.getLocation().getY() < -10) {
			p.sendMessage(ChatColor.RED + "You fell into the void.");
			if (p.getBedSpawnLocation() != null) {
				p.teleport(p.getBedSpawnLocation(), TeleportCause.PLUGIN);
			} else {
				if (sp.getHome() != null) {
					p.teleport(sp.getHome(), TeleportCause.PLUGIN);
				} else {
					p.teleport(skyWorld.getSpawnLocation(), TeleportCause.PLUGIN);
				}
			}
			p.setVelocity(new Vector(0,0,0));
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
			p.setFallDistance(0);
			if (config.getBoolean("USE_ECONOMY")) {
				if (sp.getIsland() != null && sp.getIsland().inBounds(p.getLocation())) {
					Economy econ = new Economy(p.getUniqueId(), data);
					Double loss = econ.get() / 2;
		    		DecimalFormat dec = new DecimalFormat("#0.00");
					p.sendMessage(ChatColor.RED + "You died and lost $" + dec.format(loss));
					econ.set(loss);
				}
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
	    	Island playerIsland = getPlayerIsland(p);
	    	if ((p.getLocation().getWorld() == skyWorld) && ((playerIsland == null || !(playerIsland.inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin")))) {
	    		e.setCancelled(true);
	    	}
			
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		List<String> chatworlds = config.getStringList("CHAT_WORLDS");
		Boolean useChat = false;
		for (int i = 0; i < chatworlds.size(); i++) {
			if (chatworlds.get(i).equalsIgnoreCase(e.getPlayer().getLocation().getWorld().getName()) || chatworlds.get(i).equalsIgnoreCase("*")) {
				useChat = true;
			}
		}
		if (config.getBoolean("USE_CHATROOMS") && useChat) {
			Player p = e.getPlayer();
			Island playerIsland = getPlayerIsland(p);
			if (playerIsland != null) {
				e.setCancelled(true);
				playerIsland.messageAllMembers(ChatColor.GRAY + "[" + ChatColor.AQUA + "Island" + ChatColor.GRAY + "] " + ChatColor.WHITE + p.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + e.getMessage());
				getLogger().info("[" + playerIsland.getKey() + "] " + p.getName() + ": " + e.getMessage());
			} else {
				p.sendMessage(ChatColor.RED + "You must have an island to chat. Use \"/shout\" to chat with the whole server.");
			}
		}
	}
	
	@EventHandler
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
		Entity ent = e.getTarget();
		if (ent instanceof Player) {
			Player p = (Player) ent;
	    	Island playerIsland = getPlayerIsland(p);
	    	if ((p.getLocation().getWorld() == skyWorld) && ((playerIsland == null || !(playerIsland.inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin")))) {
	    		e.setCancelled(true);
	    	}
			
		}
	}
	
	@EventHandler
	public void onEntityMount(EntityMountEvent e) {
		Entity ent = e.getEntity();
		if (ent instanceof Player) {
			Player p = (Player) ent;
	    	SkyblockPlayer sp = getSkyblockPlayer(p);
	    	if ((p.getLocation().getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getLocation().getWorld() == skyNether)) && ((sp.getIsland() == null || !(sp.getIsland().inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin")))) {
	    		if (!sp.getVisiting().getIsland().visitorsCanRideMobs()) {
	    			e.setCancelled(true);
	    		}
	    	}
			
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		if (config.getBoolean("BLAST_PROCESSING") && config.getBoolean("USE_CUSTOM_MECHANICS") && (e.getLocation().getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && e.getLocation().getWorld() == skyNether))) {
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
		if (config.getBoolean("COBBLE_HEATING") && config.getBoolean("USE_USTOM_MECHANICS") && (e.getBlock().getLocation().getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && e.getBlock().getLocation().getWorld() == skyNether))) {
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
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (e.getCause() == TeleportCause.NETHER_PORTAL && config.getBoolean("USE_NETHER")) {
			Location newTo = e.getFrom();
			if (newTo.getWorld() == skyWorld) {
				newTo.setWorld(skyNether);
				e.setTo(newTo);
			} else if (newTo.getWorld() == skyNether) {
				newTo.setWorld(skyWorld);
				e.setTo(newTo);
			}
		}
	}
    
    public void saveData() {
		for (int i = 0; i < islands.size(); i++) {
			islands.get(i).saveIsland();
		}
		for (int i = 0; i < players.size(); i++) {
			players.get(i).savePlayer();
		}
    	data.set("data.nextIsland.x", nextIsland.getBlockX());
    	data.set("data.nextIsland.y", nextIsland.getBlockY());
    	data.set("data.nextIsland.z", nextIsland.getBlockZ());
    	data.set("data.nextIsland.key", nextIslandKey);
    	data.set("data.toClear", toClear);
    }

	public void generateIsland(Location loc, Player p, Island playerIsland) {
		
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
	    Location np1 = new Location(skyWorld,x1-(config.getInt("ISLAND_WIDTH")/2),0,z1-(config.getInt("ISLAND_DEPTH")/2));
	    Location np2 = new Location(skyWorld,x1+(config.getInt("ISLAND_WIDTH")/2),skyWorld.getMaxHeight(),z1+(config.getInt("ISLAND_DEPTH")/2));
	    SkyblockPlayer sp = getSkyblockPlayer(p);
	    if (playerIsland == null) {
		    Island newIs = new Island(np1, np2, 100, nextIslandKey, p, skyWorld, data, this);
		    nextIslandKey++;
		    islands.add(newIs);
		    sp.setIsland(newIs);
	    } else {
	    	playerIsland.setP1(np1);
	    	playerIsland.setP2(np2);
	    	playerIsland.setPoints(100);
	    }
		sp.setVisiting(null);
	    sp.setHome(homeLoc);
	    Economy econ = new Economy(p.getUniqueId(), data);
	    econ.set(config.getDouble("STARTING_MONEY"));
	    
	    // set next island location
	    if (x1 < config.getInt("LIMIT_X")) {
	    	nextIsland = new Location(skyWorld, x1+config.getInt("ISLAND_WIDTH"), config.getInt("ISLAND_HEIGHT"), z1);
	    } else {
	    	nextIsland = new Location(skyWorld, -config.getInt("LIMIT_X"), config.getInt("ISLAND_HEIGHT"), z1+config.getInt("ISLAND_DEPTH"));
	    }
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
    
    @SuppressWarnings("rawtypes")
	public static int getNotNullLength(List list) {
    	int actualLength = 0;
    	for (int i = 0; i < list.size(); i++) {
    		if (list.get(i) != null) {
    			actualLength++;
    		}
    	}
    	return actualLength;
    }
    
    public void convertConfig(String _old, String _new) {
    	getLogger().info("Converting config from '" + _old + "' to '" + _new + "'...");
    	if (_old == "1.2.0" && _new == "1.2.1") {
    		ConfigurationSection oldData = (ConfigurationSection) config.get("data");
    		data.set("data", oldData);
    		config.set("data", null);
    		convertConfig("1.2.1", "1.2.2");
    	}
    	if (_old == "1.2.1" && _new == "1.2.2") {
            Set<String> keys = data.getConfigurationSection("data.players").getKeys(false);
            
            ConfigurationSection newData = data.createSection("newData");
            int islandIndex = 0;
            
            // We will store the islands we have already seen under the leaders UUID, because it
            // is a unique identifier.
            
            List<String> gottenIslands = new ArrayList<String>();
            
            for(String key : keys){
            	String leader = data.getString("data.players." + key + ".islandLeader");
            	if (!gottenIslands.contains(leader) && !gottenIslands.contains(key)) {
            		if (leader == null) {
            			newData.set("islands." + islandIndex + ".leader", key);
                		newData.set("islands." + islandIndex + ".members", data.getStringList("data.players." + key+ ".islandMembers"));
                		newData.set("islands." + islandIndex + ".points", data.getDouble("data.players." + key + ".islandPts"));
                		newData.set("islands." + islandIndex + ".allowVisitors", data.getBoolean("data.players." + key + ".settings.allowsVisitors"));
                		newData.set("islands." + islandIndex + ".canReset", data.getBoolean("data.players." + key + ".settings.resetLeft"));
            		} else {
            			newData.set("islands." + islandIndex + ".leader", leader);
                		newData.set("islands." + islandIndex + ".members", data.getStringList("data.players." + leader + ".islandMembers"));
                		newData.set("islands." + islandIndex + ".points", data.getDouble("data.players." + leader + ".islandPts"));
                		newData.set("islands." + islandIndex + ".allowVisitors", data.getBoolean("data.players." + leader + ".settings.allowsVisitors"));
                		newData.set("islands." + islandIndex + ".canReset", data.getBoolean("data.players." + leader + ".settings.resetLeft"));
            		}

            		newData.set("islands." + islandIndex + ".p1.x", data.getInt("data.players." + key + ".islandP1.x"));
            		newData.set("islands." + islandIndex + ".p1.y", data.getInt("data.players." + key + ".islandP1.y"));
            		newData.set("islands." + islandIndex + ".p1.z", data.getInt("data.players." + key + ".islandP1.z"));

            		newData.set("islands." + islandIndex + ".p2.x", data.getInt("data.players." + key + ".islandP2.x"));
            		newData.set("islands." + islandIndex + ".p2.y", data.getInt("data.players." + key + ".islandP2.y"));
            		newData.set("islands." + islandIndex + ".p2.z", data.getInt("data.players." + key + ".islandP2.z"));
            		
            		if (leader == null) {
            			gottenIslands.add(key);
            		} else {
            			gottenIslands.add(leader);
            		}
            		islandIndex++;
            	}
            }
            for (String key: keys) {
            	String leader = data.getString("data.players." + key + ".islandLeader");
            	newData.set("players." + key + ".balance", data.getDouble("data.players." + key + ".balance"));
            	if (leader != null) {
            		newData.set("players." + key + ".island", gottenIslands.indexOf(leader));
            	} else {
            		newData.set("players." + key + ".island", gottenIslands.indexOf(key));
            	}

            	newData.set("players." + key + ".home.x", data.getDouble("data.players." + key + ".islandHome.x"));
            	newData.set("players." + key + ".home.y", data.getDouble("data.players." + key + ".islandHome.y"));
            	newData.set("players." + key + ".home.z", data.getDouble("data.players." + key + ".islandHome.z"));
            	newData.set("players." + key + ".home.pitch", data.getInt("data.players." + key + ".islandHome.pitch"));
            	newData.set("players." + key + ".home.yaw", data.getInt("data.players." + key + ".islandHome.yaw"));
            }
            ConfigurationSection nextIsland = (ConfigurationSection) data.get("data.nextIsland");
            newData.set("nextIsland", nextIsland);
            newData.set("nextIsland.key", islandIndex);
            
            newData.set("toClear", data.getStringList("data.toClear"));
            
            data.set("data", newData);
            data.set("newData", null);
            
    		config.set("config-version", _new);
    		saveConfig();
        	try {
    			data.save(getDataFolder() + File.separator + "data.yml");
    		} catch (IOException e) {
                Bukkit.getLogger().warning("§4Could not save data.yml file.");
    		}
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
    
    public Island getPlayerIsland(Player p) {
    	for (int i = 0; i < islands.size(); i++) {
    		if (islands.get(i).hasPlayer(p)) {
    			return islands.get(i);
    		}
    	}
    	return null;
    }
    
    public Island getPlayerIsland(SkyblockPlayer p) {
    	return p.getIsland();
    }
    
    public Island getIslandByKey(int key) {
    	for (int i = 0; i < islands.size(); i++) {
    		if (islands.get(i).getKey() == key) {
    			return islands.get(i);
    		}
    	}
    	return null;
    }
    
    public SkyblockPlayer getSkyblockPlayer(Player p) {
    	for (int i = 0; i < players.size(); i++) {
    		if (players.get(i).getPlayer() == p) {
    			return players.get(i);
    		}
    	}
    	return null;
    }
    
    public SkyblockPlayer getSkyblockPlayer(UUID uuid) {
    	for (int i = 0; i < players.size(); i++) {
    		if (players.get(i).getPlayerUUID() == uuid) {
    			return players.get(i);
    		}
    	}
    	return null;
    }
    
    public SkyblockPlayer getSkyblockPlayer(OfflinePlayer p) {
    	if (getSkyblockPlayer(p.getUniqueId()) == null) {
    		SkyblockPlayer sp = new SkyblockPlayer(p.getUniqueId(), getPlayerIsland(p), skyWorld, data, this);
    		players.add(sp);
    		return sp;
    	} else {
    		return getSkyblockPlayer(p.getUniqueId());
    	}
    }
    
    public Island getPlayerIsland(OfflinePlayer p) {
    	if (getSkyblockPlayer(p.getUniqueId()) == null) {
    		String keyloc = "data.players." + p.getUniqueId().toString() + ".island";
    		if (data.isSet(keyloc)) {
    			int key = data.getInt(keyloc);
	    		Island pis = getIslandByKey(key);
	    		if (pis == null) {
	    			pis = new Island(key, skyWorld, data, this);
	    			islands.add(pis);
	    			return pis;
	    		}
    		} else {
    			return null;
    		}
    	} else {
    		return getSkyblockPlayer(p.getUniqueId()).getIsland();
    	}
    	return null;
    }
    
    public IslandInvite getIslandInvite(Island is, Player p) {
    	for (int i = 0; i < islandInvites.size(); i++) {
    		if (islandInvites.get(i).getTo() == p && islandInvites.get(i).getIsland() == is && islandInvites.get(i).isActive()) {
    			return islandInvites.get(i);
    		}
    	}
    	return null;
    }
}