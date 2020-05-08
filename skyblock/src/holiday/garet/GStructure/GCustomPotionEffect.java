package holiday.garet.GStructure;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import net.querz.nbt.tag.CompoundTag;

public class GCustomPotionEffect {
	private byte id;
	private byte amplifier;
	private int duration;
	private byte ambient;
	private byte showParticles;
	private byte showIcon;
	
	public void read(CompoundTag tag) {
		id = tag.getByte("Id");
		amplifier = tag.getByte("Amplifier");
		duration = tag.getInt("Duration");
		ambient = tag.getByte("Ambient");
		showParticles = tag.getByte("ShowParticles");
		showIcon = tag.getByte("ShowIcon");
	}
	
	public byte getId() {
		return id;
	}
	
	public byte getAmplifier() {
		return amplifier;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public byte getAmbient() {
		return ambient;
	}
	
	public byte getShowParticles() {
		return showParticles;
	}
	
	public byte getShowIcon() {
		return showIcon;
	}
	
	public static GCustomPotionEffect readNewEffect(CompoundTag tag) {
		GCustomPotionEffect e = new GCustomPotionEffect();
		e.read(tag);
		return e;
	}

	@SuppressWarnings("deprecation")
	public @NotNull PotionEffect get() {
		return new PotionEffect(PotionEffectType.getById(id), duration, (int)amplifier, getAmbientAsBoolean(), getShowParticlesAsBoolean(), getShowIconAsBoolean());
	}

	private boolean getShowIconAsBoolean() {
		if (showIcon == 1) {
			return true;
		}
		return false;
	}

	private boolean getShowParticlesAsBoolean() {
		if (showParticles == 1) {
			return true;
		}
		return false;
	}

	private boolean getAmbientAsBoolean() {
		if (ambient == 1) {
			return true;
		}
		return false;
	}
}
