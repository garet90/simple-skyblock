package holiday.garet.GStructure.ItemTag;

import java.util.List;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;

public class DisplayPropertiesTag extends ItemDataTag {
	private int color;
	private String name;
	private List<String> lore;
	private int hideFlags;
	
	public DisplayPropertiesTag() {
		type = 6;
	}
	
	public int getColor() {
		return color;
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getLore() {
		return lore;
	}
	
	public int getHideFlags() {
		return hideFlags;
	}
	
	public void read(CompoundTag tag) {
		CompoundTag display = tag.getCompoundTag("display");
		this.color = display.getInt("color");
		this.name = display.getString("Name");
		if (display.containsKey("Lore")) {
			ListTag<StringTag> lore = display.getListTag("Lore").asStringTagList();
			lore.forEach((l) -> {
				this.lore.add(l.getValue());
			});
		}
		this.hideFlags = tag.getInt("HideFlags");
	}
}
