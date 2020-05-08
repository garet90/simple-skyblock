package holiday.garet.GStructure.BlockEntityTag;

import net.querz.nbt.tag.CompoundTag;

public class ComparatorTag extends BlockEntityTag {
	private int outputSignal;
	
	public int getOutputSignal() {
		return outputSignal;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		outputSignal = tag.getInt("OutputSignal");
	}
}
