package poyrazinan.com.tr.tuccar.listeners.Guis;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.getLang;
import poyrazinan.com.tr.tuccar.Utils.Storage.ProductCategoryStorage;
import poyrazinan.com.tr.tuccar.gui.CategorySelectionGUI;
import poyrazinan.com.tr.tuccar.gui.ItemGui;
import poyrazinan.com.tr.tuccar.gui.ItemSelectionGui;

public class ItemSelectionListener implements Listener {

	@SuppressWarnings("unused")
	private Tuccar plugin;

	public ItemSelectionListener(Tuccar plugin) {
		this.plugin = plugin;
	}

	public static List<String> filterPlayer = new ArrayList<String>();

	@EventHandler
	public void onInventory(InventoryClickEvent e) {
		if (e.getView().getTitle().contains(Tuccar.color(getLang.getText("CategoryGui")))) {
			e.setCancelled(true);
			if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.CHEST) {

					Player player = (Player) e.getWhoClicked();
					if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType().equals(Material.AIR)))
						return;

					String[] sayfa = e.getView().getTitle().split(" ");
					String page = sayfa[sayfa.length - 1].substring(1, sayfa[sayfa.length - 1].length());

					String category = sayfa[sayfa.length - 2];

					if (e.getSlot() == 49) {
						if (filterPlayer.contains(player.getName()))
							filterPlayer.remove(player.getName());
						else
							filterPlayer.add(player.getName());
						player.closeInventory();
						ItemSelectionGui.createGui(player, category, 1);
					}

					if (!isNumeric(page))
						return;
					else {

						List<ProductCategoryStorage> item = Tuccar.productCategory.get(category);

						boolean filterStatus = ItemSelectionListener.filterPlayer.contains(player.getName());

						List<ProductCategoryStorage> listFiltered = new ArrayList<ProductCategoryStorage>();
						
						if (filterStatus)
						{
							
							for (ProductCategoryStorage pcg : Tuccar.productCategory.get(category))
							{
								
								if (Tuccar.productInfo.get(pcg.getDataName()).getAmountOfSeller() != 0)
									listFiltered.add(pcg);
								
								else
									continue;
								
							}
							
							item = listFiltered;
							
						}

						if (e.getSlot() == 45) {
							player.closeInventory();
							CategorySelectionGUI.createGui(player);
						}
						
						else if (item.size() != 0) {
							int tiklama = 0;
							if (e.getSlot() == 53)
								player.closeInventory();
							else if (e.getSlot() >= 10 && e.getSlot() <= 43) {
								if (e.getSlot() >= 10 && e.getSlot() <= 16)
									tiklama = e.getSlot() - 10;
								else if (e.getSlot() >= 19 && e.getSlot() <= 25)
									tiklama = e.getSlot() - 12; // 7
								else if (e.getSlot() >= 28 && e.getSlot() <= 34)
									tiklama = e.getSlot() - 14; // 14
								else if (e.getSlot() >= 37 && e.getSlot() <= 43)
									tiklama = e.getSlot() - 16; // 21
								else
									e.setCancelled(true);

								int tiklamaCalculator = ((Integer.valueOf(page) * 28) - 28) + tiklama;

								player.closeInventory();
								ItemGui.createGui(player, item.get(tiklamaCalculator).getDataName(), category, 1);

							} else if (e.getSlot() == 48 && e.getCurrentItem().getType() == Material
									.getMaterial(getLang.getText("Gui.previousPage.material")))
								ItemSelectionGui.createGui(player, category, Integer.valueOf(page) - 1);
							else if (e.getSlot() == 50 && e.getCurrentItem().getType() == Material
									.getMaterial(getLang.getText("Gui.nextPage.material")))
								ItemSelectionGui.createGui(player, category, Integer.valueOf(page) + 1);
						}
					}
			}
		}
	}

	public static boolean isNumeric(String strNum) {
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}
