package poyrazinan.com.tr.tuccar.Utils.Storage;

public class ItemExistCheck {
	
	int id;
	String username;
	int stock;
	double price;
	
	public ItemExistCheck(int id, String username, int stock, double price) {
		this.id = id;
		this.username = username;
		this.stock = stock;
		this.price = price;
	}
	
	public int getID() {
		return id;
	}
	public String getUserName() {
		return username;
	}
	public int getStock() {
		return stock;
	}
	public double getPrice() {
		return price;
	}
	

}
