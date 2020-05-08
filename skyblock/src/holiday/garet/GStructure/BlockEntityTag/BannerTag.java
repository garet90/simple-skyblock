package holiday.garet.GStructure.BlockEntityTag;

import java.util.ArrayList;
import java.util.List;

import holiday.garet.GStructure.GPattern;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class BannerTag extends BlockEntityTag {
	private List<GPattern> patterns;
	private String customName;
	
	public BannerTag() {
		patterns = new ArrayList<GPattern>();
	}
	
	public List<GPattern> getPatterns() {
		return patterns;
	}
	
	public String getCustomName() {
		return customName;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		customName = tag.getString("CustomName");
		
		if (tag.containsKey("Patterns")) {
			ListTag<CompoundTag> patterns = tag.getListTag("Patterns").asCompoundTagList();
			patterns.forEach((pattern) -> {
				this.patterns.add(GPattern.readNewPattern(pattern));
			});
		}
	}
}
