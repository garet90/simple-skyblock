package holiday.garet.GStructure.ItemTag;

import java.util.ArrayList;
import java.util.List;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;

public class GeneralTag extends ItemDataTag {
	private int damage;
	private byte unbreakable;
	private List<String> canDestroy;
	private int customModelData;
	
	public GeneralTag() {
		type = 10;
		canDestroy = new ArrayList<String>();
	}
	
	public int getDamage() {
		return damage;
	}
	
	public byte getUnbreakable() {
		return unbreakable;
	}
	
	public List<String> getCanDestroy() {
		return canDestroy;
	}
	
	public int getCustomModelData() {
		return customModelData;
	}
	
	public void read(CompoundTag tag) {
		this.damage = tag.getInt("Damage");
		this.unbreakable = tag.getByte("Unbreakable");
		if (tag.containsKey("CanDestroy")) {
			ListTag<StringTag> canDestroy = tag.getListTag("CanDestroy").asStringTagList();
			canDestroy.forEach((cd) -> {
				this.canDestroy.add(cd.getValue());
			});
		}
		this.customModelData = tag.getInt("CustomModelData");
	}

	public boolean getUnbreakableAsBoolean() {
		if (unbreakable == 1) {
			return true;
		}
		return false;
	}
}
