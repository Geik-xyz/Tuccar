package poyrazinan.com.tr.tuccar.commands.reload;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.getLang;

public class reloadCommand {
	
	public static void cmd(String[] args, CommandSender sender) {
		if (args.length == 1) {
			Bukkit.getScheduler().runTaskAsynchronously(Tuccar.instance, () ->
			{
				
				if (args[0].equalsIgnoreCase("reload")) {
					Tuccar.instance.reloadConfig();
					File langFile = new File("plugins/Tuccar/lang.yml");
					FileConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
					try {lang.save(langFile);} catch (IOException e) {e.printStackTrace();}
					Tuccar.categoryStore.clear();
					Tuccar.itemToObject.clear();
					Tuccar.productCategory.clear();
					Tuccar.productInfo.clear();
					Tuccar.customItems.clear();
					Tuccar.safeReload();
					sender.sendMessage(getLang.getText("Messages.reload"));
				}
				
			});
			
		}
		
	}

}
