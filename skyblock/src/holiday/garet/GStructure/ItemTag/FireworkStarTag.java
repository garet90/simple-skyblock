package holiday.garet.GStructure.ItemTag;

import holiday.garet.GStructure.GExplosion;
import net.querz.nbt.tag.CompoundTag;

public class FireworkStarTag extends ItemDataTag {
	private GExplosion explosion;
	
	public FireworkStarTag() {
		type = 16;
	}
	
	public GExplosion getExplosion() {
		return explosion;
	}
	
	public void read(CompoundTag tag) {
		explosion = GExplosion.readNewExplosion(tag.getCompoundTag("Explosion"));
	}
}
