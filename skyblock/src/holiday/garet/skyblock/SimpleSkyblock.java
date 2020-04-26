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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityMountEvent;

import holiday.garet.skyblock.economy.Economy;
import holiday.garet.skyblock.economy.Trade;
import holiday.garet.skyblock.economy.TradeRequest;
import holiday.garet.skyblock.event.PlayerCheckIslandLevelEvent;
import holiday.garet.skyblock.event.PlayerCreateIslandEvent;
import holiday.garet.skyblock.event.PlayerIslandSetHomeEvent;
import holiday.garet.skyblock.event.PlayerJoinIslandEvent;
import holiday.garet.skyblock.event.PlayerKickIslandEvent;
import holiday.garet.skyblock.event.PlayerLeaveIslandEvent;
import holiday.garet.skyblock.event.PlayerMakeIslandLeaderEvent;
import holiday.garet.skyblock.event.PlayerPayEvent;
import holiday.garet.skyblock.event.PlayerResetIslandEvent;
import holiday.garet.skyblock.event.PlayerSendIslandInviteEvent;
import holiday.garet.skyblock.event.PlayerShoutEvent;
import holiday.garet.skyblock.event.PlayerTeleportToIslandEvent;
import holiday.garet.skyblock.event.PlayerToggleFlyEvent;
import holiday.garet.skyblock.event.PlayerTradeOpenEvent;
import holiday.garet.skyblock.event.PlayerTradeRequestEvent;
import holiday.garet.skyblock.event.PlayerTrustAddEvent;
import holiday.garet.skyblock.event.PlayerTrustRemoveEvent;
import holiday.garet.skyblock.event.PlayerUpdateIslandSettingEvent;
import holiday.garet.skyblock.event.PlayerVisitIslandEvent;
import holiday.garet.skyblock.island.Island;
import holiday.garet.skyblock.island.IslandInvite;
import holiday.garet.skyblock.island.SkyblockPlayer;
import holiday.garet.skyblock.world.Generator;
import holiday.garet.skyblock.world.Structure;
import holiday.garet.skyblock.world.schematic.SchematicUtils;

@SuppressWarnings("deprecation")
public class SimpleSkyblock extends JavaPlugin implements Listener {
	
