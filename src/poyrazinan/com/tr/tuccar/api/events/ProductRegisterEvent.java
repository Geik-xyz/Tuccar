package poyrazinan.com.tr.tuccar.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ProductRegisterEvent extends Event {
	
	private final String product;
	private final String category;
	private final double price;
	
	public ProductRegisterEvent(String product, String category, double price) {
		this.product = product;
		this.category = category;
		this.price = price;
	}
	
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
        return HANDLERS;
    }
	
	public String getProduct() {
        return this.product;
    }
	public String getCategory() {
        return this.category;
    }
	public double getPrice() {
		return this.price;
	}

}
