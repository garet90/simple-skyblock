package holiday.garet.GStructure.BlockEntityTag;

import net.querz.nbt.tag.CompoundTag;

public class StructureBlockTag extends BlockEntityTag {
	private String name;
	private String author;
	private String metadata;
	private int posX;
	private int posY;
	private int posZ;
	private int sizeX;
	private int sizeY;
	private int sizeZ;
	private String rotation;
	private String mirror;
	private String mode;
	private float integrity;
	private long seed;
	private byte ignoreEntities;
	private byte showboundingbox;
	private byte powered;
	
	public String getName() {
		return name;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getMetadata() {
		return metadata;
	}
	
	public int getPosX() {
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public int getPosZ() {
		return posZ;
	}
	
	public int getSizeX() {
		return sizeX;
	}
	
	public int getSizeY() {
		return sizeY;
	}
	
	public int getSizeZ() {
		return sizeZ;
	}
	
	public String getRotation() {
		return rotation;
	}
	
	public String getMirror() {
		return mirror;
	}
	
	public String getMode() {
		return mode;
	}
	
	public float getIntegrity() {
		return integrity;
	}
	
	public long getSeed() {
		return seed;
	}
	
	public byte getIgnoreEntities() {
		return ignoreEntities;
	}
	
	public byte getShowboundingbox() {
		return showboundingbox;
	}
	
	public byte getPowered() {
		return powered;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		name = tag.getString("name");
		author = tag.getString("author");
		metadata = tag.getString("metadata");
		posX = tag.getInt("posX");
		posY = tag.getInt("posY");
		posZ = tag.getInt("posZ");
		sizeX = tag.getInt("sizeX");
		sizeY = tag.getInt("sizeY");
		sizeZ = tag.getInt("sizeZ");
		rotation = tag.getString("rotation");
		mirror = tag.getString("mirror");
		mode = tag.getString("mode");
		integrity = tag.getFloat("integrity");
		seed = tag.getLong("seed");
		ignoreEntities = tag.getByte("ignoreEntities");
		showboundingbox = tag.getByte("showboundingbox");
		powered = tag.getByte("powered");
	}
}
