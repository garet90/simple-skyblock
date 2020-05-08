package holiday.garet.GStructure;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.FireworkEffect.Type;
import org.jetbrains.annotations.NotNull;

import net.querz.nbt.tag.CompoundTag;

public class GExplosion {
	private byte flicker;
	private byte trail;
	private byte type;
	private List<Integer> colors;
	private List<Integer> fadeColors;
	
	public GExplosion() {
		colors = new ArrayList<Integer>();
		fadeColors = new ArrayList<Integer>();
	}
	
	public byte getFlicker() {
		return flicker;
	}
	
	public byte getTrail() {
		return trail;
	}
	
	public byte getType() {
		return type;
	}
	
	public List<Integer> getColors() {
		return colors;
	}
	
	public List<Integer> getFadeColors() {
		return fadeColors;
	}
	
	public void read(CompoundTag tag) {
		flicker = tag.getByte("Flicker");
		trail = tag.getByte("Trail");
		type = tag.getByte("Type");
		int[] colors = tag.getIntArray("Colors");
		for (int i = 0; i < colors.length; i++) {
			this.colors.add(colors[i]);
		}
		int[] fadeColors = tag.getIntArray("FadeColors");
		for (int i = 0; i < fadeColors.length; i++) {
			this.fadeColors.add(fadeColors[i]);
		}
	}
	
	public static GExplosion readNewExplosion(CompoundTag tag) {
		GExplosion e = new GExplosion();
		e.read(tag);
		return e;
	}

	public @NotNull boolean getFlickerAsBoolean() {
		if (flicker == 1) {
			return true;
		}
		return false;
	}

	public @NotNull Type getTypeAsType() {
		switch (type) {
		case 0: return Type.BALL;
		case 1: return Type.BALL_LARGE;
		case 2: return Type.STAR;
		case 3: return Type.CREEPER;
		case 4: return Type.BURST;
		default: return Type.BALL;
		}
	}

	public boolean getTrailAsBoolean() {
		if (trail == 1) {
			return true;
		}
		return false;
	}
}
