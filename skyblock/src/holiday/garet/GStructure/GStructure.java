package holiday.garet.GStructure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Beacon;
import org.bukkit.block.Beehive;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Campfire;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Container;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.EnchantingTable;
import org.bukkit.block.EndGateway;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Lectern;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Piston;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.json.JSONObject;

import holiday.garet.GStructure.BlockEntityTag.BannerTag;
import holiday.garet.GStructure.BlockEntityTag.BeaconTag;
import holiday.garet.GStructure.BlockEntityTag.BeehiveTag;
import holiday.garet.GStructure.BlockEntityTag.BlockEntityTag;
import holiday.garet.GStructure.BlockEntityTag.BrewingStandTag;
import holiday.garet.GStructure.BlockEntityTag.CampfireTag;
import holiday.garet.GStructure.BlockEntityTag.CauldronTag;
import holiday.garet.GStructure.BlockEntityTag.ChestTag;
import holiday.garet.GStructure.BlockEntityTag.CommandBlockTag;
import holiday.garet.GStructure.BlockEntityTag.DispenserTag;
import holiday.garet.GStructure.BlockEntityTag.EnchantingTableTag;
import holiday.garet.GStructure.BlockEntityTag.EndGatewayTag;
import holiday.garet.GStructure.BlockEntityTag.FurnaceTag;
import holiday.garet.GStructure.BlockEntityTag.HopperTag;
import holiday.garet.GStructure.BlockEntityTag.JukeboxTag;
import holiday.garet.GStructure.BlockEntityTag.LecternTag;
import holiday.garet.GStructure.BlockEntityTag.PistonTag;
import holiday.garet.GStructure.BlockEntityTag.SignTag;
import holiday.garet.GStructure.Event.StructurePaintEvent;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

public class GStructure {
	
	// format from https://minecraft.gamepedia.com/Structure_block_file_format
	
	private static int nextBuildKey = 0;
	
	private int dataVersion;
	
	private List<GBlock> blocks;
	
	private List<GEntity> entities;
	
	private List<GPalette> palette;
	
	private int width;
	private int height;
	private int depth;
	
	Plugin plugin;
	
	public GStructure(Plugin plugin) {
		blocks = new ArrayList<GBlock>();
		entities = new ArrayList<GEntity>();
		palette = new ArrayList<GPalette>();
		this.plugin = plugin;
	}
	
