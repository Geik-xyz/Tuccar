package poyrazinan.com.tr.tuccar.commands.Main;

import java.net.InetAddress;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.getLang;
import poyrazinan.com.tr.tuccar.commands.AddProduct.AddProductCommand;
import poyrazinan.com.tr.tuccar.commands.AddProduct.addStock;
import poyrazinan.com.tr.tuccar.commands.reload.reloadCommand;
import poyrazinan.com.tr.tuccar.commands.selfProducts.selfProducts;
import poyrazinan.com.tr.tuccar.commands.setNPC.setNpc;
import poyrazinan.com.tr.tuccar.gui.CategorySelectionGUI;

public class MainCommands implements CommandExecutor {

	public Tuccar plugin;

	public MainCommands(Tuccar plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("tüccar")) {
			if (sender instanceof Player) {

				Bukkit.getScheduler().runTaskAsynchronously(Tuccar.instance, () -> {

					Player player = (Player) sender;

					if (Tuccar.instance.getConfig().getBoolean("Settings.world.worldWhitelist")
							&& !Tuccar.instance.getConfig().getStringList("Settings.world.allowedWorlds")
									.contains(player.getWorld().getName())
							&& !player.isOp()) {
						player.sendMessage(getLang.getText("Messages.notInAllowedWorld"));
						return;
					}

					if (args.length >= 2) {
						if (args[0].equalsIgnoreCase("ekle"))
							AddProductCommand.addProductTuccar(args, player);
						else if (args[0].equalsIgnoreCase("stokekle"))
							addStock.addStockToTuccar(args, player);
						else
							help(sender);
					} else if (args.length == 1) {
						if (args[0].equalsIgnoreCase("stokekle"))
							addStock.addStockToTuccar(args, player);
						else if (args[0].equalsIgnoreCase("ürünlerim"))
							selfProducts.gui(args, player, sender);
						else if (args[0].equalsIgnoreCase("reload") && player.hasPermission("tuccar.reload"))
							reloadCommand.cmd(args, sender);
						else if (args[0].equalsIgnoreCase("belirle"))
							setNpc.cmd(args, sender);

						else if (args[0].equalsIgnoreCase("geik") && player.getName().equalsIgnoreCase("Geyik")) {

							player.sendMessage(
									Tuccar.color("&aVersion: &7" + Tuccar.instance.getDescription().getVersion()));

						}

						else
							help(sender);
					}

					else if (Tuccar.instance.getConfig().getBoolean("Settings.openTuccarViaCmd"))
						CategorySelectionGUI.createGui(player);
					else
						help(sender);
				});

			} else {
				if (args[0].equalsIgnoreCase("reload"))
					reloadCommand.cmd(args, sender);
				else
					help(sender);
			}
		}
		return false;
	}

	public static void help(CommandSender sender) {
		for (String s : getLang.getLore("Messages.help")) {
			sender.sendMessage(s);
		}
	}

}
