package holiday.garet.GStructure.ItemTag;

import net.querz.nbt.tag.CompoundTag;

public class PlayerHeadTag extends ItemDataTag {
	private String skullOwner;
	
	public String getSkullOwner() {
		return skullOwner;
	}
	
	public void read(CompoundTag tag) {
		this.skullOwner = tag.getString("SkullOwner");
	}
}
