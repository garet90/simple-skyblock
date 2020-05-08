package holiday.garet.GStructure.BlockEntityTag;

import net.querz.nbt.tag.CompoundTag;

public class BlockEntityTag {
	String id = null;
	int x;
	int y;
	int z;
	byte keepPacked;
	
	public void read(CompoundTag tag) {
		if (tag.containsKey("id")) {
			this.id = tag.getString("id");
		}
		this.x = tag.getInt("x");
		this.y = tag.getInt("y");
		this.z = tag.getInt("z");
		this.keepPacked = tag.getByte("keepPacked");
	}
	
	public String getId() {
		return id;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public byte getKeepPacked() {
		return keepPacked;
	}

	public static BlockEntityTag readNewEntity(CompoundTag tag, String id) {
		// determine type
		BlockEntityTag t = new BlockEntityTag();
		if (id == null) {
			id = tag.getString("id");
		}
		switch (id.replaceFirst("minecraft:", "")) {
		case "banner":
			t = new BannerTag();
			break;
		case "chest":
		case "shulker_box":
		case "barrel":
			t = new ChestTag();
			break;
		case "beacon":
			t = new BeaconTag();
			break;
		case "beehive":
			t = new BeehiveTag();
			break;
		case "furnace":
		case "smoker":
		case "blast_furnace":
			t = new FurnaceTag();
			break;
		case "brewing_stand":
			t = new BrewingStandTag();
			break;
		case "campfire": 
			t = new CampfireTag();
			break;
		case "cauldron":
			t = new CauldronTag();
			break;
		case "comparator":
			t = new ComparatorTag();
			break;
		case "command_block":
			t = new CommandBlockTag();
			break;
		case "conduit":
			t = new ConduitTag();
			break;
		case "dispenser":
		case "dropper":
			t = new DispenserTag();
			break;
		case "enchanting_table":
			t = new EnchantingTableTag();
			break;
		case "end_gateway":
			t = new EndGatewayTag();
			break;
		case "hopper":
			t = new HopperTag();
			break;
		case "jigsaw":
			t = new JigsawTag();
			break;
		case "jukebox":
			t = new JukeboxTag();
			break;
		case "lectern":
			t = new LecternTag();
			break;
		case "mob_spawner":
			t = new MobSpawnerTag();
			break;
		case "piston":
			t = new PistonTag();
			break;
		case "sign":
			t = new SignTag();
			break;
		// TODO skulltag
		case "structure_block":
			t = new StructureBlockTag();
			break;
		}
		t.read(tag);
		if (t.id == null) {
			t.id = id;
		}
		return t;
	}
}
