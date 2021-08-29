package poyrazinan.com.tr.tuccar.commands.AddProduct;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.google.common.base.Preconditions;

import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.getLang;
import poyrazinan.com.tr.tuccar.Utils.Storage.ItemExistCheck;
import poyrazinan.com.tr.tuccar.Utils.api.RegisterType;
import poyrazinan.com.tr.tuccar.Utils.api.SellType;
import poyrazinan.com.tr.tuccar.database.DatabaseQueries;
import poyrazinan.com.tr.tuccar.listeners.Guis.ItemSelectionListener;

public class AddProductCommand {

	@SuppressWarnings({ "deprecation" })
	public static void addProductTuccar(String[] args, Player player)
	{
		List<ItemStack> items = new ArrayList<ItemStack>(Tuccar.itemToObject.keySet());
		
		if (player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR))
		{
			
			player.sendMessage(getLang.getText("Messages.couldntFindItem"));
			
			return;
			
		}
		
		ItemStack toCheck = new ItemStack(player.getItemInHand());
		
		toCheck.setAmount(1);
		
		Bukkit.getScheduler().runTaskAsynchronously(Tuccar.instance, () ->
		{
			
			if (!ItemSelectionListener.isNumeric(args[1]))
			{
				player.sendMessage(getLang.getText("Messages.inputMustInteger"));
				return;
			}
			
			if (!args[0].equalsIgnoreCase("ekle")) {
				return;
			}
			
			if (toCheck == null || (!items.contains(toCheck) && !toCheck.getType().equals(Material.ENCHANTED_BOOK)))
			{
				player.sendMessage(getLang.getText("Messages.couldntFindItem"));
				return;
			}
			
			final double price = Double.valueOf(args[1]);
			
			if (price < Tuccar.instance.getConfig().getInt("Settings.minimumPrice"))
			{
				player.sendMessage(getLang.getText("Messages.priceLow").replace("{min}",
						String.valueOf(Tuccar.instance.getConfig().getInt("Settings.minimumPrice"))));
				return;
			}
			
			RegisterType type;
			
			if (args.length == 3) {
							
				if (args[2].equalsIgnoreCase("hepsi"))
					type = sellModifier(player, toCheck, price, SellType.ALL, 0);
				
				else if (args[2].equalsIgnoreCase("el"))
					type = sellModifier(player, toCheck, price, SellType.HAND, 0);
					
				else
				{
					
					if (!ItemSelectionListener.isNumeric(args[2]) || Integer.valueOf(args[2]) < 1)
					{
						player.sendMessage(getLang.getText("Messages.inputMustInteger"));
						return;
					}
					
					final int amount = Integer.valueOf(args[2]);
					
					if (getAmount(player, player.getItemInHand()) < amount)
					{
						player.sendMessage(getLang.getText("Messages.notEnoughItem"));
						return;
					}
					
					type = sellModifier(player, toCheck, price, SellType.CUSTOM, amount);
					
				}
							
			}
			
			else if (args.length == 2)
				type = sellModifier(player, toCheck, price, SellType.HAND, 0);
			
			else return;
			
			if (type != null && !type.equals(RegisterType.STOCK))
			{
				RegisterManager.updatePrice(toCheck, price);
			}
			/*
			Bukkit.getScheduler().runTask(Tuccar.instance, () ->
			{
				
				ProductRegisterEvent productRegister = new ProductRegisterEvent(
						Tuccar.itemToObject.get(toCheck).getDataName(),
						Tuccar.itemToObject.get(toCheck).getItemCategory(), price);
				
				Bukkit.getPluginManager().callEvent(productRegister);
				
			});
			*/
			
		});

	}
	
	@SuppressWarnings("deprecation")
	public static RegisterType sellModifier(Player player, ItemStack toCheck, double price, SellType type, int count)
	{
		
		int amount = player.getItemInHand().getAmount();
		if (type.equals(SellType.ALL))
			amount = getAmount(player, player.getItemInHand());
		else if (type.equals(SellType.CUSTOM)) 
			amount = count;
		
		final ItemExistCheck item = DatabaseQueries.getPlayerItem(Tuccar.itemToObject.get(toCheck).getDataName(),
					Tuccar.itemToObject.get(toCheck).getItemCategory(), player.getName());
		
		RegisterType registerType;
		
		try
		{
			
			if (item == null)
			{
				DatabaseQueries.registerProductToTable(player.getName(),
						Tuccar.itemToObject.get(toCheck).getItemCategory(),
						Tuccar.itemToObject.get(toCheck).getDataName(), amount, price);
				registerType = RegisterType.PRODUCT;
			}
				
			else
			{
				DatabaseQueries.addProductCount(item.getID(), item.getStock() + amount);
				registerType = RegisterType.STOCK;
			}
			
			if (type.equals(SellType.HAND))
				player.getInventory().removeItem(player.getItemInHand());
			
			else if (type.equals(SellType.ALL))
				removeItems(player.getInventory(), player.getItemInHand(), amount);
			
			else if (type.equals(SellType.CUSTOM))
			{
				ItemStack toRemove = new ItemStack(player.getItemInHand());
				toRemove.setAmount(amount);
				player.getInventory().removeItem(toRemove);	
			}
			
			player.sendMessage(getLang.getText("Messages.listItem"));
			
			return registerType;
			
		}
		
		catch (NullPointerException e1)
		{
			player.sendMessage(getLang.getText("Messages.couldntFindItem"));
			return null;
		}
		
	}

	public static int getAmount(Player arg0, ItemStack arg1)
	{
		
		if (arg1 == null)
			return 0;
		
		int amount = 0;
		
		for (int i = 0; i < 36; i++)
		{
			
			ItemStack slot = arg0.getInventory().getItem(i);
			
			if (slot == null || !slot.isSimilar(arg1))
				continue;
			
			amount += slot.getAmount();
			
		}
		return amount;
		
	}

	public static void removeItems(Inventory inventory, ItemStack item, int toRemove)
	{
		
		Preconditions.checkNotNull(inventory);
		
		Preconditions.checkNotNull(item);
		
		Preconditions.checkArgument(toRemove > 0);
		
		for (int i = 0; i < inventory.getSize(); i++)
		{
			
			ItemStack loopItem = inventory.getItem(i);
			
			if (loopItem == null || !item.isSimilar(loopItem))
			{
				continue;
			}
			
			if (toRemove <= 0)
			{
				return;
			}
			
			if (toRemove < loopItem.getAmount())
			{
				loopItem.setAmount(loopItem.getAmount() - toRemove);
				return;
			}
			
			inventory.clear(i);
			
			toRemove -= loopItem.getAmount();
		}
		
	}

}
