package holiday.garet.GStructure;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import net.querz.nbt.tag.CompoundTag;

public class GEffect {
	private byte effectId;
	private int effectDuration;
	
	public void read(CompoundTag tag) {
		effectId = tag.getByte("EffectId");
		effectDuration = tag.getInt("EffectDuration");
	}
	
	public byte getEffectId() {
		return effectId;
	}
	
	public int getEffectDuration() {
		return effectDuration;
	}
	
	public static GEffect readNewEffect(CompoundTag tag) {
		GEffect e = new GEffect();
		e.read(tag);
		return e;
	}

	@SuppressWarnings("deprecation")
	public @NotNull PotionEffect get() {
		return new PotionEffect(PotionEffectType.getById(effectId), effectDuration, 1);
	}
}
