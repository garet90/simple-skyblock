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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import holiday.garet.skyblock.XMaterial;

public class Trade implements Listener {
	
	Player player1;
	Player player2;
	boolean player1Accepted = false;
	boolean player2Accepted = false;
	double p1Money = 0.0;
	double p2Money = 0.0;
	Economy p1Econ;
	Economy p2Econ;
	Inventory menu;
	Plugin plugin;
	boolean active = true;
	boolean usingVault = false;
	net.milkbowl.vault.economy.Economy vaultEconomy = null;
	
	public Trade(Player _player1, Player _player2, Plugin _plugin, Economy _p1Econ, Economy _p2Econ) {
		player1 = _player1;
		player2 = _player2;
		plugin = _plugin;
		p1Econ = _p1Econ;
		p2Econ = _p2Econ;
		createMenu();
	}
	
	public Trade(Player _player1, Player _player2, Plugin _plugin, net.milkbowl.vault.economy.Economy _vaultEconomy) {
		player1 = _player1;
		player2 = _player2;
		plugin = _plugin;
		vaultEconomy = _vaultEconomy;
		usingVault = true;
		createMenu();
	}
	
	public void open() {
		player1.openInventory(menu);
		player2.openInventory(menu);
	}
	
	public void createMenu() {
		menu = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "Trade");
		// Close menu button
		ItemStack closeMenu = XMaterial.BARRIER.parseItem();
		ItemMeta closeMeta = closeMenu.getItemMeta();
		closeMeta.setDisplayName(ChatColor.RED + "Close Menu");
		closeMenu.setItemMeta(closeMeta);
		menu.setItem(49, closeMenu);
		// Grey glass borders
		ItemStack glassBorder = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
		ItemMeta glassBorderMeta = glassBorder.getItemMeta();
		glassBorderMeta.setDisplayName(" ");
		glassBorder.setItemMeta(glassBorderMeta);
		for (int i = 0; i < 54; i++) {
			if (i != 49 && ((i >= 0 && i <= 8) || (i % 9 == 0 || i % 9 == 8 || i % 9 == 4) || (i >= 45))) {
				menu.setItem(i, glassBorder);
			}
		}
		// Accept buttons
		ItemStack acceptButton = XMaterial.LIME_DYE.parseItem();
		ItemMeta acceptButtonMeta = acceptButton.getItemMeta();
		acceptButtonMeta.setDisplayName(ChatColor.GREEN + "Accept Trade");
		acceptButton.setItemMeta(acceptButtonMeta);
		menu.setItem(39, acceptButton);
		menu.setItem(41, acceptButton);
		// Gold buttons
		ItemStack moneyButton = XMaterial.GOLD_INGOT.parseItem();
		ItemMeta moneyButtonMeta = moneyButton.getItemMeta();
		moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $0.00");
		List<String> lore = Arrays.asList(ChatColor.GRAY + "Shift left click: +$100", ChatColor.GRAY + "Shift right click: -$100", ChatColor.GRAY + "Left click: +$1", ChatColor.GRAY + "Right click: -$1");
		moneyButtonMeta.setLore(lore);
		moneyButton.setItemMeta(moneyButtonMeta);
		menu.setItem(37, moneyButton);
		menu.setItem(43, moneyButton);
	}
	
	public boolean hasPlayer(UUID _player) {
		if (player1.getUniqueId() == _player || player2.getUniqueId() == _player) {
			return true;
		}
		return false;
	}
	
	public boolean hasPlayer(Player _player) {
		if (player1 == _player || player2 == _player) {
			return true;
		}
		return false;
	}
	
	public void close() {
		active = false;
		player1.closeInventory();
		player2.closeInventory();
	}
	
	public Player getPlayer1() {
		return player1;
	}
	
	public Player getPlayer2() {
		return player2;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack acceptButtonU = XMaterial.LIME_DYE.parseItem();
		ItemMeta acceptMetaU = acceptButtonU.getItemMeta();
		acceptMetaU.setDisplayName(ChatColor.GREEN + "Accept Trade");
		acceptButtonU.setItemMeta(acceptMetaU);
	    if ((p == player1 || p == player2) && isActive() && e.getClickedInventory() != p.getInventory()) {
			if (((e.getSlot() % 9 >= 1 && e.getSlot() % 9 <= 3 && p == player2) || (e.getSlot() % 9 >= 5 && e.getSlot() % 9 <= 7 && p == player1))) {
	    		e.setCancelled(true);
	    	} else if (e.getCurrentItem() != null) {
	        	String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
	        	if (itemName.equalsIgnoreCase(" ")) {
	        		e.setCancelled(true);
	        	} else if (itemName.equalsIgnoreCase(ChatColor.RED + "Close Menu")) {
	        		e.setCancelled(true);
	    			Inventory p1Inventory = player1.getInventory();
	    			Inventory p2Inventory = player2.getInventory();
	    			for (int i = 0; i < 45; i++) {
	    				if (i % 9 >= 1 && i % 9 <= 3 && i > 9 && i < 36 && i != 39) {
	    					if (e.getInventory().getItem(i) != null) {
	    						p1Inventory.addItem(e.getInventory().getItem(i));
	    					}
	    				}
	    				if (i % 9 >= 5 && i % 9 <= 7 && i > 9 && i < 36 && i != 41) {
	    					if (e.getInventory().getItem(i) != null) {
	    						p2Inventory.addItem(e.getInventory().getItem(i));
	    					}
	    				}
	    			}
	    			close();
	    			player1.sendMessage(ChatColor.RED + "The trade has been cancelled.");
	    			player2.sendMessage(ChatColor.RED + "The trade has been cancelled.");
	        	} else if (e.getSlot() == 39 || e.getSlot() == 41) {
	        		e.setCancelled(true);
					ItemStack acceptButton = XMaterial.GREEN_DYE.parseItem();
					ItemMeta acceptMeta = acceptButton.getItemMeta();
					acceptMeta.setDisplayName(ChatColor.GREEN + "Accepted!");
					acceptButton.setItemMeta(acceptMeta);
	        		if (p == player1) {
	        			if (itemName.equalsIgnoreCase(ChatColor.GREEN + "Accept Trade")) {
	        				e.getInventory().setItem(39, acceptButton);
	        				player1Accepted = true;
	        			} else {
	        				e.getInventory().setItem(39, acceptButtonU);
	        				player1Accepted = false;
	        			}
	        		} else {
	        			if (itemName.equalsIgnoreCase(ChatColor.GREEN + "Accept Trade")) {
	        				e.getInventory().setItem(41, acceptButton);
	        				player2Accepted = true;
	        			} else {
	        				e.getInventory().setItem(41, acceptButtonU);
	        				player2Accepted = false;
	        			}
	        		}
	        		if (isActive() && player1Accepted && player2Accepted) {
	        			e.getInventory().getItem(39).setAmount(3);
	        			e.getInventory().getItem(41).setAmount(3);
	        	        new BukkitRunnable() {
	        	            
	        	            @Override
	        	            public void run() {
		    					if (isActive() && player1Accepted && player2Accepted) {
		                			Inventory p1Inventory = player1.getInventory();
		                			Inventory p2Inventory = player2.getInventory();
		                			for (int i = 0; i < 45; i++) {
		                				if (i % 9 >= 1 && i % 9 <= 3 && i > 9 && i < 36 && i != 39) {
		                					if (e.getInventory().getItem(i) != null) {
		                						p2Inventory.addItem(e.getInventory().getItem(i));
		                					}
		                				}
		                				if (i % 9 >= 5 && i % 9 <= 7 && i > 9 && i < 36 && i != 41) {
		                					if (e.getInventory().getItem(i) != null) {
		                						p1Inventory.addItem(e.getInventory().getItem(i));
		                					}
		                				}
		                			}
		        					double p1Money = Double.valueOf(e.getInventory().getItem(37).getItemMeta().getDisplayName().substring(10));
		        					double p2Money = Double.valueOf(e.getInventory().getItem(43).getItemMeta().getDisplayName().substring(10));
		        					if (usingVault) {
		        						vaultEconomy.depositPlayer(player1, p2Money);
		        						vaultEconomy.withdrawPlayer(player1, p1Money);
		        						vaultEconomy.depositPlayer(player2, p1Money);
		        						vaultEconomy.withdrawPlayer(player2, p2Money);
		        					} else {
			        					p1Econ.set(p1Econ.get() + (p2Money - p1Money));
			        					p2Econ.set(p2Econ.get() + (p1Money - p2Money));
		        					}
		                			close();
		                			player1.sendMessage(ChatColor.GREEN + "The trade completed successfully.");
		                			player2.sendMessage(ChatColor.GREEN + "The trade completed successfully.");
		    					}
							}
	        			}.runTaskLater(plugin, 60L);
	        			for (int i = 1; i < 3; i++) {
	        				final int count = i;
	        		        new BukkitRunnable() {
	        		            
	        		            @Override
	            		    	public void run() {
	            		    		if (e.getInventory().getItem(39).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Accepted!") && e.getInventory().getItem(41).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Accepted!")) {
	            		    			e.getInventory().getItem(39).setAmount(3 - count);
	            		    			e.getInventory().getItem(41).setAmount(3 - count);
	            		    		}
	            		    	}
	            		    }.runTaskLater(plugin, 20*(i));
	        			}
	        		}
	        	} else if (e.getSlot() == 37 || e.getSlot() == 43) {
					e.setCancelled(true);
					ItemStack moneyButton = XMaterial.GOLD_INGOT.parseItem();
					ItemMeta moneyButtonMeta = moneyButton.getItemMeta();
					Economy econ;
					double currentMoney = 0.0;
					double currentBalance;
					if (p == player1) {
						currentMoney = p1Money;
					} else {
						currentMoney = p2Money;
					}
					if (usingVault) {
						currentBalance = vaultEconomy.getBalance(p);
					} else {
						if (p == player1) {
							econ = p1Econ;
						} else {
							econ = p2Econ;
						}
						currentBalance = econ.get();
					}
		    		DecimalFormat dec = new DecimalFormat("#0.00");
	        		if (e.isLeftClick()) {
	        			if (e.isShiftClick()) {
	        				if ((currentMoney + 100) <= currentBalance) {
	        					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $" + dec.format((int)(currentMoney + 100)));
	        					currentMoney += 100;
	        				} else {
	        					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $" + dec.format(Math.floor(currentBalance)));
	        					currentMoney = currentBalance;
	        				}
	        			} else {
	        				if ((currentMoney + 1) <= currentBalance) {
	        					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $" + dec.format((int)(currentMoney + 1)));
	        					currentMoney += 1;
	        				} else {
	        					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $" + dec.format(Math.floor(currentBalance)));
	        					currentMoney = currentBalance;
	        				}
	        			}
	        		} else if (e.isRightClick()) {
	        			if (e.isShiftClick()) {
	        				if (currentMoney > 100) {
	        					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $" + dec.format((int)(currentMoney - 100)));
	        					currentMoney -= 100;
	        				} else {
	        					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $0.00");
	        					currentMoney = 0;
	        				}
	        			} else {
	        				if (currentMoney > 0) {
	        					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $" + dec.format((int)(currentMoney - 1)));
	        					currentMoney -= 1;
	        				} else {
	        					moneyButtonMeta.setDisplayName(ChatColor.GREEN + "Money: $0.00");
	        					currentMoney = 0;
	        				}
	        			}
	        		}
	        		if (p == player1) {
	        			p1Money = currentMoney;
	        		} else {
	        			p2Money = currentMoney;
	        		}
	        		List<String> lore = Arrays.asList(ChatColor.GRAY + "Shift left click: +$100", ChatColor.GRAY + "Shift right click: -$100", ChatColor.GRAY + "Left click: +$1", ChatColor.GRAY + "Right click: -$1");
	        		moneyButtonMeta.setLore(lore);
					moneyButton.setItemMeta(moneyButtonMeta);
					e.getInventory().setItem(e.getSlot(), moneyButton);
					e.getInventory().setItem(41, acceptButtonU);
					e.getInventory().setItem(39, acceptButtonU);
					player1Accepted = false;
					player2Accepted = false;
	    		} else {
	        		e.setCancelled(true);
	    			e.getInventory().setItem(41, acceptButtonU);
	    			e.getInventory().setItem(39, acceptButtonU);
					player1Accepted = false;
					player2Accepted = false;
	    			p.getInventory().addItem(e.getCurrentItem());
	    			e.getInventory().setItem(e.getSlot(), new ItemStack(XMaterial.AIR.parseMaterial(),1));
	        	}
	    	}
	    } else if ((p == player1 || p == player2) && isActive() && e.getClickedInventory() == p.getInventory()) {
	        e.setCancelled(true);
			for (int i = 9; i < 36; i++) {
				if (p == player1) {
					if (i % 9 >= 1 && i % 9 <= 3) {
						if (e.getInventory().getItem(i) == null || e.getInventory().getItem(i).getType() == XMaterial.AIR.parseMaterial()) {
							e.getInventory().setItem(i, e.getCurrentItem());
							e.getClickedInventory().setItem(e.getSlot(), new ItemStack(XMaterial.AIR.parseMaterial(),1));
						}
					}
				} else {
					if (i % 9 >= 5 && i % 9 <= 7) {
						if (e.getInventory().getItem(i) == null || e.getInventory().getItem(i).getType() == XMaterial.AIR.parseMaterial()) {
							e.getInventory().setItem(i, e.getCurrentItem());
							e.getClickedInventory().setItem(e.getSlot(), new ItemStack(XMaterial.AIR.parseMaterial(),1));
						}
					}
				}
			}
			e.getInventory().setItem(41, acceptButtonU);
			e.getInventory().setItem(39, acceptButtonU);
			player1Accepted = false;
			player2Accepted = false;
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if ((p == player1 || p == player2) && isActive()) {
			Inventory p1Inventory = player1.getInventory();
			Inventory p2Inventory = player2.getInventory();
			for (int i = 0; i < 45; i++) {
				if (i % 9 >= 1 && i % 9 <= 3 && i > 9 && i < 36 && i != 39) {
					if (e.getInventory().getItem(i) != null) {
						p1Inventory.addItem(e.getInventory().getItem(i));
					}
				}
				if (i % 9 >= 5 && i % 9 <= 7 && i > 9 && i < 36 && i != 41) {
					if (e.getInventory().getItem(i) != null) {
						p2Inventory.addItem(e.getInventory().getItem(i));
					}
				}
			}
			close();
			player1.closeInventory();
			player2.closeInventory();
			player1.sendMessage(ChatColor.RED + "The trade has been cancelled.");
			player2.sendMessage(ChatColor.RED + "The trade has been cancelled.");
        }
	}
	
	public boolean isActive() {
		return active;
	}

}
