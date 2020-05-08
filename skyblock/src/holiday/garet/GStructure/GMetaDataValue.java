package holiday.garet.GStructure;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class GMetaDataValue implements MetadataValue {

	String value;
	Plugin plugin;
	
	public GMetaDataValue(String value, Plugin plugin) {
		this.value = value;
		this.plugin = plugin;
	}
	
	@Override
	public boolean asBoolean() {
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1")) return true;
		return false;
	}

	@Override
	public byte asByte() {
		return Byte.valueOf(value);
	}

	@Override
	public double asDouble() {
		return Double.valueOf(value);
	}

	@Override
	public float asFloat() {
		return Float.valueOf(value);
	}

	@Override
	public int asInt() {
		return Integer.valueOf(value);
	}

	@Override
	public long asLong() {
		return Long.valueOf(value);
	}

	@Override
	public short asShort() {
		return Short.valueOf(value);
	}

	@Override
	public String asString() {
		return value;
	}

	@Override
	public Plugin getOwningPlugin() {
		return plugin;
	}

	@Override
	public void invalidate() {
		return;
	}

	@Override
	public Object value() {
		return (Object)value;
	}

}
