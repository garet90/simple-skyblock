package holiday.garet.GStructure.BlockEntityTag;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

public class PistonTag extends BlockEntityTag {
	private String name;
	private HashMap<String, String> properties;
	private int facing;
	private float progress;
	private byte extending;
	private byte source;
	
	public PistonTag() {
		properties = new HashMap<String, String>();
	}
	
	public String getName() {
		return name;
	}
	
	public HashMap<String, String> getProperties() {
		return properties;
	}
	
	public int getFacing() {
		return facing;
	}
	
	public float getProgress() {
		return progress;
	}
	
	public byte getExtending() {
		return extending;
	}
	
	public byte getSource() {
		return source;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		CompoundTag blockState = tag.getCompoundTag("blockState");
		name = blockState.getString("Name");

		Set<Entry<String, Tag<?>>> props = blockState.getCompoundTag("Properties").entrySet();
		props.forEach((prop) -> {
			properties.put(prop.getKey(), ((StringTag)prop.getValue()).getValue());
		});
		
		facing = tag.getInt("facing");
		progress = tag.getFloat("progress");
		extending = tag.getByte("extending");
		source = tag.getByte("source");
	}

	public boolean getExtendingAsBoolean() {
		if (extending == 1) {
			return true;
		}
		return false;
	}
}
