package poyrazinan.com.tr.tuccar.Utils.Storage;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class CustomItemCache
{
	
	ItemStack item;
	
	List<String> commands;
	
	public CustomItemCache(ItemStack item, List<String> commands)
	{
		
		this.item = item;
		
		this.commands = commands;
		
	}
	
	public ItemStack getItem()
	{
		return item;
	}
	
	public List<String> getCommands()
	{
		return commands;
	}

}
