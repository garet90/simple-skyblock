package holiday.garet.GStructure.ItemTag;

import java.util.ArrayList;
import java.util.List;

import holiday.garet.GStructure.GExplosion;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class FireworkTag extends ItemDataTag {
	private byte flight;
	private List<GExplosion> explosions;
	
	public FireworkTag() {
		type = 9;
		explosions = new ArrayList<GExplosion>();
	}
	
	public byte getFlight() {
		return flight;
	}
	
	public List<GExplosion> getExplosions() {
		return explosions;
	}
	
	public void read(CompoundTag tag) {
		CompoundTag t = tag.getCompoundTag("Fireworks");
		flight = t.getByte("Flight");
		ListTag<CompoundTag> explosions = t.getListTag("Explosions").asCompoundTagList();
		explosions.forEach((explosion) -> {
			this.explosions.add(GExplosion.readNewExplosion(explosion));
		});
	}
}
