package holiday.garet.GStructure;

import holiday.garet.GStructure.BlockEntityTag.BlockEntityTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;

public class GBlock {
	
	// format from https://minecraft.gamepedia.com/Structure_block_file_format
	
	private int x;
	private int y;
	private int z;
	private int state;
	private BlockEntityTag nbt;
	
	public GBlock(int x, int y, int z, int state) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.state = state;
	}
	
	public GBlock() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.state = 0;
	}
	
	public void read(CompoundTag tag) {
		this.state = tag.getInt("state");
		
		ListTag<IntTag> pos = tag.getListTag("pos").asIntTagList();
		this.x = pos.get(0).asInt();
		this.y = pos.get(1).asInt();
		this.z = pos.get(2).asInt();
		
		if (tag.containsKey("nbt")) {
			nbt = BlockEntityTag.readNewEntity(tag.getCompoundTag("nbt"), null);
		}
	}
	
	public BlockEntityTag getNBT() {
		return nbt;
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
	
	public int getState() {
		return state;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setZ(int z) {
		this.z = z;
	}
	
	public void setState(int state) {
		this.state = state;
	}

	public static GBlock readNewBlock(CompoundTag tag) {
		GBlock b = new GBlock();
		b.read(tag);
		return b;
	}
}
