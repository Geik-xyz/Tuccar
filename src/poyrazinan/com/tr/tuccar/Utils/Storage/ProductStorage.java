package poyrazinan.com.tr.tuccar.Utils.Storage;

import java.util.HashMap;
import java.util.List;

import org.bukkit.enchantments.Enchantment;

public class ProductStorage {

	String dataName;
	int id;
	String seller;
	String itemCategory;
	String itemMaterial;
	String productName;
	String productDisplayName;
	List<String> productDisplayLore;
	List<String> productLore;
	List<String> enchs;
	int productDamage;
	double price;
	int stock;
	
	public ProductStorage(String dataName, String itemMaterial, String category, String productName, String productDisplayName, List<String> productLore, 
			List<String> productDisplayLore, int productDamage, int stock, double price, String seller, int id, List<String> enchs) {
		this.dataName = dataName;
		this.id = id;
		this.itemCategory = category;
		this.itemMaterial = itemMaterial;
		this.productName = productName;
		this.productDisplayName = productDisplayName;
		this.productLore = productLore;
		this.productDisplayLore = productDisplayLore;
		this.productDamage = productDamage;
		this.stock = stock;
		this.price = price;
		this.seller = seller;
		this.enchs = enchs;
	}
	
	public String getDataName() {
		return dataName;
	}
	public String getSeller() {
		return seller;
	}
	public int getStock() {
		return stock;
	}
	public double getPrice() {
		return price;
	}
	public int getID() {
		return id;
	}
	
	public String getItemCategory() {
		return itemCategory;
	}
	public String getItemMaterial() {
		return itemMaterial;
	}
	public String getItemName() {
		return productName;
	}
	public String getItemDisplayName() {
		return productDisplayName;
	}
	public List<String> getDisplayLore() {
		return productDisplayLore;
	}
	public List<String> getLore() {
		return productLore;
	}
	public int getItemDamage() {
		return productDamage;
	}
	public HashMap<Enchantment, Integer> getEnchants() {
		HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
		if (enchs != null) {
			for (String ench : enchs) {
				try{
					String[] enchant = ench.split(":");
					@SuppressWarnings("deprecation")
					Enchantment enchObj = Enchantment.getByName(enchant[0].toUpperCase());
					if (enchObj == null) continue;
					else enchantments.put(enchObj, Integer.parseInt(enchant[1]));
				} catch(NullPointerException e1) {continue;}
			}
		}
		return enchantments;
	}
	public boolean hasEnchant() {
		if (enchs != null) return true;
		else return false;
	}
	
}
