package poyrazinan.com.tr.tuccar.Utils.Storage;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class ProductCategoryStorage {

	String dataName;
	String itemCategory;
	String itemMaterial;
	String productName;
	String productDisplayName;
	List<String> productDisplayLore;
	int productDamage;
	boolean isCustom;
	ItemStack customItem;
	
	public ProductCategoryStorage(String dataName, String itemMaterial, String category, String productName, String productDisplayName, List<String> productDisplayLore, int productDamage
			) {
		this.dataName = dataName;
		this.itemCategory = category;
		this.itemMaterial = itemMaterial;
		this.productName = productName;
		this.productDisplayName = productDisplayName;
		this.productDisplayLore = productDisplayLore;
		this.productDamage = productDamage;
	//	this.isCustom = isCustom;
	//	this.customItem = customItem;
	}
	
	public String getDataName() {
		return dataName;
	}
	public String getItemCategory() {
		return itemCategory;
	}
	public String getItemMaterial() {
		return itemMaterial.toUpperCase();
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
	public int getItemDamage() {
		return productDamage;
	}
	public boolean isCustomItem() {
		return isCustom;
	}
	
	public ItemStack getCustomItem() {
		return customItem;
	}
	
}
