package poyrazinan.com.tr.tuccar.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import poyrazinan.com.tr.tuccar.Utils.api.EventReason;

public class ProductRemoveEvent extends Event {
	
	private final String product;
	private final String category;
	private final double price;
	private final EventReason eventReason;
	
	public ProductRemoveEvent(EventReason eventReason, String product, String category, double price) {
		this.eventReason = eventReason;
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
	public EventReason getReason() {
		return this.eventReason;
	}

}
