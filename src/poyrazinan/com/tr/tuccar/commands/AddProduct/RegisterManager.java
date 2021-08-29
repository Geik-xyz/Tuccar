package poyrazinan.com.tr.tuccar.commands.AddProduct;

import org.bukkit.inventory.ItemStack;

import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.Storage.GuiUtils.ProductCounts;
import poyrazinan.com.tr.tuccar.database.DatabaseQueries;

public class RegisterManager {
	
	public static void updatePrice(ItemStack toCheck, double price)
	{
		
		try
		{
			
			String sellers = Tuccar.itemToObject.get(toCheck).getDataName();
			
			ProductCounts counts = null;
			
			if (Tuccar.productInfo.containsKey(sellers))
			{
				
				counts = Tuccar.productInfo.get(sellers);
				
				counts.setAmountOfSeller(counts.getAmountOfSeller()+1);
				
				if (price < counts.getMinPrice() || counts.getMinPrice() == 0)
					counts.setMinPrice(price);
				
				Tuccar.productInfo.replace(sellers, counts);
			}
			
			else Tuccar.productInfo.put(sellers, new ProductCounts(price, 1));
			
		}
		
		catch(NullPointerException e1) {}
		
	}
	
	public static void removeValues(String toCheck, String category, double price)
	{
		
		try
		{
			String sellers = toCheck;
			ProductCounts counts = null;
			if (Tuccar.productInfo.containsKey(sellers)) {
				counts = Tuccar.productInfo.get(sellers);
				counts.setAmountOfSeller(counts.getAmountOfSeller()-1);
				if (price < counts.getMinPrice()) counts.setMinPrice(DatabaseQueries.getMinimumPrice(sellers, category));
				Tuccar.productInfo.replace(sellers, counts);
			} else { 
				counts = new ProductCounts(0, 0);
				Tuccar.productInfo.put(sellers, counts);}
		} catch(NullPointerException e1) {}
		
	}

}
