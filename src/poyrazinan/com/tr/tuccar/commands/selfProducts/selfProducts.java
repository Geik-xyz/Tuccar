package poyrazinan.com.tr.tuccar.commands.selfProducts;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import poyrazinan.com.tr.tuccar.gui.PlayerProducts;

public class selfProducts {
	
	@SuppressWarnings({ })
	public static void gui(String[] args, Player player, CommandSender sender) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("ürünlerim")) {
				if (sender instanceof Player) {
						PlayerProducts.createGui(player, 1);
					
				}
			}
		}
		
	}

}
