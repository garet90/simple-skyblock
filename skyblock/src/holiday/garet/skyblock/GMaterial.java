/*
 *  Yes, I really went through all this work so I could convert 1.12.2 item ids and data values to 1.15.2 materials. (To use structure file format)
 *  
 *
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

package holiday.garet.skyblock;

public class GMaterial {
	public static XMaterial getMaterial(int id, byte data) {
		switch(id) {
		case -103: return XMaterial.NETHER_QUARTZ_ORE;
		case -43: return XMaterial.MAGMA_BLOCK;
		case 0: return XMaterial.AIR;
		case 1: 
			switch(data) {
			case 0: return XMaterial.STONE;
			case 1: return XMaterial.GRANITE;
			case 2: return XMaterial.POLISHED_GRANITE;
			case 3: return XMaterial.DIORITE;
			case 4: return XMaterial.POLISHED_DIORITE;
			case 5: return XMaterial.ANDESITE;
			case 6: return XMaterial.POLISHED_ANDESITE;
			}
		case 2: return XMaterial.GRASS_BLOCK;
		case 3:
			switch(data) {
			case 0: return XMaterial.DIRT;
			case 1: return XMaterial.COARSE_DIRT;
			case 2: return XMaterial.PODZOL;
			}
		case 4: return XMaterial.COBBLESTONE;
		case 5:
			switch(data) {
			case 0: return XMaterial.OAK_PLANKS;
			case 1: return XMaterial.SPRUCE_PLANKS;
			case 2: return XMaterial.BIRCH_PLANKS;
			case 3: return XMaterial.JUNGLE_PLANKS;
			case 4: return XMaterial.ACACIA_PLANKS;
			case 5: return XMaterial.DARK_OAK_PLANKS;
			}
		case 6:
			switch(data) {
			case 0: return XMaterial.OAK_SAPLING;
			case 1: return XMaterial.SPRUCE_SAPLING;
			case 2: return XMaterial.BIRCH_SAPLING;
			case 3: return XMaterial.JUNGLE_SAPLING;
			case 4: return XMaterial.ACACIA_SAPLING;
			case 5: return XMaterial.DARK_OAK_SAPLING;
			}
		case 7: return XMaterial.BEDROCK;
		case 8: return XMaterial.AIR;
		case 9: return XMaterial.WATER;
		case 10: return XMaterial.AIR;
		case 11: return XMaterial.LAVA;
		case 12: 
			switch(data) {
			case 0: return XMaterial.SAND;
			case 1: return XMaterial.RED_SAND;
			}
		case 13: return XMaterial.GRAVEL;
		case 14: return XMaterial.GOLD_ORE;
		case 15: return XMaterial.IRON_ORE;
		case 16: return XMaterial.COAL_ORE;
		case 17: 
			switch(data) {
			case 0: return XMaterial.OAK_WOOD;
			case 1: return XMaterial.SPRUCE_WOOD;
			case 2: return XMaterial.BIRCH_WOOD;
			case 3: return XMaterial.JUNGLE_WOOD;
			}
		case 18:
			switch(data) {
			case 0: return XMaterial.OAK_LEAVES;
			case 1: return XMaterial.SPRUCE_LEAVES;
			case 2: return XMaterial.BIRCH_LEAVES;
			case 3: return XMaterial.JUNGLE_LEAVES;
			}
		case 19:
			switch(data) {
			case 0: return XMaterial.SPONGE;
			case 1: return XMaterial.WET_SPONGE;
			}
		case 20: return XMaterial.GLASS;
		case 21: return XMaterial.LAPIS_ORE;
		case 22: return XMaterial.LAPIS_LAZULI;
		case 23: return XMaterial.DISPENSER;
		case 24:
			switch(data) {
			case 0: return XMaterial.SANDSTONE;
			case 1: return XMaterial.CHISELED_SANDSTONE;
			case 2: return XMaterial.SMOOTH_SANDSTONE;
			}
		case 25: return XMaterial.NOTE_BLOCK;
		case 26: return XMaterial.RED_BED;
		case 27: return XMaterial.POWERED_RAIL;
		case 28: return XMaterial.DETECTOR_RAIL;
		case 29: return XMaterial.STICKY_PISTON;
		case 30: return XMaterial.COBWEB;
		case 31:
			switch(data) {
			case 0: return XMaterial.DEAD_BUSH;
			case 1: return XMaterial.GRASS;
			case 2: return XMaterial.FERN;
			}
		case 32: return XMaterial.DEAD_BUSH;
		case 33: return XMaterial.PISTON;
		case 34: return XMaterial.PISTON_HEAD;
		case 35:
			switch(data) {
			case 0: return XMaterial.WHITE_WOOL;
			case 1: return XMaterial.ORANGE_WOOL;
			case 2: return XMaterial.MAGENTA_WOOL;
			case 3: return XMaterial.LIGHT_BLUE_WOOL;
			case 4: return XMaterial.YELLOW_WOOL;
			case 5: return XMaterial.LIME_WOOL;
			case 6: return XMaterial.PINK_WOOL;
			case 7: return XMaterial.GRAY_WOOL;
			case 8: return XMaterial.LIGHT_GRAY_WOOL;
			case 9: return XMaterial.CYAN_WOOL;
			case 10: return XMaterial.PURPLE_WOOL;
			case 11: return XMaterial.BLUE_WOOL;
			case 12: return XMaterial.BROWN_WOOL;
			case 13: return XMaterial.GREEN_WOOL;
			case 14: return XMaterial.RED_WOOL;
			case 15: return XMaterial.BLACK_WOOL;
			}
		case 37: return XMaterial.DANDELION;
		case 38:
			switch(data) {
			case 0: return XMaterial.POPPY;
			case 1: return XMaterial.BLUE_ORCHID;
			case 2: return XMaterial.ALLIUM;
			case 3: return XMaterial.AZURE_BLUET;
			case 4: return XMaterial.RED_TULIP;
			case 5: return XMaterial.ORANGE_TULIP;
			case 6: return XMaterial.WHITE_TULIP;
			case 7: return XMaterial.PINK_TULIP;
			case 8: return XMaterial.OXEYE_DAISY;
			}
		case 39: return XMaterial.BROWN_MUSHROOM;
		case 40: return XMaterial.RED_MUSHROOM;
		case 41: return XMaterial.GOLD_BLOCK;
		case 42: return XMaterial.IRON_BLOCK;
		case 43:
			switch(data) { // should be double slabs
			case 0: return XMaterial.STONE_SLAB;
			case 1: return XMaterial.SANDSTONE_SLAB;
			case 2: return XMaterial.OAK_SLAB;
			case 3: return XMaterial.COBBLESTONE_SLAB;
			case 4: return XMaterial.BRICK_SLAB;
			case 5: return XMaterial.STONE_BRICK_SLAB;
			case 6: return XMaterial.NETHER_BRICK_SLAB;
			case 7: return XMaterial.QUARTZ_SLAB;
			}
		case 44:
			switch(data) {
			case 0: return XMaterial.STONE_SLAB;
			case 1: return XMaterial.SANDSTONE_SLAB;
			case 2: return XMaterial.OAK_SLAB;
			case 3: return XMaterial.COBBLESTONE_SLAB;
			case 4: return XMaterial.BRICK_SLAB;
			case 5: return XMaterial.STONE_BRICK_SLAB;
			case 6: return XMaterial.NETHER_BRICK_SLAB;
			case 7: return XMaterial.QUARTZ_SLAB;
			}
		case 45: return XMaterial.BRICKS;
		case 46: return XMaterial.TNT;
		case 47: return XMaterial.BOOKSHELF;
		case 48: return XMaterial.MOSSY_COBBLESTONE;
		case 49: return XMaterial.OBSIDIAN;
		case 50: return XMaterial.TORCH;
		case 51: return XMaterial.FIRE;
		case 52: return XMaterial.SPAWNER;
		case 53: return XMaterial.OAK_STAIRS;
		case 54: return XMaterial.CHEST;
		case 55: return XMaterial.REDSTONE_WIRE;
		case 56: return XMaterial.DIAMOND_ORE;
		case 57: return XMaterial.DIAMOND_BLOCK;
		case 58: return XMaterial.CRAFTING_TABLE;
		case 59: return XMaterial.WHEAT;
		case 60: return XMaterial.FARMLAND;
		case 61: return XMaterial.FURNACE;
		case 62: return XMaterial.FURNACE;
		case 63: return XMaterial.OAK_SIGN;
		case 64: return XMaterial.OAK_DOOR;
		case 65: return XMaterial.LADDER;
		case 66: return XMaterial.RAIL;
		case 67: return XMaterial.COBBLESTONE_STAIRS;
		case 68: return XMaterial.OAK_WALL_SIGN;
		case 69: return XMaterial.LEVER;
		case 70: return XMaterial.STONE_PRESSURE_PLATE;
		case 71: return XMaterial.IRON_DOOR;
		case 72: return XMaterial.OAK_PRESSURE_PLATE;
		case 73: return XMaterial.REDSTONE_ORE;
		case 74: return XMaterial.REDSTONE_ORE;
		case 75: return XMaterial.REDSTONE_TORCH;
		case 76: return XMaterial.REDSTONE_TORCH;
		case 77: return XMaterial.STONE_BUTTON;
		case 78: return XMaterial.SNOW;
		case 79: return XMaterial.ICE;
		case 80: return XMaterial.SNOW_BLOCK;
		case 81: return XMaterial.CACTUS;
		case 82: return XMaterial.CLAY;
		case 83: return XMaterial.SUGAR_CANE;
		case 84: return XMaterial.JUKEBOX;
		case 85: return XMaterial.OAK_FENCE;
		case 86: return XMaterial.PUMPKIN;
		case 87: return XMaterial.NETHERRACK;
		case 88: return XMaterial.SOUL_SAND;
		case 89: return XMaterial.GLOWSTONE;
		case 90: return XMaterial.NETHER_PORTAL;
		case 91: return XMaterial.JACK_O_LANTERN;
		case 92: return XMaterial.CAKE;
		case 93: return XMaterial.REPEATER;
		case 94: return XMaterial.REPEATER;
		case 95:
			switch(data) {
			case 0: return XMaterial.WHITE_STAINED_GLASS;
			case 1: return XMaterial.ORANGE_STAINED_GLASS;
			case 2: return XMaterial.MAGENTA_STAINED_GLASS;
			case 3: return XMaterial.LIGHT_BLUE_STAINED_GLASS;
			case 4: return XMaterial.YELLOW_STAINED_GLASS;
			case 5: return XMaterial.LIME_STAINED_GLASS;
			case 6: return XMaterial.PINK_STAINED_GLASS;
			case 7: return XMaterial.GRAY_STAINED_GLASS;
			case 8: return XMaterial.LIGHT_GRAY_STAINED_GLASS;
			case 9: return XMaterial.CYAN_STAINED_GLASS;
			case 10: return XMaterial.PURPLE_STAINED_GLASS;
			case 11: return XMaterial.BLUE_STAINED_GLASS;
			case 12: return XMaterial.BROWN_STAINED_GLASS;
			case 13: return XMaterial.GREEN_STAINED_GLASS;
			case 14: return XMaterial.RED_STAINED_GLASS;
			case 15: return XMaterial.BLACK_STAINED_GLASS;
			}
		case 96: return XMaterial.OAK_TRAPDOOR;
		case 97:
			switch(data) {
			case 0: return XMaterial.INFESTED_STONE;
			case 1: return XMaterial.INFESTED_COBBLESTONE;
			case 2: return XMaterial.INFESTED_STONE_BRICKS;
			case 3: return XMaterial.INFESTED_MOSSY_STONE_BRICKS;
			case 4: return XMaterial.INFESTED_CRACKED_STONE_BRICKS;
			case 5: return XMaterial.INFESTED_CHISELED_STONE_BRICKS;
			}
		case 98:
			switch(data) {
			case 0: return XMaterial.STONE_BRICKS;
			case 1: return XMaterial.MOSSY_STONE_BRICKS;
			case 2: return XMaterial.CRACKED_STONE_BRICKS;
			case 3: return XMaterial.CHISELED_STONE_BRICKS;
			}
		case 99: return XMaterial.BROWN_MUSHROOM_BLOCK;
		case 100: return XMaterial.RED_MUSHROOM_BLOCK;
		case 101: return XMaterial.IRON_BARS;
		case 102: return XMaterial.GLASS_PANE;
		case 103: return XMaterial.MELON;
		case 104: return XMaterial.PUMPKIN_STEM;
		case 105: return XMaterial.MELON_STEM;
		case 106: return XMaterial.VINE;
		case 107: return XMaterial.OAK_FENCE_GATE;
		case 108: return XMaterial.BRICK_STAIRS;
		case 109: return XMaterial.STONE_BRICK_STAIRS;
		case 110: return XMaterial.MYCELIUM;
		case 111: return XMaterial.LILY_PAD;
		case 112: return XMaterial.NETHER_BRICKS;
		case 113: return XMaterial.NETHER_BRICK_FENCE;
		case 114: return XMaterial.NETHER_BRICK_STAIRS;
		case 115: return XMaterial.NETHER_WART;
		case 116: return XMaterial.ENCHANTING_TABLE;
		case 117: return XMaterial.BREWING_STAND;
		case 118: return XMaterial.CAULDRON;
		case 119: return XMaterial.END_PORTAL;
		case 120: return XMaterial.END_PORTAL_FRAME;
		case 121: return XMaterial.END_STONE;
		case 122: return XMaterial.DRAGON_EGG;
		case 123: return XMaterial.REDSTONE_LAMP;
		case 124: return XMaterial.REDSTONE_LAMP;
		case 125:
			switch(data) {
			case 0: return XMaterial.OAK_SLAB;
			case 1: return XMaterial.SPRUCE_SLAB;
			case 2: return XMaterial.BIRCH_SLAB;
			case 3: return XMaterial.JUNGLE_SLAB;
			case 4: return XMaterial.ACACIA_SLAB;
			case 5: return XMaterial.DARK_OAK_SLAB;
			}
		case 126:
			switch(data) {
			case 0: return XMaterial.OAK_SLAB;
			case 1: return XMaterial.SPRUCE_SLAB;
			case 2: return XMaterial.BIRCH_SLAB;
			case 3: return XMaterial.JUNGLE_SLAB;
			case 4: return XMaterial.ACACIA_SLAB;
			case 5: return XMaterial.DARK_OAK_SLAB;
			}
		case 127: return XMaterial.COCOA;
		case 128: return XMaterial.SANDSTONE_STAIRS;
		case 129: return XMaterial.EMERALD_ORE;
		case 130: return XMaterial.ENDER_CHEST;
		case 131: return XMaterial.TRIPWIRE_HOOK;
		case 132: return XMaterial.TRIPWIRE;
		case 133: return XMaterial.EMERALD_BLOCK;
		case 134: return XMaterial.SPRUCE_STAIRS;
		case 135: return XMaterial.BIRCH_STAIRS;
		case 136: return XMaterial.JUNGLE_STAIRS;
		case 137: return XMaterial.COMMAND_BLOCK;
		case 138: return XMaterial.BEACON;
		case 139:
			switch(data) {
			case 0: return XMaterial.COBBLESTONE_WALL;
			case 1: return XMaterial.MOSSY_COBBLESTONE_WALL;
			}
		case 140: return XMaterial.FLOWER_POT;
		case 141: return XMaterial.CARROTS;
		case 142: return XMaterial.POTATOES;
		case 143: return XMaterial.OAK_BUTTON;
		case 144: return XMaterial.SKELETON_SKULL;
		case 145: return XMaterial.ANVIL;
		case 146: return XMaterial.TRAPPED_CHEST;
		case 147: return XMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE;
		case 148: return XMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE;
		case 149: return XMaterial.COMPARATOR;
		case 150: return XMaterial.COMPARATOR;
		case 151: return XMaterial.DAYLIGHT_DETECTOR;
		case 152: return XMaterial.REDSTONE_BLOCK;
		case 153: return XMaterial.NETHER_QUARTZ_ORE;
		case 154: return XMaterial.HOPPER;
		case 155:
			switch(data) {
			case 0: return XMaterial.QUARTZ_BLOCK;
			case 1: return XMaterial.CHISELED_QUARTZ_BLOCK;
			case 2: return XMaterial.QUARTZ_PILLAR;
			}
		case 156: return XMaterial.QUARTZ_STAIRS;
		case 157: return XMaterial.ACTIVATOR_RAIL;
		case 158: return XMaterial.DROPPER;
		case 159:
			switch(data) {
			case 0: return XMaterial.WHITE_TERRACOTTA;
			case 1: return XMaterial.ORANGE_TERRACOTTA;
			case 2: return XMaterial.MAGENTA_TERRACOTTA;
			case 3: return XMaterial.LIGHT_BLUE_TERRACOTTA;
			case 4: return XMaterial.YELLOW_TERRACOTTA;
			case 5: return XMaterial.LIME_TERRACOTTA;
			case 6: return XMaterial.PINK_TERRACOTTA;
			case 7: return XMaterial.GRAY_TERRACOTTA;
			case 8: return XMaterial.LIGHT_GRAY_TERRACOTTA;
			case 9: return XMaterial.CYAN_TERRACOTTA;
			case 10: return XMaterial.PURPLE_TERRACOTTA;
			case 11: return XMaterial.BLUE_TERRACOTTA;
			case 12: return XMaterial.BROWN_TERRACOTTA;
			case 13: return XMaterial.GREEN_TERRACOTTA;
			case 14: return XMaterial.RED_TERRACOTTA;
			case 15: return XMaterial.BLACK_TERRACOTTA;
			}
		case 160:
			switch(data) {
			case 0: return XMaterial.WHITE_STAINED_GLASS_PANE;
			case 1: return XMaterial.ORANGE_STAINED_GLASS_PANE;
			case 2: return XMaterial.MAGENTA_STAINED_GLASS_PANE;
			case 3: return XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE;
			case 4: return XMaterial.YELLOW_STAINED_GLASS_PANE;
			case 5: return XMaterial.LIME_STAINED_GLASS_PANE;
			case 6: return XMaterial.PINK_STAINED_GLASS_PANE;
			case 7: return XMaterial.GRAY_STAINED_GLASS_PANE;
			case 8: return XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE;
			case 9: return XMaterial.CYAN_STAINED_GLASS_PANE;
			case 10: return XMaterial.PURPLE_STAINED_GLASS_PANE;
			case 11: return XMaterial.BLUE_STAINED_GLASS_PANE;
			case 12: return XMaterial.BROWN_STAINED_GLASS_PANE;
			case 13: return XMaterial.GREEN_STAINED_GLASS_PANE;
			case 14: return XMaterial.RED_STAINED_GLASS_PANE;
			case 15: return XMaterial.BLACK_STAINED_GLASS_PANE;
			}
		case 161:
			switch(data) {
			case 0: return XMaterial.ACACIA_LEAVES;
			case 1: return XMaterial.DARK_OAK_LEAVES;
			}
		case 162:
			switch(data) {
			case 0: return XMaterial.ACACIA_WOOD;
			case 1: return XMaterial.DARK_OAK_WOOD;
			}
		case 163: return XMaterial.ACACIA_STAIRS;
		case 164: return XMaterial.DARK_OAK_STAIRS;
		case 165: return XMaterial.SLIME_BLOCK;
		case 166: return XMaterial.BARRIER;
		case 167: return XMaterial.IRON_TRAPDOOR;
		case 168:
			switch(data) {
			case 0: return XMaterial.PRISMARINE;
			case 1: return XMaterial.PRISMARINE_BRICKS;
			case 2: return XMaterial.DARK_PRISMARINE;
			}
		case 169: return XMaterial.SEA_LANTERN;
		case 170: return XMaterial.HAY_BLOCK;
		case 171:
			switch(data) {
			case 0: return XMaterial.WHITE_CARPET;
			case 1: return XMaterial.ORANGE_CARPET;
			case 2: return XMaterial.MAGENTA_CARPET;
			case 3: return XMaterial.LIGHT_BLUE_CARPET;
			case 4: return XMaterial.YELLOW_CARPET;
			case 5: return XMaterial.LIME_CARPET;
			case 6: return XMaterial.PINK_CARPET;
			case 7: return XMaterial.GRAY_CARPET;
			case 8: return XMaterial.LIGHT_GRAY_CARPET;
			case 9: return XMaterial.CYAN_CARPET;
			case 10: return XMaterial.PURPLE_CARPET;
			case 11: return XMaterial.BLUE_CARPET;
			case 12: return XMaterial.BROWN_CARPET;
			case 13: return XMaterial.GREEN_CARPET;
			case 14: return XMaterial.RED_CARPET;
			case 15: return XMaterial.BLACK_CARPET;
			}
		case 172: return XMaterial.TERRACOTTA;
		case 173: return XMaterial.COAL_BLOCK;
		case 174: return XMaterial.PACKED_ICE;
		case 175:
			switch(data) {
			case 0: return XMaterial.SUNFLOWER;
			case 1: return XMaterial.LILAC;
			case 2: return XMaterial.TALL_GRASS;
			case 3: return XMaterial.LARGE_FERN;
			case 4: return XMaterial.ROSE_BUSH;
			case 5: return XMaterial.PEONY;
			}
		case 176: return XMaterial.WHITE_BANNER;
		case 177: return XMaterial.WHITE_WALL_BANNER;
		case 178: return XMaterial.DAYLIGHT_DETECTOR;
		case 179:
			switch(data) {
			case 0: return XMaterial.RED_SANDSTONE;
			case 1: return XMaterial.CHISELED_RED_SANDSTONE;
			case 2: return XMaterial.SMOOTH_RED_SANDSTONE;
			}
		case 180: return XMaterial.RED_SANDSTONE_STAIRS;
		case 181: return XMaterial.RED_SANDSTONE_SLAB;
		case 182: return XMaterial.RED_SANDSTONE_SLAB;
		case 183: return XMaterial.SPRUCE_FENCE_GATE;
		case 184: return XMaterial.BIRCH_FENCE_GATE;
		case 185: return XMaterial.JUNGLE_FENCE_GATE;
		case 186: return XMaterial.DARK_OAK_FENCE_GATE;
		case 187: return XMaterial.ACACIA_FENCE_GATE;
		case 188: return XMaterial.SPRUCE_FENCE;
		case 189: return XMaterial.BIRCH_FENCE;
		case 190: return XMaterial.JUNGLE_FENCE;
		case 191: return XMaterial.DARK_OAK_FENCE;
		case 192: return XMaterial.ACACIA_FENCE;
		case 193: return XMaterial.SPRUCE_DOOR;
		case 194: return XMaterial.BIRCH_DOOR;
		case 195: return XMaterial.JUNGLE_DOOR;
		case 196: return XMaterial.ACACIA_DOOR;
		case 197: return XMaterial.DARK_OAK_DOOR;
		case 198: return XMaterial.END_ROD;
		case 199: return XMaterial.CHORUS_PLANT;
		case 200: return XMaterial.CHORUS_FLOWER;
		case 201: return XMaterial.PURPUR_BLOCK;
		case 202: return XMaterial.PURPUR_PILLAR;
		case 203: return XMaterial.PURPUR_STAIRS;
		case 204: return XMaterial.PURPUR_SLAB;
		case 205: return XMaterial.PURPUR_SLAB;
		case 206: return XMaterial.END_STONE_BRICKS;
		case 207: return XMaterial.BEETROOTS;
		case 208: return XMaterial.GRASS_PATH;
		case 209: return XMaterial.END_GATEWAY;
		case 210: return XMaterial.REPEATING_COMMAND_BLOCK;
		case 211: return XMaterial.CHAIN_COMMAND_BLOCK;
		case 212: return XMaterial.FROSTED_ICE;
		case 213: return XMaterial.MAGMA_BLOCK;
		case 214: return XMaterial.NETHER_WART_BLOCK;
		case 215: return XMaterial.RED_NETHER_BRICKS;
		case 216: return XMaterial.BONE_BLOCK;
		case 217: return XMaterial.STRUCTURE_VOID;
		case 218: return XMaterial.OBSERVER;
		case 219: return XMaterial.WHITE_SHULKER_BOX;
		case 220: return XMaterial.ORANGE_SHULKER_BOX;
		case 221: return XMaterial.MAGENTA_SHULKER_BOX;
		case 222: return XMaterial.LIGHT_BLUE_SHULKER_BOX;
		case 223: return XMaterial.YELLOW_SHULKER_BOX;
		case 224: return XMaterial.LIME_SHULKER_BOX;
		case 225: return XMaterial.PINK_SHULKER_BOX;
		case 226: return XMaterial.GRAY_SHULKER_BOX;
		case 227: return XMaterial.LIGHT_GRAY_SHULKER_BOX;
		case 228: return XMaterial.CYAN_SHULKER_BOX;
		case 229: return XMaterial.PURPLE_SHULKER_BOX;
		case 230: return XMaterial.BLUE_SHULKER_BOX;
		case 231: return XMaterial.BROWN_SHULKER_BOX;
		case 232: return XMaterial.GREEN_SHULKER_BOX;
		case 233: return XMaterial.RED_SHULKER_BOX;
		case 234: return XMaterial.BLACK_SHULKER_BOX;
		case 235: return XMaterial.WHITE_GLAZED_TERRACOTTA;
		case 236: return XMaterial.ORANGE_GLAZED_TERRACOTTA;
		case 237: return XMaterial.MAGENTA_GLAZED_TERRACOTTA;
		case 238: return XMaterial.LIGHT_BLUE_GLAZED_TERRACOTTA;
		case 239: return XMaterial.YELLOW_GLAZED_TERRACOTTA;
		case 240: return XMaterial.LIME_GLAZED_TERRACOTTA;
		case 241: return XMaterial.PINK_GLAZED_TERRACOTTA;
		case 242: return XMaterial.GRAY_GLAZED_TERRACOTTA;
		case 243: return XMaterial.LIGHT_GRAY_GLAZED_TERRACOTTA;
		case 244: return XMaterial.CYAN_GLAZED_TERRACOTTA;
		case 245: return XMaterial.PURPLE_GLAZED_TERRACOTTA;
		case 246: return XMaterial.BLUE_GLAZED_TERRACOTTA;
		case 247: return XMaterial.BROWN_GLAZED_TERRACOTTA;
		case 248: return XMaterial.GREEN_GLAZED_TERRACOTTA;
		case 249: return XMaterial.RED_GLAZED_TERRACOTTA;
		case 250: return XMaterial.BLACK_GLAZED_TERRACOTTA;
		case 251:
			switch(data) {
			case 0: return XMaterial.WHITE_CONCRETE;
			case 1: return XMaterial.ORANGE_CONCRETE;
			case 2: return XMaterial.MAGENTA_CONCRETE;
			case 3: return XMaterial.LIGHT_BLUE_CONCRETE;
			case 4: return XMaterial.YELLOW_CONCRETE;
			case 5: return XMaterial.LIME_CONCRETE;
			case 6: return XMaterial.PINK_CONCRETE;
			case 7: return XMaterial.GRAY_CONCRETE;
			case 8: return XMaterial.LIGHT_GRAY_CONCRETE;
			case 9: return XMaterial.CYAN_CONCRETE;
			case 10: return XMaterial.PURPLE_CONCRETE;
			case 11: return XMaterial.BLUE_CONCRETE;
			case 12: return XMaterial.BROWN_CONCRETE;
			case 13: return XMaterial.GREEN_CONCRETE;
			case 14: return XMaterial.RED_CONCRETE;
			case 15: return XMaterial.BLACK_CONCRETE;
			}
		case 252:
			switch(data) {
			case 0: return XMaterial.WHITE_CONCRETE_POWDER;
			case 1: return XMaterial.ORANGE_CONCRETE_POWDER;
			case 2: return XMaterial.MAGENTA_CONCRETE_POWDER;
			case 3: return XMaterial.LIGHT_BLUE_CONCRETE_POWDER;
			case 4: return XMaterial.YELLOW_CONCRETE_POWDER;
			case 5: return XMaterial.LIME_CONCRETE_POWDER;
			case 6: return XMaterial.PINK_CONCRETE_POWDER;
			case 7: return XMaterial.GRAY_CONCRETE_POWDER;
			case 8: return XMaterial.LIGHT_GRAY_CONCRETE_POWDER;
			case 9: return XMaterial.CYAN_CONCRETE_POWDER;
			case 10: return XMaterial.PURPLE_CONCRETE_POWDER;
			case 11: return XMaterial.BLUE_CONCRETE_POWDER;
			case 12: return XMaterial.BROWN_CONCRETE_POWDER;
			case 13: return XMaterial.GREEN_CONCRETE_POWDER;
			case 14: return XMaterial.RED_CONCRETE_POWDER;
			case 15: return XMaterial.BLACK_CONCRETE_POWDER;
			}
		case 255: return XMaterial.STRUCTURE_BLOCK;
		}
		return null;
	}
}
