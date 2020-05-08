package holiday.garet.GStructure;

import java.util.HashMap;

public class GPalette {
	
	// format from https://minecraft.gamepedia.com/Structure_block_file_format
	
	String name;
	
	HashMap<String, String> properties;
	
	public GPalette() {
		properties = new HashMap<String, String>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}
	
	public HashMap<String, String> getProperties() {
		return properties;
	}
}
