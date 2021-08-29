package poyrazinan.com.tr.tuccar.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.Item;
import poyrazinan.com.tr.tuccar.Utils.getLang;
import poyrazinan.com.tr.tuccar.Utils.Storage.ProductCategoryStorage;
import poyrazinan.com.tr.tuccar.Utils.Storage.GuiUtils.ProductCounts;
import poyrazinan.com.tr.tuccar.listeners.Guis.ItemSelectionListener;

@SuppressWarnings("deprecation")
public class ItemSelectionGui {

	public static void createGui(Player player, String category, int page)
	{
		
		Inventory gui = Bukkit.getServer().createInventory(player, 54,
				(Tuccar.color(getLang.getText("CategoryGui") + "&7 " + category + " #" + page)));

		Bukkit.getScheduler().runTaskAsynchronously(Tuccar.instance, () -> {

			ItemStack pageEmpty = Item.defaultItem(getLang.getText("Gui.empty.name"), getLang.getLore("Gui.empty.lore"),
					Material.getMaterial(getLang.getText("Gui.empty.material")));

			List<ProductCategoryStorage> list = Tuccar.productCategory.get(category);

			boolean filterStatus = ItemSelectionListener.filterPlayer.contains(player.getName());

			List<ProductCategoryStorage> listFiltered = new ArrayList<ProductCategoryStorage>();
			if (ItemSelectionListener.filterPlayer.contains(player.getName())) {
				for (ProductCategoryStorage pcg : list) {
					if (Tuccar.productInfo.get(pcg.getDataName()).getAmountOfSeller() != 0) {
						listFiltered.add(pcg);
					} else
						continue;
				}
			}

			ItemStack nextPage = Item.defaultItem(getLang.getText("Gui.nextPage.name"),
					getLang.getLore("Gui.nextPage.lore"),
					Material.getMaterial(getLang.getText("Gui.nextPage.material")));

			ItemStack previousPage = Item.defaultItem(getLang.getText("Gui.previousPage.name"),
					getLang.getLore("Gui.previousPage.lore"),
					Material.getMaterial(getLang.getText("Gui.previousPage.material")));

			ItemStack filter = Item.filterItem(getLang.getText("Gui.filter.name"), getLang.getLore("Gui.filter.lore"),
					Material.getMaterial(getLang.getText("Gui.filter.material")), player.getName());
			if (!filterStatus && (list == null || list.size() == 0))
				gui.setItem(22, pageEmpty);
			else if (filterStatus && (listFiltered == null || listFiltered.size() == 0))
				gui.setItem(22, pageEmpty);
			else {
				int itemCountToPage = list.size();
				if (filterStatus)
					itemCountToPage = listFiltered.size();
				int pageCount = 0;
				int startingValue = 28 * page;

				if (!filterStatus)
					filterCalculator(list.size(), pageCount, startingValue, list, category, player, gui);
				else
					filterCalculator(listFiltered.size(), pageCount, startingValue, listFiltered, category, player,
							gui);

				double maxPageCounter = (itemCountToPage / 28);
				int maxPage = (int) (maxPageCounter + 1);
				if (page == 1 && maxPage > 1)
					gui.setItem(50, nextPage);
				else if (page == maxPage && maxPage > 1)
					gui.setItem(48, previousPage);
				else if (page < maxPage && maxPage > 1) {
					gui.setItem(50, nextPage);
					gui.setItem(48, previousPage);
				}
			}

			gui.setItem(49, filter);
			gui.setItem(45,
					Item.defaultItem(getLang.getText("Gui.backToMenu.name"), getLang.getLore("Gui.backToMenu.lore"),
							Material.getMaterial(getLang.getText("Gui.backToMenu.material"))));

		});
		
		Bukkit.getScheduler().runTask(Tuccar.instance, () -> {player.openInventory(gui);});
		
	}

