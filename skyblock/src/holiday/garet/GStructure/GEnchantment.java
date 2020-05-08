package holiday.garet.GStructure;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import net.querz.nbt.tag.CompoundTag;

public class GEnchantment {
	private String id;
	private int lvl;
	
	public String getId() {
		return id;
	}
	
	public int getLvl() {
		return lvl;
	}
	
	public void read(CompoundTag tag) {
		this.id = tag.getString("id");
		this.lvl = tag.getShort("lvl");
	}
	
	public static GEnchantment readNewEnchantment(CompoundTag tag) {
		GEnchantment e = new GEnchantment();
		e.read(tag);
		return e;
	}

	public Enchantment get() {
		return Enchantment.getByKey(NamespacedKey.minecraft(id.replaceFirst("minecraft:", "")));
	}
}
