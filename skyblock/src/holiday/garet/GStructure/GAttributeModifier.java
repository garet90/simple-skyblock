package holiday.garet.GStructure;

import net.querz.nbt.tag.CompoundTag;

public class GAttributeModifier {
	private String attributeName;
	private String name;
	private String slot;
	private int operation;
	private double amount;
	private long UUIDMost;
	private long UUIDLeast;
	
	public void read(CompoundTag tag) {
		this.attributeName = tag.getString("AttributeName");
		this.name = tag.getString("Name");
		this.slot = tag.getString("Slot");
		this.operation = tag.getInt("Operation");
		this.amount = tag.getDouble("Amount");
		this.UUIDMost = tag.getLong("UUIDMost");
		this.UUIDLeast = tag.getLong("UUIDLeast");
	}
	
	public String getAttributeName() {
		return attributeName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSlot() {
		return slot;
	}
	
	public int getOperation() {
		return operation;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public long getUUIDMost() {
		return UUIDMost;
	}
	
	public long getUUIDLeast() {
		return UUIDLeast;
	}
	
	public static GAttributeModifier readNewModifier(CompoundTag tag) {
		GAttributeModifier m = new GAttributeModifier();
		m.read(tag);
		return m;
	}
}
