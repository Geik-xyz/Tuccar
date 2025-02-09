package poyrazinan.com.tr.tuccar.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.jojodmo.customitems.api.CustomItemsAPI;

import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.Storage.ItemExistCheck;
import poyrazinan.com.tr.tuccar.Utils.Storage.ProductStorage;
import poyrazinan.com.tr.tuccar.Utils.Storage.GuiUtils.ProductCounts;
import poyrazinan.com.tr.tuccar.Utils.api.EventReason;
import poyrazinan.com.tr.tuccar.api.events.ProductRemoveEvent;
import poyrazinan.com.tr.tuccar.commands.AddProduct.RegisterManager;

public class DatabaseQueries {

	public static void createTable() {
		String createTableSQL = "CREATE TABLE IF NOT EXISTS `Tablo` (" +
				"`id` INTEGER PRIMARY KEY AUTO_INCREMENT," +
				"`username` varchar(20) NOT NULL," +
				"`category` text NOT NULL," +
				"`product` text NOT NULL," +
				"`stock` int DEFAULT 0," +
				"`price` double DEFAULT 0" +
				") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

		try (Connection connection = ConnectionPool.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute(createTableSQL);
			Bukkit.getLogger().info("[Tuccar] Veritabanı tablosu başarıyla kontrol edildi/oluşturuldu.");

		} catch (SQLException e) {
			Bukkit.getLogger().severe("[Tuccar] Tablo oluşturulurken hata: " + e.getMessage());
			e.printStackTrace();
		}
	}


	public static boolean registerProductToTable(String playerName, String category, String product, int stock, double price)
	{
		String SQL_QUERY = "INSERT INTO Tablo (username, category, product, stock, price) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);

            pst.setString(1, playerName);
            pst.setString(2, category);
            pst.setString(3, product);
            pst.setInt(4, stock);
            pst.setDouble(5, price);

            pst.executeUpdate();
        	
            pst.close();
            return true;
        } 
        
