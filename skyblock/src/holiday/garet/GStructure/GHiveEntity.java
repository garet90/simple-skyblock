package holiday.garet.GStructure;

import net.querz.nbt.tag.CompoundTag;

public class GHiveEntity {
	private int minOccupationTicks;
	private int ticksInHive;
	private GEntityData entityData;
	
	public void read(CompoundTag tag) {
		minOccupationTicks = tag.getInt("MinOccupationTicks");
		ticksInHive = tag.getInt("TicksInHive");
		entityData = GEntityData.readNewEntity(tag.getCompoundTag("EntityData"));
	}
	
	public int getMinOccupationTicks() {
		return minOccupationTicks;
	}
	
	public int getTicksInHive() {
		return ticksInHive;
	}
	
	public GEntityData getEntityData() {
		return entityData;
	}
	
	public static GHiveEntity readNewEntity(CompoundTag tag) {
		GHiveEntity e = new GHiveEntity();
		e.read(tag);
		return e;
	}
}
