package holiday.garet.GStructure;

import net.querz.nbt.tag.CompoundTag;

public class GDecoration {
	private String id;
	private byte type;
	private double x;
	private double z;
	private double rot;
	
	public void read(CompoundTag tag) {
		id = tag.getString("id");
		type = tag.getByte("type");
		x = tag.getDouble("x");
		z = tag.getDouble("z");
		rot = tag.getDouble("rot");
	}
	
	public String getId() {
		return id;
	}
	
	public byte getType() {
		return type;
	}
	
	public double getX() {
		return x;
	}
	
	public double getZ() {
		return z;
	}
	
	public double getRot() {
		return rot;
	}
	
	public static GDecoration readNewDecoration(CompoundTag tag) {
		GDecoration d = new GDecoration();
		d.read(tag);
		return d;
	}
}
