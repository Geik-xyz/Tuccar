package poyrazinan.com.tr.tuccar.listeners.Guis;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.BuyItem;
import poyrazinan.com.tr.tuccar.Utils.Title;
import poyrazinan.com.tr.tuccar.Utils.getLang;
import poyrazinan.com.tr.tuccar.Utils.Storage.ConfirmationStorage;
import poyrazinan.com.tr.tuccar.Utils.Storage.ProductStorage;
import poyrazinan.com.tr.tuccar.Utils.api.EventReason;
import poyrazinan.com.tr.tuccar.api.events.ProductRemoveEvent;
import poyrazinan.com.tr.tuccar.api.events.ProductSoldEvent;
import poyrazinan.com.tr.tuccar.database.DatabaseQueries;

public class ConfirmationGuiListener implements Listener {
	
	@SuppressWarnings("unused")
	private Tuccar plugin;
	public ConfirmationGuiListener(Tuccar plugin) {
		this.plugin = plugin;
	}
	
	public static HashMap<String, ConfirmationStorage> confirmation = new HashMap<String, ConfirmationStorage>();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void itemGuiEvent(InventoryClickEvent e) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException, ParseException {
		if (e.getView().getTitle().contains(Tuccar.color(getLang.getText("confirmationGui")))) {
			e.setCancelled(true);
			if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.CHEST) {
					Player player = (Player) e.getWhoClicked();
					if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType().equals(Material.AIR))) return;
					
					ProductStorage item = confirmation.get(player.getName()).getProduct();
					
					int itemID = item.getID();
					
					int buyAmount = confirmation.get(player.getName()).getBuyAmount();
					
					double price = buyAmount*item.getPrice();
					