	public static void filterCalculator(int itemCount, int pageCount, int startingValue,
			List<ProductCategoryStorage> list, String category, Player player, Inventory gui) {
		for (int b = startingValue - 28; b <= itemCount - 1; b++) {
			if (pageCount > (itemCount - 1) - (startingValue - 28))
				break;
			if (!category.equalsIgnoreCase(list.get(b).getItemCategory()))
				continue;
			if (Material.getMaterial(list.get(b).getItemMaterial()) == null)
				continue;
			ItemStack guiItem = new ItemStack(Material.getMaterial(list.get(b).getItemMaterial()));
			if (guiItem.getType() == null || guiItem.getType() == Material.AIR)
				continue;
			List<String> newList = new ArrayList<String>();
			String nms = Tuccar.getNMSVersion();
			if (nms.contains("1_16") || nms.contains("1_15") || nms.contains("1_14") || nms.contains("1_13")) {
				Material mat = Material.getMaterial(list.get(b).getItemMaterial());
				if ((mat.equals(Material.SPLASH_POTION) || mat.equals(Material.POTION)
						|| mat.equals(Material.LINGERING_POTION))
						&& (Tuccar.instance.getConfig().isSet("Tuccar." + list.get(b).getItemCategory() + ".items."
								+ list.get(b).getDataName() + ".potionType"))) {
					PotionType type = null;
					try {
						type = PotionType
								.valueOf(Tuccar.instance.getConfig().getString("Tuccar." + list.get(b).getItemCategory()
										+ ".items." + list.get(b).getDataName() + ".potionType"));
						
					Potion potion = null;
					if (list.get(b).getItemDamage() == 1 || list.get(b).getItemDamage() == 2)
						potion = new Potion(type, list.get(b).getItemDamage());
					else {
						potion = new Potion(type, 1);
						potion.extend();
					}
					if (mat.equals(Material.SPLASH_POTION))
						potion.setSplash(true);
					guiItem = potion.toItemStack(1);
					if (mat.equals(Material.LINGERING_POTION))
						guiItem.setType(Material.LINGERING_POTION);
					
					} 
					
					catch (NullPointerException | IllegalArgumentException e1) {continue;}
					
				}
			}
			ItemMeta meta = guiItem.getItemMeta();
			
			meta.addItemFlags(ItemFlag.values());

			String sellers = list.get(b).getDataName();
			ProductCounts counts = Tuccar.productInfo.get(sellers);

			int amountOfSeller = 0;
			double minPrice = 0;
			if (counts != null) {
				if (counts.getAmountOfSeller() != 0) {
					amountOfSeller = counts.getAmountOfSeller();
					if (amountOfSeller <= guiItem.getMaxStackSize() && amountOfSeller > 0)
						guiItem.setAmount(amountOfSeller);
					else if (amountOfSeller > guiItem.getMaxStackSize())
						guiItem.setAmount(guiItem.getMaxStackSize());
				}
				if (counts.getMinPrice() != 0)
					minPrice = counts.getMinPrice();
			}

			if (getLang.isSet("Gui.itemCategoryTemplate"))
				for (String string : getLang.getLore("Gui.itemCategoryTemplate")) {
					newList.add(Tuccar.color(string.replace("{seller_amount}", String.valueOf(amountOfSeller))
							.replace("{min_price}", String.valueOf(minPrice))));
				}

			if (list.get(b).getItemDisplayName() != null)
				meta.setDisplayName(Tuccar.color(list.get(b).getItemDisplayName()));
			if (list.get(b).getDisplayLore() != null)
				for (String string : list.get(b).getDisplayLore()) {
					newList.add(string.replace("&", "§"));
				}
			meta.setLore(newList);

			if (list.get(b).getItemDamage() != 0)
				guiItem.setDurability((short) list.get(b).getItemDamage());

			List<String> enchantment = null;
			if (Tuccar.instance.getConfig().isSet("Tuccar." + category + ".items." + sellers + ".enchantment"))
				enchantment = Tuccar.instance.getConfig()
						.getStringList("Tuccar." + category + ".items." + sellers + ".enchantment");
			if (enchantment != null) {
				for (String ench : enchantment) {
					try {
						String[] enchant = ench.split(":");
						Enchantment enchObj = Enchantment.getByName(enchant[0].toUpperCase());
						if (enchObj == null)
							continue;
						else {
							if (guiItem.getType().name().equalsIgnoreCase("ENCHANTED_BOOK")) {
								EnchantmentStorageMeta AMeta = (EnchantmentStorageMeta) guiItem.getItemMeta();
								AMeta.addStoredEnchant(enchObj, Integer.parseInt(enchant[1]), true);
								if (list.get(b).getItemDisplayName() != null)
									AMeta.setDisplayName(Tuccar.color(list.get(b).getItemDisplayName()));
								AMeta.setLore(newList);
								guiItem.setItemMeta(AMeta);
							} else
								meta.addEnchant(enchObj, Integer.parseInt(enchant[1]), true);
						}
					} catch (NullPointerException e1) {
						continue;
					}
				}
			}
			if (!guiItem.getType().name().equalsIgnoreCase("ENCHANTED_BOOK"))
				guiItem.setItemMeta(meta);

			if (pageCount <= 6)
				gui.setItem(10 + pageCount, guiItem);
			else if (pageCount <= 13 && pageCount > 6)
				gui.setItem(12 + pageCount, guiItem);
			else if (pageCount <= 20 && pageCount > 13)
				gui.setItem(14 + pageCount, guiItem);
			else if (pageCount == 28)
				break;
			else
				gui.setItem(16 + pageCount, guiItem);
			pageCount = pageCount + 1;
		}
	}

}
