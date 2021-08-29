package poyrazinan.com.tr.tuccar.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import poyrazinan.com.tr.tuccar.listeners.Guis.ItemSelectionListener;

public class Item {
	
	/*public static ItemStack guiItemModifier(Player player, Material mat) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
		List<String> newList = new ArrayList<String>();
    	for (String string : getLang.getLore("Gui.farmItemTemplate.lore")) {
    		newList.add(string.replace("&", "§")
    				.replace("{stock}", String.valueOf(DatabaseQueries.getProductCount(SkyblockUtils.getIslandID(player.getLocation()), mat.name())))
    				.replace("{maxstock}", String.valueOf(Main.instance.getConfig().getInt("FarmerLevels." + farmerLevel + ".Capacity")))
    				.replace("{level}", String.valueOf(farmerLevel+1))
    				.replace("{taxRate}", String.valueOf(Main.instance.getConfig().getInt("tax.taxRate"))));}
    	ItemStack item = new ItemStack(mat);
    	ItemMeta meta = item.getItemMeta();
    	meta.setLore(newList);
    	item.setItemMeta(meta);
    	return item;
	}*/
	
	public static ItemStack defaultItem(String name, List<String> lore, Material itemMaterial) {
		List<String> newList = new ArrayList<String>();
    	for (String string : lore) {
    		newList.add(string.replace("&", "§"));}
    	ItemStack item = new ItemStack(itemMaterial);
    	ItemMeta meta = item.getItemMeta();
    	meta.setDisplayName(name.replace("&", "§"));
    	meta.setLore(newList);
    	item.setItemMeta(meta);
    	return item;
	}
	
	public static ItemStack filterItem(String name, List<String> lore, Material itemMaterial, String playerName) {
		List<String> newList = new ArrayList<String>();
		String filter_status = "Kapalı";
		if (ItemSelectionListener.filterPlayer.contains(playerName)) filter_status = "Açık";
    	for (String string : lore) {
    		newList.add(string.replace("&", "§").replace("{filter_status}", filter_status)) ;}
    	ItemStack item = new ItemStack(itemMaterial);
    	ItemMeta meta = item.getItemMeta();
    	meta.setDisplayName(name.replace("&", "§"));
    	meta.setLore(newList);
    	item.setItemMeta(meta);
    	return item;
	}

}
