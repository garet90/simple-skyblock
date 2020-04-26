package holiday.garet.skyblock.world.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

public class SchematicUtils {
	static File schematicsDir;
	
	static List<Schematic> schematics = new ArrayList<Schematic>();
	
	public SchematicUtils(File schematicsDir) {
		SchematicUtils.schematicsDir = schematicsDir;
		SchematicUtils.initSchematics();
	}
	
	public static void initSchematics(){
		schematics.clear();
		
		for(File schematicFile : schematicsDir.listFiles()){
			if(!(schematicFile.getName().startsWith("."))){
				Schematic schematic = loadSchematic(schematicFile);
				
				if(schematic != null){
					schematics.add(schematic);
				}
			}
		}
	}
	
	public Schematic getSchematic(String name) {
		for (int i = 0; i < schematics.size(); i++) {
			if (schematics.get(i).getName().equalsIgnoreCase(name)) {
				return schematics.get(i);
			}
		}
		return null;
	}
	
	public static Schematic loadSchematic(File file) {
		try {
			if (file.exists()) {
				@SuppressWarnings("deprecation")
				NBTInputStream nbt = new NBTInputStream(new FileInputStream(file));
				CompoundTag compound = (CompoundTag) nbt.readTag();
				Map<String, Tag> tags = compound.getValue();
				Short width = ((ShortTag) tags.get("Width")).getValue();
				Short height = ((ShortTag) tags.get("Height")).getValue();
				Short length = ((ShortTag) tags.get("Length")).getValue();
				
				String materials = ((StringTag) tags.get("Materials")).getValue();

				byte[] blocks = ((ByteArrayTag) tags.get("Blocks")).getValue();
				byte[] data = ((ByteArrayTag) tags.get("Data")).getValue();
				
				nbt.close();
				
				Schematic schematic = new Schematic(file.getName().replace(".schematic", ""), width, height, length, materials, blocks, data);
			
				return schematic;
			}
		} catch (Exception e) {
		}
		return null;
	}
}
