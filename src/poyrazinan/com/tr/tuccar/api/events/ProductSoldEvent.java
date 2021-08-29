package poyrazinan.com.tr.tuccar.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import poyrazinan.com.tr.tuccar.Utils.Storage.ProductStorage;

public class ProductSoldEvent extends Event
{
	
	private final int buyAmount;
	private final double price;
	private final String whoSold;
	private final String whoBought;
	private final ProductStorage item;
	
	public ProductSoldEvent(int buyAmount, double price, String whoSold, String whoBought, ProductStorage item)
	{
		
		this.buyAmount = buyAmount;
		this.price = price;
		this.whoSold = whoSold;
		this.whoBought = whoBought;
		this.item = item;
	}
	
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
        return HANDLERS;
    }
	
	public int getBuyAmount() {
        return this.buyAmount;
    }
	public double getPrice() {
		return this.price;
	}
	public String getWhoSold() {
		return this.whoSold;
	}
	public String getWhoBought()
	{
		return this.whoBought;
	}
	public ProductStorage getItem()
	{
		return this.item;
	}

}
