package holiday.garet.GStructure.BlockEntityTag;

import net.querz.nbt.tag.CompoundTag;

public class ConduitTag extends BlockEntityTag {
	private long l;
	private long m;
	
	public long getL() {
		return l;
	}
	
	public long getM() {
		return m;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		CompoundTag targetUUID = tag.getCompoundTag("target_uuid");
		l = targetUUID.getLong("L");
		m = targetUUID.getLong("M");
	}
}
