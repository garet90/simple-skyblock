package holiday.garet.GStructure.BlockEntityTag;

import net.querz.nbt.tag.CompoundTag;

public class EndGatewayTag extends BlockEntityTag {
	private long age;
	private byte exactTeleport;
	private int exitPortalX;
	private int exitPortalY;
	private int exitPortalZ;
	
	public long getAge() {
		return age;
	}
	
	public byte getExactTeleport() {
		return exactTeleport;
	}
	
	public int getExitPortalX() {
		return exitPortalX;
	}
	
	public int getExitPortalY() {
		return exitPortalY;
	}
	
	public int getExitPortalZ() {
		return exitPortalZ;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		age = tag.getLong("Age");
		exactTeleport = tag.getByte("ExactTeleport");
		
		CompoundTag exitPortal = tag.getCompoundTag("ExitPortal");
		exitPortalX = exitPortal.getInt("X");
		exitPortalY = exitPortal.getInt("Y");
		exitPortalZ = exitPortal.getInt("Z");
	}

	public boolean getExactTeleportAsBoolean() {
		if (exactTeleport == 1) {
			return true;
		}
		return false;
	}
}
