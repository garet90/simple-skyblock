package holiday.garet.GStructure;

import net.querz.nbt.tag.CompoundTag;

public class GSpawnPotential {
	private GEntityData entity;
	private int weight;
	
	public void read(CompoundTag tag) {
		entity = GEntityData.readNewEntity(tag.getCompoundTag("Entity"));
		weight = tag.getInt("Weight");
	}
	
	public GEntityData getEntity() {
		return entity;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public static GSpawnPotential readNewPotential(CompoundTag tag) {
		GSpawnPotential p = new GSpawnPotential();
		p.read(tag);
		return p;
	}
}
