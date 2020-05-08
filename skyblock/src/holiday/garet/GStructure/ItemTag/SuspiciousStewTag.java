package holiday.garet.GStructure.ItemTag;

import java.util.ArrayList;
import java.util.List;

import holiday.garet.GStructure.GEffect;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class SuspiciousStewTag extends ItemDataTag {
	private List<GEffect> effects;
	
	public SuspiciousStewTag() {
		type = 14;
		effects = new ArrayList<GEffect>();
	}
	
	public List<GEffect> getEffects() {
		return effects;
	}
	
	public void read(CompoundTag tag) {
		if (tag.containsKey("Effects")) {
			ListTag<CompoundTag> effects = tag.getListTag("Effects").asCompoundTagList();
			effects.forEach((effect) -> {
				this.effects.add(GEffect.readNewEffect(effect));
			});
		}
	}
}