	FileConfiguration config = this.getConfig();
	FileConfiguration data = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "data.yml"));
	FileConfiguration language = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "language.yml"));
	
	SchematicUtils schematics;

	public static List<SkyblockPlayer> players = new ArrayList<SkyblockPlayer>();
	public static List<Island> islands = new ArrayList<Island>();
	List<TradeRequest> tradingRequests = new ArrayList<TradeRequest>();
	List<IslandInvite> islandInvites = new ArrayList<IslandInvite>();
	
	int nextIslandKey = 0;
	
	List<String> toClear = new ArrayList<String>();
	
	Location nextIsland;
	
	World skyWorld;
	World skyNether;
	
	Boolean usingVault = false;
	net.milkbowl.vault.economy.Economy vaultEconomy = null;
	Boolean usingSkript = false;
	
    @Override
    public void onEnable() {
        @SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);
    	Bukkit.getPluginManager().registerEvents(this, this);
    	if (config.isSet("data")) {
    		// if the config is out of date, lets fix that.
    		convertConfig("1.2.0","1.2.1");
    	} else if (config.getString("config-version").equalsIgnoreCase("1.2.1")) {
    		convertConfig("1.2.1","1.2.2");
    	} else if (config.getString("config-version").equalsIgnoreCase("1.3.7")) {
    		// config is up to date.
    	} else {
    		getLogger().warning("Your configuration file is out of date! You may continue to use it, but it may not contain the latest settings.");
    	}
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        config = this.getConfig();
    	reloadConfig();
    	File langFile = new File(getDataFolder(), "language.yml");
    	if (!langFile.exists()) {
    		try {
				language = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getResource("language.yml"), "UTF8"));
				language.save(getDataFolder() + File.separator + "language.yml");
			} catch (Exception e) {
				getLogger().warning("Unable to save language.yml!");
			}
    	}
    	
    	// check schematic loading
    	File sc1 = new File(getDataFolder() + File.separator + "structures" + File.separator + "default.schematic");
    	File sc2 = new File(getDataFolder() + File.separator + "structures" + File.separator + "nether.schematic");
    	File scdir = new File(getDataFolder() + File.separator + "structures");
    	if (!scdir.exists()) {
    		scdir.mkdir();
    	}
    	if (!sc1.exists()) {
    		try {
				sc1.createNewFile();
				OutputStream os1 = new FileOutputStream(sc1);
				byte[] buffer = new byte[this.getResource("default.schematic").available()];
				this.getResource("default.schematic").read(buffer);
				os1.write(buffer);
				os1.close();
			} catch (IOException e1) {
				getLogger().severe("An error occurred trying to save default.schematic!");
				e1.printStackTrace();
			}
    	}
    	if (!sc2.exists()) {
    		try {
				sc2.createNewFile();
				OutputStream os1 = new FileOutputStream(sc2);
				byte[] buffer = new byte[this.getResource("nether.schematic").available()];
				this.getResource("nether.schematic").read(buffer);
				os1.write(buffer);
				os1.close();
			} catch (IOException e1) {
				getLogger().severe("An error occurred trying to save default.schematic!");
				e1.printStackTrace();
			}
    	}
    	
    	schematics = new SchematicUtils(new File(getDataFolder() + File.separator + "structures"));
    	
    	
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
	                wc.generator(new Generator());
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
	                wc.generator(new Generator());
	                wc.environment(Environment.NETHER);
	                wc.createWorld();
	                skyNether = getServer().getWorld(worldName + "_nether");
                }
            }, 0L);
    	}
    	toClear = data.getStringList("data.toClear");
    	if (data.isSet("data.nextIsland")) {
    		nextIsland = new Location(skyWorld, data.getInt("data.nextIsland.x"), config.getInt("ISLAND_HEIGHT"), data.getInt("data.nextIsland.x"));
    	} else {
    		nextIsland = new Location(skyWorld, -config.getInt("LIMIT_X"), config.getInt("ISLAND_HEIGHT"),-config.getInt("LIMIT_Z"));
    	}
    	if (data.isSet("data.nextIsland.key")) {
    		nextIslandKey = data.getInt("data.nextIsland.key");
    	}
    	if (getServer().getPluginManager().getPlugin("Vault")!=null) {
    		RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
    		if (rsp != null) {
            	vaultEconomy = rsp.getProvider();
            	usingVault = true;
    		}
    	}
    	if (getServer().getPluginManager().getPlugin("Skript")!=null) {
    		usingSkript = true;
    		/*
    		 * Events:
    		 * - onPlayerCreateIsland (called after reset or create)
    		 * - onPlayerResetIsland (called after reset)
    		 * - onPlayerTeleportToIsland
    		 * - onPlayerVisitIsland
    		 * - onPlayerUpdateIslandSetting
    		 * - onPlayerSetHome
    		 * - onPlayerIslandInvite
    		 * - onPlayerJoinIsland
    		 * - onPlayerLeaveIsland
    		 * - onPlayerKickedIsland
    		 * - onPlayerMakeLeader
    		 * - onPlayerCheckIslandLevel
    		 * - onPlayerTradeRequest
    		 * - onPlayerTradeAccept
    		 * - onPlayerTradeDeny
    		 * - onPlayerTradeOpen
    		 * - onPlayerPay
    		 * - onPlayerToggleFly
    		 */
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
    			players.add(new SkyblockPlayer(p, playerIsland, skyWorld, skyNether, data, this));
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
    	if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new SkyPlaceholderExpansion(this).register();
    	}
    	
    	if (schematics.getSchematic("default") == null) {
    		getLogger().warning("Default schematic could not be loaded!");
    	}
    	
    	if (schematics.getSchematic("nether") == null) {
    		getLogger().warning("Nether schematic could not be loaded!");
    	}
    }
    
    public String getLanguage(String _key) {
    	if (language.isSet(_key)) {
    		return language.getString(_key).replace("&", "§");
    	}
    	getLogger().warning(_key + " not found in language.yml!");
    	return "";
    }
    
    public boolean activateVault() {
    	return true;
    }
    
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
    	return new Generator();
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
			            PlayerCreateIslandEvent e = new PlayerCreateIslandEvent(sp, p, nextIsland, false);
			            Bukkit.getPluginManager().callEvent(e);
			            if (!e.getCancelled()) {
				            sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("generating-island"));
			            	generateIsland(e.getIslandLocation(), p, null, e.getOverrideResetMoney());
			            }
			            return true;
	        		} else {
	        			PlayerTeleportToIslandEvent e = new PlayerTeleportToIslandEvent(p, sp, playerIsland, true);
	        			Bukkit.getPluginManager().callEvent(e);
	        			if (!e.getCancelled()) {
		        			sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("teleporting-to-island"));
		        			p.teleport(sp.getHome(), TeleportCause.PLUGIN);
		        			if (e.getSetSkySpawn()) {
		        				sp.setSkySpawn(p.getLocation());
		        			}
		        			p.setFallDistance(0);
		        			sp.setVisiting(null);
	        			}
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
	            		if (config.getBoolean("USE_TRUSTS")) {
		            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is trust <add | remove> <player>" + ChatColor.RESET + ": trust a player as if they were a member of your island.");
	            		}
	            		return true;
            		}
            	} else if (args[0].equalsIgnoreCase("home")) {
            		if (sp.getHome() != null) {
	        			PlayerTeleportToIslandEvent e = new PlayerTeleportToIslandEvent(p, sp, playerIsland, true);
	        			Bukkit.getPluginManager().callEvent(e);
	        			if (!e.getCancelled()) {
		        			sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("teleporting-to-island"));
		        			p.teleport(sp.getHome(), TeleportCause.PLUGIN);
		        			if (e.getSetSkySpawn()) {
		        				sp.setSkySpawn(p.getLocation());
		        			}
		        			p.setFallDistance(0);
		        			sp.setVisiting(null);
	        			}
	        			return true;
            		} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("sethome")) {
            		if (playerIsland != null) {
	            		if (playerIsland.inBounds(p.getLocation())) {
	            			PlayerIslandSetHomeEvent e = new PlayerIslandSetHomeEvent(p, sp, playerIsland, p.getLocation(), true);
	            			Bukkit.getPluginManager().callEvent(e);
	            			if (!e.getCancelled()) {
			            		sp.setHome(e.getHomeLocation());
			            		if (e.getSetSkySpawn()) {
			            			sp.setSkySpawn(e.getHomeLocation());
			            		}
			            		sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("set-island-home"));
	            			}
	            		} else {
	            			sender.sendMessage(ChatColor.RED + getLanguage("not-on-island"));
	            		}
	            		return true;
            		} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("reset")) {
            		if (playerIsland != null) {
	            		if (playerIsland.getLeader().toString().equalsIgnoreCase(p.getUniqueId().toString())) {
		            		if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
		            			if (playerIsland.canReset() || config.getBoolean("INFINITE_RESETS")) {
		            				Boolean goAhead = false;
		            				Double rcost = null;
		            				if (config.isBoolean("RESET_COST")) {
		            					goAhead = true;
		            				} else {
					            		if (!usingVault) {
						            		Economy tempEcon = new Economy(p.getUniqueId(), data);
						            		int resetNumber = config.getInt("RESET_COUNT") - sp.getIsland().getResetsLeft();
						            		List<Double> resetCost = config.getDoubleList("RESET_COST");
						            		if (resetNumber >= resetCost.size()) {
						            			resetNumber = resetCost.size() - 1;
						            		}
						            		if (tempEcon.get() >= resetCost.get(resetNumber)) {
						            			goAhead = true;
						            			rcost = resetCost.get(resetNumber);
						            		}
					            		} else {
						            		int resetNumber = config.getInt("RESET_COUNT") - sp.getIsland().getResetsLeft();
						            		List<Double> resetCost = config.getDoubleList("RESET_COST");
						            		if (resetNumber >= resetCost.size()) {
						            			resetNumber = resetCost.size() - 1;
						            		}
						            		if (vaultEconomy.getBalance(p) >= resetCost.get(resetNumber)) {
						            			goAhead = true;
						            			rcost = resetCost.get(resetNumber);
						            		}
					            		}
		            				}
				            		if (goAhead) {
				            			PlayerResetIslandEvent e = new PlayerResetIslandEvent(p, sp, playerIsland, rcost, nextIsland);
				            			Bukkit.getPluginManager().callEvent(e);
				            			if (!e.getCancelled()) {
					            			if (e.getResetCost() != null) {
							            		if (!usingVault) {
								            		Economy tempEcon = new Economy(p.getUniqueId(), data);
								            		tempEcon.withdraw(e.getResetCost());
							            		} else {
							            			vaultEconomy.withdrawPlayer(p, e.getResetCost());
							            		}
					            				generateIsland(e.getLocation(), p, playerIsland, true);
					            			} else {
					            				generateIsland(e.getLocation(), p, playerIsland, false);
					            			}
								            List<UUID> islandMembers = playerIsland.getMembers();
								            for (int i = 0; i < islandMembers.size(); i++) {
								            	if (islandMembers.get(i) != null) {
								            		Player tempP = getServer().getPlayer(islandMembers.get(i));
								            		SkyblockPlayer tempSP = getSkyblockPlayer(tempP);
								            		tempSP.setHome(sp.getHome());
								            		if (!usingVault) {
									            		Economy tempEcon = new Economy(islandMembers.get(i), data);
									            		tempEcon.set(0);
								            		} else {
								            			OfflinePlayer op = getServer().getOfflinePlayer(islandMembers.get(i));
								            			vaultEconomy.withdrawPlayer(op, vaultEconomy.getBalance(op));
								            		}
								            		if (tempP == null) {
								            			toClear.add(islandMembers.get(i).toString());
								            		} else {
								            			PlayerInventory tempInv = tempP.getInventory();
								            			tempInv.clear();
								            			tempInv.setArmorContents(new ItemStack[4]);
								            			tempP.teleport(sp.getHome(), TeleportCause.PLUGIN);
								            			tempP.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("reset-success"));
								            		}
								            	}
								            }
								            PlayerInventory inv = p.getInventory();
								            inv.clear();
								            inv.setArmorContents(new ItemStack[4]);
								            sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("reset-success"));
				            			}
							            return true;
				            		} else {
			            				sender.sendMessage(ChatColor.RED + getLanguage("reset-not-enough-money"));
			            				return true;
				            		}
		            			} else {
		            				sender.sendMessage(ChatColor.RED + getLanguage("reset-no-resets"));
		            				return true;
		            			}
		            		} else {
		            			if (!config.isBoolean("RESET_COST")) {
				            		int resetNumber = config.getInt("RESET_COUNT") - sp.getIsland().getResetsLeft();
				            		List<Double> resetCost = config.getDoubleList("RESET_COST");
				            		if (resetNumber >= resetCost.size()) {
				            			resetNumber = resetCost.size() - 1;
				            		}
					        		DecimalFormat dec = new DecimalFormat("#0.00");
				            		sender.sendMessage(ChatColor.GREEN + getLanguage("reset-confirm-cost").replace("{money}", String.valueOf(dec.format(resetCost.get(resetNumber)))));
		            			}
		            			sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("reset-confirm"));
		            			if (!config.getBoolean("INFINITE_RESETS")) {
		            				sender.sendMessage(ChatColor.RED + getLanguage("reset-warn").replace("{resets}", String.valueOf(playerIsland.getResetsLeft())));
		            			}
		            			return true;
		            		}
	            		} else {
	            			sender.sendMessage(ChatColor.RED + getLanguage("leader-only"));
	            			return true;
	            		}
            		} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
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
	            				sender.sendMessage(ChatColor.RED + getLanguage("visit-no-island"));
	            				return true;
	            			}
            			} else {
            				pName = getServer().getPlayer(args[1]).getName();
            				getServer().getPlayer(args[1]).getUniqueId().toString();
            				sz = getSkyblockPlayer(getServer().getPlayer(args[1]));
            			}
            			Boolean allowsVisitors = false;
            			if (sz.getIsland() != null) {
            				allowsVisitors = sz.getIsland().allowsVisitors();
            			}
            			if (playerIsland != null && sz.getIsland() == playerIsland) {
            				sender.sendMessage(ChatColor.RED + getLanguage("visit-own-island"));
            				return true;
            			}
            			if ((sz.getHome() != null && !(p.getName().equalsIgnoreCase(args[1])) && allowsVisitors) || p.hasPermission("skyblock.admin")) {
                			PlayerVisitIslandEvent e = new PlayerVisitIslandEvent(p, sp, sz);
                			Bukkit.getPluginManager().callEvent(e);
                			if (!e.getCancelled()) {
	            				p.teleport(sz.getHome(), TeleportCause.PLUGIN);
			            		sp.setSkySpawn(p.getLocation());
			        			p.setFallDistance(0);
			        			sp.setVisiting(sz);
	                			sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("visit-teleport").replace("{player}", pName));
                			}
                			return true;
            			} else if (p.getName().equalsIgnoreCase(args[1])) { 
            				sender.sendMessage(ChatColor.RED + getLanguage("visit-own-island"));
            				return true;
            			} else if (!(allowsVisitors)) {
            				sender.sendMessage(ChatColor.RED + getLanguage("visit-no-visitors"));
            				return true;
            			} else {
            				sender.sendMessage(ChatColor.RED + getLanguage("visit-no-island"));
            				return true;
            			}
            		} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("visit-usage"));
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
			            			ChatColor visitorsCanPortal = ChatColor.RED;
			            			if (playerIsland.visitorsCanPortal()) {
			            				visitorsCanPortal = ChatColor.GREEN;
			            			}
			            			sender.sendMessage(getLanguage("settings") + allowVisitors + "allow-visitors" + ChatColor.RESET + ", " + visitorsCanRideMobs + "visitors-can-ride-mobs" + ChatColor.RESET + ", " + visitorsCanPortal + "visitors-can-portal");
			            			return true;
			            		} else if (args[1].equalsIgnoreCase("allow-visitors")) {
			            			if (args.length > 2) {
			            				Boolean val = null;
			            				if (args[2].equalsIgnoreCase("true")) {
			            					val = true;
			            				} else if (args[2].equalsIgnoreCase("false")) {
			            					val = false;
			            				}
		            					PlayerUpdateIslandSettingEvent e = new PlayerUpdateIslandSettingEvent(p, sp, playerIsland, args[1], val);
		            					Bukkit.getPluginManager().callEvent(e);
		            					if (!e.getCancelled()) {
				            				if (e.getValue() == true) {
				            					playerIsland.setAllowsVisitors(e.getValue());
				            					sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("settings-set-to-true").replace("{setting}", "allow-visitors"));
				            				} else if (e.getValue() == false) {
				            					playerIsland.setAllowsVisitors(e.getValue());
				            					sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("settings-set-to-false").replace("{setting}", "allow-visitors"));
				            				} else {
					                			sender.sendMessage(ChatColor.RED + getLanguage("settings-usage"));
				            				}
		            					}
			            				return true;
			            			} else {
			                			sender.sendMessage(ChatColor.RED + getLanguage("settings-usage"));
			                			return true;
			            			}
			            		} else if (args[1].equalsIgnoreCase("visitors-can-ride-mobs")) {
			            			if (args.length > 2) {
			            				Boolean val = null;
			            				if (args[2].equalsIgnoreCase("true")) {
			            					val = true;
			            				} else if (args[2].equalsIgnoreCase("false")) {
			            					val = false;
			            				}
		            					PlayerUpdateIslandSettingEvent e = new PlayerUpdateIslandSettingEvent(p, sp, playerIsland, args[1], val);
		            					Bukkit.getPluginManager().callEvent(e);
		            					if (!e.getCancelled()) {
				            				if (e.getValue() == true) {
				            					playerIsland.setVisitorsCanRideMobs(true);
				            					sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("settings-set-to-true").replace("{setting}", "visitors-can-ride-mobs"));
				            				} else if (e.getValue() == false) {
				            					playerIsland.setVisitorsCanRideMobs(false);
				            					sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("settings-set-to-false").replace("{setting}", "visitors-can-ride-mobs"));
				            				} else {
					                			sender.sendMessage(ChatColor.RED + getLanguage("settings-usage"));
				            				}
		            					}
			            				return true;
			            			} else {
			                			sender.sendMessage(ChatColor.RED + getLanguage("settings-usage"));
			                			return true;
			            			}
			            		} else if (args[1].equalsIgnoreCase("visitors-can-portal")) {
			            			if (args.length > 2) {
			            				Boolean val = null;
			            				if (args[2].equalsIgnoreCase("true")) {
			            					val = true;
			            				} else if (args[2].equalsIgnoreCase("false")) {
			            					val = false;
			            				}
		            					PlayerUpdateIslandSettingEvent e = new PlayerUpdateIslandSettingEvent(p, sp, playerIsland, args[1], val);
		            					Bukkit.getPluginManager().callEvent(e);
		            					if (!e.getCancelled()) {
				            				if (e.getValue() == true) {
				            					playerIsland.setVisitorsCanPortal(true);
				            					sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("settings-set-to-true").replace("{setting}", "visitors-can-portal"));
				            				} else if (e.getValue() == false) {
				            					playerIsland.setVisitorsCanPortal(false);
				            					sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("settings-set-to-false").replace("{setting}", "visitors-can-portal"));
				            				} else {
					                			sender.sendMessage(ChatColor.RED + getLanguage("settings-usage"));
				            				}
		            					}
			            				return true;
			            			} else {
			                			sender.sendMessage(ChatColor.RED + getLanguage("settings-usage"));
			                			return true;
			            			}
			            		} else {
		                			sender.sendMessage(ChatColor.RED + getLanguage("setting-not-found"));
		                			return true;
			            		}
		            		} else {
		            			sender.sendMessage(ChatColor.RED + getLanguage("settings-usage"));
		            			return true;
		            		}
	            		} else {
	            			sender.sendMessage(ChatColor.RED + getLanguage("leader-only"));
	            			return true;
	            		}
            		} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
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
				            				PlayerSendIslandInviteEvent e = new PlayerSendIslandInviteEvent(p, sp, playerIsland, r);
				            				Bukkit.getPluginManager().callEvent(e);
				            				if (!e.getCancelled()) {
					            				sender.sendMessage(ChatColor.GREEN + getLanguage("invite-success").replace("{player}", r.getName()));
					            				islandInvites.add(new IslandInvite(r, playerIsland, this));
				            				}
				            				return true;
			            				} else {
			            					p.sendMessage(ChatColor.RED + getLanguage("invite-already-member"));
			            					return true;
			            				}
		            				} else {
			                			sender.sendMessage(ChatColor.RED + getLanguage("invite-already-invited"));
			                			return true;
		            				}
		            			} else {
		                			sender.sendMessage(ChatColor.RED + getLanguage("invite-not-online"));
		                			return true;
		            			}
		            		} else {
		            			sender.sendMessage(ChatColor.RED + getLanguage("invite-usage"));
		            			return true;
		            		}
	            		} else {
	            			sender.sendMessage(ChatColor.RED + getLanguage("leader-only"));
	            			return true;
	            		}
            		} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
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
	            					PlayerJoinIslandEvent e = new PlayerJoinIslandEvent(p, sp, playerIsland, toJoin);
	            					Bukkit.getPluginManager().callEvent(e);
	            					if (!e.getCancelled()) {
		            					if (playerIsland != null) {
		            						playerIsland.messageAllMembers(ChatColor.GREEN + getLanguage("leave").replace("{player}", p.getName()), p);
			    	        				playerIsland.leaveIsland(p);
		            					}
		            					ii.setActive(false);
		            					sp.setHome(sj.getHome());
		            					if (usingVault) {
		            						vaultEconomy.withdrawPlayer(p,vaultEconomy.getBalance(p));
		            					} else {
		            						Economy econ = new Economy(p.getUniqueId(), data);
			            					econ.set(0); // set to 0 to prevent abuse.
		            					}
		    				            PlayerInventory inv = p.getInventory();
		    				            inv.clear();
		    				            inv.setArmorContents(new ItemStack[4]);
		    				            p.teleport(sj.getHome(), TeleportCause.PLUGIN);
		    				            p.setFallDistance(0);
		    		        			sp.setVisiting(null);
		    				    	    sp.setSkySpawn(sj.getHome());
		                    			toJoin.addPlayer(p);
		                    			sp.setIsland(toJoin);
		                    			toJoin.messageAllMembers(ChatColor.GREEN + getLanguage("joined").replace("{player}", p.getName()), p);
		                    			p.sendMessage(ChatColor.GREEN + getLanguage("i-joined").replace("{player}", j.getName()));
	            					}
	    				            return true;
	            				} else {
	                    			sender.sendMessage(ChatColor.RED + getLanguage("not-invited"));
	                    			return true;
	            				}
	            			} else {
	                			sender.sendMessage(ChatColor.RED + getLanguage("join-not-online"));
	                			return true;
	            			}
            			} else {
                			sender.sendMessage(ChatColor.RED + getLanguage("join-leader"));
                			return true;
            			}
        	    	} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("join-usage"));
            			return true;
        	    	}
        		} else if (args[0].equalsIgnoreCase("leave")) {
        			if (playerIsland != null) {
	        			if (!playerIsland.getLeader().toString().equalsIgnoreCase(p.getUniqueId().toString())) {
	        				PlayerLeaveIslandEvent e = new PlayerLeaveIslandEvent(p, sp, playerIsland);
	        				Bukkit.getPluginManager().callEvent(e);
	        				if (!e.getCancelled()) {
		        				playerIsland.messageAllMembers(ChatColor.GREEN + getLanguage("leave").replace("{player}", p.getName()), p);
		        				playerIsland.leaveIsland(p);
		        				sp.setHome(null);
		        				sp.setIsland(null);
		            			sender.sendMessage(ChatColor.GREEN + getLanguage("leave-success"));
	        				}
	        				return true;
	        			} else {
	            			sender.sendMessage(ChatColor.RED + getLanguage("leave-leader"));
	            			return true;
	        			}
        			} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
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
                						sender.sendMessage(ChatColor.RED + getLanguage("kick-yourself"));
                						return true;
                					}
                					ps = getSkyblockPlayer(k);
            					}
        						if (playerIsland.hasPlayer(pUUID)) {
        							PlayerKickIslandEvent e = new PlayerKickIslandEvent(p, sp, playerIsland, ps);
        							Bukkit.getPluginManager().callEvent(e);
        							if (!e.getCancelled()) {
	        							ps.setHome(null);
	        							ps.setIsland(null);
	        							playerIsland.leaveIsland(pUUID);
	            		        		sender.sendMessage(ChatColor.GREEN + getLanguage("kick").replace("{player}", kName));
	            		        		if (k != null) {
	            		        			k.sendMessage(ChatColor.RED + getLanguage("kicked"));
	            		        		}
	            		        		playerIsland.messageAllMembers(ChatColor.GREEN + getLanguage("leave").replace("{player}", kName), p);
        							}
            		        		return true;
        						} else {
                					sender.sendMessage(ChatColor.RED + getLanguage("kick-not-member"));
                					return true;
        						}
            				} else {
            					sender.sendMessage(ChatColor.RED + getLanguage("kick-usage"));
            					return true;
            				}
            			} else {
                			sender.sendMessage(ChatColor.RED + getLanguage("leader-only"));
                			return true;
            			}
            		} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
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
        				sender.sendMessage(ChatColor.AQUA + getLanguage("island-members") + ChatColor.GRAY + ":");
        				sender.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.GOLD + getLanguage("island-leader") + " " + leaderColor + leaderName);
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
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
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
	        								PlayerMakeIslandLeaderEvent e = new PlayerMakeIslandLeaderEvent(p, sp, playerIsland, l);
	        								Bukkit.getPluginManager().callEvent(e);
	        								if (!e.getCancelled()) {
			        							playerIsland.makeLeader(l);
			        							sender.sendMessage(ChatColor.GREEN + getLanguage("promoted").replace("{player}", l.getName()));
			        							l.sendMessage(ChatColor.GREEN + getLanguage("promoted-you"));
	        								}
		        							return true;
	        							} else {
	                            			sender.sendMessage(ChatColor.RED + getLanguage("promoted-not-member"));
	                            			return true;
	        							}
            						} else {
            							sender.sendMessage(ChatColor.RED + getLanguage("promoted-already-leader"));
            							return true;
            						}
        						} else {
                        			sender.sendMessage(ChatColor.RED + getLanguage("promoted-not-online"));
                        			return true;
        						}
        					} else {
                    			sender.sendMessage(ChatColor.RED + getLanguage("promoted-usage"));
                    			return true;
        					}
        				} else {
                			sender.sendMessage(ChatColor.RED + getLanguage("leader-only"));
                			return true;
        				}
        			} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
            			return true;
        			}
            	} else if (args[0].equalsIgnoreCase("level")) {
            		if (playerIsland != null) {
            			int isLevel = 0;
            			isLevel = (int)Math.floor(Math.sqrt(playerIsland.getPoints())-9);
            			if (isLevel < 0) {
            				isLevel = 0;
            			}
            			PlayerCheckIslandLevelEvent e = new PlayerCheckIslandLevelEvent(p, sp, playerIsland, isLevel, playerIsland.getPoints());
            			Bukkit.getPluginManager().callEvent(e);
            			if (!e.getCancelled()) {
            				sender.sendMessage(ChatColor.GREEN + getLanguage("level").replace("{level}", String.valueOf(e.getLevel())));
            			}
	            		return true;
            		} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
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
        		} else if (args[0].equalsIgnoreCase("trust")) {
        			if (playerIsland != null) {
        				if (playerIsland.getLeader().toString().equalsIgnoreCase(p.getUniqueId().toString())) {
        					if (sender.hasPermission("skyblock.trust")) {
        						if (config.getBoolean("USE_TRUSTS")) {
		        					if (args.length == 3) {
		        						if (args[1].equalsIgnoreCase("add")) {
		        							OfflinePlayer op = getServer().getOfflinePlayer(args[2]);
		        							if (playerIsland.trustContains(op.getUniqueId())) {
		        								sender.sendMessage(ChatColor.RED + getLanguage("trust-already-trusted"));
		        								return true;
		        							} else {
		        								PlayerTrustAddEvent e = new PlayerTrustAddEvent(p, sp, playerIsland, op.getUniqueId());
		        								Bukkit.getPluginManager().callEvent(e);
		        								if (!e.getCancelled()) {
				        							playerIsland.trustPlayer(op.getUniqueId());
				        							sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("trust-added"));
		        								}
			        							return true;
		        							}
		        						} else if (args[1].equalsIgnoreCase("remove")) {
		        							OfflinePlayer op = getServer().getOfflinePlayer(args[2]);
		        							if (playerIsland.trustContains(op.getUniqueId())) {
		        								PlayerTrustRemoveEvent e = new PlayerTrustRemoveEvent(p, sp, playerIsland, op.getUniqueId());
		        								Bukkit.getPluginManager().callEvent(e);
		        								if (!e.getCancelled()) {
			            							playerIsland.removeTrust(op.getUniqueId());
				        							sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("trust-removed"));
		        								}
			        							return true;
		        							} else {
		        								sender.sendMessage(ChatColor.RED + getLanguage("trust-not-trusted"));
		        								return true;
		        							}
		        						} else {
		                        			sender.sendMessage(ChatColor.RED + getLanguage("trust-usage"));
		                        			return true;
		        						}
		        					} else {
		                    			sender.sendMessage(ChatColor.RED + getLanguage("trust-usage"));
		                    			return true;
		        					}
	        					} else {
	                    			sender.sendMessage(ChatColor.RED + getLanguage("trust-disabled"));
	                    			return true;
	        					}
        					} else {
                    			sender.sendMessage(ChatColor.RED + getLanguage("no-permission"));
                    			return true;
        					}
        				} else {
                			sender.sendMessage(ChatColor.RED + getLanguage("leader-only"));
                			return true;
        				}
        			} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
            			return true;
        			}
        		} else {
            		sender.sendMessage(getLanguage("unknown-command"));
            		return true;
            	}
        	} else {
        		sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
	            return true;
        	}
        } else if (command.getName().equalsIgnoreCase("bal") || command.getName().equalsIgnoreCase("balance")) {
        	if (usingVault) {
        		if (sender instanceof Player) {
        			if (args.length == 0) {
        				Player p = (Player) sender;
		        		DecimalFormat dec = new DecimalFormat("#0.00");
						sender.sendMessage(getLanguage("balance").replace("{money}", dec.format(vaultEconomy.getBalance(p))));
		        		return true;
        			} else {
        				OfflinePlayer op = getServer().getOfflinePlayer(args[0]);
		        		DecimalFormat dec = new DecimalFormat("#0.00");
						sender.sendMessage(getLanguage("balance-other").replace("{player}", op.getName()).replace("{money}",dec.format(vaultEconomy.getBalance(op))));
		        		return true;
        			}
        		} else {
	        		sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
	        		return true;
	        	}
        	} else if (config.getBoolean("USE_ECONOMY")) {
	        	if (sender instanceof Player) {
	        		if (args.length == 0) {
		        		Player p = (Player) sender;
		        		Economy econ = new Economy(p.getUniqueId(), data);
		        		DecimalFormat dec = new DecimalFormat("#0.00");
						sender.sendMessage(getLanguage("balance").replace("{money}", dec.format(econ.get())));
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
						sender.sendMessage(getLanguage("balance-other").replace("{player}", pName).replace("{money}", dec.format(econ.get())));
	        			return true;
	        		}
	        	} else {
	        		sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
	        		return true;
	        	}
        	} else {
        		sender.sendMessage(ChatColor.RED + getLanguage("economy-disabled"));
        		return true;
        	}
        } else if (command.getName().equalsIgnoreCase("pay")) {
        	if (usingVault) {
        		if (sender instanceof Player) {
        			Player p = (Player) sender;
        			if (args.length > 1) {
        				Player r = getServer().getPlayer(args[0]);
        				if (r != null) {
        					double payAmount;
	        			    try {
	            				payAmount = Double.valueOf(args[1]);
	        			    } catch (NumberFormatException | NullPointerException nfe) {
	        					sender.sendMessage(ChatColor.RED + getLanguage("money-not-valid"));
	        			        return true;
	        			    }
	        			    if (vaultEconomy.getBalance(p) >= payAmount && payAmount > 0) {
	        			    	PlayerPayEvent e = new PlayerPayEvent(p, r, payAmount);
	        			    	Bukkit.getPluginManager().callEvent(e);
	        			    	if (!e.getCancelled()) {
		        			    	vaultEconomy.withdrawPlayer(p, payAmount);
		        			    	vaultEconomy.depositPlayer(r, payAmount);
		        	        		DecimalFormat dec = new DecimalFormat("#0.00");
		        					sender.sendMessage(ChatColor.GREEN + getLanguage("pay-send").replace("{money}", dec.format(payAmount)).replace("{player}", r.getName()));
		        					r.sendMessage(ChatColor.GREEN + getLanguage("pay-receive").replace("{money}", dec.format(payAmount)).replace("{player}", p.getName()));
	        			    	}
	        					return true;
	        			    } else if (payAmount <= 0) {
	        					sender.sendMessage(ChatColor.RED + getLanguage("money-not-valid"));
	        					return true;
	        			    } else {
	        					sender.sendMessage(ChatColor.RED + getLanguage("not-enough-money"));
	        					return true;
	        			    }
        				} else {
	        				sender.sendMessage(ChatColor.RED + getLanguage("receiver-offline"));
	        				return true;
	        			}
        			} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("pay-usage"));
            			return true;
            		}
	        	} else {
	        		sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
	        		return true;
	        	}
        	} else if (config.getBoolean("USE_ECONOMY")) {
	        	if (sender instanceof Player) {
	        		Player p = (Player) sender;
	        		if (args.length > 1) {
	        			Player r = getServer().getPlayer(args[0]);
	        			if (r != null) {
	        				double payAmount;
	        			    try {
	            				payAmount = Double.valueOf(args[1]);
	        			    } catch (NumberFormatException | NullPointerException nfe) {
	        					sender.sendMessage(ChatColor.RED + getLanguage("money-not-valid"));
	        			        return true;
	        			    }
	        			    Economy pecon = new Economy(p.getUniqueId(), data);
	        				if (payAmount <= pecon.get() && payAmount > 0) {
	        					PlayerPayEvent e = new PlayerPayEvent(p, r, payAmount);
	        					Bukkit.getPluginManager().callEvent(e);
	        					if (!e.getCancelled()) {
		        					pecon.withdraw(payAmount);
		        					Economy recon = new Economy(r.getUniqueId(), data);
		        					recon.deposit(payAmount);
		        	        		DecimalFormat dec = new DecimalFormat("#0.00");
		        					sender.sendMessage(ChatColor.GREEN + getLanguage("pay-send").replace("{money}", dec.format(payAmount)).replace("{player}", r.getName()));
		        					r.sendMessage(ChatColor.GREEN + getLanguage("pay-receive").replace("{money}", dec.format(payAmount)).replace("{player}", p.getName()));
		        					if (r.getName() == sender.getName()) {
		        						sender.sendMessage(ChatColor.LIGHT_PURPLE + "Congratulations, you payed yourself.");
		        					}
	        					}
	        					return true;
	        				} else if (payAmount <= 0) {
	        					sender.sendMessage(ChatColor.RED + getLanguage("money-not-valid"));
	        					return true;
	        				} else {
	        					sender.sendMessage(ChatColor.RED + getLanguage("not-enough-money"));
	        					return true;
	        				}
	        			} else {
	        				sender.sendMessage(ChatColor.RED + getLanguage("receiver-offline"));
	        				return true;
	        			}
	        		} else {
	        			sender.sendMessage(ChatColor.RED + getLanguage("pay-usage"));
	        			return true;
	        		}
	        	} else {
	        		sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
	        		return true;
	        	}
        	} else {
        		sender.sendMessage(ChatColor.RED + getLanguage("economy-disabled"));
        		return true;
        	}
        } else if (command.getName().equalsIgnoreCase("fly")) {
        	if (sender instanceof Player) {
        		Player p = (Player) sender;
        		PlayerToggleFlyEvent e = new PlayerToggleFlyEvent(p, !p.getAllowFlight());
        		Bukkit.getPluginManager().callEvent(e);
        		if (!e.getCancelled()) {
	        		p.setAllowFlight(e.getValue());
	        		if (p.getAllowFlight()) {
	        			sender.sendMessage(ChatColor.GREEN + getLanguage("fly-enable"));
	        		} else {
	        			sender.sendMessage(ChatColor.GREEN + getLanguage("fly-disable"));
	        		}
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
	        		PlayerShoutEvent e = new PlayerShoutEvent(sender, m);
	        		Bukkit.getPluginManager().callEvent(e);
	        		if (!e.getCancelled()) {
		        		for (Player p: getServer().getOnlinePlayers()) {
		    				p.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "World" + ChatColor.GRAY + "] " + ChatColor.WHITE + sender.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + e.getMessage());
		        		}
		        		getLogger().info("[World] " + sender.getName() + ": " + m);
	        		}
	        		return true;
	        	} else {
	        		sender.sendMessage(ChatColor.RED + getLanguage("shout-usage"));
	        		return true;
	        	}
        	} else {
        		sender.sendMessage(ChatColor.RED + getLanguage("chatrooms-disabled"));
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
		        						PlayerTradeOpenEvent e = new PlayerTradeOpenEvent(t, p);
		        						Bukkit.getPluginManager().callEvent(e);
		        						if (!e.getCancelled()) {
			        						Trade trade = null;
			        						if (usingVault) {
			        							trade = new Trade(t, p, this, vaultEconomy);
			        						} else {
			        							trade = new Trade(t, p, this, new Economy(t.getUniqueId(), data), new Economy(p.getUniqueId(), data));
			        						}
				        					this.getServer().getPluginManager().registerEvents(trade, this);
				        					trade.open();
				        					trec.close();
		        						}
			        					return true;
		        					} else {
			        					sender.sendMessage(ChatColor.RED + getLanguage("trade-not-close"));
			        					return true;
		        					}
		        				} else if (t == p) {
		        					sender.sendMessage(ChatColor.RED + getLanguage("trade-yourself"));
		        					return true;
		        				} else {
		        					sender.sendMessage(ChatColor.RED + getLanguage("trade-not-online"));
		        					return true;
		        				}
		        			} else {
		        				sender.sendMessage(ChatColor.RED + getLanguage("trade-no-requests"));
		        				return true;
		        			}
		        		} else {
		        			if (t != null && t != p) {
	        					if (p.getLocation().distance(t.getLocation()) < 10) {
			        				TradeRequest trt = getTradeRequestToPlayer(t);
	        						if (trt == null || trt.from() != p) {
	        							PlayerTradeRequestEvent e = new PlayerTradeRequestEvent(p, t);
	        							Bukkit.getPluginManager().callEvent(e);
	        							if (!e.getCancelled()) {
					        				TradeRequest treq = new TradeRequest(p,t,this);
					        				tradingRequests.add(treq);
	        							}
				        				return true;
	        						} else {
	        							sender.sendMessage(ChatColor.RED + getLanguage("trade-already-requested"));
	        							return true;
	        						}
	        					} else {
		        					sender.sendMessage(ChatColor.RED + getLanguage("trade-not-close"));
		        					return true;
	        					}
		        			} else if (t == p) {
	        					sender.sendMessage(ChatColor.RED + getLanguage("trade-yourself"));
	        					return true;
		        			} else {
		        				sender.sendMessage(ChatColor.RED + getLanguage("trade-not-online"));
		        				return true;
		        			}
		        		}
		        	} else {
		        		sender.sendMessage(ChatColor.RED + getLanguage("trade-usage"));
		        		return true;
		        	}
	        	} else {
	        		sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
	        		return true;
	        	}
        	} else {
        		sender.sendMessage(ChatColor.RED + getLanguage("economy-disabled"));
        		return true;
        	}
        } else if (command.getName().equalsIgnoreCase("spawn")) {
        	if (sender instanceof Player) {
        		Player p = (Player) sender;
        		World sw = getServer().getWorlds().get(0);
        		p.teleport(sw.getSpawnLocation());
        		sender.sendMessage(config.getString("CHAT_PREFIX").replace("$", "§") + getLanguage("spawn"));
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
    			SkyblockPlayer sp = getSkyblockPlayer(p);
	    		if (sp.getVisiting() != null) {
	    			if (sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation())) {
	    				e.setCancelled(false);
	    				playerIsland = sp.getVisiting().getIsland();
	    			}
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
	    					Material bmat = XMaterial.matchXMaterial(btype).get().parseMaterial();
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
    			SkyblockPlayer sp = getSkyblockPlayer(p);
	    		if (sp.getVisiting() != null) {
	    			if (sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation())) {
	    				e.setCancelled(false);
	    				playerIsland = sp.getVisiting().getIsland();
	    			}
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
	    					Material bmat = XMaterial.matchXMaterial(btype).get().parseMaterial();
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
    			SkyblockPlayer sp = getSkyblockPlayer(p);
	    		if (sp.getVisiting() != null) {
	    			if (sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation())) {
	    				e.setCancelled(false);
	    				playerIsland = sp.getVisiting().getIsland();
	    			}
    			}
    		}
    	}
    	if (!e.isCancelled() && config.getBoolean("BONEMEAL_DOES_MORE") && config.getBoolean("USE_CUSTOM_MECHANICS") && (p.getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getWorld() == skyNether))) {
			if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
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
						Effect happyVillager = Effect.valueOf("HAPPY_VILLAGER");
						if (happyVillager != null) {
							skyWorld.playEffect(b.getLocation(), happyVillager, 0);
						} else {
							skyWorld.playEffect(b.getLocation(), Effect.valueOf("VILLAGER_PLANT_GROW"), 0);
						}
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
								Effect happyVillager = Effect.valueOf("HAPPY_VILLAGER");
								if (happyVillager != null) {
									skyWorld.playEffect(b.getLocation(), happyVillager, 0);
								} else {
									skyWorld.playEffect(b.getLocation(), Effect.valueOf("VILLAGER_PLANT_GROW"), 0);
								}
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
									Effect happyVillager = Effect.valueOf("HAPPY_VILLAGER");
									if (happyVillager != null) {
										skyWorld.playEffect(b.getLocation(), happyVillager, 0);
									} else {
										skyWorld.playEffect(b.getLocation(), Effect.valueOf("VILLAGER_PLANT_GROW"), 0);
									}
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
	            		SkyblockPlayer sp = getSkyblockPlayer(p);
		            	if ((playerIsland == null || !(playerIsland.inBounds(e.getEntity().getLocation()))) && !(edbeEvent.getDamager().isOp()) && !(sp != null && sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
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
			            				if (usingVault) {
			            					if (getMoney > 0) {
			            						vaultEconomy.depositPlayer(p, getMoney);
			            					} else {
			            						vaultEconomy.withdrawPlayer(p, -1*getMoney);
			            					}
			            				} else {
				            				Economy econ = new Economy(p.getUniqueId(), data);
				            				econ.deposit(getMoney);
			            				}
				            			DecimalFormat dec = new DecimalFormat("#0.00");
				            			if (getMoney > 0) {
				            				p.sendMessage(ChatColor.GREEN + getLanguage("kill-get").replace("{money}", dec.format(getMoney)).replace("{mob}", e.getEntity().getType().getName().replace("_", " ")));
				            			} else {
				            				p.sendMessage(ChatColor.RED + getLanguage("kill-lose").replace("{money}", dec.format(getMoney)).replace("{mob}", e.getEntity().getType().getName().replace("_", " ")));
				            			}
			            			}
			            			Entity ent = e.getEntity();
			            			Location entloc = ent.getLocation();
			            			entloc.add(0,-1,0);
			            			if (config.getBoolean("SOUL_SAND") && config.getBoolean("USE_CUSTOM_MECHANICS") && entloc.getWorld() == skyNether && entloc.getBlock().getType() == Material.SAND) {
			            				entloc.getBlock().setType(XMaterial.SOUL_SAND.parseMaterial());
			            			}
			            		}
			            	}
		            	}
	            	}
	            }
	        }
    	}
    	if (e.getEntity() instanceof Player && (e.getEntity().getWorld() == skyWorld || e.getEntity().getWorld() == skyNether)) {
    		Player p = (Player) e.getEntity();
        	Island playerIsland = getPlayerIsland(p);
        	SkyblockPlayer sp = getSkyblockPlayer(p);
        	if ((playerIsland == null || !(playerIsland.inBounds(e.getEntity().getLocation()))) && !(p.hasPermission("skyblock.admin"))&& !(sp != null && sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
        		if (e.getCause() != DamageCause.VOID) {
        			// make it so people are invincible on other people's islands
        			e.setCancelled(true);
        		}
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
    			players.add(new SkyblockPlayer(p, playerIsland, skyWorld, skyNether, data, this));
    		} else {
    	    	if (sp.wasVisiting()) {
    	    		sp.goHome(p);
    	    	}
    	    	sp.setSkySpawn(sp.getHome());
    		}
    		sp = getSkyblockPlayer(p);
    		if (config.getBoolean("DISABLE_PLAYER_COLLISIONS") && (getServer().getVersion().contains("1.12") || getServer().getVersion().contains("1.13") || getServer().getVersion().contains("1.14") || getServer().getVersion().contains("1.15"))) {
    			if (p.getWorld() == skyWorld || p.getWorld() == skyNether) {
    				noCollideAddPlayer(p);
    			} else {
    				noCollideRemovePlayer(p);
    			}
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
							    	Material itemMat = XMaterial.matchXMaterial(GENERATOR_ORES.get(i).split(":")[0]).get().parseMaterial();
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
			            					Material bmat = XMaterial.matchXMaterial(btype).get().parseMaterial();
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
			SkyblockPlayer sp = getSkyblockPlayer(p);
    		if (sp.getVisiting() != null) {
    			if (sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation())) {
    				e.setCancelled(false);
    				playerIsland = sp.getVisiting().getIsland();
    			}
			}
    	}
    }
    
	@EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
    	Player p = e.getPlayer();
    	Island playerIsland = getPlayerIsland(p);
    	if ((p.getLocation().getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getLocation().getWorld() == skyNether)) && ((playerIsland == null || !(playerIsland.inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin")))) {
    		e.setCancelled(true);
			SkyblockPlayer sp = getSkyblockPlayer(p);
    		if (sp.getVisiting() != null) {
    			if (sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation())) {
    				e.setCancelled(false);
    				playerIsland = sp.getVisiting().getIsland();
    			}
			}
    	}
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SkyblockPlayer sp = getSkyblockPlayer(p);
		if (sp != null) {
			if (sp.getVisiting() != null) {
				if (!sp.getVisiting().getIsland().inBounds(p.getLocation())) {
					sp.setVisiting(null);
				}
			}
		}
		if ((p.getWorld() == skyWorld || p.getWorld() == skyNether) && p.isFlying() && !(p.isOp() || p.hasPermission("skyblock.admin"))) {
			if (sp.getIsland() != null) {
				if (!sp.getIsland().inBounds(p.getLocation())) {
					p.setFlying(false);
				}
			}
		}
		if (config.getBoolean("VOID_INSTANT_DEATH") && p.getLocation().getY() < -10 && sp != null) {
			boolean d = false;
			for (int i = 0; i < config.getStringList("VOID_INSTANT_DEATH_WORLDS").size(); i++) {
				if (config.getStringList("VOID_INSTANT_DEATH_WORLDS").get(i).equalsIgnoreCase(p.getLocation().getWorld().getName()) || config.getStringList("VOID_INSTANT_DEATH_WORLDS").get(i).equalsIgnoreCase("*")) {
					d = true;
				}
			}
			if (d) {
				p.sendMessage(ChatColor.RED + "You fell into the void.");
				if (sp.skySpawn() != null && (p.getWorld() == skyWorld || p.getWorld() == skyNether)) {
					p.teleport(sp.skySpawn(), TeleportCause.PLUGIN);
				} else {
					if (sp.getHome() != null && (p.getWorld() == skyWorld || p.getWorld() == skyNether)) {
						p.teleport(sp.getHome(), TeleportCause.PLUGIN);
					} else if (p.getBedSpawnLocation() != null) {
						p.teleport(p.getBedSpawnLocation());
					} else {
						p.teleport(p.getWorld().getSpawnLocation());
					}
				}
				p.setVelocity(new Vector(0,0,0));
				p.setHealth(p.getMaxHealth());
				p.setFoodLevel(20);
				p.setFallDistance(0);
				if (config.getBoolean("USE_ECONOMY")) {
					if ((sp.getIsland() != null && sp.getIsland().inBounds(p.getLocation())) || (sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
						if (config.isString("LOSS_ON_DEATH")) {
							if (usingVault) {
								Double loss = vaultEconomy.getBalance(p) / 2;
					    		DecimalFormat dec = new DecimalFormat("#0.00");
								p.sendMessage(ChatColor.RED + "You died and lost $" + dec.format(loss));
								vaultEconomy.withdrawPlayer(p, loss);
							} else {
								Economy econ = new Economy(p.getUniqueId(), data);
								Double loss = econ.get() / 2;
					    		DecimalFormat dec = new DecimalFormat("#0.00");
								p.sendMessage(ChatColor.RED + "You died and lost $" + dec.format(loss));
								econ.set(loss);
							}
						} else {
							Double loss = config.getDouble("LOSS_ON_DEATH");
							if (usingVault) {
					    		DecimalFormat dec = new DecimalFormat("#0.00");
								p.sendMessage(ChatColor.RED + "You died and lost $" + dec.format(loss));
								vaultEconomy.withdrawPlayer(p, loss);
							} else {
								Economy econ = new Economy(p.getUniqueId(), data);
					    		DecimalFormat dec = new DecimalFormat("#0.00");
								p.sendMessage(ChatColor.RED + "You died and lost $" + dec.format(loss));
								econ.withdraw(loss);
							}
						}
					}
				}
				if (skyWorld.getGameRuleValue("keepInventory") != "true" && !(!(sp.getIsland() != null && sp.getIsland().inBounds(p.getLocation())) && (p.getLocation().getWorld() == skyWorld || p.getLocation().getWorld() == skyNether)) && p.getGameMode() != GameMode.CREATIVE) {
					p.getInventory().clear();
					p.getInventory().setArmorContents(new ItemStack[4]);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Entity ent = e.getEntity();
		if (ent instanceof Player) {
			Player p = (Player) ent;
			SkyblockPlayer sp = getSkyblockPlayer(p);
			if ((p.getLocation().getWorld() == skyWorld || p.getLocation().getWorld() == skyNether) && sp.skySpawn() != null) {
				sp.setOldBedSpawn(p.getBedSpawnLocation());
				p.setBedSpawnLocation(sp.skySpawn(), true);
			}
			if (config.getBoolean("USE_ECONOMY")) {
				Boolean runloss = false;
				if (p.getWorld() == skyWorld || p.getWorld() == skyNether) {
					if ((sp.getIsland() != null && sp.getIsland().inBounds(p.getLocation())) || (sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
						runloss = true;
					}
				} else {
					runloss = true;
				}
				if (runloss) {
					if (config.isString("LOSS_ON_DEATH")) {
						if (usingVault) {
							Double loss = vaultEconomy.getBalance(p) / 2;
				    		DecimalFormat dec = new DecimalFormat("#0.00");
							p.sendMessage(ChatColor.RED + "You died and lost $" + dec.format(loss));
							vaultEconomy.withdrawPlayer(p, loss);
						} else {
							Economy econ = new Economy(p.getUniqueId(), data);
							Double loss = econ.get() / 2;
				    		DecimalFormat dec = new DecimalFormat("#0.00");
							p.sendMessage(ChatColor.RED + "You died and lost $" + dec.format(loss));
							econ.set(loss);
						}
					} else {
						Double loss = config.getDouble("LOSS_ON_DEATH");
						if (usingVault) {
				    		DecimalFormat dec = new DecimalFormat("#0.00");
							p.sendMessage(ChatColor.RED + "You died and lost $" + dec.format(loss));
							vaultEconomy.withdrawPlayer(p, loss);
						} else {
							Economy econ = new Economy(p.getUniqueId(), data);
				    		DecimalFormat dec = new DecimalFormat("#0.00");
							p.sendMessage(ChatColor.RED + "You died and lost $" + dec.format(loss));
							econ.withdraw(loss);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		SkyblockPlayer sp = getSkyblockPlayer(p);
		if (sp.oldBedSpawn() != null) {
			p.setBedSpawnLocation(sp.oldBedSpawn(), false);
			sp.setOldBedSpawn(null);
		}
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		ProjectileSource ent = e.getEntity().getShooter();
		if (ent instanceof Player) {
			Player p = (Player) ent;
	    	Island playerIsland = getPlayerIsland(p);
	    	SkyblockPlayer sp = getSkyblockPlayer(p);
	    	if ((p.getLocation().getWorld() == skyWorld) && ((playerIsland == null || !(playerIsland.inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin"))) && !(sp != null && sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
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
	    	SkyblockPlayer sp = getSkyblockPlayer(p);
	    	if ((p.getLocation().getWorld() == skyWorld) && ((playerIsland == null || !(playerIsland.inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin"))) && !(sp != null && sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
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
	    	if ((p.getLocation().getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getLocation().getWorld() == skyNether)) && ((sp.getIsland() == null || !(sp.getIsland().inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin"))) && !(sp != null && sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
	    		if (sp.getVisiting() != null && !sp.getVisiting().getIsland().visitorsCanRideMobs()) {
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
	            	if (b.getWorld() == skyNether) {
						double cr = Math.random() * 100.0;
						if (cr < 50) {
							b.setType(XMaterial.NETHERRACK.parseMaterial());
						} else {
							b.setType(XMaterial.GRAVEL.parseMaterial());
						}
	            	} else {
	            		b.setType(XMaterial.GRAVEL.parseMaterial());
	            	}
	            } else if (b.getType() == XMaterial.GRAVEL.parseMaterial()) {
	            	if (b.getWorld() == skyNether) {
						double cr = Math.random() * 100.0;
						if (cr < 50) {
							b.setType(XMaterial.GLOWSTONE.parseMaterial());
						} else {
							b.setType(XMaterial.SAND.parseMaterial());
						}
	            	} else {
	            		b.setType(XMaterial.SAND.parseMaterial());
	            	}
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
		Player p = e.getPlayer();
		if (e.getCause() == TeleportCause.NETHER_PORTAL && config.getBoolean("USE_NETHER")) {
			SkyblockPlayer sp = getSkyblockPlayer(p);
			if ((p.getLocation().getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getLocation().getWorld() == skyNether)) && ((sp.getIsland() == null || !(sp.getIsland().inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin"))) && !(sp != null && sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
	    		if (sp.getVisiting() != null && sp.getIsland().inBounds(p.getLocation()) && !sp.getVisiting().getIsland().visitorsCanPortal()) {
	    			e.setCancelled(true);
	    		}
	    	}
			Location newTo = e.getPlayer().getLocation();
			if (newTo.getWorld() == skyWorld) {
				newTo.setWorld(skyNether);
				e.setTo(newTo);
				if (!sp.getIsland().getNether()) {
					Location p1 = sp.getIsland().getP1().clone();
					p1.setWorld(skyNether);
					generateNetherIsland(p1.add(new Location(skyNether, Math.round(config.getInt("ISLAND_WIDTH")/2),config.getInt("ISLAND_HEIGHT"),Math.round(config.getInt("ISLAND_DEPTH")/2))));
					sp.getIsland().setNether(true);
				}
				if (newTo.getBlock() != null && newTo.getBlock().getRelative(BlockFace.DOWN,1).getType() == XMaterial.AIR.parseMaterial()) {
					newTo.getBlock().getRelative(BlockFace.DOWN,1).setType(XMaterial.OBSIDIAN.parseMaterial());
					newTo.getBlock().setType(XMaterial.AIR.parseMaterial());
					newTo.getBlock().getRelative(BlockFace.UP,1).setType(XMaterial.AIR.parseMaterial());
				}
			} else if (newTo.getWorld() == skyNether) {
				newTo.setWorld(skyWorld);
				e.setTo(newTo);
			}
		}
		if (config.getBoolean("DISABLE_PLAYER_COLLISIONS") && (getServer().getVersion().contains("1.12") || getServer().getVersion().contains("1.13") || getServer().getVersion().contains("1.14") || getServer().getVersion().contains("1.15"))) {
			if (e.getTo().getWorld() == skyWorld || e.getTo().getWorld() == skyNether) {
				noCollideAddPlayer(p);
			} else {
				noCollideRemovePlayer(p);
			}
		}
	}
	
	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent e) {
		if (e.getCause() == TeleportCause.NETHER_PORTAL && config.getBoolean("USE_NETHER")) {
			Player p = e.getPlayer();
			SkyblockPlayer sp = getSkyblockPlayer(p);
			if ((p.getLocation().getWorld() == skyWorld || (config.getBoolean("USE_NETHER") && p.getLocation().getWorld() == skyNether)) && ((sp.getIsland() == null || !(sp.getIsland().inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin"))) && !(sp != null && sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
	    		if (sp.getVisiting() != null && sp.getIsland().inBounds(p.getLocation()) && !sp.getVisiting().getIsland().visitorsCanPortal()) {
	    			e.setCancelled(true);
	    		}
	    	}
			Location newTo = e.getPlayer().getLocation();
			if (newTo.getWorld() == skyWorld) {
				newTo.setWorld(skyNether);
				e.setTo(newTo);
				if (!sp.getIsland().getNether()) {
					Location p1 = sp.getIsland().getP1().clone();
					p1.setWorld(skyNether);
					generateNetherIsland(p1.add(new Location(skyNether, Math.round(config.getInt("ISLAND_WIDTH")/2),config.getInt("ISLAND_HEIGHT"),Math.round(config.getInt("ISLAND_DEPTH")/2))));
					sp.getIsland().setNether(true);
				}
				if (newTo.getBlock() != null && newTo.getBlock().getRelative(BlockFace.DOWN,1).getType() == XMaterial.AIR.parseMaterial()) {
					newTo.getBlock().getRelative(BlockFace.DOWN,1).setType(XMaterial.OBSIDIAN.parseMaterial());
					newTo.getBlock().setType(XMaterial.AIR.parseMaterial());
					newTo.getBlock().getRelative(BlockFace.UP,1).setType(XMaterial.AIR.parseMaterial());
				}
			} else if (newTo.getWorld() == skyNether) {
				newTo.setWorld(skyWorld);
				e.setTo(newTo);
			}
		}
	}
	
	@EventHandler
	public void onWorldSave(WorldSaveEvent e) {
		saveData();
		// save data
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

	public void generateIsland(Location loc, Player p, Island playerIsland, Boolean overrideCost) {
		
		Structure iss = new Structure(loc, schematics.getSchematic("default"), skyWorld, config, getLogger());
		iss.generate();
		
		p.setFallDistance(0);
		p.teleport(iss.getPlayerSpawn());
	    
	    // set values
	    Location np1 = new Location(skyWorld,loc.getX()-(config.getInt("ISLAND_WIDTH")/2),0,loc.getZ()-(config.getInt("ISLAND_DEPTH")/2));
	    Location np2 = new Location(skyWorld,loc.getX()+(config.getInt("ISLAND_WIDTH")/2),skyWorld.getMaxHeight(),loc.getZ()+(config.getInt("ISLAND_DEPTH")/2));
	    SkyblockPlayer sp = getSkyblockPlayer(p);
	    sp.setSkySpawn(iss.getPlayerSpawn());
	    if (playerIsland == null) {
		    Island newIs = new Island(np1, np2, 100, nextIslandKey, p, skyWorld, config.getInt("RESET_COUNT"), data, this);
		    nextIslandKey++;
		    newIs.setNether(false);
		    islands.add(newIs);
		    sp.setIsland(newIs);
	    } else {
	    	playerIsland.setP1(np1);
	    	playerIsland.setP2(np2);
	    	playerIsland.setPoints(100);
	    	playerIsland.setNether(false);
	    	playerIsland.removeReset();
	    }
		sp.setVisiting(null);
	    sp.setHome(p.getLocation());
	    if (!overrideCost) {
		    if (usingVault) {
		    	vaultEconomy.withdrawPlayer(p, vaultEconomy.getBalance(p));
		    	vaultEconomy.depositPlayer(p, config.getDouble("STARTING_MONEY"));
		    } else {
			    Economy econ = new Economy(p.getUniqueId(), data);
			    econ.set(config.getDouble("STARTING_MONEY"));
		    }
	    }
	    
	    // set next island location
	    if (loc.getX() < config.getInt("LIMIT_X")) {
	    	nextIsland = new Location(skyWorld, loc.getX()+config.getInt("ISLAND_WIDTH"), config.getInt("ISLAND_HEIGHT"), loc.getZ());
	    } else {
	    	nextIsland = new Location(skyWorld, -config.getInt("LIMIT_X"), config.getInt("ISLAND_HEIGHT"), loc.getZ()+config.getInt("ISLAND_DEPTH"));
	    }
	}
	
	public void generateNetherIsland(Location loc) {
		
		Structure iss = new Structure(loc, schematics.getSchematic("nether"), skyNether, config, getLogger());
		iss.generate();
		
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
	
	public static void noCollideRemovePlayer(Player p) {
		p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
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
    		SkyblockPlayer sp = new SkyblockPlayer(p.getUniqueId(), getPlayerIsland(p), skyWorld, skyNether, data, this);
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