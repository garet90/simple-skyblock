package holiday.garet.GStructure;

import java.util.ArrayList;
import java.util.List;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;

public class GEntityData {
	
	// format from https://minecraft.gamepedia.com/Chunk_format#Entity_format
	
	private String id;
	
	private double x;
	private double y;
	private double z;
	
	private double dX;
	private double dY;
	private double dZ;
	
	private float yaw;
	private float pitch;
	
	private float fallDistance;
	private short fire;
	private short air;
	private byte onGround;
	
	private int dimension;
	private byte invulnerable;
	private int portalCooldown;
	private long UUIDMost;
	private long UUIDLeast;
	
	private String customName;
	
	private byte customNameVisible;
	private byte silent;
	
	private List<GEntityData> passengers;
	
	private byte glowing;
	
	private List<String> tags;
	
	public GEntityData() {
		passengers = new ArrayList<GEntityData>();
		tags = new ArrayList<String>();
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	public void setDX(double dX) {
		this.dX = dX;
	}
	
	public void setDY(double dY) {
		this.dY = dY;
	}
	
	public void setDZ(double dZ) {
		this.dZ = dZ;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public void setFallDistance(float fallDistance) {
		this.fallDistance = fallDistance;
	}
	
	public void setFire(short fire) {
		this.fire = fire;
	}
	
	public void setAir(short air) {
		this.air = air;
	}
	
	public void setOnGround(byte onGround) {
		this.onGround = onGround;
	}
	
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}
	
	public void setInvulnerable(byte invulnerable) {
		this.invulnerable = invulnerable;
	}
	
	public void setPortalCooldown(int portalCooldown) {
		this.portalCooldown = portalCooldown;
	}
	
	public void setUUIDMost(long UUIDMost) {
		this.UUIDMost = UUIDMost;
	}
	
	public void setUUIDLeast(long UUIDLeast) {
		this.UUIDLeast = UUIDLeast;
	}
	
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	
	public void setCustomNameVisible(byte customNameVisible) {
		this.customNameVisible = customNameVisible;
	}
	
	public void setSilent(byte silent) {
		this.silent = silent;
	}
	
	public void setPassengers(List<GEntityData> passengers) {
		this.passengers = passengers;
	}
	
	public void setGlowing(byte glowing) {
		this.glowing = glowing;
	}
	
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	public String getId() {
		return id;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public double getDX() {
		return dX;
	}
	
	public double getDY() {
		return dY;
	}
	
	public double getDZ() {
		return dZ;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getFallDistance() {
		return fallDistance;
	}
	
	public short getFire() {
		return fire;
	}
	
	public short getAir() {
		return air;
	}
	
	public byte getOnGround() {
		return onGround;
	}
	
	public int getDimension() {
		return dimension;
	}
	
	public byte getInvulnerable() {
		return invulnerable;
	}
	
	public boolean getInvulnerableAsBoolean() {
		if (invulnerable == 1) {
			return true;
		}
		return false;
	}
	
	public int getPortalCooldown() {
		return portalCooldown;
	}
	
	public long getUUIDMost() {
		return UUIDMost;
	}
	
	public long getUUIDLeast() {
		return UUIDLeast;
	}
	
	public String getCustomName() {
		return customName;
	}
	
	public byte getCustomNameVisible() {
		return customNameVisible;
	}
	
	public boolean getCustomNameVisibleAsBoolean() {
		if (customNameVisible == 1) {
			return true;
		}
		return false;
	}
	
	public byte getSilent() {
		return silent;
	}
	
	public boolean getSilentAsBoolean() {
		if (silent == 1) {
			return true;
		}
		return false;
	}
	
	public List<GEntityData> getPassengers() {
		return passengers;
	}
	
	public byte getGlowing() {
		return glowing;
	}
	
	public boolean getGlowingAsBoolean() {
		if (glowing == 1) {
			return true;
		}
		return false;
	}
	
	public List<String> getTags() {
		return tags;
	}
	
	public void read(CompoundTag tag) {
		this.id = tag.getString("id");
		
		ListTag<DoubleTag> pos = tag.getListTag("Pos").asDoubleTagList();
		this.x = pos.get(0).asDouble();
		this.y = pos.get(1).asDouble();
		this.z = pos.get(2).asDouble();

		ListTag<DoubleTag> motion = tag.getListTag("Motion").asDoubleTagList();
		this.dX = motion.get(0).asDouble();
		this.dY = motion.get(1).asDouble();
		this.dZ = motion.get(2).asDouble();
		
		ListTag<FloatTag> rotation = tag.getListTag("Rotation").asFloatTagList();
		this.yaw = rotation.get(0).asFloat();
		this.pitch = rotation.get(1).asFloat();
		
		this.fallDistance = tag.getFloat("FallDistance");
		
		this.fire = tag.getShort("Fire");
		
		this.air = tag.getShort("Air");
		
		this.onGround = tag.getByte("OnGround");
		
		this.dimension = tag.getInt("Dimension");
		
		this.invulnerable = tag.getByte("Invulnerable");
		
		this.portalCooldown = tag.getInt("PortalCooldown");
		
		this.UUIDMost = tag.getLong("UUIDMost");
		this.UUIDLeast = tag.getLong("UUIDLeast");
		
		this.customName = tag.getString("CustomName");
		
		this.customNameVisible = tag.getByte("CustomNameVisible");
		
		this.silent = tag.getByte("Silent");
		
		if (tag.containsKey("Passengers")) {
			ListTag<CompoundTag> Passengers = tag.getListTag("Passengers").asCompoundTagList();
			Passengers.forEach((Passenger) -> {
				this.passengers.add(GEntityData.readNewEntity(Passenger));
			});
		}
		
		this.glowing = tag.getByte("Glowing");

		if (tag.containsKey("Tags")) {
			ListTag<StringTag> Tags = tag.getListTag("Tags").asStringTagList();
			Tags.forEach((TagE) -> {
				tags.add(TagE.getValue());
			});
		}
	}
	
	public static GEntityData readNewEntity(CompoundTag tag) {
		GEntityData e = new GEntityData();
		e.read(tag);
		return e;
	}
}
