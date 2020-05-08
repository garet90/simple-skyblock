package holiday.garet.GStructure.BlockEntityTag;

import java.util.ArrayList;
import java.util.List;

import holiday.garet.GStructure.GItem;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class CauldronTag extends BlockEntityTag {
	private int customColor;
	private List<GItem> items; // unsure what this is used for, custom potions perhaps
	private short potionId;
	private byte splashPotion;
	private byte isMovable;
	
	public CauldronTag() {
		items = new ArrayList<GItem>();
	}
	
	public int getCustomColor() {
		return customColor;
	}
	
	public List<GItem> getItems() {
		return items;
	}
	
	public short getPotionId() {
		return potionId;
	}
	
	public byte getSplashPotion() {
		return splashPotion;
	}
	
	public byte getIsMovable() {
		return isMovable;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		customColor = tag.getInt("CustomColor");
		
		ListTag<CompoundTag> items = tag.getListTag("Items").asCompoundTagList();
		items.forEach((item) -> {
			this.items.add(GItem.readNewItem(item));
		});
		
		potionId = tag.getShort("PotionId");
		splashPotion = tag.getByte("SplashPotion");
		isMovable = tag.getByte("isMovable");
	}
}
