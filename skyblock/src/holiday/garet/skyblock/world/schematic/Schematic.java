package holiday.garet.skyblock.world.schematic;

public class Schematic {
	private short height;
	private short width;
	private short length;
	
	private String name;
	private String materials;
	
	private byte[] blocks;
	private byte[] data;
	
	public Schematic(String name, short width, short height, short length, String materials, byte[] blocks, byte[] data) {
		this.height = height;
		this.width = width;
		this.length = length;
		this.name = name;
		this.materials = materials;
		this.blocks = blocks;
		this.data = data;
	}

	public short getHeight() {
		return this.height;
	}
	
	public short getWidth() {
		return this.width;
	}
	
	public short getLength() {
		return this.length;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getMaterials() {
		return this.materials;
	}
	
	public byte[] getBlocks() {
		return this.blocks;
	}
	
	public byte[] getData() {
		return this.data;
	}
}
