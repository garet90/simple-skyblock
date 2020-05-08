package holiday.garet.GStructure.ItemTag;

import java.util.ArrayList;
import java.util.List;

import holiday.garet.GStructure.GItem;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class CrossbowsTag extends ItemDataTag {
	private List<GItem> chargedProjectiles;
	private byte charged;
	
	public CrossbowsTag() {
		type = 4;
		chargedProjectiles = new ArrayList<GItem>();
	}
	
	public List<GItem> getChargedProjectiles() {
		return chargedProjectiles;
	}
	
	public byte getCharged() {
		return charged;
	}
	
	public void read(CompoundTag tag) {
		ListTag<CompoundTag> chargedProjectiles = tag.getListTag("ChargedProjectiles").asCompoundTagList();
		chargedProjectiles.forEach((projectile) -> {
			this.chargedProjectiles.add(GItem.readNewItem(projectile));
		});
		charged = tag.getByte("Charged");
	}
}
