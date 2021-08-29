package poyrazinan.com.tr.tuccar.commands.setNPC;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.getLang;

public class setNpc {
	
	public static void cmd(String[] args, CommandSender sender) {
		if (args[0].equalsIgnoreCase("belirle")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.hasPermission("tuccar.belirle")) {
					
					Bukkit.getScheduler().runTask(Tuccar.instance, () ->
					{
						
						NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(player);
						if (npc == null) {
							player.sendMessage(Tuccar.color("&4Seçili bir npc bulunamadı! /npc select"));
							return;
						}
						if (Tuccar.instance.getConfig().isSet("npc.id")) {
							Tuccar.instance.getConfig().set("npc.id", npc.getId());
						} else Tuccar.instance.getConfig().set("npc.id", npc.getId());
						Tuccar.instance.saveConfig();
						player.sendMessage(getLang.getText("Messages.setNpcSuccess"));
						
					});
				} else player.sendMessage(Tuccar.color(getLang.getText("Messages.dontHavePerm")));
			} else sender.sendMessage(Tuccar.color("&4Bunun için oyuncu olman gerek!"));
		}
	}


}