	public int getDataVersion() {
		return dataVersion;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public void read(File file) {
		if (file.exists()) {
			NamedTag nbt = new NamedTag(null, null);
			try {
				nbt = NBTUtil.read(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			CompoundTag nbt_t = (CompoundTag) nbt.getTag();
			
			this.dataVersion = nbt_t.getInt("DataVersion"); // DataVersion
			
			ListTag<CompoundTag> blocks = nbt_t.getListTag("blocks").asCompoundTagList(); // blocks
			blocks.forEach((block) -> {
				this.blocks.add(GBlock.readNewBlock(block));
			});
			plugin.getLogger().info("Loaded " + this.blocks.size() + " blocks");
			
			ListTag<CompoundTag> entities = nbt_t.getListTag("entities").asCompoundTagList();
			entities.forEach((entity) -> {
				this.entities.add(GEntity.readNewEntity(entity));
				// TODO specific entity data
			});
			plugin.getLogger().info("Loaded " + this.entities.size() + " entities");
			
			ListTag<CompoundTag> palette = nbt_t.getListTag("palette").asCompoundTagList();
			palette.forEach((pal) -> {
				GPalette p = new GPalette();
				
				p.setName(pal.getString("Name"));
				
				if (pal.containsKey("Properties")) {
					CompoundTag Properties = pal.getCompoundTag("Properties");
					
					Set<Entry<String, Tag<?>>> props = Properties.entrySet();
					props.forEach((prop) -> {
						p.setProperty(prop.getKey(), ((StringTag)prop.getValue()).getValue());
					});
				}
				this.palette.add(p);
			});
			plugin.getLogger().info("Loaded " + this.palette.size() + " palettes");
			
			ListTag<IntTag> size = nbt_t.getListTag("size").asIntTagList();
			this.width = size.get(0).asInt();
			this.height = size.get(1).asInt();
			this.depth = size.get(2).asInt();
		} else {
			plugin.getLogger().warning("File " + file.getName() + " does not exist!");
		}
	}
	
	// Use GEntityData.readNewEntity instead
	@Deprecated
	public GEntityData readEntity(CompoundTag reader) {
		GEntityData e = new GEntityData();
		
		e.setId(reader.getString("id"));
		
		ListTag<DoubleTag> Pos = reader.getListTag("Pos").asDoubleTagList();
		e.setX(Pos.get(0).asDouble());
		e.setY(Pos.get(1).asDouble());
		e.setZ(Pos.get(2).asDouble());

		ListTag<DoubleTag> Motion = reader.getListTag("Motion").asDoubleTagList();
		e.setDX(Motion.get(0).asDouble());
		e.setDY(Motion.get(1).asDouble());
		e.setDZ(Motion.get(2).asDouble());
		
		ListTag<FloatTag> Rotation = reader.getListTag("Rotation").asFloatTagList();
		e.setYaw(Rotation.get(0).asFloat());
		e.setPitch(Rotation.get(1).asFloat());
		
		e.setFallDistance(reader.getFloat("FallDistance"));
		
		e.setFire(reader.getShort("Fire"));
		
		e.setAir(reader.getShort("Air"));
		
		e.setOnGround(reader.getByte("OnGround"));
		
		e.setDimension(reader.getInt("Dimension"));
		
		e.setInvulnerable(reader.getByte("Invulnerable"));
		
		e.setPortalCooldown(reader.getInt("PortalCooldown"));
		
		e.setUUIDMost(reader.getLong("UUIDMost"));
		e.setUUIDLeast(reader.getLong("UUIDLeast"));
		
		e.setCustomName(reader.getString("CustomName"));
		
		e.setCustomNameVisible(reader.getByte("CustomNameVisible"));
		
		e.setSilent(reader.getByte("Silent"));
		
		List<GEntityData> passengers = new ArrayList<GEntityData>();
		ListTag<CompoundTag> Passengers = reader.getListTag("Passengers").asCompoundTagList();
		Passengers.forEach((Passenger) -> {
			passengers.add(readEntity(Passenger));
		});
		e.setPassengers(passengers);
		
		e.setGlowing(reader.getByte("Glowing"));

		List<String> tags = new ArrayList<String>();
		ListTag<StringTag> Tags = reader.getListTag("Tags").asStringTagList();
		Tags.forEach((TagE) -> {
			tags.add(TagE.getValue());
		});
		e.setTags(tags);
		
		return e;
	}
	
	public static int getNextBuildKey() {
		return nextBuildKey;
	}
	
	public void generate(Location location) {
		int buildKey = nextBuildKey++;
		// generate blocks
		for (int i = 0; i < blocks.size(); i++) {
			GBlock block = blocks.get(i);
			Location blockLocation = location.clone().add(new Vector(block.getX(), block.getY(), block.getZ()));
			paint(blockLocation, palette.get(block.getState()), block.getNBT(), buildKey);
		}
		// add entities
		for (int i = 0; i < entities.size(); i++) {
			GEntity entity = entities.get(i);
			Location entityLocation = location.clone().add(new Vector(entity.getX(), entity.getY(), entity.getZ()));
			summonEntity(entityLocation, entity.getEntityData());
		}
	}
	
	@SuppressWarnings("deprecation")
	private void paint(Location location, GPalette palette, BlockEntityTag nbt, int buildKey) {
		StructurePaintEvent e = new StructurePaintEvent(location, palette, nbt, buildKey);
		Bukkit.getPluginManager().callEvent(e);
		if (!e.getCancelled()) {
			World world = location.getWorld();
			Block block = world.getBlockAt(location);
			String material = palette.getName().split(":")[1];
			block.setType(Material.matchMaterial(material));
			if (nbt != null) {
				switch (nbt.getId().replaceFirst("minecraft:", "")) {
				case "banner":
					if (true) {
						BannerTag n = (BannerTag) nbt;
						Banner b = (Banner) block.getState();
						n.getPatterns().forEach((pattern) -> {
							b.addPattern(pattern.get());
						});
						b.update();
					}
					break;
				case "chest":
				case "shulker_box":
				case "barrel":
					if (true) {
						ChestTag n = (ChestTag) nbt;
						Container c = (Container)block.getState();
						if (n.getCustomName() != null && n.getCustomName() != "") {
							c.setCustomName(new JSONObject(n.getCustomName()).getString("text"));
						}
						c.setLock(n.getLock());
						c.update();
						/*if (n.getLootTable() != null && LootTables.valueOf(n.getLootTable()) != null) {
							c.setLootTable(LootTables.valueOf(n.getLootTable()).getLootTable());
						}
						TODO loot tables are beign wack.
						*/
						// TODO loottableseed
						n.getItems().forEach((citem) -> {
							c.getInventory().setItem(citem.getSlot(), citem.get(plugin));
						});
					}
					break;
				case "beacon":
					if (true) {
						BeaconTag n = (BeaconTag) nbt;
						Beacon b = (Beacon) block.getState();
						b.setPrimaryEffect(PotionEffectType.getById(n.getPrimary()));
						b.setSecondaryEffect(PotionEffectType.getById(n.getSecondary()));
						b.setLock(n.getLock());
						b.update();
					}
					break;
				case "beehive":
					if (true) {
						BeehiveTag n = (BeehiveTag) nbt;
						Beehive b = (Beehive) block.getState();
						b.setFlower(new Location(location.getWorld(), n.getFlowerPosX(), n.getFlowerPosY(), n.getFlowerPosZ()));
						// TODO bees in hive
						b.update();
					}
					break;
				case "blast_furnace":
				case "furnace":
				case "smoker":
					if (true) {
						FurnaceTag n = (FurnaceTag) nbt;
						Furnace f = (Furnace)block.getState();
						if (n.getCustomName() != null && n.getCustomName() != "") {
							f.setCustomName(new JSONObject(n.getCustomName()).getString("text"));
						}
						f.setLock(n.getLock());
						f.setBurnTime(n.getBurnTime());
						f.setCookTime(n.getCookTime());
						f.setCookTimeTotal(n.getCookTimeTotal());
						f.update();
						n.getItems().forEach((item) -> {
							f.getInventory().setItem(item.getSlot(), item.get(plugin));
						});
					}
					break;
				case "brewing_stand":
					if (true) {
						BrewingStandTag n = (BrewingStandTag) nbt;
						BrewingStand b = (BrewingStand) block.getState();
						if (n.getCustomName() != null && n.getCustomName() != "") {
							b.setCustomName(new JSONObject(n.getCustomName()).getString("text"));
						}
						b.setLock(n.getLock());
						b.setBrewingTime(n.getBrewTime());
						b.setFuelLevel(n.getFuel());
						b.update();
						n.getItems().forEach((item) -> {
							b.getInventory().setItem(item.getSlot(), item.get(plugin));
						});
					}
					break;
				case "campfire": 
					if (true) {
						CampfireTag n = (CampfireTag) nbt;
						Campfire c = (Campfire) block.getState();
						for (int i = 0; i < n.getCookingTimes().length; i++) {
							c.setCookTime(i, n.getCookingTimes()[i]);
						}
						for (int i = 0; i < n.getCookingTotalTimes().length; i++) {
							c.setCookTimeTotal(i, n.getCookingTotalTimes()[i]);
						}
						n.getItems().forEach((item) -> {
							c.setItem(item.getSlot(), item.get(plugin));
						});
						c.update();
					}
					break;
				case "cauldron":
					if (true) {
						@SuppressWarnings("unused")
						CauldronTag n = (CauldronTag) nbt;
						// TODO cauldrons
					}
					break;
				case "command_block":
					if (true) {
						CommandBlockTag n = (CommandBlockTag) nbt;
						CommandBlock c = (CommandBlock) block.getState();
						if (n.getCustomName() != null && n.getCustomName() != "") {
							c.setName(new JSONObject(n.getCustomName()).getString("text"));
						}
						c.setCommand(n.getCommand());
						// TODO other command block stuff (PRIORITY: LOW)
						c.update();
					}
					break;
				case "dispenser":
					if (true) {
						DispenserTag n = (DispenserTag) nbt;
						Dispenser d = (Dispenser) block.getState();
						if (n.getCustomName() != null && n.getCustomName() != "") {
							d.setCustomName(new JSONObject(n.getCustomName()).getString("text"));
						}
						d.setLock(n.getLock());
						d.update();
						n.getItems().forEach((item) -> {
							d.getInventory().setItem(item.getSlot(), item.get(plugin));
						});
						// TODO loot tables
					}
					break;
				case "dropper":
					if (true) {
						DispenserTag n = (DispenserTag) nbt;
						Dropper d = (Dropper) block.getState();
						if (n.getCustomName() != null && n.getCustomName() != "") {
							d.setCustomName(new JSONObject(n.getCustomName()).getString("text"));
						}
						d.setLock(n.getLock());
						d.update();
						n.getItems().forEach((item) -> {
							d.getInventory().setItem(item.getSlot(), item.get(plugin));
						});
						// TODO loot tables
					}
					break;
				case "enchanting_table":
					if (true) {
						EnchantingTableTag n = (EnchantingTableTag) nbt;
						EnchantingTable t = (EnchantingTable) block.getState();
						if (n.getCustomName() != null && n.getCustomName() != "") {
							t.setCustomName(new JSONObject(n.getCustomName()).getString("text"));
						}
						t.update();
					}
					break;
				case "end_gateway":
					if (true) {
						EndGatewayTag n = (EndGatewayTag) nbt;
						EndGateway g = (EndGateway) block.getState();
						g.setAge(n.getAge());
						g.setExactTeleport(n.getExactTeleportAsBoolean());
						g.setExitLocation(new Location(null, n.getExitPortalX(), n.getExitPortalY(), n.getExitPortalZ()));
						g.update();
					}
					break;
				case "hopper":
					if (true) {
						HopperTag n = (HopperTag) nbt;
						Hopper h = (Hopper) block.getState();
						if (n.getCustomName() != null && n.getCustomName() != "") {
							h.setCustomName(new JSONObject(n.getCustomName()).getString("text"));
						}
						h.setLock(n.getLock());
						h.update();
						n.getItems().forEach((item) -> {
							h.getInventory().setItem(item.getSlot(), item.get(plugin));
						});
						// TODO loot table
					}
					break;
				case "jukebox":
					if (true) {
						JukeboxTag n = (JukeboxTag) nbt;
						Jukebox j = (Jukebox) block.getState();
						if (n.getRecordItem() != null) {
							j.setRecord(n.getRecordItem().get(plugin));
						}
						j.update();
					}
					break;
				case "lectern":
					if (true) {
						LecternTag n = (LecternTag) nbt;
						Lectern l = (Lectern) block.getState();
						l.getInventory().setItem(0, n.getBook().get(plugin));
						l.setPage(n.getPage());
					}
					break;
				// TODO mob spawners
				case "piston":
					if (true) {
						PistonTag n = (PistonTag) nbt;
						Piston p = (Piston) block.getState();
						p.setExtended(n.getExtendingAsBoolean());
						p.setFacing(BlockFace.values()[n.getFacing()]);
						// TODO block state values
					}
					break;
				case "sign":
					if (true) {
						SignTag n = (SignTag) nbt;
						Sign s = (Sign) block.getState();
						s.setColor(n.getColorAsDyeColor());
						if (n.getText1() != null && n.getText1() != "") {
							s.setLine(0, new JSONObject(n.getText1()).getString("text"));
						}
						if (n.getText2() != null && n.getText2() != "") {
							s.setLine(1, new JSONObject(n.getText2()).getString("text"));
						}
						if (n.getText3() != null && n.getText3() != "") {
							s.setLine(2, new JSONObject(n.getText3()).getString("text"));
						}
						if (n.getText4() != null && n.getText4() != "") {
							s.setLine(3, new JSONObject(n.getText4()).getString("text"));
						}
						s.update();
					}
					break;
				// TODO skulltag
				// TODO structureblock
				}
			}
			BlockData bd = block.getBlockData();
			palette.getProperties().forEach((name, value) -> {
				switch(name) {
				case "facing":
					if (bd instanceof Directional) {
						((Directional) bd).setFacing(BlockFace.valueOf(value.toUpperCase()));
					} else {
						plugin.getLogger().warning("Unable to set direction of block!");
					}
					break;
				case "rotation":
					BlockFace f = BlockFace.SOUTH;
					switch (value) {
					case "0": f = BlockFace.SOUTH; break;
					case "1": f = BlockFace.SOUTH_SOUTH_WEST; break;
					case "2": f = BlockFace.SOUTH_WEST; break;
					case "3": f = BlockFace.WEST_SOUTH_WEST; break;
					case "4": f = BlockFace.WEST; break;
					case "5": f = BlockFace.WEST_NORTH_WEST; break;
					case "6": f = BlockFace.NORTH_WEST; break;
					case "7": f = BlockFace.NORTH_NORTH_WEST; break;
					case "8": f = BlockFace.NORTH; break;
					case "9": f = BlockFace.NORTH_NORTH_EAST; break;
					case "10": f = BlockFace.NORTH_EAST; break;
					case "11": f = BlockFace.EAST_NORTH_EAST; break;
					case "12": f = BlockFace.EAST; break;
					case "13": f = BlockFace.EAST_SOUTH_EAST; break;
					case "14": f = BlockFace.SOUTH_EAST; break;
					case "15": f = BlockFace.EAST_SOUTH_EAST; break;
					}
					if (bd instanceof Rotatable) {
						((Rotatable) bd).setRotation(f);
					} else {
						plugin.getLogger().warning("Unable to rotate block!");
					}
					break;
				default:
					block.setMetadata(name, new GMetaDataValue(value, plugin));
				}
			});
			block.setBlockData(bd);
		}
	}
	
	private Entity summonEntity(Location location, GEntityData entityData) {
		EntityType entityType = EntityType.valueOf(entityData.getId().replaceFirst("minecraft:", "").toUpperCase());
		Entity entity = location.getWorld().spawnEntity(location, entityType);
		entity.setVelocity(new Vector(entityData.getDX(), entityData.getDY(), entityData.getDZ()));
		entity.setRotation(entityData.getYaw(), entityData.getPitch());
		entity.setFallDistance(entityData.getFallDistance());
		entity.setFireTicks(entityData.getFire());
		entity.setInvulnerable(entityData.getInvulnerableAsBoolean());
		entity.setPortalCooldown(entityData.getPortalCooldown());
		entity.setCustomName(entityData.getCustomName());
		entity.setCustomNameVisible(entityData.getCustomNameVisibleAsBoolean());
		entity.setSilent(entityData.getSilentAsBoolean());
		entity.setGlowing(entityData.getGlowingAsBoolean());
		List<GEntityData> passengers = entityData.getPassengers();
		for (int i = 0; i < passengers.size(); i++) {
			entity.addPassenger(summonEntity(location, passengers.get(i)));
		}
		List<String> tags = entityData.getTags();
		for (int i = 0; i < tags.size(); i++) {
			entity.addScoreboardTag(tags.get(i));
		}
		return entity;
	}
}
