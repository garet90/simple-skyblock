package holiday.garet.skyblock.island;

public class IslandLevel {
	private String name;
	private int level;
	
	public IslandLevel(String _name, int _level) {
		name = _name;
		level = _level;
	}
	
	public String getName() {
		return name;
	}
	
	public int getLevel() {
		return level;
	}
}
