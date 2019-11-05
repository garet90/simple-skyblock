package holiday.garet.skyblock.economy;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

public class Economy {
	FileConfiguration dataLocation;
	UUID uuid;
	double balance;
	String dataAddress;
	
	public Economy(UUID _uuid, FileConfiguration _dataLocation) {
		uuid = _uuid;
		dataLocation = _dataLocation;
		dataAddress = "data.players." + _uuid.toString() + ".balance";
		if (dataLocation.isSet(dataAddress)) {
			balance = dataLocation.getDouble(dataAddress);
		} else {
			balance = 0.0;
			saveBalance();
		}
	}
	
	public void withdraw(double _amount) {
		balance -= _amount;
		saveBalance();
	}
	
	public void deposit(double _amount) {
		balance += _amount;
		saveBalance();
	}
	
	public void set(double _amount) {
		balance = _amount;
		saveBalance();
	}
	
	public double get() {
		return balance;
	}
	
	private void saveBalance() {
		dataLocation.set(dataAddress, balance);
	}
	
}
