package holiday.garet.skyblock;

import java.util.Random;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;

public class EmptyWorldGenerator extends ChunkGenerator {
	FileConfiguration config = Bukkit.getServer().getPluginManager().getPlugin("SimpleSkyblock").getConfig();

    @Override
    @Nonnull
    public ChunkData generateChunkData(@Nonnull World world, @Nonnull Random random, int chunkX, int chunkZ, @Nonnull BiomeGrid biome) {
    	Biome b;
    	if (config.isSet("BIOME")) {
    		b = Biome.valueOf(config.getString("BIOME"));
    	} else {
    		b = Biome.PLAINS;
    		config.set("BIOME","PLAINS");
    	}
        ChunkData chunk = createChunkData(world);
        for (int x = 0; x < 16; x++) {
        	for (int z = 0; z < 16; z++) {
                biome.setBiome(x, z, b);
        	}
        }
    	return chunk;
    }
}

// chunk generator from robertlit (https://www.spigotmc.org/threads/creating-empty-void-worlds-in-1-13.400698/)