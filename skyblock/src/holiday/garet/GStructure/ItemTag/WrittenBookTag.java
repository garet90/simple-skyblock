package holiday.garet.GStructure.ItemTag;

import net.querz.nbt.tag.CompoundTag;

public class WrittenBookTag extends BookAndQuillTag {
	private byte resolved;
	private int generation;
	private String author;
	private String title;
	
	public WrittenBookTag() {
		type = 15;
	}
	
	public byte getResolved() {
		return resolved;
	}
	
	public int getGeneration() {
		return generation;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void read(CompoundTag tag) {
		super.read(tag);
		this.resolved = tag.getByte("resolved");
		this.generation = tag.getInt("generation");
		this.author = tag.getString("author");
		this.title = tag.getString("title");
	}
}
