package holiday.garet.GStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;

import holiday.garet.GStructure.BlockEntityTag.ChestTag;
import holiday.garet.GStructure.BlockEntityTag.BlockEntityTag;
import holiday.garet.GStructure.ItemTag.AttributeModifiersTag;
import holiday.garet.GStructure.ItemTag.BlockTag;
import holiday.garet.GStructure.ItemTag.BookAndQuillTag;
import holiday.garet.GStructure.ItemTag.BucketOfFishTag;
import holiday.garet.GStructure.ItemTag.CrossbowsTag;
import holiday.garet.GStructure.ItemTag.DebugStickTag;
import holiday.garet.GStructure.ItemTag.DisplayPropertiesTag;
import holiday.garet.GStructure.ItemTag.EnchantmentTag;
import holiday.garet.GStructure.ItemTag.EntityTag;
import holiday.garet.GStructure.ItemTag.FireworkStarTag;
import holiday.garet.GStructure.ItemTag.FireworkTag;
import holiday.garet.GStructure.ItemTag.GeneralTag;
import holiday.garet.GStructure.ItemTag.ItemDataTag;
import holiday.garet.GStructure.ItemTag.MapTag;
import holiday.garet.GStructure.ItemTag.PlayerHeadTag;
import holiday.garet.GStructure.ItemTag.PotionEffectsTag;
import holiday.garet.GStructure.ItemTag.SuspiciousStewTag;
import holiday.garet.GStructure.ItemTag.WrittenBookTag;
import net.querz.nbt.tag.CompoundTag;

public class GItem {
	private byte count;
	private byte slot;
	private String id;
	private List<ItemDataTag> tags;
	
	public GItem() {
		tags = new ArrayList<ItemDataTag>();
	}
	
	public void read(CompoundTag tag) {
		this.count = tag.getByte("Count");
		this.slot = tag.getByte("Slot");
		this.id = tag.getString("id");
		CompoundTag dataTag = tag.getCompoundTag("tag");
		if (dataTag != null) {
			if (dataTag.containsKey("Damage")) { // general tag
				GeneralTag t = new GeneralTag();
				t.read(dataTag);
				tags.add(t);
			}
			if (dataTag.containsKey("CanPlaceOn") || dataTag.containsKey("BlockEntityTag") || dataTag.containsKey("BlockStateTag")) {
				BlockTag t = new BlockTag();
				t.read(dataTag, id);
				tags.add(t);
			}
			if (dataTag.containsKey("Enchantments") || dataTag.containsKey("StoredEnchantments")) {
				EnchantmentTag t = new EnchantmentTag();
				t.read(dataTag);
				tags.add(t);
			}
			if (dataTag.containsKey("AttributeModifiers")) {
				AttributeModifiersTag t = new AttributeModifiersTag();
				t.read(dataTag);
				tags.add(t);
			}
			if (dataTag.containsKey("CustomPotionEffects") || dataTag.containsKey("Potion")) {
				PotionEffectsTag t = new PotionEffectsTag();
				t.read(dataTag);
				tags.add(t);
			}
			if (dataTag.containsKey("Charged")) {
				CrossbowsTag t = new CrossbowsTag();
				t.read(dataTag);
				tags.add(t);
			}
			if (dataTag.containsKey("display")) {
				DisplayPropertiesTag t = new DisplayPropertiesTag();
				t.read(dataTag);
				tags.add(t);
			}
			if (dataTag.containsKey("pages")) {
				if (dataTag.containsKey("title")) {
					WrittenBookTag t = new WrittenBookTag();
					t.read(dataTag);
					tags.add(t);
				} else {
					BookAndQuillTag t = new BookAndQuillTag();
					t.read(dataTag);
					tags.add(t);
				}
			}
			if (dataTag.containsKey("SkullOwner")) {
				PlayerHeadTag t = new PlayerHeadTag();
				t.read(dataTag);
				tags.add(t);
			}
			if (dataTag.containsKey("Fireworks")) {
				FireworkTag t = new FireworkTag();
				t.read(dataTag);
				tags.add(t);
			}
			if (dataTag.containsKey("Explosion")) {
				FireworkStarTag t = new FireworkStarTag();
				t.read(dataTag);
				tags.add(t);
			}
			if (dataTag.containsKey("EntityTag")) {
				if (dataTag.containsKey("BucketVariantTag")) {
					BucketOfFishTag t = new BucketOfFishTag();
					t.read(dataTag);
					tags.add(t);
				} else {
					EntityTag t = new EntityTag();
					t.read(dataTag);
					tags.add(t);
				}
			}
			if (dataTag.containsKey("map")) {
				MapTag t = new MapTag();
				t.read(dataTag);
				tags.add(t);
			}
			if (dataTag.containsKey("Effects")) {
				SuspiciousStewTag t = new SuspiciousStewTag();
				t.read(dataTag);
				tags.add(t);
			}
			if (dataTag.containsKey("DebugProperty")) {
				DebugStickTag t = new DebugStickTag();
				t.read(dataTag);
				tags.add(t);
			}
		}
	}
	
