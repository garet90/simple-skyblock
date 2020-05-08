package holiday.garet.GStructure.BlockEntityTag;

import holiday.garet.GStructure.GItem;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import java.util.ArrayList;
import java.util.List;

public class FurnaceTag extends BlockEntityTag {
	private String customName;
	private String lock;
	private List<GItem> items;
	private short burnTime;
	private short cookTime;
	private short cookTimeTotal;
	// TODO recipe location n
	
	public FurnaceTag() {
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
	
	public short getBurnTime() {
		return burnTime;
	}
	
	public short getCookTime() {
		return cookTime;
	}
	
	public short getCookTimeTotal() {
		return cookTimeTotal;
	}
	
	public void read(CompoundTag tag) {
		customName = tag.getString("CustomName");
		lock = tag.getString("Lock");
		
		ListTag<CompoundTag> items = tag.getListTag("Items").asCompoundTagList();
		items.forEach((item) -> {
			this.items.add(GItem.readNewItem(item));
		});
		
		burnTime = tag.getShort("BurnTime");
		cookTime = tag.getShort("CookTime");
		cookTimeTotal = tag.getShort("CookTimeTotal");
	}
}
