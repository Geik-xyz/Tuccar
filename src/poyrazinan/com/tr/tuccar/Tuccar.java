package poyrazinan.com.tr.tuccar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import com.jojodmo.customitems.api.CustomItemsAPI;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import poyrazinan.com.tr.tuccar.Utils.Item;
import poyrazinan.com.tr.tuccar.Utils.getLang;
import poyrazinan.com.tr.tuccar.Utils.Storage.CategoryStorage;
import poyrazinan.com.tr.tuccar.Utils.Storage.CustomItemCache;
import poyrazinan.com.tr.tuccar.Utils.Storage.ProductCategoryStorage;
import poyrazinan.com.tr.tuccar.Utils.Storage.GuiUtils.ProductCounts;
import poyrazinan.com.tr.tuccar.commands.CommandRegister;
import poyrazinan.com.tr.tuccar.database.ConnectionPool;
import poyrazinan.com.tr.tuccar.database.DatabaseQueries;
import poyrazinan.com.tr.tuccar.listeners.ListenerRegister;

@SuppressWarnings("deprecation")
public class Tuccar extends JavaPlugin {

	public static Tuccar instance;
	public static Economy econ = null;

	public static List<CategoryStorage> categoryStore = new ArrayList<CategoryStorage>();
	public static HashMap<String, List<ProductCategoryStorage>> productCategory = new HashMap<String, List<ProductCategoryStorage>>();
	public static HashMap<ItemStack, ProductCategoryStorage> itemToObject = new HashMap<ItemStack, ProductCategoryStorage>();
	public static HashMap<String, ProductCounts> productInfo = new HashMap<String, ProductCounts>();

