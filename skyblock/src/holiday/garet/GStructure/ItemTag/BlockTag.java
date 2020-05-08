package holiday.garet.GStructure.ItemTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import holiday.garet.GStructure.BlockEntityTag.BlockEntityTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

public class BlockTag extends ItemDataTag {
	private List<String> canPlaceOn;
	private BlockEntityTag blockEntityTag;
	private HashMap<String, String> blockStateTag;
	
	public BlockTag() {
		type = 1;
		canPlaceOn = new ArrayList<String>();
		blockStateTag = new HashMap<String, String>();
	}
	
	public List<String> getCanPlaceOn() {
		return canPlaceOn;
	}
	
	public BlockEntityTag getBlockEntityTag() {
		return blockEntityTag;
	}
	
	public HashMap<String, String> getBlockStateTag() {
		return blockStateTag;
	}
	
	public void read(CompoundTag tag, String id) {
		if (tag.containsKey("CanPlaceOn")) {
			ListTag<StringTag> canPlaceOns = tag.getListTag("CanPlaceOn").asStringTagList();
			canPlaceOns.forEach((canPlaceOn) -> {
				this.canPlaceOn.add(canPlaceOn.getValue());
			});
		}
		if (tag.containsKey("BlockEntityTag")) {
			this.blockEntityTag = BlockEntityTag.readNewEntity(tag.getCompoundTag("BlockEntityTag"), id);
		}
		if (tag.containsKey("BlockStateTag")) {
			CompoundTag blockStateTags = tag.getCompoundTag("BlockStateTag");
			Set<Entry<String, Tag<?>>> stateTags = blockStateTags.entrySet();
			stateTags.forEach((state) -> {
				blockStateTag.put(state.getKey(), ((StringTag)state.getValue()).getValue());
			});
		}
	}
}
