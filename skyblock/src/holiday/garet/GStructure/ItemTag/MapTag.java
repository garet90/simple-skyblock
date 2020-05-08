package holiday.garet.GStructure.ItemTag;

import java.util.ArrayList;
import java.util.List;

import holiday.garet.GStructure.GDecoration;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class MapTag extends ItemDataTag {
	private int map;
	private int map_scale_direction;
	private List<GDecoration> decorations;
	private int mapColor;
	
	public MapTag() {
		type = 11;
		decorations = new ArrayList<GDecoration>();
	}
	
	public int getMap() {
		return map;
	}
	
	public int getMapScaleDirection() {
		return map_scale_direction;
	}
	
	public List<GDecoration> getDecorations() {
		return decorations;
	}
	
	public int getMapColor() {
		return mapColor;
	}
	
	public void read(CompoundTag tag) {
		map = tag.getInt("map");
		map_scale_direction = tag.getInt("map_scale_direction");
		if (tag.containsKey("Decorations")) {
			ListTag<CompoundTag> decorations = tag.getListTag("Decorations").asCompoundTagList();
			decorations.forEach((decoration) -> {
				this.decorations.add(GDecoration.readNewDecoration(decoration));
			});
		}
		if (tag.containsKey("display")) {
			this.mapColor = tag.getCompoundTag("display").getInt("MapColor");
		}
	}
}
