package holiday.garet.GStructure.ItemTag;

import java.util.ArrayList;
import java.util.List;

import holiday.garet.GStructure.GAttributeModifier;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class AttributeModifiersTag extends ItemDataTag {
	private List<GAttributeModifier> attributeModifiers;

	public AttributeModifiersTag() {
		type = 0;
		attributeModifiers = new ArrayList<GAttributeModifier>();
	}
	
	public List<GAttributeModifier> getAttributeModifiers() {
		return attributeModifiers;
	}
	
	public void read(CompoundTag tag) {
		ListTag<CompoundTag> attributeModifiers = tag.getListTag("AttributeModifiers").asCompoundTagList();
		attributeModifiers.forEach((attributeModifier) -> {
			this.attributeModifiers.add(GAttributeModifier.readNewModifier(attributeModifier));
		});
	}
}
