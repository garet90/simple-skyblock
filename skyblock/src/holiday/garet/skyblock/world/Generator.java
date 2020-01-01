/*
 * The MIT License (MIT)
 *
 * Copyright (c) Garet Halliday
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package holiday.garet.skyblock.world;

import java.util.Random;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;

public class Generator extends ChunkGenerator {
	FileConfiguration config = Bukkit.getServer().getPluginManager().getPlugin("SimpleSkyblock").getConfig();

    @SuppressWarnings("deprecation")
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
        		if (world.getEnvironment() == Environment.NETHER) {
        			Biome nether = Biome.valueOf("HELL");
        			if (nether != null) {
        				biome.setBiome(x, z, nether);
        			} else {
        				biome.setBiome(x, z, Biome.valueOf("NETHER"));
        			}
        		} else {
        			biome.setBiome(x, z, b);
        		}
        	}
        }
    	return chunk;
    }
}

// chunk generator from robertlit (https://www.spigotmc.org/threads/creating-empty-void-worlds-in-1-13.400698/)