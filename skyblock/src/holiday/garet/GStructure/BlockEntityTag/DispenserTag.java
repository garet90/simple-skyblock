package holiday.garet.GStructure.BlockEntityTag;

import java.util.ArrayList;
import java.util.List;

import holiday.garet.GStructure.GItem;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class DispenserTag extends BlockEntityTag {
	private String customName;
	private String lock;
	private List<GItem> items;
	private String lootTable;
	private long lootTableSeed;
	
	public DispenserTag() {
		items = new ArrayList<GItem>();
	}
	
	public String getCustomName() {
		return customName;
	}
	
	public String getLock() {
		return lock;
	}
	
	public List<GItem> getItems() {
		return items;
	}
	
	public String getLootTable() {
		return lootTable;
	}
	
	public long getLootTableSeed() {
		return lootTableSeed;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		customName = tag.getString("CompoundTag");
		lock = tag.getString("Lock");
		
		ListTag<CompoundTag> items = tag.getListTag("Items").asCompoundTagList();
		items.forEach((item) -> {
			this.items.add(GItem.readNewItem(item));
		});
		
		lootTable = tag.getString("LootTable");
		lootTableSeed = tag.getLong("LootTableSeed");
	}
}
