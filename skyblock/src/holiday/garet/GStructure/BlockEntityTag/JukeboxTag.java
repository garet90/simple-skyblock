package holiday.garet.GStructure.BlockEntityTag;

import holiday.garet.GStructure.GItem;
import net.querz.nbt.tag.CompoundTag;

public class JukeboxTag extends BlockEntityTag {
	private GItem recordItem;
	
	public GItem getRecordItem() {
		return recordItem;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		
		if (tag.containsKey("RecordItem")) {
			recordItem = GItem.readNewItem(tag.getCompoundTag("RecordItem"));
		}
	}
}
