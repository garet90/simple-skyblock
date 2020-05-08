package holiday.garet.GStructure;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import net.querz.nbt.tag.CompoundTag;

public class GPattern {
	private int color;
	private String pattern;
	
	public void read(CompoundTag tag) {
		color = tag.getInt("Color");
		pattern = tag.getString("Pattern");
	}
	
	public int getColor() {
		return color;
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public static GPattern readNewPattern(CompoundTag tag) {
		GPattern p = new GPattern();
		p.read(tag);
		return p;
	}

	public Pattern get() {
		return new Pattern(DyeColor.values()[color], PatternType.getByIdentifier(pattern));
	}
}
