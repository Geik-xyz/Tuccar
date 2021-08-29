package poyrazinan.com.tr.tuccar.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.Item;
import poyrazinan.com.tr.tuccar.Utils.getLang;
import poyrazinan.com.tr.tuccar.Utils.Storage.ConfirmationStorage;

public class ConfirmationGui {
	
	@SuppressWarnings("deprecation")
	public static void createGui(Player player, ConfirmationStorage storage, String dName)
	{
			
			Inventory gui = Bukkit.getServer().createInventory(player, 27, (Tuccar.color(getLang.getText("confirmationGui"))));
			
			Bukkit.getScheduler().runTaskAsynchronously(Tuccar.instance, () ->
			{
				
				int amount = storage.getBuyAmount();
				ItemStack confirmItem = new ItemStack(Material.getMaterial(storage.getProduct().getItemMaterial()));
				ItemMeta meta = confirmItem.getItemMeta();
				
				if (amount > confirmItem.getMaxStackSize())
					amount = confirmItem.getMaxStackSize();
				
				confirmItem.setAmount(amount);
				List<String> lore = new ArrayList<String>();
				
				if (getLang.isSet("Gui.cancelTemplate"))
					for (String text : getLang.getLore("Gui.cancelTemplate")) {
						if (storage.isSelfItem() && (text.contains("{seller}")
								|| text.contains("{price}")))
							continue;
						lore.add(Tuccar.color(text
								.replace("{seller}", storage.getProduct().getSeller())
								.replace("{category}", storage.getProduct().getItemCategory())
								.replace("{name}", storage.getProduct().getItemDisplayName())
								.replace("{amount}", String.valueOf(storage.getBuyAmount()))
								.replace("{price}", String.valueOf(roundDouble(storage.getProduct().getPrice()*storage.getBuyAmount(), 2)))
								));
					}
				
				else
				{
					lore.add("");
					lore.add(Tuccar.color(" &8▪ &7Kategori: &a" + storage.getProduct().getItemCategory()));
					lore.add(Tuccar.color(" &8▪ &7Adet: &a" + storage.getBuyAmount()));
					if (!storage.isSelfItem())
					{
						lore.add(Tuccar.color(" &8▪ &7Satıcı: &6" + storage.getProduct().getSeller()));
						lore.add(Tuccar.color(" &8▪ &7Fiyat: &e" + String.valueOf(roundDouble(storage.getProduct().getPrice()*storage.getBuyAmount(), 2))));
					}
					lore.add("");
				}
				
				meta.setDisplayName(Tuccar.color(dName));
				meta.setLore(lore);
				meta.addItemFlags(ItemFlag.values());
				confirmItem.setItemMeta(meta);
				
				gui.setItem(4, confirmItem);
				
				if (Tuccar.instance.getConfig().isSet("confirmation.fill.material")) {
					int damage = 0;
					String material = null;
					if (Tuccar.instance.getConfig().isSet("confirmation.fill.damage")) damage = Tuccar.instance.getConfig().getInt("confirmation.fill.damage");
					if (Tuccar.instance.getConfig().isSet("confirmation.fill.material") && Material.getMaterial(Tuccar.instance.getConfig().getString("confirmation.fill.material").toUpperCase()) != null) 
						material = Tuccar.instance.getConfig().getString("confirmation.fill.material").toUpperCase();
					if (damage == 0 && material != null) {
						ItemStack item = new ItemStack(Material.getMaterial(material), 1);
						
						for (int i = 0; i < 27; i++)
							if (i != 4)
								gui.setItem(i, item);
						
					} else if (damage != 0 && material != null){
						ItemStack item = new ItemStack(Material.getMaterial(material), 1);
						item.setDurability((short) damage);
						
						for (int i = 0; i < 27; i++)
							if (i != 4)
								gui.setItem(i, item);
						
					} else Bukkit.getServer().getConsoleSender().sendMessage(Tuccar.color("&4&lHATA &cConfig>confirmation>fill>material kısmında hata!"));
				}
				if (Material.getMaterial(Tuccar.instance.getConfig().getString("confirmation.yesItem.material").toUpperCase()) != null) {
					ItemStack yes = Item.defaultItem(Tuccar.instance.getConfig().getString("confirmation.yesItem.name"), Tuccar.instance.getConfig().getStringList("confirmation.yesItem.lore"), 
							Material.getMaterial(Tuccar.instance.getConfig().getString("confirmation.yesItem.material").toUpperCase()));
					if (Tuccar.instance.getConfig().isSet("confirmation.yesItem.damage")) yes.setDurability((short) Tuccar.instance.getConfig().getInt("confirmation.yesItem.damage"));
					gui.setItem(Tuccar.instance.getConfig().getInt("confirmation.yesItem.slot"), yes);
				} else Bukkit.getServer().getConsoleSender().sendMessage(Tuccar.color("&4&lHATA &cConfig>confirmation>yesItem>material kısmında hata!"));
				if (Material.getMaterial(Tuccar.instance.getConfig().getString("confirmation.noItem.material").toUpperCase()) != null) {
					ItemStack no = Item.defaultItem(Tuccar.instance.getConfig().getString("confirmation.noItem.name"), Tuccar.instance.getConfig().getStringList("confirmation.noItem.lore"), 
							Material.getMaterial(Tuccar.instance.getConfig().getString("confirmation.noItem.material").toUpperCase()));
					if (Tuccar.instance.getConfig().isSet("confirmation.noItem.damage")) no.setDurability((short) Tuccar.instance.getConfig().getInt("confirmation.noItem.damage"));
					gui.setItem(Tuccar.instance.getConfig().getInt("confirmation.noItem.slot"), no);
				} else Bukkit.getServer().getConsoleSender().sendMessage(Tuccar.color("&4&lHATA &cConfig>confirmation>noItem>material kısmında hata!")); 
				
			});
			Bukkit.getScheduler().runTask(Tuccar.instance, () -> {player.openInventory(gui);});
		
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

}
