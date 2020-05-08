package holiday.garet.GStructure.BlockEntityTag;

import java.util.ArrayList;
import java.util.List;

import holiday.garet.GStructure.GHiveEntity;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class BeehiveTag extends BlockEntityTag {
	private int flowerPosX;
	private int flowerPosY;
	private int flowerPosZ;
	private List<GHiveEntity> bees;
	
	public BeehiveTag() {
		bees = new ArrayList<GHiveEntity>();
	}
	
	public int getFlowerPosX() {
		return flowerPosX;
	}
	
	public int getFlowerPosY() {
		return flowerPosY;
	}
	
	public int getFlowerPosZ() {
		return flowerPosZ;
	}
	
	public List<GHiveEntity> getBees() {
		return bees;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		if (tag.containsKey("FlowerPos")) {
			CompoundTag flowerPos = tag.getCompoundTag("FlowerPos");
			flowerPosX = flowerPos.getInt("X");
			flowerPosY = flowerPos.getInt("Y");
			flowerPosZ = flowerPos.getInt("Z");
		}
		
		if (tag.containsKey("Bees")) {
			ListTag<CompoundTag> bees = tag.getListTag("Bees").asCompoundTagList();
			bees.forEach((bee) -> {
				this.bees.add(GHiveEntity.readNewEntity(bee));
			});
		}
	}
}
