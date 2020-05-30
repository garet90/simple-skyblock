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

// SimpleSkyblock is written event driven and object oriented. When possible, loops
// that span multiple ticks or an onUpdate checker should be avoided.

package holiday.garet.skyblock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import holiday.garet.GStructure.GStructure;
import holiday.garet.GStructure.BlockEntityTag.JigsawTag;
import holiday.garet.GStructure.Event.StructurePaintEvent;
import holiday.garet.skyblock.economy.Economy;
import holiday.garet.skyblock.economy.Trade;
import holiday.garet.skyblock.economy.TradeRequest;
import holiday.garet.skyblock.event.GUISelectItemEvent;
import holiday.garet.skyblock.event.LevelCalculatorFinishEvent;
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
import holiday.garet.skyblock.event.PlayerVisitIslandEvent;
import holiday.garet.skyblock.gui.GUI;
import holiday.garet.skyblock.island.Achievement;
import holiday.garet.skyblock.island.Island;
import holiday.garet.skyblock.island.IslandInvite;
import holiday.garet.skyblock.island.IslandLevel;
import holiday.garet.skyblock.island.Quest;
import holiday.garet.skyblock.island.SkyblockPlayer;
import holiday.garet.skyblock.island.Upgrade;
import holiday.garet.skyblock.world.Generator;
import holiday.garet.skyblock.world.tools.LevelCalculator;

