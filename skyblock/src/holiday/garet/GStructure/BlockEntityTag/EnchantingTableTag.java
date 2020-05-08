package holiday.garet.GStructure.BlockEntityTag;

import net.querz.nbt.tag.CompoundTag;

public class EnchantingTableTag extends BlockEntityTag {
	private String customName;
	
	public String getCustomName() { 
		return customName;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		customName = tag.getString("CustomName");
	}
}
