package poyrazinan.com.tr.tuccar.commands.AddProduct;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.getLang;
import poyrazinan.com.tr.tuccar.Utils.Storage.ItemExistCheck;
import poyrazinan.com.tr.tuccar.database.DatabaseQueries;
import poyrazinan.com.tr.tuccar.listeners.Guis.ItemSelectionListener;

public class addStock {

	@SuppressWarnings({ "deprecation" })
	public static void addStockToTuccar(String[] args, Player player)
	{
		
		List<ItemStack> items = new ArrayList<ItemStack>(Tuccar.itemToObject.keySet());
		
		if (player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR))
		{
			
			player.sendMessage(getLang.getText("Messages.couldntFindItem"));
			
			return;
			
		}
		
		ItemStack toCheck = new ItemStack(player.getItemInHand());
		
		for (Enchantment s : toCheck.getItemMeta().getEnchants().keySet()) {
			toCheck.getItemMeta().removeEnchant(s);
		}
		
		toCheck.setAmount(1);
		
		Bukkit.getScheduler().runTaskAsynchronously(Tuccar.instance, () ->
		{
			
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("stokekle")) {
					if (toCheck != null && items.contains(toCheck)) {
						if (args[1].equalsIgnoreCase("hepsi")) {
							int amount = getAmount(player, player.getItemInHand());
							ItemExistCheck item = DatabaseQueries.getPlayerItem(
									Tuccar.itemToObject.get(toCheck).getDataName(),
									Tuccar.itemToObject.get(toCheck).getItemCategory(), player.getName());
							if (item == null) {

								player.sendMessage(getLang.getText("Messages.couldntFindItem"));

								return;

							} else
								DatabaseQueries.addProductCount(item.getID(), item.getStock() + amount);
							removeItems(player.getInventory(), player.getItemInHand(), amount);
							player.sendMessage(getLang.getText("Messages.listItem"));
						} else if (args[1].equalsIgnoreCase("el")) {
							int amount = player.getItemInHand().getAmount();
							ItemExistCheck item = DatabaseQueries.getPlayerItem(
									Tuccar.itemToObject.get(toCheck).getDataName(),
									Tuccar.itemToObject.get(toCheck).getItemCategory(), player.getName());
							if (item == null) {

								player.sendMessage(getLang.getText("Messages.couldntFindItem"));

								return;

							} else
								DatabaseQueries.addProductCount(item.getID(), item.getStock() + amount);
							player.getInventory().removeItem(player.getItemInHand());
							player.sendMessage(getLang.getText("Messages.listItem"));
						} else {
							if (ItemSelectionListener.isNumeric(args[1]) && Integer.valueOf(args[1]) >= 1) {
								int amount = Integer.valueOf(args[1]);
								if (getAmount(player, player.getItemInHand()) >= amount) {
									ItemExistCheck item = DatabaseQueries.getPlayerItem(
											Tuccar.itemToObject.get(toCheck).getDataName(),
											Tuccar.itemToObject.get(toCheck).getItemCategory(), player.getName());
									if (item == null) {

										player.sendMessage(getLang.getText("Messages.couldntFindItem"));

										return;

									} else
										DatabaseQueries.addProductCount(item.getID(), item.getStock() + amount);
									ItemStack toRemove = new ItemStack(player.getItemInHand());
									toRemove.setAmount(amount);
									player.getInventory().removeItem(toRemove);
									player.sendMessage(getLang.getText("Messages.listItem"));
								} else
									player.sendMessage(getLang.getText("Messages.notEnoughItem"));
							} else
								player.sendMessage(getLang.getText("Messages.inputMustInteger"));
						}
					} else
						player.sendMessage(getLang.getText("Messages.couldntFindItem"));
				}
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("stokekle")) {
					if (toCheck != null && items.contains(toCheck)) {
						int amount = player.getItemInHand().getAmount();
						ItemExistCheck item = DatabaseQueries.getPlayerItem(Tuccar.itemToObject.get(toCheck).getDataName(),
								Tuccar.itemToObject.get(toCheck).getItemCategory(), player.getName());
						if (item == null) {

							player.sendMessage(getLang.getText("Messages.couldntFindItem"));

							return;

						} else
							DatabaseQueries.addProductCount(item.getID(), item.getStock() + amount);
						player.getInventory().removeItem(player.getItemInHand());
						player.sendMessage(getLang.getText("Messages.listItem"));
					} else
						player.sendMessage(getLang.getText("Messages.couldntFindItem"));
				}
			}
			
		});

	}

	public static int getAmount(Player arg0, ItemStack arg1) {
		if (arg1 == null)
			return 0;
		int amount = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack slot = arg0.getInventory().getItem(i);
			if (slot == null || !slot.isSimilar(arg1))
				continue;
			amount += slot.getAmount();
		}
		return amount;
	}

	public static void removeItems(Inventory inventory, ItemStack item, int toRemove) {
		Preconditions.checkNotNull(inventory);
		Preconditions.checkNotNull(item);
		Preconditions.checkArgument(toRemove > 0);
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack loopItem = inventory.getItem(i);
			if (loopItem == null || !item.isSimilar(loopItem)) {
				continue;
			}
			if (toRemove <= 0) {
				return;
			}
			if (toRemove < loopItem.getAmount()) {
				loopItem.setAmount(loopItem.getAmount() - toRemove);
				return;
			}
			inventory.clear(i);
			toRemove -= loopItem.getAmount();
		}
	}

}