        catch (SQLException e) {return false;}
        
	}
	
	public static ProductCounts getProductInfos(String product, String category) {
		double minPrice = 0;
		int seller = 0;
		String SQL_QUERY = "SELECT * FROM Tablo WHERE product = ? AND category = ? ORDER BY price ASC";
		String SQL_QUERY2 = "SELECT COUNT(*) FROM Tablo WHERE product = ? AND category = ? ORDER BY price ASC";
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);
            pst.setString(1, product);
            pst.setString(2, category);
            ResultSet resultSet = pst.executeQuery();
            
            while (resultSet.next()) {
            	minPrice = resultSet.getDouble("price");
            	break;
            }
            resultSet.close();
            pst.close();
            
            PreparedStatement pst2 = con.prepareStatement(SQL_QUERY2);
            pst2.setString(1, product);
            pst2.setString(2, category);
            ResultSet resultSet2 = pst2.executeQuery();
            
            while (resultSet2.next()) {
            	seller = resultSet2.getInt(1);
            	break;
            }
            resultSet2.close();
            pst2.close();
            
            
        } catch (SQLException e1) { e1.printStackTrace(); }
        
		return new ProductCounts(minPrice, seller);
	}
	
	public static double getMinimumPrice(String product, String category) {
		double minPrice = 0;
		String SQL_QUERY = "SELECT * FROM Tablo WHERE product = ? AND category = ? ORDER BY price ASC";
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);
            pst.setString(1, product);
            pst.setString(2, category);
            ResultSet resultSet = pst.executeQuery();
            
            while (resultSet.next()) {
            	minPrice = resultSet.getDouble("price");
            	break;}
            resultSet.close();
            pst.close();
        } catch (SQLException e1) {}
		return minPrice;
	}
	
	public static List<ProductStorage> getAllListsOnProduct(String product) {
		String SQL_QUERY = "SELECT * FROM Tablo WHERE product = ? ORDER BY price ASC";
		List<ProductStorage> storage = new ArrayList<ProductStorage>();	
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);
            pst.setString(1, product);
            ResultSet resultSet = pst.executeQuery();
            
            while (resultSet.next()) {
            	int id = resultSet.getInt("id");
            	String seller = resultSet.getString("username");
            	String category =  resultSet.getString("category");
            	int stock =  resultSet.getInt("stock");
            	double price =  resultSet.getDouble("price");
            	String displayName = Tuccar.instance.getConfig().getString("Tuccar." + category + ".items." + product + ".displayName");
            	String material = Tuccar.instance.getConfig().getString("Tuccar." + category + ".items." + product + ".material");
            	String itemName = null;
            	if (Tuccar.instance.getConfig().isSet("Tuccar." + category + ".items." + product + ".itemName")) itemName = Tuccar.instance.getConfig().getString("Tuccar." + category + ".items." + product + ".itemName");
            	List<String> displayLore = Tuccar.instance.getConfig().getStringList("Tuccar." + category + ".items." + product + ".displayLore");
            	List<String> lore = Tuccar.instance.getConfig().getStringList("Tuccar." + category + ".items." + product + ".itemLore");
            	int damage =  0;
            	if (Tuccar.instance.getConfig().isSet("Tuccar." + category + ".items." + product + ".damage")) damage = Tuccar.instance.getConfig().getInt("Tuccar." + category + ".items." + product + ".damage");
            	List<String> enchantment = null;
            	if (Tuccar.instance.getConfig().isSet("Tuccar." + category + ".items." + product + ".enchantment")) enchantment = Tuccar.instance.getConfig().getStringList("Tuccar." + category + ".items." + product + ".enchantment");
            	storage.add(new ProductStorage(product, material.toUpperCase(), category, itemName, displayName, lore, displayLore, damage, stock, price, seller, id, enchantment));
            }
            resultSet.close();
            pst.close();
        } catch (SQLException e1) {}
		return storage;
	}
	
	public static List<ProductStorage> getCategoryItemList(String product, String category) {
		String SQL_QUERY = "SELECT * FROM Tablo WHERE product = ? AND category = ? ORDER BY price ASC";
		List<ProductStorage> storage = new ArrayList<ProductStorage>();	
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);
            pst.setString(1, product);
            pst.setString(2, category);
            ResultSet resultSet = pst.executeQuery();
            
            while (resultSet.next()) {
            	int id = resultSet.getInt("id");
            	String seller = resultSet.getString("username");
            	int stock =  resultSet.getInt("stock");
            	double price =  resultSet.getDouble("price");
            	String displayName = Tuccar.instance.getConfig().getString("Tuccar." + category + ".items." + product + ".displayName");
            	String material = Tuccar.instance.getConfig().getString("Tuccar." + category + ".items." + product + ".material");
            	String itemName = null;
            	if (Tuccar.instance.getConfig().isSet("Tuccar." + category + ".items." + product + ".itemName")) itemName = Tuccar.instance.getConfig().getString("Tuccar." + category + ".items." + product + ".itemName");
            	List<String> displayLore = Tuccar.instance.getConfig().getStringList("Tuccar." + category + ".items." + product + ".displayLore");
            	List<String> lore = Tuccar.instance.getConfig().getStringList("Tuccar." + category + ".items." + product + ".itemLore");
            	int damage =  0;
            	if (Tuccar.instance.getConfig().isSet("Tuccar." + category + ".items." + product + ".damage")) damage = Tuccar.instance.getConfig().getInt("Tuccar." + category + ".items." + product + ".damage");
            	List<String> enchantment = null;
            	if (Tuccar.instance.getConfig().isSet("Tuccar." + category + ".items." + product + ".enchantment")) enchantment = Tuccar.instance.getConfig().getStringList("Tuccar." + category + ".items." + product + ".enchantment");
            	storage.add(new ProductStorage(product, material.toUpperCase(), category, itemName, displayName, lore, displayLore, damage, stock, price, seller, id, enchantment));
            }
            resultSet.close();
            pst.close();
        } catch (SQLException e1) {}
		return storage;
	}
	
	public static boolean isProductHasSeller(String product, String category) {
		String SQL_QUERY = "SELECT * FROM Tablo WHERE product = ? AND category = ? LIMIT 1";
		boolean status = false;
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);
            pst.setString(1, product);
            pst.setString(2, category);
            ResultSet resultSet = pst.executeQuery();
            
            if (resultSet.next()) {
            	status = true;
            }
            resultSet.close();
            pst.close();
        } catch (SQLException e1) {}
		return status;
	}
	
	public static List<ProductStorage> getPlayerProducts(String player) {
		String SQL_QUERY = "SELECT * FROM Tablo WHERE username = ? ORDER BY price ASC";
		List<ProductStorage> storage = new ArrayList<ProductStorage>();	
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);
            pst.setString(1, player);
            ResultSet resultSet = pst.executeQuery();
            
            while (resultSet.next()) {
            	int id = resultSet.getInt("id");
            	String seller = resultSet.getString("username");
            	int stock =  resultSet.getInt("stock");
            	double price =  resultSet.getDouble("price");
            	String product = resultSet.getString("product");
            	String category = resultSet.getString("category");
            	
            	if (!Tuccar.instance.getConfig().isSet("Tuccar." + category + ".items." + product)) continue;
            	
            	if (Tuccar.instance.getConfig().isSet("Tuccar." + category + ".items." + product + ".customitem"))
            	{
            		ItemStack item = CustomItemsAPI.getCustomItem(
		  					Tuccar.instance.getConfig().getString("Tuccar." + category + ".items." + product + ".customitem"));
            		storage.add(new ProductStorage(product, item.getType().name(), category, 
            				null, 
            				"", 
            				new ArrayList<String>(),
            				new ArrayList<String>(), 0, stock, price, seller, id , null));
            		continue;
            	}
            	
            	String displayName = Tuccar.instance.getConfig().getString("Tuccar." + category + ".items." + product + ".displayName");
            	String material = Tuccar.instance.getConfig().getString("Tuccar." + category + ".items." + product + ".material");
            	String itemName = null;
            	if (Tuccar.instance.getConfig().isSet("Tuccar." + category + ".items." + product + ".itemName")) itemName = Tuccar.instance.getConfig().getString("Tuccar." + category + ".items." + product + ".itemName");
            	List<String> displayLore = Tuccar.instance.getConfig().getStringList("Tuccar." + category + ".items." + product + ".displayLore");
            	List<String> lore = Tuccar.instance.getConfig().getStringList("Tuccar." + category + ".items." + product + ".itemLore");
            	int damage =  0;
            	if (Tuccar.instance.getConfig().isSet("Tuccar." + category + ".items." + product + ".damage")) damage = Tuccar.instance.getConfig().getInt("Tuccar." + category + ".items." + product + ".damage");
            	List<String> enchantment = null;
            	if (Tuccar.instance.getConfig().isSet("Tuccar." + category + ".items." + product + ".enchantment")) enchantment = Tuccar.instance.getConfig().getStringList("Tuccar." + category + ".items." + product + ".enchantment");
            	
            	
            	storage.add(new ProductStorage(product, material.toUpperCase(), category, itemName, displayName, lore, displayLore, damage, stock, price, seller, id, enchantment));
            }
            resultSet.close();
            pst.close();
        } catch (SQLException e1) {}
		return storage;
	}
	
	public static boolean checkStock(int id, int amount) {
		String SQL_QUERY = "SELECT stock FROM Tablo WHERE id = ? AND stock >= ?";
		boolean check = false;
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);
            pst.setInt(1, id);
            pst.setInt(2, amount);
            ResultSet resultSet = pst.executeQuery();
            if (resultSet.next()) check = true;
            else check = false;
            resultSet.close();
            pst.close();
        } catch (SQLException e1) {}
		return check;
	}
	
	public static int getProductCount(int id) {
		String SQL_QUERY = "SELECT stock FROM Tablo WHERE id = ?";
		int count = 0;
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);

            pst.setInt(1, id);

            ResultSet resultSet = pst.executeQuery();
            
            if (resultSet.next()) {
            	count = resultSet.getInt("stock");
            }
            resultSet.close();
            pst.close();
        }
        
        catch (SQLException e) { return 0; }
        
        return count;
	}
	
	public static void removeProductCount(int id, int count, String dataName, String category, double price){
		String SQL_QUERY = "UPDATE Tablo SET stock = ? WHERE id = ?";
		int newCount = getProductCount(id)-count;
        if (newCount < 1) {
        	SQL_QUERY = "DELETE FROM Tablo WHERE id = ?";
        	try (Connection con = ConnectionPool.getConnection()) {
                PreparedStatement pst = con.prepareStatement(SQL_QUERY);
                pst.setInt(1, id);
                pst.executeUpdate();
                pst.close();
             // EVENT
                
                Bukkit.getScheduler().runTask(Tuccar.instance, () ->
                {
                	
                	ProductRemoveEvent productRemove = new ProductRemoveEvent(EventReason.SELL, dataName, category, price);
    				Bukkit.getPluginManager().callEvent(productRemove);	
                	
                });
				//
                
                
                RegisterManager.removeValues(dataName, category, price);
                
            }
        	
        	catch (SQLException e) {}
        	
        } else {
        	try (Connection con = ConnectionPool.getConnection()) {
                PreparedStatement pst = con.prepareStatement(SQL_QUERY);
                
                pst.setInt(1, newCount);
                pst.setInt(2, id);

                pst.executeUpdate();
                pst.close();
            }
        	
        	catch (SQLException e) {}
        	
        }
	}
	
	public static void addProductCount(int id, int count) {
		String SQL_QUERY = "UPDATE Tablo SET stock = ? WHERE id = ?";
    	try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);
            
            pst.setInt(1, count);
            pst.setInt(2, id);

            pst.executeUpdate();
            pst.close();
        }
    	
    	catch (SQLException e) {}
	}
	
	public static void setProductPrice(int id, double price) {
		String SQL_QUERY = "UPDATE Tablo SET price = ? WHERE id = ?";
    	try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);
            
            pst.setDouble(1, price);
            pst.setInt(2, id);

            pst.executeUpdate();
            pst.close();
        }
    	
    	catch (SQLException e) {}
	}
	
	public static ItemExistCheck getPlayerItem(String product, String category, String player) {
		String SQL_QUERY = "SELECT * FROM Tablo WHERE product = ? AND category = ? AND username = ?";
		ItemExistCheck storage = null;	
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);
            pst.setString(1, product);
            pst.setString(2, category);
            pst.setString(3, player);
            ResultSet resultSet = pst.executeQuery();
            
            while (resultSet.next()) {
            	int id = resultSet.getInt("id");
            	int stock =  resultSet.getInt("stock");
            	double price =  resultSet.getDouble("price");
            	storage = new ItemExistCheck(id, player, stock, price);
            }
            resultSet.close();
            pst.close();
        } catch (SQLException e1) { return null; }
		return storage;
	}

	
}
