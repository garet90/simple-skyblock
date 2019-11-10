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
