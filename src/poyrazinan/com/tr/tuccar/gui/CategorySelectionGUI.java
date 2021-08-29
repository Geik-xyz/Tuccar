package poyrazinan.com.tr.tuccar.gui;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.Item;
import poyrazinan.com.tr.tuccar.Utils.getLang;
import poyrazinan.com.tr.tuccar.Utils.Storage.CategoryStorage;
import poyrazinan.com.tr.tuccar.Utils.Storage.CustomItemCache;

public class CategorySelectionGUI {
	
	public static void createGui(Player player) 
	{
			
			Inventory gui = Bukkit.getServer().createInventory(player, Tuccar.instance.getConfig().getInt("Settings.categorySize"), (Tuccar.color(getLang.getText("TuccarGui"))));
			
			Bukkit.getScheduler().runTaskAsynchronously(Tuccar.instance, () -> {
				
				ItemStack pageEmpty = Item.defaultItem(getLang.getText("Gui.empty.name"),
						getLang.getLore("Gui.empty.lore"),
						Material.getMaterial(getLang.getText("Gui.empty.material")));
				
				List<CategoryStorage> list = Tuccar.categoryStore;
				
				for (CategoryStorage s : list)
				{
					
					if (Material.getMaterial(s.getMaterial().toUpperCase()) != null)
					{
						
						ItemStack categoryItem = new ItemStack(Material.getMaterial(s.getMaterial().toUpperCase()), 1);
						
						ItemMeta categoryItemMeta = categoryItem.getItemMeta();
						
						List<String> lore = new ArrayList<String>();
						
						for (String d : s.getDisplayLore()) lore.add(Tuccar.color(d));
						
						categoryItemMeta.setLore(lore);
						
						categoryItemMeta.setDisplayName(Tuccar.color(s.getDisplayName()));
						
						categoryItem.setItemMeta(categoryItemMeta);
						
						gui.setItem(s.getSlot(), categoryItem);
						
					} 
					
					else continue;
					
				}

				if (list == null || list.size() == 0) gui.setItem(22, pageEmpty);
				
				if (!Tuccar.customItems.isEmpty())
				{
					
					for (Integer custom : Tuccar.customItems.keySet())
					{
						
						CustomItemCache cache = Tuccar.customItems.get(custom);
						
						gui.setItem(custom, cache.getItem());
						
					}
					
				}
				
				gui.setItem(Integer.valueOf(getLang.getText("Gui.help.slot")), Item.defaultItem(getLang.getText("Gui.help.name"),
						getLang.getLore("Gui.help.lore"),
						Material.getMaterial(getLang.getText("Gui.help.material"))));
				if (getLang.isSet("Gui.myProducts")) gui.setItem(Integer.valueOf(getLang.getText("Gui.myProducts.slot")), Item.defaultItem(getLang.getText("Gui.myProducts.name"),
						getLang.getLore("Gui.myProducts.lore"),
						Material.getMaterial(getLang.getText("Gui.myProducts.material"))));
				
			});
			
			Bukkit.getScheduler().runTask(Tuccar.instance, () -> {player.openInventory(gui);});
		
	}

}
