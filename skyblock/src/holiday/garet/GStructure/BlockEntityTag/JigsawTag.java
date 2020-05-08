package holiday.garet.GStructure.BlockEntityTag;

import net.querz.nbt.tag.CompoundTag;

public class JigsawTag extends BlockEntityTag {
	private String target_pool;
	private String final_state;
	private String attachement_type;
	
	public String getTargetPool() {
		return target_pool;
	}
	
	public String getFinalState() {
		return final_state;
	}
	
	public String getAttachementType() {
		return attachement_type;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		target_pool = tag.getString("target_pool");
		final_state = tag.getString("final_state");
		attachement_type = tag.getString("attachement_type");
	}
}