					if (e.getSlot() == Tuccar.instance.getConfig().getInt("confirmation.yesItem.slot")) {
						if (DatabaseQueries.checkStock(itemID, buyAmount)) {
							
							int emptySlots = getEmptySlotsAmount(player);
							
							int itemStackSize = Material.getMaterial(item.getItemMaterial().toUpperCase()).getMaxStackSize();
							
							if (confirmation.get(player.getName()).isSelfItem()) {
								
								if (emptySlots*itemStackSize >= buyAmount) {
									
									confirmation.remove(player.getName());
									player.closeInventory();
									Title.send(player, getLang.getText("Titles.processSuccess.title"), getLang.getText("Titles.processSuccess.subTitle"), 3);
									DatabaseQueries.removeProductCount(item.getID(), buyAmount, item.getDataName(), item.getItemCategory(), item.getPrice());
									// EVENT
									
									Bukkit.getScheduler().runTask(Tuccar.instance, () ->
									{
										
										ProductRemoveEvent productRemove = new ProductRemoveEvent(EventReason.CANCEL, item.getDataName(), item.getItemCategory(), price);
				        				Bukkit.getPluginManager().callEvent(productRemove);
										
									});
			        				//
									
									addItemToPlayer(buyAmount, BuyItem.buyItem(item, itemStackSize), player);
									
								} else {Title.send(player, getLang.getText("Titles.notEnoughSpace.title"), getLang.getText("Titles.notEnoughSpace.subTitle"), 3); confirmation.remove(player.getName()); 
								player.closeInventory();}
							} else {
								if (Tuccar.econ.getBalance(player) >= price) {
									
									if (emptySlots*itemStackSize >= buyAmount) {
										Tuccar.econ.withdrawPlayer(player, price);
										int tax = Tuccar.instance.getConfig().getInt("Tax.taxRate");
										
										double toDeposit = price;
										
										if (tax != 0) {
											if (Tuccar.instance.getConfig().getBoolean("Tax.depositAccount")) 
												Tuccar.econ.depositPlayer(Bukkit.getOfflinePlayer(Tuccar.instance.getConfig().getString("Tax.account")), (tax*price)/100);
											
											toDeposit = price-((tax*price)/100);
											
										}
										
										Tuccar.econ.depositPlayer(Bukkit.getOfflinePlayer(item.getSeller()), toDeposit);
										
										if (Bukkit.getOfflinePlayer(item.getSeller()).isOnline()
												&& getLang.isSet("Messages.productSold"))
											Bukkit.getPlayer(item.getSeller()).sendMessage(Tuccar.color(getLang.getText("Messages.productSold")
													.replace("{product}", item.getItemDisplayName())
													.replace("{amount}", String.valueOf(buyAmount))
													.replace("{price}", String.valueOf(  roundDouble(toDeposit, 2)  ))
													));
										
										
										
										confirmation.remove(player.getName());
										
										player.closeInventory();
										
										Title.send(player, getLang.getText("Titles.processSuccess.title"), getLang.getText("Titles.processSuccess.subTitle"), 3);
										
										DatabaseQueries.removeProductCount(item.getID(), buyAmount, item.getDataName(), item.getItemCategory(), item.getPrice());
										
										// EVENT
										Bukkit.getScheduler().runTask(Tuccar.instance, () ->
										{
					        				
					        				ProductSoldEvent soldEvent = new ProductSoldEvent(buyAmount, item.getPrice(), item.getSeller(), player.getName(), item);
					        				Bukkit.getPluginManager().callEvent(soldEvent);
											
										});
										
										addItemToPlayer(buyAmount, BuyItem.buyItem(item, itemStackSize), player);
										
									} else {Title.send(player, getLang.getText("Titles.notEnoughSpace.title"), getLang.getText("Titles.notEnoughSpace.subTitle"), 3); confirmation.remove(player.getName()); 
									player.closeInventory();}
								} else {Title.send(player, getLang.getText("Titles.notEnoughMoney.title"), getLang.getText("Titles.notEnoughMoney.subTitle"), 3); confirmation.remove(player.getName()); 
							}
							player.closeInventory();
							
							}
						} else {Title.send(player, getLang.getText("Titles.errorConfirmation.title"), getLang.getText("Titles.errorConfirmation.subTitle"), 3); confirmation.remove(player.getName()); 
						player.closeInventory();}
					} else if (e.getSlot() == Tuccar.instance.getConfig().getInt("confirmation.noItem.slot")) {
						confirmation.remove(player.getName());
						player.closeInventory();
						Title.send(player, getLang.getText("Titles.processCancelled.title"), getLang.getText("Titles.processCancelled.subTitle"), 3);
						
					}
			}
		}
	}
	
	/**
	 * @author Geik
	 * @since 1.0.0
	 * @param value
	 * @param places
	 * @return
	 */
	public static String roundDouble(double value, int places)
	{
		
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    
	    value = value * factor;
	    
	    long tmp = Math.round(value);
	    
	    double result = (double) tmp / factor;
	    
	    return String.valueOf( result );
	    
	}
	
	public static void addItemToPlayer(int amount, ItemStack item, Player player)
	{
		
		Bukkit.getScheduler().runTask(Tuccar.instance, () ->
		{
			
			int calculatedAmount = amount;
			
			int maxAmount = item.getMaxStackSize();
			
			if (amount > maxAmount)
			{
				
				item.setAmount(maxAmount);
				
				for (int i = 0; i <= amount/maxAmount; i++)
				{
					
					if (calculatedAmount > maxAmount)
					{
						
						item.setAmount(maxAmount);
						
						player.getInventory().addItem(item);
						
						calculatedAmount = calculatedAmount - maxAmount;
						
					}
					
					else
					{
						
						item.setAmount(calculatedAmount);
						
						player.getInventory().addItem(item);
						
						calculatedAmount = 0;
						
						break;
						
					}
					
				}
				
			}
			
			else
			{
				
				item.setAmount(calculatedAmount);
				
				player.getInventory().addItem(item);
				
			}
			
		});
		
	}
	
	public static int getEmptySlotsAmount(Player player) {
		int count = 0;
		for (ItemStack i : player.getInventory()) {
			if (i == null) {
				count++;
			} else continue;
		}
		return count;
	}

}