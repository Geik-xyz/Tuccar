package poyrazinan.com.tr.tuccar.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import poyrazinan.com.tr.tuccar.Tuccar;

public class getLang {
	
	public static boolean getBoolean(String title) {
		File langFile = new File("plugins/Tuccar/lang.yml");
		FileConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
		return lang.getBoolean(title);
	}
	
	public static int getInt(String title) {
		File langFile = new File("plugins/Tuccar/lang.yml");
		FileConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
		return lang.getInt(title);
	}
	
	public static String getText(String title) {
		File langFile = new File("plugins/Tuccar/lang.yml");
		FileConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
		return Tuccar.color(lang.getString(title));
	}
	public static List<String> getLore(String title) {
		File langFile = new File("plugins/Tuccar/lang.yml");
		FileConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
		List<String> list = new ArrayList<String>();
		for (String s : lang.getStringList(title)) {
			list.add(Tuccar.color(s));
		}
		return list;
	}
	public static boolean isSet(String path) {
		File langFile = new File("plugins/Tuccar/lang.yml");
		FileConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
		return lang.isSet(path);
	}
	
	public static void FileChecker(String fileName) {
		File c = new File("plugins/Tuccar/" + fileName + ".yml");
		if (!c.exists()) {
			Tuccar.instance.saveResource(fileName + ".yml", false);}
	}
	
	public static Set<String> getConfigurationSection(String file, String title)
	{
		
		File langFile = new File("plugins/" + Tuccar.instance.getDescription().getName() + "/" + file + ".yml");
		
		FileConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
		
		Set<String> list = lang.getConfigurationSection(title).getKeys(false);
		
		return list;
		
	}

}
