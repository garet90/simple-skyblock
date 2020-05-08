package holiday.garet.GStructure.ItemTag;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

public class DebugStickTag extends ItemDataTag {
	private HashMap<String, String> properties;
	
	public DebugStickTag() {
		type = 5;
		properties = new HashMap<String, String>();
	}
	
	public HashMap<String, String> getProperties() {
		return properties;
	}
	
	public void read(CompoundTag tag) {
		Set<Entry<String, Tag<?>>> props = tag.getCompoundTag("DebugProperty").entrySet();
		props.forEach((prop) -> {
			this.properties.put(prop.getKey(), ((StringTag)prop.getValue()).getValue());
		});
	}
}
