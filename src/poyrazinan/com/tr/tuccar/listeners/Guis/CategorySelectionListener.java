package poyrazinan.com.tr.tuccar.listeners.Guis;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.getLang;
import poyrazinan.com.tr.tuccar.Utils.Storage.CategoryStorage;
import poyrazinan.com.tr.tuccar.Utils.Storage.CustomItemCache;
import poyrazinan.com.tr.tuccar.commands.Main.MainCommands;
import poyrazinan.com.tr.tuccar.gui.ItemSelectionGui;
import poyrazinan.com.tr.tuccar.gui.PlayerProducts;

public class CategorySelectionListener implements Listener {
	
	@SuppressWarnings("unused")
	private Tuccar plugin;
	public CategorySelectionListener(Tuccar plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInventory(InventoryClickEvent e)
	{
		
		
		if (e.getView().getTitle().equalsIgnoreCase(Tuccar.color(getLang.getText("TuccarGui")))) {
			
			
			Player player = (Player) e.getWhoClicked();
			e.setCancelled(true);
				
			if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType().equals(Material.AIR))) return;
			 if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.CHEST) {
				 List<CategoryStorage> storage = Tuccar.categoryStore;
				 CategoryStorage categoryItem = null;
				 
				 if (Tuccar.customItems.containsKey(e.getSlot()))
				 {
					 
					 player.closeInventory();
					 
					 CustomItemCache items = Tuccar.customItems.get(e.getSlot());
						 
						for (String cmd : items.getCommands())
						{
							
							Bukkit.dispatchCommand(player, cmd.replace("%player%", player.getName()));
							
						}
					 
					 return;
					 
				 }
				 
				 
				 if (e.getSlot() == Integer.parseInt(getLang.getText("Gui.help.slot"))) {
					 player.closeInventory();
					 MainCommands.help(player);
				 } 
				 if (getLang.isSet("Gui.myProducts")) {
					 if (e.getSlot() == Integer.parseInt(getLang.getText("Gui.myProducts.slot"))) {
						 player.closeInventory();
						 PlayerProducts.createGui(player, 1);}
				 }
				 for (CategoryStorage x : storage) {
					 if (x.getSlot() == e.getSlot()) {
						 categoryItem = x;
						 break;
					 }
                 }
				 if (categoryItem != null) {
					 player.closeInventory();
					 ItemSelectionGui.createGui(player, categoryItem.getCategoryDataName(), 1);
				 }
				 
			 }
				 
		}
		
		
	}

}