	public static HashMap<Integer, CustomItemCache> customItems = new HashMap<Integer, CustomItemCache>();

	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		getLang.FileChecker("lang");
		setupEconomy();
		ConnectionPool.initsqlite();
		DatabaseQueries.createTable();
		new CommandRegister();
		new ListenerRegister();
		safeReload();
		MetricLoader();
	}

	@SuppressWarnings({})
	public static void safeReload() {
		Bukkit.getScheduler().runTaskAsynchronously(Tuccar.instance, new Runnable() {
			public void run() {

				String nms = getNMSVersion();
				if (nms.contains("1_16") || nms.contains("1_15") || nms.contains("1_14") || nms.contains("1_13")) {

					Bukkit.getConsoleSender()
							.sendMessage(Tuccar.color(" &6Tüccar &8▸ &aEğer yüksek sürüm configini çıkarmadıysanız."));

					Bukkit.getConsoleSender().sendMessage(
							Tuccar.color(" &6Tüccar &8▸ &aEklentiyi winrar ile açıp 1.16-config.yml'yi dizine atıp,"));

					Bukkit.getConsoleSender()
							.sendMessage(Tuccar.color(" &6Tüccar &8▸ &aAdını config.yml olarak değiştirin."));

				}

				Set<String> categories = Tuccar.instance.getConfig().getConfigurationSection("Tuccar").getKeys(false);
				for (String s : categories) {
					categoryStore
							.add(new CategoryStorage(s, Tuccar.instance.getConfig().getInt("Tuccar." + s + ".slot"),
									Tuccar.instance.getConfig().getString("Tuccar." + s + ".displayName"),
									Tuccar.instance.getConfig().getStringList("Tuccar." + s + ".displayLore"),
									Tuccar.instance.getConfig().getString("Tuccar." + s + ".material")));
					Set<String> products = Tuccar.instance.getConfig().getConfigurationSection("Tuccar." + s + ".items")
							.getKeys(false);
					List<ProductCategoryStorage> allStore = new ArrayList<ProductCategoryStorage>();
					for (String d : products) {

						try {

							final boolean hasCUI = Bukkit.getPluginManager().getPlugin("CustomItems") != null;

							if (hasCUI && Tuccar.instance.getConfig()
									.isSet("Tuccar." + s + ".items." + d + ".customitem")) {

								String customItem = Tuccar.instance.getConfig()
										.getString("Tuccar." + s + ".items." + d + ".customitem");

								ItemStack CItem = CustomItemsAPI.getCustomItem(customItem);

								ProductCategoryStorage productStorage = new ProductCategoryStorage(d,
										CItem.getType().name().toLowerCase(), s, CItem.getItemMeta().getDisplayName(),
										CItem.getItemMeta().getDisplayName(), CItem.getItemMeta().getLore(),
										(int) CItem.getDurability());

								itemToObject.put(CItem, productStorage);
								productInfo.put(d, DatabaseQueries.getProductInfos(d, s));
								allStore.add(productStorage);

								continue;

							}

							String itemMaterial = Tuccar.instance.getConfig()
									.getString("Tuccar." + s + ".items." + d + ".material");
							if (Material.getMaterial(itemMaterial.toUpperCase()) != null) {
								int damage = 0;
								String displayName = null;
								List<String> displayLore = null;
								List<String> enchantment = null;
								String itemName = null;
								if (Tuccar.instance.getConfig().isSet("Tuccar." + s + ".items." + d + ".itemName"))
									itemName = Tuccar.instance.getConfig()
											.getString("Tuccar." + s + ".items." + d + ".itemName");
								if (Tuccar.instance.getConfig().isSet("Tuccar." + s + ".items." + d + ".displayLore"))
									displayLore = Tuccar.instance.getConfig()
											.getStringList("Tuccar." + s + ".items." + d + ".displayLore");
								if (Tuccar.instance.getConfig().isSet("Tuccar." + s + ".items." + d + ".displayName"))
									displayName = Tuccar.instance.getConfig()
											.getString("Tuccar." + s + ".items." + d + ".displayName");
								if (Tuccar.instance.getConfig().isSet("Tuccar." + s + ".items." + d + ".damage"))
									damage = Tuccar.instance.getConfig()
											.getInt("Tuccar." + s + ".items." + d + ".damage");
								if (Tuccar.instance.getConfig().isSet("Tuccar." + s + ".items." + d + ".enchantment"))
									enchantment = Tuccar.instance.getConfig()
											.getStringList("Tuccar." + s + ".items." + d + ".enchantment");
								ProductCategoryStorage productStorage = new ProductCategoryStorage(d, itemMaterial, s,
										itemName, displayName, displayLore, damage);
								allStore.add(productStorage);
								ItemStack item = new ItemStack(Material.getMaterial(itemMaterial.toUpperCase()));
								if (damage != 0)
									item.setDurability((short) damage);
								if (nms.contains("1_16") || nms.contains("1_15") || nms.contains("1_14")
										|| nms.contains("1_13")) {
									Material mat = Material.getMaterial(itemMaterial.toUpperCase());
									if ((mat.equals(Material.SPLASH_POTION) || mat.equals(Material.POTION)
											|| mat.equals(Material.LINGERING_POTION))
											&& (Tuccar.instance.getConfig()
													.isSet("Tuccar." + s + ".items." + d + ".potionType"))) {

										PotionType type = PotionType.valueOf(Tuccar.instance.getConfig()
												.getString("Tuccar." + s + ".items." + d + ".potionType"));

										Potion potion = null;

										if (damage == 1 || damage == 2)
											potion = new Potion(type, damage);
										else {
											potion = new Potion(type, 1);
											potion.extend();
										}
										if (mat.equals(Material.SPLASH_POTION))
											potion.setSplash(true);
										item = potion.toItemStack(1);
										if (mat.equals(Material.LINGERING_POTION))
											item.setType(Material.LINGERING_POTION);

									}

								}
								ItemMeta meta = item.getItemMeta();

								if (itemName != null && !itemMaterial.equalsIgnoreCase("ENCHANTED_BOOK"))
									meta.setDisplayName(Tuccar.color(itemName));
								if (enchantment != null) {
									for (String ench : enchantment) {
										try {
											String[] enchant = ench.split(":");
											Enchantment enchObj = Enchantment.getByName(enchant[0].toUpperCase());
											if (enchObj == null)
												continue;
											else {
												if (itemMaterial.equalsIgnoreCase("ENCHANTED_BOOK")) {
													EnchantmentStorageMeta AMeta = (EnchantmentStorageMeta) item
															.getItemMeta();
													AMeta.addStoredEnchant(enchObj, Integer.parseInt(enchant[1]), true);
													if (itemName != null)
														AMeta.setDisplayName(Tuccar.color(itemName));
													item.setItemMeta(AMeta);
												} else
													meta.addEnchant(enchObj, Integer.parseInt(enchant[1]), true);
											}
										} catch (NullPointerException e1) {
											Bukkit.getConsoleSender().sendMessage(Tuccar.color(
													"&aTüccar &4" + s + " > " + d + " &cEnchantında hata var..."));
											continue;
										}
									}
								} else if (enchantment == null && Tuccar.instance.getConfig()
										.isSet("Tuccar." + s + ".items." + d + ".enchantment"))
									Bukkit.getConsoleSender().sendMessage(
											Tuccar.color("&aTüccar &4" + s + " > " + d + " &cEnchantında hata var..."));

								if (!itemMaterial.equalsIgnoreCase("ENCHANTED_BOOK"))
									item.setItemMeta(meta);
								itemToObject.put(item, productStorage);
								productInfo.put(d, DatabaseQueries.getProductInfos(d, s));
							} else
								Bukkit.getConsoleSender().sendMessage(Tuccar.color(
										"&4&lHATA &bTüccar materyal yanlış bulundu geçiliyor... &c" + s + ">" + d));

						}

						catch (NullPointerException | IllegalArgumentException e1) {
							Bukkit.getConsoleSender().sendMessage(Tuccar
									.color("&4&lHATA &bTüccar materyal yanlış bulundu geçiliyor... &c" + s + ">" + d));
							continue;
						}
					}
					productCategory.put(s, allStore);
				}

				if (getLang.isSet("Gui.custom")) {

					List<String> customList = new ArrayList<String>(
							getLang.getConfigurationSection("lang", "Gui.custom"));

					for (String data : customList) {

						String name = getLang.getText("Gui.custom." + data + ".name");

						int slot = getLang.getInt("Gui.custom." + data + ".slot");

						List<String> lore = getLang.getLore("Gui.custom." + data + ".lore");

						List<String> commands = getLang.getLore("Gui.custom." + data + ".commands");

						Material mat = Material.getMaterial(getLang.getText("Gui.custom." + data + ".material"));

						ItemStack customItem = Item.defaultItem(name, lore, mat);

						customItems.put(slot, new CustomItemCache(customItem, commands));

					}

				}

			}
		});
	}

	public static String getNMSVersion() {
		String v = Bukkit.getServer().getClass().getPackage().getName();
		return v.substring(v.lastIndexOf('.') + 1);
	}

	public static String color(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public static Economy getEconomy() {
		return econ;
	}

	public void MetricLoader() {
		Metrics metrics = new Metrics(this, 10085);
		metrics.addCustomChart(new Metrics.DrilldownPie("java_version", () -> {
			Map<String, Map<String, Integer>> map = new HashMap<>();
			String javaVersion = System.getProperty("java.version");
			Map<String, Integer> entry = new HashMap<>();
			entry.put(javaVersion, 1);
			if (javaVersion.startsWith("1.7")) {
				map.put("Java 1.7", entry);
			} else if (javaVersion.startsWith("1.8")) {
				map.put("Java 1.8", entry);
			} else if (javaVersion.startsWith("1.9")) {
				map.put("Java 1.9", entry);
			} else {
				map.put("Other", entry);
			}
			return map;
		}));
	}
}
