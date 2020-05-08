package holiday.garet.GStructure;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;

public class GEntity {
	
	// format from https://minecraft.gamepedia.com/Structure_block_file_format
	
	private double x;
	private double y;
	private double z;
	
	private int blockX;
	private int blockY;
	private int blockZ;

	private GEntityData entityData;
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	public void setBlockX(int blockX) {
		this.blockX = blockX;
	}
	
	public void setBlockY(int blockY) {
		this.blockY = blockY;
	}
	
	public void setBlockZ(int blockZ) {
		this.blockZ = blockZ;
	}
	
	public void setEntityData(GEntityData entityData) {
		this.entityData = entityData;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public int getBlockX() {
		return blockX;
	}
	
	public int getBlockY() {
		return blockY;
	}
	
	public int getBlockZ() {
		return blockZ;
	}
	
	public GEntityData getEntityData() {
		return entityData;
	}
	
	public void read(CompoundTag tag) {
		ListTag<DoubleTag> pos = tag.getListTag("pos").asDoubleTagList();
		this.x = pos.get(0).asDouble();
		this.y = pos.get(1).asDouble();
		this.z = pos.get(2).asDouble();
		ListTag<IntTag> blockPos = tag.getListTag("blockPos").asIntTagList();
		this.blockX = blockPos.get(0).asInt();
		this.blockY = blockPos.get(1).asInt();
		this.blockZ = blockPos.get(2).asInt();
		
		this.entityData = GEntityData.readNewEntity(tag.getCompoundTag("nbt"));
	}
	
	public static GEntity readNewEntity(CompoundTag tag) {
		GEntity e = new GEntity();
		e.read(tag);
		return e;
	}
}
