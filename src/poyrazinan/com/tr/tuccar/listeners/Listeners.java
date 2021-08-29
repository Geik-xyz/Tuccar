package poyrazinan.com.tr.tuccar.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.getLang;
import poyrazinan.com.tr.tuccar.Utils.Storage.ProductStorage;
import poyrazinan.com.tr.tuccar.Utils.Storage.GuiUtils.ProductCounts;
import poyrazinan.com.tr.tuccar.database.DatabaseQueries;
import poyrazinan.com.tr.tuccar.gui.CategorySelectionGUI;
import poyrazinan.com.tr.tuccar.listeners.Guis.ConfirmationGuiListener;
import poyrazinan.com.tr.tuccar.listeners.Guis.ItemSelectionListener;
import poyrazinan.com.tr.tuccar.listeners.Guis.PlayerProductsListener;

public class Listeners implements Listener {

	@SuppressWarnings("unused")
	private Tuccar plugin;
	public Listeners(Tuccar plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void inventoryCloseEvent(InventoryCloseEvent e)
	{
		if (e.getView().getTitle().contains(getLang.getText("confirmationGui")))
			if (ConfirmationGuiListener.confirmation.containsKey(e.getPlayer().getName()))
				ConfirmationGuiListener.confirmation.remove(e.getPlayer().getName());
			
	}
	
	@EventHandler
	public void leaveEvent(PlayerQuitEvent e) {
		if (ItemSelectionListener.filterPlayer.contains(e.getPlayer().getName())) ItemSelectionListener.filterPlayer.remove(e.getPlayer().getName());
	}
	
	@EventHandler
	public void chatEvent(AsyncPlayerChatEvent e) {
		if (PlayerProductsListener.rePrice.keySet().contains(e.getPlayer().getName())) {
			e.setCancelled(true);
			String message = e.getMessage().split(" ")[0];
			if (ItemSelectionListener.isNumeric(message)) {
				int amount = Integer.valueOf(message);
				if (amount >= Tuccar.instance.getConfig().getInt("Settings.minimumPrice")) {
					ProductStorage storage = PlayerProductsListener.rePrice.get(e.getPlayer().getName());
					storage.getID();
					Bukkit.getScheduler().runTaskAsynchronously(Tuccar.instance, new Runnable() {
						public void run() {
							String sellers = storage.getDataName();
							DatabaseQueries.setProductPrice(storage.getID(), amount);
							if (Tuccar.productInfo.containsKey(sellers)) {
								ProductCounts counts = Tuccar.productInfo.get(sellers);
								if (amount < counts.getMinPrice()) counts.setMinPrice(amount);
								else if (storage.getPrice() == counts.getMinPrice()) counts.setMinPrice(DatabaseQueries.getMinimumPrice(sellers, storage.getItemCategory()));
								Tuccar.productInfo.replace(sellers, counts);
							}
						}
					});
					e.getPlayer().sendMessage(getLang.getText("Messages.rePriceSuccess"));
				} else e.getPlayer().sendMessage(getLang.getText("Messages.priceLow").replace("{min}", String.valueOf(Tuccar.instance.getConfig().getInt("Settings.minimumPrice"))));
			} else {
				PlayerProductsListener.rePrice.remove(e.getPlayer().getName());
				e.getPlayer().sendMessage(getLang.getText("Messages.inputMustInteger"));}
		} else return;
	}
	
	@EventHandler
	public void onNpcRightClickEvent(NPCRightClickEvent e) {
		int npcid = e.getNPC().getId();
		if (Tuccar.instance.getConfig().isSet("npc.id")) {
			if (Tuccar.instance.getConfig().getInt("npc.id") == npcid) {
				if (Tuccar.instance.getConfig().getBoolean("Settings.world.worldWhitelist")) {
					List<String> allowedWorlds = Tuccar.instance.getConfig().getStringList("Settings.world.allowedWorlds");
					if (allowedWorlds.contains(e.getClicker().getWorld().getName())) {
						e.setCancelled(true);
						CategorySelectionGUI.createGui(e.getClicker());
					} else e.getClicker().sendMessage(getLang.getText("Messages.notInAllowedWorld"));
				} else CategorySelectionGUI.createGui(e.getClicker());
			}
		}
		
	}
	
}