	public byte getCount() {
		return count;
	}
	
	public byte getSlot() {
		return slot;
	}
	
	public String getId() {
		return id;
	}
	
	public List<ItemDataTag> getTags() {
		return tags;
	}
	
	public static GItem readNewItem(CompoundTag tag) {
		GItem i = new GItem();
		i.read(tag);
		return i;
	}

	@SuppressWarnings("deprecation")
	public ItemStack get(Plugin plugin) {
		ItemStack i = new ItemStack(Material.matchMaterial(id), count);
		this.tags.forEach((tag) -> {
			org.bukkit.inventory.meta.ItemMeta im = i.getItemMeta();
			boolean setItemMeta = true;
			switch(tag.getType()) {
			case 0:
				if (true) {
					AttributeModifiersTag t = (AttributeModifiersTag) tag;
					t.getAttributeModifiers().forEach((modifier) -> {
						AttributeModifier.Operation op = null;
						switch(modifier.getOperation()) {
						case 0: op = AttributeModifier.Operation.ADD_NUMBER;
						case 1: op = AttributeModifier.Operation.ADD_SCALAR;
						case 2: op = AttributeModifier.Operation.MULTIPLY_SCALAR_1;
						}
						AttributeModifier am = new AttributeModifier(new UUID(modifier.getUUIDMost(), modifier.getUUIDLeast()), modifier.getName(), modifier.getAmount(), op, EquipmentSlot.valueOf(modifier.getSlot()));
						im.addAttributeModifier(Attribute.valueOf(modifier.getAttributeName()), am);
					});
				}
				break;
			case 1:
				if (true) {
					BlockTag t = (BlockTag) tag;
					BlockStateMeta bsm = (BlockStateMeta) i.getItemMeta();
					BlockState bs = bsm.getBlockState();
					t.getBlockStateTag().forEach((name, value) -> {
						bs.setMetadata(name, new GMetaDataValue(value, plugin));
					});
					// TODO blockEntityTag
					// TODO Make shulker boxes work.
					BlockEntityTag bet = t.getBlockEntityTag();
					switch (bet.getId()) {
					case "minecraft:shulker_box":
					case "shulker_box":
						ChestTag ct = (ChestTag) bet;
						Container box = (Container) bs;
						ct.getItems().forEach((item) -> {
							box.getInventory().setItem(item.getSlot(), item.get(plugin));
						});
						break;
					}
					bsm.setBlockState(bs);
					i.setItemMeta((ItemMeta) bsm);
					setItemMeta = false;
				}
				break;
			case 2:
				if (true) {
					BookAndQuillTag t = (BookAndQuillTag) tag;
					BookMeta bm = (BookMeta) im;
					for (int ii = 0; ii < t.getPages().size(); ii++) {
						bm.addPage(t.getPages().get(ii));
					}
				}
				break;
			case 4:
				if (true) {
					CrossbowsTag t = (CrossbowsTag) tag;
					CrossbowMeta cm = (CrossbowMeta) im;
					t.getChargedProjectiles().forEach((projectile) -> {
						cm.addChargedProjectile(projectile.get(plugin));
					});
				}
				break;
			case 6:
				if (true) {
					DisplayPropertiesTag t = (DisplayPropertiesTag) tag;
					if (im instanceof LeatherArmorMeta) {
						LeatherArmorMeta lam = (LeatherArmorMeta) im;
						lam.setColor(Color.fromRGB(t.getColor()));
					}
					if (t.getName() != null && t.getName() != "") {
						im.setDisplayName(new JSONObject(t.getName()).getString("text"));
					}
					im.setLore(t.getLore());
					// TODO hideflags
				}
				break;
			case 7:
				if (true) {
					EnchantmentTag t = (EnchantmentTag) tag;
					if (im instanceof EnchantmentStorageMeta) {
						EnchantmentStorageMeta esm = (EnchantmentStorageMeta) im;
						t.getStoredEnchantments().forEach((storedEnchantment) -> {
							esm.addStoredEnchant(storedEnchantment.get(), storedEnchantment.getLvl(), true);
						});
					} else {
						t.getEnchantments().forEach((enchantment) -> {
							im.addEnchant(enchantment.get(), enchantment.getLvl(), true);
						});
					}
					// TODO repair cost
				}
				break;
			// TODO spawn eggs
			case 9:
				if (true) {
					FireworkTag t = (FireworkTag) tag;
					FireworkMeta fm = (FireworkMeta) im;
					t.getExplosions().forEach((explosion) -> {
						List<Color> c = new ArrayList<Color>();
						List<Color> fc = new ArrayList<Color>();
						explosion.getColors().forEach((color) -> {
							c.add(Color.fromRGB(color));
						});
						explosion.getFadeColors().forEach((fadeColor) -> {
							fc.add(Color.fromRGB(fadeColor));
						});
						FireworkEffect e = FireworkEffect.builder().flicker(explosion.getFlickerAsBoolean()).withColor(c).withFade(fc).with(explosion.getTypeAsType()).trail(explosion.getTrailAsBoolean()).build();
						fm.addEffect(e);
					});
				}
				break;
			case 10:
				if (true) {
					GeneralTag t = (GeneralTag) tag;
					if (im instanceof Damageable) {
						((Damageable)im).setDamage(t.getDamage());
					}
					im.setUnbreakable(t.getUnbreakableAsBoolean());
					im.setCustomModelData(t.getCustomModelData());
					// TODO can destroy
				}
				break;
			case 11:
				if (true) {
					MapTag t = (MapTag) tag;
					MapMeta mm = (MapMeta) im;
					mm.setMapId(t.getMap());
					mm.setColor(Color.fromRGB(t.getMapColor()));
					// TODO map scale
				}
				break;
			case 12:
				if (true) {
					PlayerHeadTag t = (PlayerHeadTag) tag;
					SkullMeta sm = (SkullMeta) im;
					sm.setOwner(t.getSkullOwner());
				}
				break;
			case 13:
				if (true) {
					PotionEffectsTag t = (PotionEffectsTag) tag;
					PotionMeta pm = (PotionMeta) im;
					if (t.getCustomPotionColor() != 0) {
						pm.setColor(Color.fromRGB(t.getCustomPotionColor()));
					}
					if (t.getPotion() != null) {
						pm.setBasePotionData(t.getPotionData());
					}
					t.getCustomPotionEffects().forEach((potionEffect) -> {
						pm.addCustomEffect(potionEffect.get(), true);
					});
				}
				break;
			case 14:
				if (true) {
					SuspiciousStewTag t = (SuspiciousStewTag) tag;
					SuspiciousStewMeta sm = (SuspiciousStewMeta) im;
					t.getEffects().forEach((effect) -> {
						sm.addCustomEffect(effect.get(), true);
					});
				}
				break;
			case 15:
				if (true) {
					WrittenBookTag t = (WrittenBookTag) tag;
					BookMeta bm = (BookMeta) im;
					for (int ii = 0; ii < t.getPages().size(); ii++) {
						bm.addPage(new JSONObject(t.getPages().get(ii)).getString("text"));
					}
					bm.setAuthor(t.getAuthor());
					bm.setTitle(t.getTitle());
				}
				break;
			case 16:
				if (true) {
					FireworkStarTag t = (FireworkStarTag) tag;
					FireworkEffectMeta fm = (FireworkEffectMeta) im;
					List<Color> c = new ArrayList<Color>();
					List<Color> fc = new ArrayList<Color>();
					t.getExplosion().getColors().forEach((color) -> {
						c.add(Color.fromRGB(color));
					});
					t.getExplosion().getFadeColors().forEach((fadeColor) -> {
						fc.add(Color.fromRGB(fadeColor));
					});
					FireworkEffect e = FireworkEffect.builder().flicker(t.getExplosion().getFlickerAsBoolean()).withColor(c).withFade(fc).with(t.getExplosion().getTypeAsType()).trail(t.getExplosion().getTrailAsBoolean()).build();
					fm.setEffect(e);
				}
				break;
			}
			if (setItemMeta) {
				i.setItemMeta(im);
			}
		});
		return i;
	}
}
