package holiday.garet.GStructure.BlockEntityTag;

import net.querz.nbt.tag.CompoundTag;

public class CommandBlockTag extends BlockEntityTag {
	private String customName;
	private String command;
	private int successCount;
	private String lastOutput;
	private byte trackOutput;
	private byte powered;
	private byte auto;
	private byte conditionMet;
	private byte updateLastExecution;
	private long lastExecution;
	
	public String getCustomName() {
		return customName;
	}
	
	public String getCommand() {
		return command;
	}
	
	public int getSuccessCount() {
		return successCount;
	}
	
	public String getLastOutput() {
		return lastOutput;
	}
	
	public byte getTrackOutput() {
		return trackOutput;
	}
	
	public byte getPowered() {
		return powered;
	}
	
	public byte getAuto() {
		return auto;
	}
	
	public byte getConditionMet() {
		return conditionMet;
	}
	
	public byte getUpdateLastExecution() {
		return updateLastExecution;
	}
	
	public long getLastExecution() {
		return lastExecution;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		customName = tag.getString("CustomName");
		command = tag.getString("Command");
		successCount = tag.getInt("SuccessCount");
		lastOutput = tag.getString("LastOutput");
		trackOutput = tag.getByte("TrackOutput");
		powered = tag.getByte("powered");
		auto = tag.getByte("auto");
		conditionMet = tag.getByte("conditionMet");
		updateLastExecution = tag.getByte("UpdateLastExecution");
		lastExecution = tag.getLong("LastExecution");
	}
}
