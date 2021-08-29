package poyrazinan.com.tr.tuccar.Utils.Storage;

import java.util.List;

public class CategoryStorage {

	String categoryDataName;
	int slot;
	String displayName;
	List<String> displayLore;
	String material;
	
	public CategoryStorage(String categoryDataName, int slot, String displayName, List<String> displayLore, String material) {
		this.categoryDataName = categoryDataName;
		this.slot = slot;
		this.displayName = displayName;
		this.displayLore = displayLore;
		this.material = material;
	}
	
	public String getCategoryDataName() {
		return categoryDataName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public List<String> getDisplayLore() {
		return displayLore;
	}
	public int getSlot() {
		return slot;
	}
	public String getMaterial() {
		return material;
	}
	
}
