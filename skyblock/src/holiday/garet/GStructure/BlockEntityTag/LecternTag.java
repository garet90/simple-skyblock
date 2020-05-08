package holiday.garet.GStructure.BlockEntityTag;

import holiday.garet.GStructure.GItem;
import net.querz.nbt.tag.CompoundTag;

public class LecternTag extends BlockEntityTag {
	private GItem book;
	private int page;
	
	public GItem getBook() {
		return book;
	}
	
	public int getPage() {
		return page;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		book = GItem.readNewItem(tag.getCompoundTag("Book"));
		page = tag.getInt("Page");
	}
}
