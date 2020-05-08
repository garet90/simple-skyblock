package holiday.garet.GStructure.ItemTag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import holiday.garet.GStructure.GCustomPotionEffect;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class PotionEffectsTag extends ItemDataTag {
	private List<GCustomPotionEffect> customPotionEffects;
	private String potion;
	private int customPotionColor;
	
	public PotionEffectsTag() {
		type = 13;
		customPotionEffects = new ArrayList<GCustomPotionEffect>();
	}
	
	public List<GCustomPotionEffect> getCustomPotionEffects() {
		return customPotionEffects;
	}
	
	public String getPotion() {
		return potion;
	}
	
	public int getCustomPotionColor() {
		return customPotionColor;
	}
	
	public void read(CompoundTag tag) {
		if (tag.containsKey("CustomPotionEffects")) {
			ListTag<CompoundTag> customPotionEffects = tag.getListTag("CustomPotionEffects").asCompoundTagList();
			customPotionEffects.forEach((customPotionEffect) -> {
				this.customPotionEffects.add(GCustomPotionEffect.readNewEffect(customPotionEffect));
			});
		}
		if (tag.containsKey("Potion")) {
			this.potion = tag.getString("Potion");
		}
	}

	public PotionData getPotionData() {
		boolean strong = false;
		boolean extended = false;
		PotionType type = PotionType.UNCRAFTABLE;
		switch(potion.replaceFirst("minecraft:", "")) {
		case "empty": type = null; break;
		case "water": type = PotionType.WATER; break;
		case "mundane": type = PotionType.MUNDANE; break;
		case "thick": type = PotionType.THICK; break;
		case "awkward": type = PotionType.AWKWARD; break;
		case "night_vision": type = PotionType.NIGHT_VISION; break;
		case "long_night_vision": type = PotionType.NIGHT_VISION; extended = true; break;
		case "invisibility": type = PotionType.INVISIBILITY; break;
		case "long_invisibility": type = PotionType.INVISIBILITY; extended = true; break;
		case "leaping": type = PotionType.JUMP; break;
		case "strong_leaping": type = PotionType.JUMP; strong = true; break;
		case "long_leaping": type = PotionType.JUMP; extended = true; break;
		case "fire_resistance": type = PotionType.FIRE_RESISTANCE; break;
		case "long_fire_resistance": type = PotionType.FIRE_RESISTANCE; extended = true; break;
		case "swiftness": type = PotionType.SPEED; break;
		case "strong_swiftness": type = PotionType.SPEED; strong = true; break;
		case "long_swiftness": type = PotionType.SPEED; extended = true; break;
		case "slowness": type = PotionType.SLOWNESS; break;
		case "strong_slowness": type = PotionType.SLOWNESS; strong=true; break;
		case "long_slowness": type = PotionType.SLOWNESS; extended=true; break;
		case "water_breathing": type = PotionType.WATER_BREATHING; break;
		case "long_water_breathing": type = PotionType.WATER_BREATHING; extended=true; break;
		case "healing": type = PotionType.INSTANT_HEAL; break;
		case "strong_healing": type = PotionType.INSTANT_HEAL; strong=true; break;
		case "harming": type = PotionType.INSTANT_DAMAGE; break;
		case "strong_harming": type = PotionType.INSTANT_DAMAGE; strong=true; break;
		case "poison": type = PotionType.POISON; break;
		case "strong_poison": type = PotionType.POISON; strong=true; break;
		case "long_poison": type = PotionType.POISON; extended=true; break;
		case "regeneration": type = PotionType.REGEN; break;
		case "strong_regeneration": type = PotionType.REGEN; strong=true; break;
		case "long_regeneration": type = PotionType.REGEN; extended=true; break;
		case "strength": type = PotionType.STRENGTH; break;
		case "strong_strength": type = PotionType.STRENGTH; strong=true; break;
		case "long_strength": type = PotionType.STRENGTH; extended=true; break;
		case "weakness": type = PotionType.WEAKNESS; break;
		case "long_weakness": type = PotionType.WEAKNESS; extended=true; break;
		case "luck": type = PotionType.LUCK; break;
		case "turtle_master": type = PotionType.TURTLE_MASTER; break;
		case "strong_turtle_master": type = PotionType.TURTLE_MASTER; strong=true; break;
		case "long_turtle_master": type = PotionType.TURTLE_MASTER; extended=true; break;
		case "slow_falling": type = PotionType.SLOW_FALLING; break;
		case "long_slow_falling": type = PotionType.SLOW_FALLING; extended=true; break;
		}
		return new PotionData(type, extended, strong);
	}
}
