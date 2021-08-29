package poyrazinan.com.tr.tuccar.Utils.Storage.GuiUtils;

public class ProductCounts {
	
	double minPrice;
	int amount;
	
	public ProductCounts(double minPrice, int amount) {
		this.minPrice = minPrice;
		this.amount = amount;
	}
	public double getMinPrice() {
		return minPrice;
	}
	public int getAmountOfSeller() {
		return amount;
	}
	
	public void setMinPrice(double minPrice) {
		this.minPrice = minPrice;
	}
	public void setAmountOfSeller(int amount) {
		this.amount = amount;
	}
}
