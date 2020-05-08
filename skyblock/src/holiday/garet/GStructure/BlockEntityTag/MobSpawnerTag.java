package holiday.garet.GStructure.BlockEntityTag;

import java.util.ArrayList;
import java.util.List;

import holiday.garet.GStructure.GEntityData;
import holiday.garet.GStructure.GSpawnPotential;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class MobSpawnerTag extends BlockEntityTag {
	private List<GSpawnPotential> spawnPotentials;
	private GEntityData spawnData;
	private short spawnCount;
	private short spawnRange;
	private short delay;
	private short minSpawnDelay;
	private short maxSpawnDelay;
	private short maxNearbyEntities;
	private short requiredPlayerRange;
	
	public MobSpawnerTag() {
		spawnPotentials = new ArrayList<GSpawnPotential>();
	}
	
	public List<GSpawnPotential> getSpawnPotentials() {
		return spawnPotentials;
	}
	
	public GEntityData getSpawnData() {
		return spawnData;
	}
	
	public short getSpawnCount() {
		return spawnCount;
	}
	
	public short getSpawnRange() {
		return spawnRange;
	}
	
	public short getDelay() {
		return delay;
	}
	
	public short getMinSpawnDelay() {
		return minSpawnDelay;
	}
	
	public short getMaxSpawnDelay() {
		return maxSpawnDelay;
	}
	
	public short getMaxNearbyEntities() {
		return maxNearbyEntities;
	}
	
	public short getRequiredPlayerRange() {
		return requiredPlayerRange;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		ListTag<CompoundTag> spawnPotentials = tag.getListTag("SpawnPotentials").asCompoundTagList();
		spawnPotentials.forEach((spawnPotential) -> {
			this.spawnPotentials.add(GSpawnPotential.readNewPotential(spawnPotential));
		});
		
		spawnData = GEntityData.readNewEntity(tag.getCompoundTag("SpawnData"));
		spawnCount = tag.getShort("SpawnCount");
		spawnRange = tag.getShort("SpawnRange");
		delay = tag.getShort("Delay");
		minSpawnDelay = tag.getShort("MinSpawnDelay");
		maxSpawnDelay = tag.getShort("MaxSpawnDelay");
		maxNearbyEntities = tag.getShort("MaxNearbyEntities");
		requiredPlayerRange = tag.getShort("requiredPlayerRange");
	}
}