@SuppressWarnings("deprecation")
public class SimpleSkyblock extends JavaPlugin implements Listener {
	// Can't be static because they use "this" to get data.
	public FileConfiguration config = this.getConfig();
	public FileConfiguration data = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "data.yml"));
	public FileConfiguration levelpoints = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "levelpoints.yml"));
	public FileConfiguration language = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "language.yml"));

	public static List<SkyblockPlayer> players = new ArrayList<SkyblockPlayer>();
	public static List<Island> islands = new ArrayList<Island>();
	public static List<TradeRequest> tradingRequests = new ArrayList<TradeRequest>();
	public static List<IslandInvite> islandInvites = new ArrayList<IslandInvite>();
	
	public static HashMap<String, GStructure> structures = new HashMap<String, GStructure>();
	// Going to have a cache of structures so they do not have to be loaded every time
	
	public static HashMap<Integer, Location> spawnLocations = new HashMap<Integer, Location>();
	
	public static List<IslandLevel> topIslands = new ArrayList<IslandLevel>(10);
	
	public static int nextIslandKey = 0;
	
	public static List<String> toClear = new ArrayList<String>();
	
	public static List<Integer> runningCalculators = new ArrayList<Integer>();
	// had to add so people couldn't crash the server by running like 5000 level calculators at once.
	// It would be kinda funny if it were possible though, DDoSing a server with like 5 billion level calculator requests
	
	public static Location nextIsland;
	
	public static World skyWorld;
	public static World skyNether;
	
	public static Boolean usingVault = false;
	public static net.milkbowl.vault.economy.Economy vaultEconomy = null;
	public static String mvHookedWorlds = "world";
	
	public static String chatPrefix = "";
	
	// common settings to help improve performance.
	public static Boolean usingNether;
	public static List<String> chatWorlds;
	public static Boolean useChatrooms;
	
	public static List<Achievement> achievements = new ArrayList<Achievement>();
	public static List<Quest> quests = new ArrayList<Quest>();
	public static List<Upgrade> upgrades = new ArrayList<Upgrade>();
	
    @Override
    public void onEnable() {
        
        // Initialize metrics
        @SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);
        
        // register events
    	Bukkit.getPluginManager().registerEvents(this, this);
    	
    	// make sure configuration file is up to date
    	if (config.isSet("data")) {
    		// if the config is out of date, lets fix that.
    		convertConfig("1.2.0","1.2.1");
    	} else if (config.getString("config-version").equalsIgnoreCase("1.2.1")) {
    		convertConfig("1.2.1","1.2.2");
    	} else if (config.getString("config-version").equalsIgnoreCase("1.4.0")) {
    		// config is up to date.
    	} else {
    		getLogger().warning("Your configuration file is out of date! You may continue to use it, but it may not contain the latest settings.");
    	}
    	
    	// save config defaults so we don't have to deal with null in the config upon updating
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        config = this.getConfig();
    	reloadConfig();
    	
    	// copy needed files if they don't exist
    	File langFile = new File(getDataFolder(), "language.yml");
    	if (!langFile.exists()) {
    		try {
				language = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getResource("language.yml"), "UTF8"));
				language.save(getDataFolder() + File.separator + "language.yml");
			} catch (Exception e) {
				getLogger().warning("Unable to save language.yml!");
			}
    	}
    	File levelpointsFile = new File(getDataFolder(), "levelpoints.yml");
    	if (!levelpointsFile.exists()) {
    		try {
				levelpoints = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getResource("levelpoints.yml"), "UTF8"));
				levelpoints.save(getDataFolder() + File.separator + "levelpoints.yml");
			} catch (Exception e) {
				getLogger().warning("Unable to save levelpoints.yml!");
			}
    	}
    	
    	// set levelpoint values from the levelpoints file
    	for (String key : levelpoints.getKeys(false)) {
    		try {
    			LevelCalculator.points.put(XMaterial.matchXMaterial(key).get().parseMaterial(), levelpoints.getInt(key));
    		} catch (Exception e) {
    			getLogger().severe("Could not add item to leveling calculator: " + key);
    		}
    	}
    	
    	// write schematic files
    	// check schematic loading
    	File sc1 = new File(getDataFolder() + File.separator + "structures" + File.separator + "default.nbt");
    	File sc2 = new File(getDataFolder() + File.separator + "structures" + File.separator + "default_nether.nbt");
    	File scdir = new File(getDataFolder() + File.separator + "structures");
    	if (!scdir.exists()) {
    		scdir.mkdir();
    	}
    	if (!sc1.exists()) {
    		try {
				sc1.createNewFile();
				OutputStream os1 = new FileOutputStream(sc1);
				byte[] buffer = new byte[this.getResource("default.nbt").available()];
				this.getResource("default.nbt").read(buffer);
				os1.write(buffer);
				os1.close();
			} catch (IOException e1) {
				getLogger().severe("An error occurred trying to save default.nbt!");
				e1.printStackTrace();
			}
    	}
    	if (!sc2.exists()) {
    		try {
				sc2.createNewFile();
				OutputStream os1 = new FileOutputStream(sc2);
				byte[] buffer = new byte[this.getResource("default_nether.nbt").available()];
				this.getResource("default_nether.nbt").read(buffer);
				os1.write(buffer);
				os1.close();
			} catch (IOException e1) {
				getLogger().severe("An error occurred trying to save default_nether.nbt!");
				e1.printStackTrace();
			}
    	}

    	// delete old schematic files (Not technically needed, will be removed in SS 1.5.0)
    	File oldsc1 = new File(getDataFolder() + File.separator + "structures" + File.separator + "default.yml");
    	File oldsc2 = new File(getDataFolder() + File.separator + "structures" + File.separator + "nether.yml");
    	File oldsc3 = new File(getDataFolder() + File.separator + "structures" + File.separator + "default.schematic");
    	File oldsc4 = new File(getDataFolder() + File.separator + "structures" + File.separator + "default_nether.schematic");
    	if (oldsc1.exists()) {
    		getLogger().info("Deleting old structures/default.yml");
    		oldsc1.delete();
    	}
    	if (oldsc2.exists()) {
    		getLogger().info("Deleting old structures/nether.yml");
    		oldsc2.delete();
    	}
    	if (oldsc3.exists()) {
    		getLogger().info("Deleting old structures/default.schematic");
    		oldsc3.delete();
    	}
    	if (oldsc4.exists()) {
    		getLogger().info("Deleting old structures/default_nether.schematic");
    		oldsc4.delete();
    	}
    	
    	// Using nether must be placed before worlds are generated or we'll run into null pointer problems because it is used
    	usingNether = config.getBoolean("USE_NETHER");
    	
    	// generate worlds
    	// get world name
    	final String worldName;
    	if (config.isSet("WORLD")) {
    		worldName = config.getString("WORLD");
    	} else {
    		worldName = "world";
    	}
    	// shout out to Mr. Test
    	if (worldName == "mrtest") {
    		getLogger().info("OOGA BOOGA BOOGA!");
    	}
    	// get worlds and if they're null, generate them
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
	            	if (!(skyWorld.getGenerator() instanceof Generator)) {
	            		getLogger().warning("Warning! You are not using SimpleSkyblock's world generator! Add the following to your 'bukkit.yml' file:");
	            		getLogger().warning("worlds:");
	            		getLogger().warning("  " + worldName + ": ");
	            		getLogger().warning("    generator: SimpleSkyblock");
	            		getLogger().warning("  " + worldName + "_nether: ");
	            		getLogger().warning("    generator: SimpleSkyblock");
	            	}
                }
            }, 0L);
    	}
    	if (skyNether == null && usingNether) { // If we don't have a world generated yet
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
    	
    	// toClear are people's inventories that should be reset because they were offline when a reset happened
    	toClear = data.getStringList("data.toClear");
    	
    	// set next island location and key
    	if (data.isSet("data.nextIsland")) {
    		nextIsland = new Location(skyWorld, data.getInt("data.nextIsland.x"), config.getInt("ISLAND_HEIGHT"), data.getInt("data.nextIsland.x"));
    	} else {
    		nextIsland = new Location(skyWorld, -config.getInt("LIMIT_X"), config.getInt("ISLAND_HEIGHT"),-config.getInt("LIMIT_Z"));
    	}
    	if (data.isSet("data.nextIsland.key")) {
    		nextIslandKey = data.getInt("data.nextIsland.key");
    	}
    	
    	// set mvHookedWorld so we don't try and hook it every time
    	if (data.isSet("mvhookedworlds")) {
    		mvHookedWorlds = data.getString("mvhookedworlds");
    	}
    	
    	// connect to vault
    	if (getServer().getPluginManager().getPlugin("Vault")!=null) {
    		RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
    		if (rsp != null) {
            	vaultEconomy = rsp.getProvider();
            	usingVault = true;
    		}
    	}
    	
    	// in case of /reload we need to grab that data
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

    	// check for placeholderapi and register if found
    	if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new SkyPlaceholderExpansion(this).register();
    	}
    	
    	// check for multiverse core and register if present
    	if(Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null && mvHookedWorlds != worldName) {
	        BukkitScheduler scheduler = getServer().getScheduler();
	        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
	            @Override
	            public void run() {
	        		getLogger().info("Communicating with Multiverse to add worlds...");
	        		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + worldName + " NORMAL SimpleSkyblock -n");
		    		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + worldName + "_nether NETHER SimpleSkyblock -n");
		    		mvHookedWorlds = worldName;
	            }
	        }, 1L);
    	}
    	
    	// register breedhandler if version allows it
    	if (!(getServer().getVersion().contains("1.8") || getServer().getVersion().contains("1.9"))) {
    		EntityBreedHandler breedHandler = new EntityBreedHandler(this);
    		this.getServer().getPluginManager().registerEvents(breedHandler, this);
    	}
    	
    	// load top islands
    	if (data.contains("data.topIslands")) {
    		for (int i = 0; i < 10; i++) {
    			if (data.contains("data.topIslands." + i)) {
    				IslandLevel il = new IslandLevel(data.getString("data.topIslands." + i + ".name"), data.getInt("data.topIslands." + i + ".level"));
    				topIslands.add(il);
    			}
    		}
    	}
    	
    	// set chat prefix (It is used fairly often, there is no reason to search in config.yml every time
    	chatPrefix = config.getString("CHAT_PREFIX").replaceAll("&", "§");
    	
    	// load common settings
    	chatWorlds = config.getStringList("CHAT_WORLDS");
    	useChatrooms = config.getBoolean("USE_CHATROOMS");
    	
    	// load achievements
    	ConfigurationSection achievements = config.getConfigurationSection("ACHIEVEMENTS");
    	if (achievements != null && achievements.getKeys(false) != null) {
	    	achievements.getKeys(false).forEach((achievement) -> {
	    		Achievement n = new Achievement(achievement, config, data, vaultEconomy);
	    		SimpleSkyblock.achievements.add(n);
	    	});
    	}
    	getLogger().info("Loaded " + SimpleSkyblock.achievements.size() + " achievements.");
    	
    	// load quests
    	ConfigurationSection quests = config.getConfigurationSection("QUESTS");
    	if (quests != null && quests.getKeys(false) != null) {
	    	quests.getKeys(false).forEach((quest) -> {
	    		Quest n = new Quest(quest, config, data, vaultEconomy);
	    		SimpleSkyblock.quests.add(n);
	    	});
    	}
    	getLogger().info("Loaded " + SimpleSkyblock.quests.size() + " quests.");
    	
    	// load upgrades
    	ConfigurationSection upgrades = config.getConfigurationSection("UPGRADES");
    	if (upgrades != null && upgrades.getKeys(false) != null) {
	    	upgrades.getKeys(false).forEach((upgrade) -> {
	    		Upgrade n = new Upgrade(upgrade, config, data, vaultEconomy);
	    		SimpleSkyblock.upgrades.add(n);
	    	});
    	}
    	getLogger().info("Loaded " + SimpleSkyblock.upgrades.size() + " upgrades.");
    	
    	// YAY
    	getLogger().info("SimpleSkyblock has been enabled!");
    	
    	// check for updates
    	try {
	        getLogger().info("Checking for updates...");
	        UpdateChecker updater = new UpdateChecker(this, 72100);
	        if(updater.checkForUpdates()) {
		        getLogger().info(ChatColor.GREEN + "There is an update for SimpleSkyblock! Go to https://www.spigotmc.org/resources/simple-skyblock.72100/ to download it.");
	        }else{
		        getLogger().info("There are no updates for SimpleSkyblock at this time.");
	        }
	    } catch(Exception e) {
	        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not proceed update-checking!");
	    }
    	
    }
    
    public String getLanguage(String _key) {
    	// getting language from config and formatting it
    	// Could be changed to HashMap for performance benefit, but I don't really care too much as this file is relatively small.
    	if (language.isSet(_key)) {
    		return language.getString(_key).replaceAll("&", "§");
    	}
    	getLogger().warning(_key + " not found in language.yml!");
    	return "";
    }
    
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
    	// returns the generator so you can use "generator: SimpleSkyblock" in bukkit.yml
    	return new Generator();
    }
    
    @Override
    public void onDisable() {
    	// save data
    	getLogger().info("Saving skyblock data...");
    	saveData();
    	
    	// YAY
    	getLogger().info("SimpleSkyblock has been disabled!");
    }
    
	@Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        // This function is quite beefy, most of it is just ifs and elses to say things to the player
		// when they try to do something that doesn't make sense. Otherwise things would just break.
		// Also, I have return true at the end of each else because it helps to catch when there is no error catching.
		if (command.getName().equalsIgnoreCase("island") || command.getName().equalsIgnoreCase("is")) {
        	if (sender instanceof Player) {
    			Player p = (Player) sender;
    			
    			// Could be changed because these functions are relatively CPU intensive, but they are not used
    			// that often, and they are used in almost every instance below.
        		Island playerIsland = getPlayerIsland(p);
    			SkyblockPlayer sp = getSkyblockPlayer(p);
            	if (args.length == 0) {
	        		if (playerIsland == null) {
	        			String islandName = "default";
	        			ConfigurationSection it = config.getConfigurationSection("ISLAND_TYPES");
	        			if (it != null && it.getKeys(false).size() > 1) {
	        				
	        				// max size 45 items (8 island types)
	        				int types = it.getKeys(false).size();
	        				int size = 0;
	        				if (types <= 4) {
	        					size = 27;
	        				} else if (size <= 8) {
	        					size = 45;
	        				}
	        				GUI isg = new GUI(getLanguage("gui-select-island-title"), p, size, 0);
        					this.getServer().getPluginManager().registerEvents(isg, this);
	        				int i = 0;
	        				for (String key : it.getKeys(false)) {
	        					ConfigurationSection itk = it.getConfigurationSection(key);
	        					ItemStack item = XMaterial.matchXMaterial(itk.getString("item")).get().parseItem();
	        					ItemMeta itemmeta = item.getItemMeta();
	        					itemmeta.setDisplayName(itk.getString("name").replaceAll("&", "§"));
	        					List<String> lore = itk.getStringList("lore");
	        					for (int ii = 0; ii < lore.size(); ii++) {
	        						lore.set(ii, lore.get(ii).replaceAll("&", "§"));
	        					}
				        		DecimalFormat dec = new DecimalFormat("#0.00");
	        					if (itk.getDouble("cost") > 0) {
	        						lore.add("");
	        						lore.add(ChatColor.GREEN + "Cost: $" + dec.format(itk.getDouble("cost")));
	        					}
	        					itemmeta.setLore(lore);
	        					item.setItemMeta(itemmeta);
	        					if (i < 4) {
	        						isg.setSlot(10 + i*2, item, key);
	        					} else if (i < 8) {
	        						isg.setSlot(28 + (i-4)*2, item, key);
	        					}
	        					i++;
	        				}
	        			} else {
				            PlayerCreateIslandEvent e = new PlayerCreateIslandEvent(sp, p, nextIsland, config.getBoolean("RETAIN_MONEY"), islandName);
				            Bukkit.getPluginManager().callEvent(e);
				            if (!e.getCancelled()) {
					            sender.sendMessage(chatPrefix + getLanguage("generating-island"));
				            	generateIsland(e.getIslandLocation(), p, null, e.getOverrideResetMoney(), it.getString(islandName + ".schematic"), islandName);
				            }
	        			}
			            return true;
	        		} else {
	        			PlayerTeleportToIslandEvent e = new PlayerTeleportToIslandEvent(p, sp, playerIsland, true);
	        			Bukkit.getPluginManager().callEvent(e);
	        			if (!e.getCancelled()) {
		        			sender.sendMessage(chatPrefix + getLanguage("teleporting-to-island"));
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
                		sender.sendMessage(ChatColor.GREEN + "Island Commands (2/3)" + ChatColor.GRAY + ": ");
                		sender.sendMessage(ChatColor.GRAY + "usage: /is [args]\n");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is join <player>" + ChatColor.RESET + ": join a player\'s island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is leave" + ChatColor.RESET + ": leave a co-op island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is kick <player>" + ChatColor.RESET + ": kick a member of your island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is list" + ChatColor.RESET + ": list members of your island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is makeleader <player>" + ChatColor.RESET + ": make another player leader of the island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is level" + ChatColor.RESET + ": check what level your island is.");
            			return true;
            		} else if (args.length > 1 && args[1].equalsIgnoreCase("3")) {
                		sender.sendMessage(ChatColor.GREEN + "Island Commands (3/3)" + ChatColor.GRAY + ": ");
                		sender.sendMessage(ChatColor.GRAY + "usage: /is [args]\n");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is top" + ChatColor.RESET + ": display the top islands on the server.");
	            		if (SimpleSkyblock.achievements.size() > 0)
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is achievements" + ChatColor.RESET + ": show your skyblock achievements.");
	            		if (SimpleSkyblock.quests.size() > 0)
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is quests" + ChatColor.RESET + ": show available quests.");
	            		if (SimpleSkyblock.upgrades.size() > 0)
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is upgrades" + ChatColor.RESET + ": show island upgrades.");
	            		return true;
            		} else {
                		sender.sendMessage(ChatColor.GREEN + "Island Commands (1/3)" + ChatColor.GRAY + ": ");
                		sender.sendMessage(ChatColor.GRAY + "usage: /is [args]\n");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is help [n]" + ChatColor.RESET + ": view page [n] of /island help.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is home" + ChatColor.RESET + ": teleports you to your island home.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is sethome" + ChatColor.RESET + ": sets your island\'s home location.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is reset" + ChatColor.RESET + ": resets your island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is visit <player>" + ChatColor.RESET + ": visit another player\'s island.");
	            		sender.sendMessage(ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " /is settings" + ChatColor.RESET + ": change various island visitor settings.");
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
		        			sender.sendMessage(chatPrefix + getLanguage("teleporting-to-island"));
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
			            		sender.sendMessage(chatPrefix + getLanguage("set-island-home"));
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
		            				String islandName = "default";
		    	        			ConfigurationSection it = config.getConfigurationSection("ISLAND_TYPES");
		            				Double rcost = null;
		            				if (config.isBoolean("RESET_COST")) {
		            					
		            				} else {
					            		int resetNumber = config.getInt("RESET_COUNT") - sp.getIsland().getResetsLeft();
					            		List<Double> resetCost = config.getDoubleList("RESET_COST");
					            		if (resetNumber >= resetCost.size()) {
					            			resetNumber = resetCost.size() - 1;
					            		}
					            		rcost = resetCost.get(resetNumber);
		            				}
		    	        			if (it != null && it.getKeys(false).size() > 1) {
		    	        				if (rcost == null) {
		    	        					rcost = 0.0;
		    	        				}
		    	        				// max size 45 items (8 island types)
		    	        				int types = it.getKeys(false).size();
		    	        				int size = 0;
		    	        				if (types <= 4) {
		    	        					size = 27;
		    	        				} else if (size <= 8) {
		    	        					size = 45;
		    	        				}
		    	        				GUI isg = new GUI(getLanguage("gui-select-island-title"), p, size, 1);
		            					this.getServer().getPluginManager().registerEvents(isg, this);
		    	        				int i = 0;
		    	        				for (String key : it.getKeys(false)) {
		    	        					ConfigurationSection itk = it.getConfigurationSection(key);
		    	        					ItemStack item = XMaterial.matchXMaterial(itk.getString("item")).get().parseItem();
		    	        					ItemMeta itemmeta = item.getItemMeta();
		    	        					itemmeta.setDisplayName(itk.getString("name").replaceAll("&", "§"));
		    	        					List<String> lore = itk.getStringList("lore");
		    	        					for (int ii = 0; ii < lore.size(); ii++) {
		    	        						lore.set(ii, lore.get(ii).replaceAll("&", "§"));
		    	        					}
		    				        		DecimalFormat dec = new DecimalFormat("#0.00");
		    	        					if (itk.getDouble("cost") + rcost > 0) {
		    	        						lore.add("");
		    	        						lore.add(ChatColor.GREEN + "Cost: $" + dec.format(itk.getDouble("cost") + rcost));
		    	        					}
		    	        					itemmeta.setLore(lore);
		    	        					item.setItemMeta(itemmeta);
		    	        					if (i < 4) {
		    	        						isg.setSlot(10 + i*2, item, key);
		    	        					} else if (i < 8) {
		    	        						isg.setSlot(28 + (i-4)*2, item, key);
		    	        					}
		    	        					i++;
		    	        				}
		    	        				return true;
		    	        			} else {
			            				Boolean goAhead = false;
			            				if (config.isBoolean("RESET_COST")) {
			            					goAhead = true;
			            				} else {
						            		if (!usingVault) {
							            		Economy tempEcon = new Economy(p.getUniqueId(), data);
							            		if (tempEcon.get() >= rcost) {
							            			goAhead = true;
							            		}
						            		} else {
							            		if (vaultEconomy.getBalance(p) >= rcost) {
							            			goAhead = true;
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
						            				generateIsland(e.getLocation(), p, playerIsland, config.getBoolean("RETAIN_MONEY"), it.getString(islandName + ".schematic"), islandName);
						            			} else {
						            				generateIsland(e.getLocation(), p, playerIsland, config.getBoolean("RETAIN_MONEY"), it.getString(islandName + ".schematic"), islandName);
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
									            			tempP.sendMessage(chatPrefix + getLanguage("reset-success"));
									            		}
									            	}
									            }
									            PlayerInventory inv = p.getInventory();
									            inv.clear();
									            inv.setArmorContents(new ItemStack[4]);
									            sender.sendMessage(chatPrefix + getLanguage("reset-success"));
					            			}
								            return true;
					            		} else {
				            				sender.sendMessage(ChatColor.RED + getLanguage("reset-not-enough-money"));
				            				return true;
					            		}
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
		            			sender.sendMessage(chatPrefix + getLanguage("reset-confirm"));
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
            				allowsVisitors = sz.getIsland().getSetting("allowVisitors");
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
	                			sender.sendMessage(chatPrefix + getLanguage("visit-teleport").replace("{player}", pName));
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
	            			// Crazy, ikr, sadly it cannot be cached because of the Allowed / Disallowed in the lore
	            			// It does take quite some power to load this, but it should not be used that often.
	            			GUI g = new GUI(getLanguage("settings-title"), p, 27, 2);
        					this.getServer().getPluginManager().registerEvents(g, this);
	            			int i = 0;
	            			ItemStack item;
	            			if (XMaterial.GRASS_PATH.parseMaterial() != null) {
	            				item = new ItemStack(XMaterial.GRASS_PATH.parseMaterial(), 1);
	            			} else {
	            				item = new ItemStack(XMaterial.GRASS_BLOCK.parseMaterial(), 1);
	            			}
	            			ItemMeta itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Allow Visitors");
	            			List<String> lore = new ArrayList<String>(1);
	            			lore.add("");
	            			if (playerIsland.getSetting("allowVisitors")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "allowVisitors");
	            			
	            			item = new ItemStack(XMaterial.SADDLE.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Ride Mobs");
	            			if (playerIsland.getSetting("rideMobs")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "rideMobs");
	            			
	            			item = new ItemStack(XMaterial.NETHERRACK.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Portals");
	            			if (playerIsland.getSetting("usePortals")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "usePortals");
	            			
	            			item = XMaterial.IRON_DOOR.parseItem();
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Doors");
	            			if (playerIsland.getSetting("useDoors")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useDoors");

	            			item = new ItemStack(XMaterial.OAK_TRAPDOOR.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Trapdoors");
	            			if (playerIsland.getSetting("useTrapdoors")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useTrapdoors");
	            			
	            			item = new ItemStack(XMaterial.OAK_FENCE_GATE.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Fence Gates");
	            			if (playerIsland.getSetting("useFenceGates")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useFenceGates");
	            			
	            			item = new ItemStack(XMaterial.ANVIL.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Anvils");
	            			if (playerIsland.getSetting("useAnvils")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useAnvils");
	            			
	            			item = new ItemStack(XMaterial.CRAFTING_TABLE.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Crafting Tables");
	            			if (playerIsland.getSetting("useCraftingTables")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useCraftingTables");
	            			
	            			item = new ItemStack(XMaterial.FURNACE.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Furnaces");
	            			if (playerIsland.getSetting("useFurnaces")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useFurnaces");
	            			
	            			item = new ItemStack(XMaterial.BLAZE_ROD.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Brewing Stands");
	            			if (playerIsland.getSetting("useBrewingStands")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useBrewingStands");
	            			
	            			item = new ItemStack(XMaterial.ENCHANTING_TABLE.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Enchanting Tables");
	            			if (playerIsland.getSetting("useEnchantingTables")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useEnchantingTables");
	            			
	            			item = new ItemStack(XMaterial.JUKEBOX.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Jukeboxes");
	            			if (playerIsland.getSetting("useJukeboxes")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useJukeboxes");
	            			
	            			item = new ItemStack(XMaterial.BEACON.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Beacons");
	            			if (playerIsland.getSetting("useBeacons")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useBeacons");
	            			
	            			item = new ItemStack(XMaterial.CHEST.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Chests");
	            			if (playerIsland.getSetting("useChests")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useChests");
	            			
	            			item = new ItemStack(XMaterial.TRAPPED_CHEST.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Trapped Chests");
	            			if (playerIsland.getSetting("useTrappedChests")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useTrappedChests");
	            			
	            			item = new ItemStack(XMaterial.ENDER_CHEST.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Ender Chests");
	            			if (playerIsland.getSetting("useEnderChests")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useEnderChests");
	            			
	            			item = new ItemStack(XMaterial.RED_WOOL.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Beds");
	            			if (playerIsland.getSetting("useBeds")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useBeds");
	            			
	            			item = new ItemStack(XMaterial.EMERALD.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Trade With Villagers");
	            			if (playerIsland.getSetting("tradeWithVillagers")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "tradeWithVillagers");
	            			
	            			item = new ItemStack(XMaterial.STONE_BUTTON.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Buttons and Pressure Plates");
	            			if (playerIsland.getSetting("buttonsAndPressurePlates")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "buttonsAndPressurePlates");
	            			
	            			item = new ItemStack(XMaterial.DIAMOND.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Drop Items");
	            			if (playerIsland.getSetting("dropItems")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "dropItems");
	            			
	            			item = new ItemStack(XMaterial.IRON_INGOT.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Pickup Items");
	            			if (playerIsland.getSetting("pickupItems")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "pickupItems");
	            			
	            			item = new ItemStack(XMaterial.EGG.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Throw Eggs");
	            			if (playerIsland.getSetting("throwEggs")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "throwEggs");
	            			
	            			item = new ItemStack(XMaterial.ENDER_PEARL.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Use Ender Pearls");
	            			if (playerIsland.getSetting("useEnderPearls")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "useEnderPearls");
	            			
	            			item = new ItemStack(XMaterial.WHEAT.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Breed Animals");
	            			if (playerIsland.getSetting("breedAnimals")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "breedAnimals");
	            			
	            			item = new ItemStack(XMaterial.SHEARS.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Shear Sheep");
	            			if (playerIsland.getSetting("shearSheep")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "shearSheep");
	            			
	            			item = new ItemStack(XMaterial.WOODEN_SWORD.parseMaterial(), 1);
	            			itemMeta = item.getItemMeta();
	            			itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "Kill Animals");
	            			if (playerIsland.getSetting("killAnimals")) {
	            				lore.set(0, ChatColor.GREEN + "Allowed");
	            			} else {
	            				lore.set(0, ChatColor.RED + "Disallowed");
	            			}
	            			itemMeta.setLore(lore);
	            			item.setItemMeta(itemMeta);
	            			g.setSlot(i++, item, "killAnimals");
	            			
	            			return true;
	            		} else {
	            			sender.sendMessage(ChatColor.RED + getLanguage("leader-only"));
	            			return true;
	            		}
            		} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("achievements")) {
            		if (playerIsland != null) {
            			GUI g = new GUI(getLanguage("achievements-title"), p, (int)(Math.ceil(SimpleSkyblock.achievements.size() / 9.0)*9), 3);
    					this.getServer().getPluginManager().registerEvents(g, this);
            			for (int i = 0; i < SimpleSkyblock.achievements.size(); i++) {
            				ItemStack it = achievements.get(i).getItem().clone();
            				ItemMeta im = it.getItemMeta();
            				List<String> lore = im.getLore();
            				lore.add("");
            				if (sp.hasAchievement(achievements.get(i).getName())) {
            					lore.add(ChatColor.GREEN + "Completed");
            				} else {
            					lore.add(ChatColor.RED + "Not Completed");
            				}
            				im.setLore(lore);
            				it.setItemMeta(im);
            				g.setSlot(i, it, achievements.get(i).getName());
            			}
            			return true;
            		} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("quests")) {
            		if (playerIsland != null) {
            			if (p.getLocation().getWorld() == skyWorld || (usingNether && p.getLocation().getWorld() == skyNether)) {
	            			GUI g = new GUI(getLanguage("quests-title"), p, (int)(Math.ceil(SimpleSkyblock.quests.size() / 9.0)*9), 4);
	    					this.getServer().getPluginManager().registerEvents(g, this);
	            			for (int i = 0; i < SimpleSkyblock.quests.size(); i++) {
	            				ItemStack it = quests.get(i).getItem().clone();
	            				ItemMeta im = it.getItemMeta();
	            				List<String> lore = im.getLore();
	            				lore.add("");
	            				if (sp.hasQuest(quests.get(i).getName())) {
	            					lore.add(ChatColor.GREEN + "Completed");
	            				} else {
	            					lore.add(ChatColor.RED + "Not Completed");
	            					lore.add("");
	            					if (SimpleSkyblock.quests.get(i).checkCompletion(p, playerIsland)) {
	            						lore.add(ChatColor.GREEN + "" + ChatColor.ITALIC + "Click here to complete!");
	            					} else {
	            						lore.add(ChatColor.RED + "Requirements not met.");
	            					}
	            				}
	            				im.setLore(lore);
	            				it.setItemMeta(im);
	            				g.setSlot(i, it, quests.get(i).getName());
	            			}
            			} else {
            				sender.sendMessage(ChatColor.RED + getLanguage("not-on-island"));
            			}
            			return true;
            		} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("upgrades")) {
            		if (playerIsland != null) {
            			if (p.getLocation().getWorld() == skyWorld || (usingNether && p.getLocation().getWorld() == skyNether)) {
	            			GUI g = new GUI(getLanguage("upgrades-title"), p, (int)(Math.ceil(SimpleSkyblock.upgrades.size() / 9.0)*9), 5);
	    					this.getServer().getPluginManager().registerEvents(g, this);
	            			for (int i = 0; i < SimpleSkyblock.upgrades.size(); i++) {
	            				ItemStack it = upgrades.get(i).getItem().clone();
	            				ItemMeta im = it.getItemMeta();
	            				List<String> lore = im.getLore();
	            				lore.add("");
	            				if (playerIsland.hasUpgrade(SimpleSkyblock.upgrades.get(i).getName())) {
	            					lore.add(ChatColor.GREEN + "Unlocked");
	            				} else {
	            					lore.add(ChatColor.RED + "Not unlocked");
	            					lore.add("");
	            					if (SimpleSkyblock.upgrades.get(i).checkCompletion(p, playerIsland)) {
	            						lore.add(ChatColor.GREEN + "" + ChatColor.ITALIC + "Click here to unlock!");
	            					} else {
	            						lore.add(ChatColor.RED + "Requirements not met.");
	            					}
	            				}
	            				im.setLore(lore);
	            				it.setItemMeta(im);
	            				g.setSlot(i, it, upgrades.get(i).getName());
	            			}
            			} else {
            				sender.sendMessage(ChatColor.RED + getLanguage("not-on-island"));
            			}
            			return true;
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
            			// Make it so an island can only run one level calculator at a time
            			if (!runningCalculators.contains(playerIsland.getKey())) {
	            			List<Chunk> c = new ArrayList<Chunk>(2);
	            			Chunk o = skyWorld.getChunkAt(playerIsland.getP1().clone().add(config.getInt("ISLAND_WIDTH")/2, 0, config.getInt("ISLAND_DEPTH")/2));
	            			c.add(o);
	            			if (usingNether) {
	            				Chunk n = skyNether.getChunkAt(playerIsland.getP1().clone().add(config.getInt("ISLAND_WIDTH")/2, 0, config.getInt("ISLAND_DEPTH")/2));
	            				c.add(n);
	            			}
	            			new LevelCalculator(c, p, playerIsland);
	            			sender.sendMessage(ChatColor.GREEN + getLanguage("level-calculating"));
	            			runningCalculators.add(playerIsland.getKey());
            			}
	            		return true;
            		} else {
            			sender.sendMessage(ChatColor.RED + getLanguage("must-have-island"));
            			return true;
            		}
            	} else if (args[0].equalsIgnoreCase("top")) {
            		sender.sendMessage(ChatColor.GREEN + "Top Islands" + ChatColor.GRAY + ":");
            		for (int i = 0; i < topIslands.size(); i++) {
            			sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + (i + 1) + ChatColor.GRAY + "] " + ChatColor.GOLD + topIslands.get(i).getName() + ChatColor.GRAY + " (Level " + topIslands.get(i).getLevel() + ")");
            		}
            		return true;
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
				        							sender.sendMessage(chatPrefix + getLanguage("trust-added"));
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
				        							sender.sendMessage(chatPrefix + getLanguage("trust-removed"));
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
    				Player r = getServer().getPlayer(args[0]);
    				if (r != null) {
    					double payAmount;
        			    try {
            				payAmount = Double.valueOf(args[1]);
        			    } catch (NumberFormatException | NullPointerException nfe) {
        					sender.sendMessage(ChatColor.RED + getLanguage("money-not-valid"));
        			        return true;
        			    }
    	        		DecimalFormat dec = new DecimalFormat("#0.00");
    					sender.sendMessage(ChatColor.GREEN + getLanguage("pay-send").replace("{money}", dec.format(payAmount)).replace("{player}", r.getName()));
    					r.sendMessage(ChatColor.GREEN + getLanguage("pay-receive").replace("{money}", dec.format(payAmount)).replace("{player}", sender.getName()));
    					vaultEconomy.depositPlayer(r, payAmount);
    				}
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
    				Player r = getServer().getPlayer(args[0]);
    				if (r != null) {
    					double payAmount;
        			    try {
            				payAmount = Double.valueOf(args[1]);
        			    } catch (NumberFormatException | NullPointerException nfe) {
        					sender.sendMessage(ChatColor.RED + getLanguage("money-not-valid"));
        			        return true;
        			    }
    	        		DecimalFormat dec = new DecimalFormat("#0.00");
    					sender.sendMessage(ChatColor.GREEN + getLanguage("pay-send").replace("{money}", dec.format(payAmount)).replace("{player}", r.getName()));
    					r.sendMessage(ChatColor.GREEN + getLanguage("pay-receive").replace("{money}", dec.format(payAmount)).replace("{player}", sender.getName()));
        			    Economy econ = new Economy(r.getUniqueId(), data);
    					econ.deposit(payAmount);
    				}
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
        		sender.sendMessage(chatPrefix + getLanguage("spawn"));
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
    	// Just make sure that we can't place on other's islands
    	Block b = e.getBlock();
    	Player p = e.getPlayer();
    	Island playerIsland = getPlayerIsland(p);
    	if (p != null) {
    		if ((p.getWorld() == skyWorld || (usingNether && p.getWorld() == skyNether)) && !(p.hasPermission("skyblock.admin"))) {
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
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
    	// make sure we can't break on other peoples islands
    	Block b = e.getBlock();
    	Player p = e.getPlayer();
    	Island playerIsland = getPlayerIsland(p);
    	if (p != null) {
    		if ((p.getWorld() == skyWorld || (usingNether && p.getWorld() == skyNether)) && !(p.hasPermission("skyblock.admin"))) {
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
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
    	// This one is fairly beefy because settings can change which types of interactions are allowed
    	Block b = e.getClickedBlock();
    	Player p = e.getPlayer();
    	SkyblockPlayer sp = getSkyblockPlayer(p);
    	Island playerIsland = sp.getIsland();
    	if (p != null && b != null) {
    		if ((p.getWorld() == skyWorld || (usingNether && p.getWorld() == skyNether)) && !(p.hasPermission("skyblock.admin"))) {
	    		if (sp.getVisiting() != null) {
    				Island visitingIsland = sp.getVisiting().getIsland();
	    			if (visitingIsland.trustContains(p.getUniqueId()) && visitingIsland.inBounds(p.getLocation())) {
	    				playerIsland = sp.getVisiting().getIsland();
	    			} else {
	    				if (!(
	    						// I know, this is a bit crazy and confusing, they are for visiting settings.
	    						
	    						((b.getType() == XMaterial.ACACIA_DOOR.parseMaterial()
	    						|| b.getType() == XMaterial.BIRCH_DOOR.parseMaterial()
	    						|| b.getType() == XMaterial.DARK_OAK_DOOR.parseMaterial()
	    						|| b.getType() == XMaterial.JUNGLE_DOOR.parseMaterial()
	    						|| b.getType() == XMaterial.OAK_DOOR.parseMaterial()
	    						|| b.getType() == XMaterial.SPRUCE_DOOR.parseMaterial()) && visitingIsland.getSetting("useDoors"))
	    						||
	    						((b.getType() == XMaterial.ACACIA_TRAPDOOR.parseMaterial()
	    						|| b.getType() == XMaterial.BIRCH_TRAPDOOR.parseMaterial()
	    						|| b.getType() == XMaterial.DARK_OAK_TRAPDOOR.parseMaterial()
	    						|| b.getType() == XMaterial.JUNGLE_TRAPDOOR.parseMaterial()
	    						|| b.getType() == XMaterial.OAK_TRAPDOOR.parseMaterial()
	    						|| b.getType() == XMaterial.SPRUCE_TRAPDOOR.parseMaterial()) && visitingIsland.getSetting("useTrapdoors"))
	    						||
	    						((b.getType() == XMaterial.ACACIA_FENCE_GATE.parseMaterial()
	    						|| b.getType() == XMaterial.BIRCH_FENCE_GATE.parseMaterial()
	    						|| b.getType() == XMaterial.DARK_OAK_FENCE_GATE.parseMaterial()
	    						|| b.getType() == XMaterial.JUNGLE_FENCE_GATE.parseMaterial()
	    						|| b.getType() == XMaterial.OAK_FENCE_GATE.parseMaterial()
	    						|| b.getType() == XMaterial.SPRUCE_FENCE_GATE.parseMaterial()) && visitingIsland.getSetting("useFenceGates"))
	    						||
	    						((b.getType() == XMaterial.ANVIL.parseMaterial()) && visitingIsland.getSetting("useAnvils"))
	    						||
	    						((b.getType() == XMaterial.CRAFTING_TABLE.parseMaterial()) && visitingIsland.getSetting("useCraftingTables"))
	    						||
	    						((b.getType() == XMaterial.FURNACE.parseMaterial()
	    						|| b.getType() == XMaterial.BLAST_FURNACE.parseMaterial()
	    						|| b.getType() == XMaterial.SMOKER.parseMaterial()) && visitingIsland.getSetting("useFurnaces"))
	    						||
	    						((b.getType() == XMaterial.BREWING_STAND.parseMaterial()) && visitingIsland.getSetting("useBrewingStands"))
	    						||
	    						((b.getType() == XMaterial.ENCHANTING_TABLE.parseMaterial()) && visitingIsland.getSetting("useEnchantingTables"))
	    						||
	    						((b.getType() == XMaterial.JUKEBOX.parseMaterial()) && visitingIsland.getSetting("useJukeboxes"))
	    						||
	    						((b.getType() == XMaterial.BEACON.parseMaterial()) && visitingIsland.getSetting("useBeacons"))
	    						||
	    						((b.getType() == XMaterial.CHEST.parseMaterial()) && visitingIsland.getSetting("useChests"))
	    						||
	    						((b.getType() == XMaterial.TRAPPED_CHEST.parseMaterial()) && visitingIsland.getSetting("useTrappedChests"))
	    						||
	    						((b.getType() == XMaterial.ENDER_CHEST.parseMaterial()) && visitingIsland.getSetting("useEnderChests"))
	    						||
	    						((b.getType() == XMaterial.BLACK_BED.parseMaterial()
	    						|| b.getType() == XMaterial.BLUE_BED.parseMaterial()
	    						|| b.getType() == XMaterial.BROWN_BED.parseMaterial()
	    						|| b.getType() == XMaterial.CYAN_BED.parseMaterial()
	    						|| b.getType() == XMaterial.GRAY_BED.parseMaterial()
	    						|| b.getType() == XMaterial.GREEN_BED.parseMaterial()
	    						|| b.getType() == XMaterial.LIGHT_BLUE_BED.parseMaterial()
	    						|| b.getType() == XMaterial.LIGHT_GRAY_BED.parseMaterial()
	    						|| b.getType() == XMaterial.LIME_BED.parseMaterial()
	    						|| b.getType() == XMaterial.MAGENTA_BED.parseMaterial()
	    						|| b.getType() == XMaterial.ORANGE_BED.parseMaterial()
	    						|| b.getType() == XMaterial.PINK_BED.parseMaterial()
	    						|| b.getType() == XMaterial.PURPLE_BED.parseMaterial()
	    						|| b.getType() == XMaterial.RED_BED.parseMaterial()
	    						|| b.getType() == XMaterial.WHITE_BED.parseMaterial()
	    						|| b.getType() == XMaterial.YELLOW_BED.parseMaterial()) && visitingIsland.getSetting("useBeds"))
	    						||
	    						((b.getType() == XMaterial.ACACIA_BUTTON.parseMaterial()
	    						|| b.getType() == XMaterial.BIRCH_BUTTON.parseMaterial()
	    						|| b.getType() == XMaterial.DARK_OAK_BUTTON.parseMaterial()
	    						|| b.getType() == XMaterial.JUNGLE_BUTTON.parseMaterial()
	    						|| b.getType() == XMaterial.OAK_BUTTON.parseMaterial()
	    						|| b.getType() == XMaterial.SPRUCE_BUTTON.parseMaterial()
	    						|| b.getType() == XMaterial.STONE_BUTTON.parseMaterial()
	    						|| b.getType() == XMaterial.LEVER.parseMaterial()
	    						|| b.getType() == XMaterial.ACACIA_PRESSURE_PLATE.parseMaterial()
	    						|| b.getType() == XMaterial.BIRCH_PRESSURE_PLATE.parseMaterial()
	    						|| b.getType() == XMaterial.DARK_OAK_PRESSURE_PLATE.parseMaterial()
	    						|| b.getType() == XMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.parseMaterial()
	    						|| b.getType() == XMaterial.JUNGLE_PRESSURE_PLATE.parseMaterial()
	    						|| b.getType() == XMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.parseMaterial()
	    						|| b.getType() == XMaterial.OAK_PRESSURE_PLATE.parseMaterial()
	    						|| b.getType() == XMaterial.SPRUCE_PRESSURE_PLATE.parseMaterial()
	    						|| b.getType() == XMaterial.STONE_PRESSURE_PLATE.parseMaterial()) && visitingIsland.getSetting("buttonsAndPressurePlates"))
	    						||
	    						((e.getItem() != null && e.getItem().getType() == XMaterial.EGG.parseMaterial()) && visitingIsland.getSetting("throwEggs"))
	    						||
	    						((e.getItem() != null && e.getItem().getType() == XMaterial.ENDER_PEARL.parseMaterial()) && visitingIsland.getSetting("useEnderPearls"))
	    						
	    						)) {
	    					e.setCancelled(true);
	    				}
	    			}
	    		} else {
	    			if (playerIsland != null) {
		    			if (!(playerIsland.inBounds(b.getLocation()))) {
		    				e.setCancelled(true);
		    			}
		    		} else {
		    			e.setCancelled(true);
		    		}
	    		}
    		}
    	}
    	if (!e.isCancelled() && config.getBoolean("BONEMEAL_DOES_MORE") && config.getBoolean("USE_CUSTOM_MECHANICS") && (p.getWorld() == skyWorld || (usingNether && p.getWorld() == skyNether))) {
			// also have some stuff for custom mechanics
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
								// Gotta have those cross-version effects
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
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
    	// make it so people can only breed animals and trade with villagers if it's allowed
    	Player p = e.getPlayer();
    	SkyblockPlayer sp = getSkyblockPlayer(p);
    	if (p.getLocation().getWorld() == skyWorld || p.getLocation().getWorld() == skyNether) {
    		if (sp.getIsland() == null || !sp.getIsland().inBounds(p.getLocation())) {
    			if (sp.getVisiting() != null) {
    				SkyblockPlayer visiting = sp.getVisiting();
    				if (visiting.getIsland().inBounds(p.getLocation())) {
    					if (!visiting.getIsland().trustContains(p.getUniqueId())) {
    						if (!(
    								(e.getRightClicked() instanceof Sheep && visiting.getIsland().getSetting("shearSheep"))
    								||
    								(e.getRightClicked() instanceof Villager && visiting.getIsland().getSetting("tradeWithVillagers"))
    								||
    								(e.getRightClicked() instanceof Animals && visiting.getIsland().getSetting("breedAnimals"))
    								
    								)) {
    							e.setCancelled(true);
    						}
    					}
    				} else {
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
    	// No pvp, no killing stuff unless allowed, and getting money from kills
    	if(e instanceof EntityDamageByEntityEvent && (e.getEntity().getLocation().getWorld() == skyWorld || (usingNether && e.getEntity().getLocation().getWorld() == skyNether))){
	        EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent)e;
	        Player p = null;
	        boolean goAhead = false;
	        if (edbeEvent.getDamager() instanceof Player){
	            if (e.getEntity() instanceof Player) {
	            	if ((e.getEntity().getLocation().getWorld() == skyWorld || (usingNether && e.getEntity().getLocation().getWorld() == skyNether)) && !(edbeEvent.getDamager().isOp())) {
	            		// make it so nobody can attack anybody on an island
	            		e.setCancelled(true);
	            	}
	            } else {
	            	goAhead = true;
	            }
	        } else if (edbeEvent.getDamager() instanceof Projectile) {
	        	Projectile a = (Projectile)edbeEvent.getDamager();
	        	if (a.getShooter() instanceof Player) {
	        		p = (Player)a.getShooter();
	        		goAhead = true;
	        	}
	        }
	        if (goAhead) {
            	if (p == null && edbeEvent.getDamager() instanceof Arrow) {
            		if ((Arrow)edbeEvent.getDamager() instanceof Arrow) {
	            		p = (Player)((Arrow)edbeEvent.getDamager()).getShooter();
            		}
            	}
            	Island playerIsland = getPlayerIsland(p);
            	if (p != null) {
            		SkyblockPlayer sp = getSkyblockPlayer(p);
	            	if ((playerIsland == null || !(playerIsland.inBounds(e.getEntity().getLocation()))) && !(edbeEvent.getDamager().isOp()) && !(sp != null && sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
	            		if (!(sp.getVisiting() != null && sp.getVisiting().getIsland() != null && sp.getVisiting().getIsland().inBounds(e.getEntity().getLocation()) && sp.getVisiting().getIsland().getSetting("killAnimals"))) {
		            		// make it so people cant hurt mobs on other people's islands
		            		e.setCancelled(true);
	            		}
	            	} else {
	            		if ((e.getEntity() instanceof LivingEntity || e.getEntity() instanceof Arrow) && config.getBoolean("USE_ECONOMY")) {
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
    	// player join handling. We gotta clear their inventories if they're queued to, and we also gotta disable collisions and take them home if they are visiting an island because
    	// that island is no longer loaded.
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
    	// save the players data when they leave.
    	Player p = e.getPlayer();
    	SkyblockPlayer sp = getSkyblockPlayer(p);
    	if (sp != null) {
    		sp.savePlayer();
    	}
    }
    
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
    	Block f = e.getBlock();
    	Block t = e.getToBlock();
    	if ((f.getLocation().getWorld() == skyWorld || (usingNether && f.getLocation().getWorld() == skyNether)) && (f.getType() == XMaterial.LAVA.parseMaterial() || f.getType() == Material.matchMaterial("STATIONARY_LAVA"))) {
    		BlockFace[] faces = {
    				BlockFace.EAST,
    				BlockFace.WEST,
    				BlockFace.NORTH,
    				BlockFace.SOUTH
    		};
    		for (BlockFace face : faces) {
    			if ((t.getRelative(face).getType() == XMaterial.WATER.parseMaterial() || t.getRelative(face).getType() == Material.matchMaterial("STATIONARY_WATER"))  && config.getBoolean("GENERATE_ORES")) {
    				e.setCancelled(true);
					double oreType = Math.random() * 100.0;
					List<String> GENERATOR_ORES = config.getStringList("GENERATOR_ORES");
					Island is = null;
					for (int i = 0; i < islands.size(); i++) {
						if (islands.get(i).inBounds(e.getBlock().getLocation())) {
							is = islands.get(i);
							break;
						}
					}
					if (is != null && is.getGeneratorOres().size() > 0) {
						GENERATOR_ORES = is.getGeneratorOres();
					}
				    for(int i = 0; i < GENERATOR_ORES.size(); i++) {
				    	Material itemMat = XMaterial.matchXMaterial(GENERATOR_ORES.get(i).split(":")[0]).get().parseMaterial();
				    	Double itemChance = Double.parseDouble(GENERATOR_ORES.get(i).split(":")[1]);
				    	if (itemMat != null) {
				    		oreType -= itemChance;
				    		if (oreType < 0) {
				    			t.setType(itemMat);
				    			break;
				    		}
				    	} else {
				    		getLogger().severe("Unknown material \'" + GENERATOR_ORES.get(i).split(":")[0] + "\' at GENERATOR_ORES item " + (i + 1) + "!");
				    	}
				    }
    			    if (t.getType() == XMaterial.AIR.parseMaterial()) {
    			    	t.setType(XMaterial.COBBLESTONE.parseMaterial());
    			    } else if (t.getType() != XMaterial.COBBLESTONE.parseMaterial()) {
    			    	BlockFormEvent ne = new BlockFormEvent(e.getBlock(), e.getBlock().getState());
    			    	this.getServer().getPluginManager().callEvent(ne);
    			    	// call event so we can do fancy stuff in things like Skript and change the material.
    			    	// Don't do it for cobblestone though or we'll create an infinite loop.
    			    }
				}
			}
    	}
    }
    
    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
    	if (e.getNewState().getType() == XMaterial.COBBLESTONE.parseMaterial() && (e.getBlock().getWorld() == skyWorld || (usingNether && e.getBlock().getWorld() == skyNether))) {
    		if (config.getBoolean("GENERATE_ORES")) {
    			e.setCancelled(true);
				double oreType = Math.random() * 100.0;
				List<String> GENERATOR_ORES = config.getStringList("GENERATOR_ORES");
				Island is = null;
				for (int i = 0; i < islands.size(); i++) {
					if (islands.get(i).inBounds(e.getBlock().getLocation())) {
						is = islands.get(i);
						break;
					}
				}
				if (is != null && is.getGeneratorOres().size() > 0) {
					GENERATOR_ORES = is.getGeneratorOres();
				}
			    for(int i = 0; i < GENERATOR_ORES.size(); i++) {
			    	Material itemMat = XMaterial.matchXMaterial(GENERATOR_ORES.get(i).split(":")[0]).get().parseMaterial();
			    	Double itemChance = Double.parseDouble(GENERATOR_ORES.get(i).split(":")[1]);
			    	if (itemMat != null) {
			    		oreType -= itemChance;
			    		if (oreType < 0) {
			    			e.getBlock().setType(itemMat);
			    			break;
			    		}
			    	} else {
			    		getLogger().severe("Unknown material \'" + GENERATOR_ORES.get(i).split(":")[0] + "\' at GENERATOR_ORES item " + (i + 1) + "!");
			    	}
			    }
			    if (e.getBlock().getType() == XMaterial.AIR.parseMaterial()) {
			    	e.getBlock().setType(XMaterial.COBBLESTONE.parseMaterial());
			    } else if (e.getBlock().getType() != XMaterial.COBBLESTONE.parseMaterial()) {
			    	BlockFormEvent ne = new BlockFormEvent(e.getBlock(), e.getBlock().getState());
			    	this.getServer().getPluginManager().callEvent(ne);
			    	// call event so we can do fancy stuff in things like Skript and change the material.
			    	// Don't do it for cobblestone though or we'll create an infinite loop.
			    }
			}
    	}
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
    	// make it so only in certain cases can players pickup items.
    	Player p = e.getPlayer();
    	Island playerIsland = getPlayerIsland(p);
    	if ((p.getLocation().getWorld() == skyWorld || (usingNether && p.getLocation().getWorld() == skyNether)) && ((playerIsland == null || !(playerIsland.inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin")))) {
    		e.setCancelled(true);
			SkyblockPlayer sp = getSkyblockPlayer(p);
    		if (sp.getVisiting() != null) {
    			if ((sp.getVisiting().getIsland().trustContains(p.getUniqueId()) || sp.getVisiting().getIsland().getSetting("pickupItems")) && sp.getVisiting().getIsland().inBounds(p.getLocation())) {
    				e.setCancelled(false);
    				playerIsland = sp.getVisiting().getIsland();
    			}
			}
    	}
    	if (!e.isCancelled() && (e.getPlayer().getLocation().getWorld() == skyWorld || (usingNether && e.getPlayer().getLocation().getWorld() == skyNether))) {
			SkyblockPlayer sp = getSkyblockPlayer(p);
			p.getInventory().addItem(e.getItem().getItemStack());
			for (int i = 0; i < SimpleSkyblock.achievements.size(); i++) {
				if (!sp.hasAchievement(SimpleSkyblock.achievements.get(i).getName()) && SimpleSkyblock.achievements.get(i).checkCompletion(p, playerIsland)) {
					SimpleSkyblock.achievements.get(i).awardPlayer(p);
					sp.setAchievement(SimpleSkyblock.achievements.get(i).getName(), true);
					if (config.getBoolean("ANNOUNCE_ACHIEVEMENTS")) {
						for (Player player: getServer().getOnlinePlayers()) {
							player.sendMessage(getLanguage("achievements-announce").replace("{achievement}", SimpleSkyblock.achievements.get(i).getItem().getItemMeta().getDisplayName()).replace("{player}", e.getPlayer().getName()));
						}
					} else {
						e.getPlayer().sendMessage(getLanguage("achievements-award").replace("{achievement}", SimpleSkyblock.achievements.get(i).getItem().getItemMeta().getDisplayName()));
					}
				}
			}
			p.getInventory().removeItem(e.getItem().getItemStack());
    	}
    }
    
	@EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
		// make it so only in certain cases players can drop items.
    	Player p = e.getPlayer();
    	Island playerIsland = getPlayerIsland(p);
    	if ((p.getLocation().getWorld() == skyWorld || (usingNether && p.getLocation().getWorld() == skyNether)) && ((playerIsland == null || !(playerIsland.inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin")))) {
    		e.setCancelled(true);
			SkyblockPlayer sp = getSkyblockPlayer(p);
    		if (sp.getVisiting() != null) {
    			if ((sp.getVisiting().getIsland().trustContains(p.getUniqueId()) || sp.getVisiting().getIsland().getSetting("dropItems")) && sp.getVisiting().getIsland().inBounds(p.getLocation())) {
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
					// your no longer visiting them
				}
			}
		}
		if ((p.getWorld() == skyWorld || p.getWorld() == skyNether) && p.isFlying() && !(p.isOp() || p.hasPermission("skyblock.admin")) && !config.getBoolean("FLIGHT_EVERYWHERE")) {
			if (sp.getIsland() != null) {
				if (!sp.getIsland().inBounds(p.getLocation())) {
					p.setFlying(false);
				}
			} else {
				p.setFlying(false);
			}
		}
		// void instant death stuff
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
		// player death loss and stuff
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
		// spawn management
		Player p = e.getPlayer();
		SkyblockPlayer sp = getSkyblockPlayer(p);
		if (sp.oldBedSpawn() != null) {
			p.setBedSpawnLocation(sp.oldBedSpawn(), false);
			sp.setOldBedSpawn(null);
		}
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		// make it so you can't use like bows on other player's islands
		ProjectileSource ent = e.getEntity().getShooter();
		if (ent instanceof Player) {
			Player p = (Player) ent;
	    	Island playerIsland = getPlayerIsland(p);
	    	SkyblockPlayer sp = getSkyblockPlayer(p);
	    	if ((p.getLocation().getWorld() == skyWorld) && ((playerIsland == null || !(playerIsland.inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin"))) && !(sp != null && sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
	    		if (!(sp.getVisiting() != null && sp.getVisiting().getIsland() != null && sp.getVisiting().getIsland().inBounds(p.getLocation()) &&
	    				((sp.getVisiting().getIsland().getSetting("throwEggs") && e.getEntityType() == EntityType.EGG)
	    						|| (sp.getVisiting().getIsland().getSetting("useEnderPearls") && e.getEntityType() == EntityType.ENDER_PEARL)
	    				))) {
	    		e.setCancelled(true);
	    		}
	    	}
			
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		// manage chat (this is very commonly run so things are cached to improve performance
		Boolean useChat = false;
		for (int i = 0; i < chatWorlds.size(); i++) {
			if (chatWorlds.get(i).equalsIgnoreCase(e.getPlayer().getLocation().getWorld().getName()) || chatWorlds.get(i).equalsIgnoreCase("*")) {
				useChat = true;
			}
		}
		if (useChatrooms && useChat) {
			Player p = e.getPlayer();
			Island playerIsland = getPlayerIsland(p);
			if (playerIsland != null) {
				e.setCancelled(true);
				playerIsland.messageAllMembers(ChatColor.GRAY + "[" + ChatColor.AQUA + "Island" + ChatColor.GRAY + "] " + ChatColor.WHITE + p.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + e.getMessage());
				getLogger().info("[" + playerIsland.getKey() + "] " + p.getName() + ": " + e.getMessage());
			} else {
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You must have an island to chat. Use \"/shout\" to chat with the whole server.");
			}
		}
	}
	
	@EventHandler
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
		// make it so mobs don't target visiting players
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
	public void onVehicleEnter(VehicleEnterEvent e) {
		// make it so visitors can't enter vehicles unless allowed to
		// NOTE: this is really weird because they teleport to that location, but it is the only bukkit event, except for perhaps EntityInteract, I will have to look into that in the future
		Entity ent = e.getEntered();
		if (ent instanceof Player) {
			Player p = (Player) ent;
	    	SkyblockPlayer sp = getSkyblockPlayer(p);
	    	if ((p.getLocation().getWorld() == skyWorld || (usingNether && p.getLocation().getWorld() == skyNether)) && ((sp.getIsland() == null || !(sp.getIsland().inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin"))) && !(sp != null && sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
	    		if (sp.getVisiting() != null && !sp.getVisiting().getIsland().getSetting("rideMobs")) {
	    			e.setCancelled(true);
	    		}
	    	}
			
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		// Gotta love that blast processing
		if (config.getBoolean("BLAST_PROCESSING") && config.getBoolean("USE_CUSTOM_MECHANICS") && (e.getLocation().getWorld() == skyWorld || (usingNether && e.getLocation().getWorld() == skyNether))) {
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
		// gotta love that cobble heating
		if (config.getBoolean("COBBLE_HEATING") && config.getBoolean("USE_USTOM_MECHANICS") && (e.getBlock().getLocation().getWorld() == skyWorld || (usingNether && e.getBlock().getLocation().getWorld() == skyNether))) {
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
		// catch players using portals in some versions
		Player p = e.getPlayer();
		if (e.getCause() == TeleportCause.NETHER_PORTAL && usingNether && !Bukkit.getServer().getVersion().contains("1.15")) {
			SkyblockPlayer sp = getSkyblockPlayer(p);
			if ((p.getLocation().getWorld() == skyWorld || (usingNether && p.getLocation().getWorld() == skyNether)) && ((sp.getIsland() == null || !(sp.getIsland().inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin"))) && !(sp != null && sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
	    		if (sp.getVisiting() != null && sp.getIsland().inBounds(p.getLocation()) && !sp.getVisiting().getIsland().getSetting("usePortals")) {
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
					Location netherIslandLocation = p1.add(new Location(skyNether, Math.round(config.getInt("ISLAND_WIDTH")/2),config.getInt("ISLAND_HEIGHT"),Math.round(config.getInt("ISLAND_DEPTH")/2)));
					generateNetherIsland(netherIslandLocation, sp.getIsland().getSchematic() + "_nether");
					sp.getIsland().setNether(true);
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
		// catch players using nether portals in other versions
		if (e.getCause() == TeleportCause.NETHER_PORTAL && usingNether) {
			Player p = e.getPlayer();
			SkyblockPlayer sp = getSkyblockPlayer(p);
			if ((p.getLocation().getWorld() == skyWorld || (usingNether && p.getLocation().getWorld() == skyNether)) && ((sp.getIsland() == null || !(sp.getIsland().inBounds(p.getLocation()))) && !(p.hasPermission("skyblock.admin"))) && !(sp != null && sp.getVisiting() != null && sp.getVisiting().getIsland().trustContains(p.getUniqueId()) && sp.getVisiting().getIsland().inBounds(p.getLocation()))) {
	    		if (sp.getVisiting() != null && sp.getIsland().inBounds(p.getLocation()) && !sp.getVisiting().getIsland().getSetting("usePortals")) {
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
					generateNetherIsland(p1.add(new Location(skyNether, Math.round(config.getInt("ISLAND_WIDTH")/2),config.getInt("ISLAND_HEIGHT"),Math.round(config.getInt("ISLAND_DEPTH")/2))), sp.getIsland().getSchematic() + "_nether");
					sp.getIsland().setNether(true);
				}
			} else if (newTo.getWorld() == skyNether) {
				newTo.setWorld(skyWorld);
				e.setTo(newTo);
			}
		}
	}
	
	@EventHandler
	public void onWorldSave(WorldSaveEvent e) {
		if (e.getWorld() == skyWorld || (usingNether && e.getWorld() == skyNether)) {
			saveData();
		}
		// Save island data when world data is saved
		// Changed to be only when skyworld or skynether is saved because this actually does
		// require lotsa processing if we run it a lot
	}
	
	@EventHandler
	public void onGUISelectItem(GUISelectItemEvent e) {
		// manage our GUIs that we have open
		if (e.getType() == 0 && e.getData() != null) {
			ConfigurationSection c = config.getConfigurationSection("ISLAND_TYPES." + e.getData());
			if (!c.isSet("permission") || e.getPlayer().hasPermission(c.getString("permission"))) {
				// check if has enough money
				double cost = c.getDouble("cost");
				boolean hasMoney = false;
				if (usingVault) {
					if (vaultEconomy.getBalance(e.getPlayer()) >= cost) {
						hasMoney = true;
					}
	        	} else if (config.getBoolean("USE_ECONOMY")) {
	        		Economy econ = new Economy(e.getPlayer().getUniqueId(), data);
	        		if (econ.get() >= cost) {
	        			hasMoney = true;
	        		}
	        	} else if (cost != 0) {
	        		getLogger().severe("Cannot subtract cost from player because no economy is used at playerCreateIslandEvent!");
	        	}
				if (hasMoney) {
					String islandType = c.getString("schematic");
		            PlayerCreateIslandEvent ev = new PlayerCreateIslandEvent(getSkyblockPlayer(e.getPlayer()), e.getPlayer(), nextIsland, config.getBoolean("RETAIN_MONEY"), islandType);
		            Bukkit.getPluginManager().callEvent(ev);
		            if (!ev.getCancelled()) {
			            e.getPlayer().sendMessage(chatPrefix + getLanguage("generating-island"));
		            	generateIsland(ev.getIslandLocation(), e.getPlayer(), null, ev.getOverrideResetMoney(), islandType, e.getData());
		            	if (cost != 0) {
			            	if (usingVault) {
			            		vaultEconomy.withdrawPlayer(e.getPlayer(), cost);
			            	} else {
			            		Economy econ = new Economy(e.getPlayer().getUniqueId(), data);
			            		econ.withdraw(cost);
			            	}
		            	}
		            }
				} else {
					e.getPlayer().sendMessage(ChatColor.RED + getLanguage("not-enough-money"));
				}
			} else {
				e.getPlayer().sendMessage(ChatColor.RED + getLanguage("no-permission-island"));
			}
		} else if (e.getType() == 1 && e.getData() != null) {
			ConfigurationSection c = config.getConfigurationSection("ISLAND_TYPES." + e.getData());
			if (!c.isSet("permission") || e.getPlayer().hasPermission(c.getString("permission"))) {
				Double rcost = null;
				if (config.isBoolean("RESET_COST")) {
					
				} else {
            		int resetNumber = config.getInt("RESET_COUNT") - getPlayerIsland(e.getPlayer()).getResetsLeft();
            		List<Double> resetCost = config.getDoubleList("RESET_COST");
            		if (resetNumber >= resetCost.size()) {
            			resetNumber = resetCost.size() - 1;
            		}
            		rcost = resetCost.get(resetNumber);
				}
				if (rcost == null) {
					rcost = 0.0;
				}
				double cost = c.getDouble("cost") + rcost;
				boolean hasMoney = false;
				if (usingVault) {
					if (vaultEconomy.getBalance(e.getPlayer()) >= cost) {
						hasMoney = true;
					}
	        	} else if (config.getBoolean("USE_ECONOMY")) {
	        		Economy econ = new Economy(e.getPlayer().getUniqueId(), data);
	        		if (econ.get() >= cost) {
	        			hasMoney = true;
	        		}
	        	} else if (cost != 0) {
	        		getLogger().severe("Cannot subtract cost from player because no economy is used at playerCreateIslandEvent!");
	        	}
				if (hasMoney) {
        			PlayerResetIslandEvent ev = new PlayerResetIslandEvent(e.getPlayer(), getSkyblockPlayer(e.getPlayer()), getPlayerIsland(e.getPlayer()), cost, nextIsland);
        			Bukkit.getPluginManager().callEvent(ev);
        			if (!ev.getCancelled()) {
        				String islandType = c.getString("schematic");
        				Player p = e.getPlayer();
        				SkyblockPlayer sp = getSkyblockPlayer(p);
        				Island playerIsland = sp.getIsland();
            			if (ev.getResetCost() != null) {
		            		if (!usingVault) {
			            		Economy tempEcon = new Economy(p.getUniqueId(), data);
			            		tempEcon.withdraw(ev.getResetCost());
		            		} else {
		            			vaultEconomy.withdrawPlayer(p, ev.getResetCost());
		            		}
            				generateIsland(ev.getLocation(), p, playerIsland, config.getBoolean("RETAIN_MONEY"), islandType, e.getData());
            			} else {
            				generateIsland(ev.getLocation(), p, playerIsland, config.getBoolean("RETAIN_MONEY"), islandType, e.getData());
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
			            			tempP.sendMessage(chatPrefix + getLanguage("reset-success"));
			            		}
			            	}
			            }
			            PlayerInventory inv = p.getInventory();
			            inv.clear();
			            inv.setArmorContents(new ItemStack[4]);
			            p.sendMessage(chatPrefix + getLanguage("reset-success"));
        			}
				} else {
					e.getPlayer().sendMessage(ChatColor.RED + getLanguage("not-enough-money"));
				}
			} else {
				e.getPlayer().sendMessage(ChatColor.RED + getLanguage("no-permission-island"));
			}
		} else if (e.getType() == 2 && e.getData() != null) {
			if (e.getItem() != null && e.getItem().getItemMeta() != null && e.getItem().getItemMeta().getLore() != null && e.getItem().getItemMeta().getLore().size() == 1 && e.getData() != "" && e.getData() != null) {
				boolean setVal = true;
				List<String> lore = new ArrayList<String>();
				ItemMeta im = e.getItem().getItemMeta();
				if (im.getLore().get(0).equalsIgnoreCase(ChatColor.GREEN + "Allowed")) {
					setVal = false;
					lore.add(ChatColor.RED + "Disallowed");
				} else {
					lore.add(ChatColor.GREEN + "Allowed");
				}
				getPlayerIsland(e.getPlayer()).setSetting(e.getData(), setVal);
				im.setLore(lore);
				e.getItem().setItemMeta(im);
			}
		} else if (e.getType() == 4 && e.getData() != null) {
			String q = e.getData();
			Quest qe = null;
			for (int i = 0; i < SimpleSkyblock.quests.size(); i++) {
				if (SimpleSkyblock.quests.get(i).getName() == q) {
					qe = SimpleSkyblock.quests.get(i);
					break;
				}
			}
			if (qe != null) {
				SkyblockPlayer sp = getSkyblockPlayer(e.getPlayer());
				if (!sp.hasQuest(q) && qe.checkCompletion(e.getPlayer(), getPlayerIsland(e.getPlayer()))) {
					qe.awardPlayer(e.getPlayer());
					sp.setQuest(q, true);
					e.getPlayer().sendMessage(getLanguage("quests-award").replace("{quest}", qe.getItem().getItemMeta().getDisplayName()));
					ItemMeta n = qe.getItem().getItemMeta();
					List<String> nl = n.getLore();
					nl.add("");
					nl.add(ChatColor.GREEN + "Completed");
					n.setLore(nl);
					e.getItem().setItemMeta(n);
				}
			}
		} else if (e.getType() == 5 && e.getData() != null) {
			String u = e.getData();
			Upgrade up = null;
			for (int i = 0; i < SimpleSkyblock.upgrades.size(); i++) {
				if (SimpleSkyblock.upgrades.get(i).getName() == u) {
					up = SimpleSkyblock.upgrades.get(i);
					break;
				}
			}
			if (up != null) {
				SkyblockPlayer sp = getSkyblockPlayer(e.getPlayer());
				Island playerIsland = sp.getIsland();
				if (!playerIsland.hasUpgrade(u) && up.checkCompletion(e.getPlayer(), playerIsland)) {
					up.awardPlayer(e.getPlayer(), playerIsland);
					playerIsland.setUpgrade(u, true);
					e.getPlayer().sendMessage(getLanguage("upgrades-purchase").replace("{upgrade}", up.getItem().getItemMeta().getDisplayName()));
					ItemMeta n = up.getItem().getItemMeta();
					List<String> nl = n.getLore();
					nl.add("");
					nl.add(ChatColor.GREEN + "Unlocked");
					n.setLore(nl);
					e.getItem().setItemMeta(n);
				}
			}
		}
	}
	
	@EventHandler
	public void onLevelCalculatorFinish(LevelCalculatorFinishEvent e) {
		// Do stuff when level calculator has finished
		runningCalculators.set(runningCalculators.indexOf(e.getIsland().getKey()), null);
		getLogger().info("Checked " + e.getCalculator().getCheckedCount() + " chunks and calculated " + e.getCalculator().getPts() + " pts");
		if (e.getPlayer() != null) {
			int lvl = (int)Math.floor((e.getCalculator().getPts()*config.getDouble("LEVEL_POINTS_MULTIPLIER"))+(e.getCalculator().getCheckedCount()*config.getDouble("LEVEL_SPREAD_MULTIPLIER")));
			e.getPlayer().sendMessage(ChatColor.GREEN + getLanguage("level").replace("{level}", String.valueOf(lvl)));
			getPlayerIsland(e.getPlayer()).setLevel(lvl);
			int num = -1;
			int me = -2;
			SkyblockPlayer sp = getSkyblockPlayer(e.getPlayer());
			OfflinePlayer l = getServer().getOfflinePlayer(sp.getIsland().getLeader());
			String lname = l.getName();
			for (int i = 0; i < topIslands.size(); i++) {
				if (topIslands.get(i).getName() == lname) {
					me = i;
				}
				if (topIslands.get(i).getLevel() < lvl) {
					num = i;
					break;
				}
			}
			if (num > -1 && me == -2) {
				IslandLevel n = new IslandLevel(lname, lvl);
				String name = n.getName();
				for (int i = num; i < topIslands.size(); i++) {
					IslandLevel t = topIslands.get(i);
					topIslands.set(i, n);
					n = t;
					if (n.getName() == name) {
						break;
					}
				}
			} else if (num == me) {
				IslandLevel n = new IslandLevel(lname, lvl);
				topIslands.set(me, n);
			} else if (topIslands.size() < 10 && me == -2) {
				IslandLevel n = new IslandLevel(lname, lvl);
				topIslands.add(n);
			}
		}
	}
	
	@EventHandler
	public void onStructurePaint(StructurePaintEvent e) {
		// Detecting when GStructure is gonna place a jigsaw block because we tryna replace it with ore, spawn, or a starter chest
		// A lot of this was copied from the internal code of GStructure
		if (e.getPalette().getName().replaceFirst("minecraft:", "").equalsIgnoreCase("jigsaw")) {
			Block currentBlock = e.getLocation().getWorld().getBlockAt(e.getLocation());
			if (e.getBlockEntityTag() instanceof JigsawTag) {
				JigsawTag tag = (JigsawTag) e.getBlockEntityTag();
				if (tag.getFinalState().equalsIgnoreCase("simpleskyblock:ore")) {
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
					    		this.getLogger().severe("Unknown material \'" + GENERATOR_ORES.get(i).split(":")[0] + "\' at GENERATOR_ORES item " + (i + 1) + "!");
					    	}
					    }
					    if (currentBlock.getType() == XMaterial.AIR.parseMaterial()) {
					    	currentBlock.setType(XMaterial.COBBLESTONE.parseMaterial());
					    }
					} else {
						currentBlock.setType(XMaterial.DIRT.parseMaterial());
					}
				} else if (tag.getFinalState().equalsIgnoreCase("simpleskyblock:chest")) {
					currentBlock.setType(XMaterial.CHEST.parseMaterial());
					Chest chest = (Chest) currentBlock.getState();
				    Inventory chestinv = chest.getBlockInventory();
				    List<String> CHEST_ITEMS = config.getStringList("CHEST_ITEMS");
				    for(int i = 0; i < CHEST_ITEMS.size(); i++) {
				    	Material itemMat = XMaterial.matchXMaterial(CHEST_ITEMS.get(i).split(":")[0]).get().parseMaterial();
				    	int itemCnt = Integer.parseInt(CHEST_ITEMS.get(i).split(":")[1]);
				    	if (itemMat != null) {
				    		chestinv.addItem(new ItemStack(itemMat,itemCnt));
				    	} else {
				    		this.getLogger().severe("Unknown material \'" + CHEST_ITEMS.get(i).split(":")[0] + "\' at CHEST_ITEMS item " + (i + 1) + "!");
				    	}
				    }
				    if (getServer().getVersion().contains("1.13") || getServer().getVersion().contains("1.14") || getServer().getVersion().contains("1.15")) {
					    BlockData bd = currentBlock.getBlockData();
						if (bd instanceof Directional) {
							((Directional) bd).setFacing(BlockFace.valueOf(e.getPalette().getProperties().get("facing").toUpperCase()));
						} else {
							this.getLogger().warning("Unable to set direction of block!");
						}
						currentBlock.setBlockData(bd);
					}
				} else if (tag.getFinalState().equalsIgnoreCase("simpleskyblock:spawn")) {
					spawnLocations.put(e.getBuildKey(), e.getLocation().clone().add(new Vector(0.5,0,0.5)));
				}
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		// Make sure we don't hit the animal cap
		Entity ent = e.getEntity();
		if (ent instanceof Animals) {
			if (e.getLocation().getWorld() == skyWorld || e.getLocation().getWorld() == skyNether) {
				islands.forEach((is) -> {
					if (is != null && is.inBounds(e.getLocation())) {
						if (is.getPassiveMobs() >= config.getInt("ANIMAL_CAP")) {
							e.setCancelled(true);
						} else {
							is.setPassiveMobs(is.getPassiveMobs() + 1);
						}
						return;
					}
				});
			}
		}
	}
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
    	// free up part of the animal cap (Not gonna lie, it is pretty intense to check if it's in bounds of every island)
		Entity ent = e.getEntity();
		if (ent instanceof Animals) {
			if (ent.getLocation().getWorld() == skyWorld || ent.getLocation().getWorld() == skyNether) {
				islands.forEach((is) -> {
					if (is != null && is.inBounds(e.getEntity().getLocation())) {
						is.setPassiveMobs(is.getPassiveMobs() - 1);
						return;
					}
				});
			}
		}
    }
	
    public void saveData() {
    	// save all open islands and players
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
    	data.set("mvhookedworlds", mvHookedWorlds);
    	for (int i = 0; i < topIslands.size(); i++) {
    		data.set("data.topIslands." + i + ".name", topIslands.get(i).getName());
    		data.set("data.topIslands." + i + ".level", topIslands.get(i).getLevel());
    	}
    	try {
			data.save(getDataFolder() + File.separator + "data.yml");
		} catch (IOException e) {
            Bukkit.getLogger().warning("§4Could not save data.yml file.");
		}
    }

	public void generateIsland(Location loc, Player p, Island playerIsland, Boolean overrideCost, String schematic, String opName) {
		// if not set, let's use default (for legacy islands)
		if (schematic == "null" || schematic == "") {
			schematic = "default";
		}
		// generate structure and setup new Island
		if (!structures.containsKey(schematic)) {
			GStructure structure = new GStructure(this);
			structure.read(new File(getDataFolder() + File.separator + "structures" + File.separator + schematic + ".nbt"));
			structures.put(schematic, structure);
		}
		GStructure structure = structures.get(schematic);
		loc.setWorld(skyWorld);
		int buildKey = GStructure.getNextBuildKey();
		structure.generate(loc);
		
		if (config.contains("ISLAND_TYPES." + opName + ".structures")) {
			ConfigurationSection it = config.getConfigurationSection("ISLAND_TYPES." + opName + ".structures");
			for (String key : it.getKeys(false)) {
				GStructure str = new GStructure(this);
				str.read(new File(getDataFolder() + File.separator + "structures" + File.separator + it.getString(key + ".schematic") + ".nbt"));
				Location genLoc = loc.clone().add(it.getInt(key + ".x"), 0, it.getInt(key + ".z"));
				str.generate(genLoc);
				if (it.contains(key + ".biome")) {
					if (Biome.valueOf(it.getString(key + ".biome").toUpperCase()) != null) {
						for (int x = 0; x < str.getWidth(); x++) {
							for (int z = 0; z < str.getDepth(); z++) {
								skyWorld.setBiome(x + loc.getBlockX() + it.getInt(key + ".x"), z + loc.getBlockZ() + it.getInt(key + ".z"), Biome.valueOf(it.getString(key + ".biome").toUpperCase()));
							}
						}
					} else {
						getLogger().warning("Unknown biome: " + it.getString(key + ".biome"));
					}
				}
			}
		}
		
		p.setFallDistance(0);
		if (spawnLocations.containsKey(buildKey)) {
			p.teleport(spawnLocations.get(buildKey));
		} else {
			p.teleport(loc);
		}
		
	    // set values
	    Location np1 = new Location(skyWorld,loc.getX()-(config.getInt("ISLAND_WIDTH")/2),0,loc.getZ()-(config.getInt("ISLAND_DEPTH")/2));
	    Location np2 = new Location(skyWorld,loc.getX()+(config.getInt("ISLAND_WIDTH")/2),skyWorld.getMaxHeight(),loc.getZ()+(config.getInt("ISLAND_DEPTH")/2));
	    SkyblockPlayer sp = getSkyblockPlayer(p);
	    if (spawnLocations.containsKey(buildKey)) {
	    	sp.setSkySpawn(spawnLocations.get(buildKey));
	    	spawnLocations.remove(buildKey);
	    } else {
	    	sp.setSkySpawn(loc);
	    }
	    if (playerIsland == null) {
		    Island newIs = new Island(np1, np2, 100, nextIslandKey, p, skyWorld, config.getInt("RESET_COUNT"), schematic, data, this);
		    nextIslandKey++;
		    newIs.setNether(false);
		    islands.add(newIs);
		    sp.setIsland(newIs);
	    } else {
	    	playerIsland.setP1(np1);
	    	playerIsland.setP2(np2);
	    	playerIsland.setNether(false);
	    	playerIsland.setSchematic(schematic);
	    	playerIsland.removeReset();
	    	playerIsland.resetUpgrades();
	    	playerIsland.setGeneratorOres(new ArrayList<String>());
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
	
	public void generateNetherIsland(Location loc, String schematic) {
		if (schematic == "null" || schematic == "") {
			schematic = "default_nether";
		}
		// Same as above but we don't setup the island stuff
		if (!structures.containsKey(schematic)) {
			GStructure structure = new GStructure(this);
			structure.read(new File(getDataFolder() + File.separator + "structures" + File.separator + schematic + ".nbt"));
			structures.put(schematic, structure);
		}
		GStructure structure = structures.get(schematic);
		loc.setWorld(skyNether);
		structure.generate(loc);
	}

	public static void noCollideAddPlayer(Player p) {
		// use scoreboards to make it so players don't collide
        ScoreboardManager sm = Bukkit.getScoreboardManager();
        Scoreboard board = sm.getNewScoreboard();
        String rand = (""+Math.random()).substring(0, 3);
        Team team = board.registerNewTeam("no_collision"+rand);
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.addEntry(p.getName());
        p.setScoreboard(board);
    }
	
	public static void noCollideRemovePlayer(Player p) {
		// for use when using no collision worlds
		p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
    
    @SuppressWarnings("rawtypes")
	public static int getNotNullLength(List list) {
    	// what it sounds like
    	int actualLength = 0;
    	for (int i = 0; i < list.size(); i++) {
    		if (list.get(i) != null) {
    			actualLength++;
    		}
    	}
    	return actualLength;
    }
    
    public void convertConfig(String _old, String _new) {
    	// converting from old versions of SS data to the newest
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
    	// search through active trade requests
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
    	// same as above
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
    	// same as above
    	for (int i = tradingRequests.size() - 1; i >= 0; i--) {
    		if (tradingRequests.get(i) != null && tradingRequests.get(i).isActive() && ((tradingRequests.get(i).to() == p && tradingRequests.get(i).from() == t) || (tradingRequests.get(i).to() == t && tradingRequests.get(i).from() == p))) {
    			return tradingRequests.get(i);
    		} else if (tradingRequests.get(i) != null && !tradingRequests.get(i).isActive()) {
    			tradingRequests.set(i, null);
    		}
    	}
    	return null;
    }
    
    // Below is a bunch of functions called often to switch between types like Player, SkyblockPlayer, and Island.
    
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
    	// Same as getting trade invites
    	for (int i = 0; i < islandInvites.size(); i++) {
    		if (islandInvites.get(i).getTo() == p && islandInvites.get(i).getIsland() == is && islandInvites.get(i).isActive()) {
    			return islandInvites.get(i);
    		}
    	}
    	return null;
    }
}

class EntityBreedHandler implements Listener {
	
	// This has to be put in a seperate class because the event does not exist for some versions of Minecraft.
	
	private SimpleSkyblock s;
	
	public EntityBreedHandler(SimpleSkyblock ss) {
		s = ss;
	}
	
	@EventHandler
	public void onEntityBreed(EntityBreedEvent e) {
		Entity ent = e.getEntity();
		if (ent instanceof Animals) {
			if (ent.getLocation().getWorld() == SimpleSkyblock.skyWorld || ent.getLocation().getWorld() == SimpleSkyblock.skyNether) {
				for (int i = 0; i < SimpleSkyblock.players.size(); i++) {
					Island is = SimpleSkyblock.players.get(i).getIsland();
					if (is.inBounds(ent.getLocation())) {
						if (is.getPassiveMobs() >= s.config.getInt("ANIMAL_CAP")) {
							e.setCancelled(true);
						} else {
							is.setPassiveMobs(is.getPassiveMobs() + 1);
						}
						return;
					}
				}
			}
		}
	}
}