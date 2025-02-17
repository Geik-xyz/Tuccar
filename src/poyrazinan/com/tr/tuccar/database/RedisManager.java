package poyrazinan.com.tr.tuccar.database;

import poyrazinan.com.tr.tuccar.Utils.Storage.GuiUtils.ProductCounts;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import org.bukkit.configuration.file.FileConfiguration;
import com.google.gson.Gson;
import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.Storage.ProductStorage;
import org.bukkit.Bukkit;

public class RedisManager {
    private static JedisPool pool;
    private static final Gson gson = new Gson();
    private static String serverId;
    private static Thread listenerThread;
    private static boolean isRunning = true;

    public static void initialize(FileConfiguration config) throws Exception {
        try {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(8);
            poolConfig.setMaxIdle(8);
            poolConfig.setMinIdle(0);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);
            poolConfig.setMaxWaitMillis(10000);

            String host = config.getString("Redis.host", "localhost");
            int port = config.getInt("Redis.port", 6379);
            String password = config.getString("Redis.password", "");
            serverId = config.getString("Redis.serverId", "server1");

            if (password.isEmpty()) {
                pool = new JedisPool(poolConfig, host, port, 2000);
            } else {
                pool = new JedisPool(poolConfig, host, port, 2000, password);
            }

            // Test connection
            try (Jedis jedis = pool.getResource()) {
                jedis.ping();
            }

            startMessageListener();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Tuccar] Redis başlatılamadı: " + e.getMessage());
            throw e;
        }
    }

    public static void shutdown() {
        isRunning = false;
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
        if (pool != null && !pool.isClosed()) {
            pool.close();
        }
    }

    public static void publishProductUpdate(ProductStorage product, String action) {
        publishProductUpdate(product, action, null);
    }

    public static void publishProductUpdate(ProductStorage product, String action, String data) {
        if (!isRunning || pool == null || pool.isClosed()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Tuccar.instance, () -> {
            try (Jedis jedis = pool.getResource()) {
                CrossServerMessage message = new CrossServerMessage(
                        serverId,
                        action,
                        data != null ? data : (product != null ? gson.toJson(product) : "")
                );
                jedis.publish("tuccar_updates", gson.toJson(message));
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Tuccar] Redis mesajı gönderilemedi: " + e.getMessage());
            }
        });
    }

    private static void startMessageListener() {
        listenerThread = new Thread(() -> {
            while (isRunning) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.subscribe(new TuccarMessageListener(), "tuccar_updates");
                } catch (Exception e) {
                    if (isRunning) {
                        Bukkit.getLogger().warning("[Tuccar] Redis bağlantısı koptu, yeniden bağlanılıyor...");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        }, "Tuccar-Redis-Listener");

        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private static class TuccarMessageListener extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {
            if (!channel.equals("tuccar_updates")) return;

            try {
                CrossServerMessage crossServerMessage = gson.fromJson(message, CrossServerMessage.class);
                if (!crossServerMessage.getSourceServer().equals(serverId)) {
                    handleCrossServerMessage(crossServerMessage);
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Tuccar] Redis mesajı işlenemedi: " + e.getMessage());
            }
        }
    }

    public static class CrossServerMessage {
        private final String sourceServer;
        private final String action;
        private final String data;

        public CrossServerMessage(String sourceServer, String action, String data) {
            this.sourceServer = sourceServer;
            this.action = action;
            this.data = data;
        }

        public String getSourceServer() {
            return sourceServer;
        }

        public String getAction() {
            return action;
        }

        public String getData() {
            return data;
        }
    }

    private static void handleCrossServerMessage(CrossServerMessage message) {
        Bukkit.getScheduler().runTask(Tuccar.instance, () -> {
            try {
                switch (message.getAction()) {
                    case "PRODUCT_SOLD":
                        ProductStorage product = gson.fromJson(message.getData(), ProductStorage.class);
                        Tuccar.productInfo.clear(); // Force refresh of cache
                        break;

                    case "PRICE_UPDATE":
                        PriceUpdateData priceData = gson.fromJson(message.getData(), PriceUpdateData.class);
                        handlePriceUpdate(priceData);
                        break;

                    case "STOCK_UPDATE":
                        StockUpdateData stockData = gson.fromJson(message.getData(), StockUpdateData.class);
                        handleStockUpdate(stockData);
                        break;

                    case "RELOAD":
                        Tuccar.safeReload();
                        break;
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Tuccar] Mesaj işlenirken hata: " + e.getMessage());
            }
        });
    }

    private static void handlePriceUpdate(PriceUpdateData data) {
        try {
            DatabaseQueries.setProductPrice(data.getId(), data.getNewPrice());
            String product = data.getDataName();
            String category = data.getCategory();

            if (Tuccar.productInfo.containsKey(product)) {
                ProductCounts counts = Tuccar.productInfo.get(product);
                if (data.getNewPrice() < counts.getMinPrice()) {
                    counts.setMinPrice(data.getNewPrice());
                } else if (data.getOldPrice() == counts.getMinPrice()) {
                    counts.setMinPrice(DatabaseQueries.getMinimumPrice(product, category));
                }
                Tuccar.productInfo.replace(product, counts);
            }

            Bukkit.getLogger().info("[Tuccar] Fiyat güncellendi - Ürün: " + product + ", Yeni Fiyat: " + data.getNewPrice());
        } catch (Exception e) {
            Bukkit.getLogger().warning("[Tuccar] Fiyat güncellenirken hata: " + e.getMessage());
        }
    }

    private static void handleStockUpdate(StockUpdateData data) {
        try {
            if (data.getNewStock() <= 0) {
                DatabaseQueries.removeProductCount(
                        data.getId(),
                        data.getOldStock(),
                        data.getDataName(),
                        data.getCategory(),
                        data.getPrice()
                );
            } else {
                DatabaseQueries.addProductCount(data.getId(), data.getNewStock());
            }

            String product = data.getDataName();
            if (Tuccar.productInfo.containsKey(product)) {
                ProductCounts counts = Tuccar.productInfo.get(product);
                if (data.getNewStock() <= 0) {
                    counts.setAmountOfSeller(counts.getAmountOfSeller() - 1);
                    if (data.getPrice() == counts.getMinPrice()) {
                        counts.setMinPrice(DatabaseQueries.getMinimumPrice(product, data.getCategory()));
                    }
                }
                Tuccar.productInfo.replace(product, counts);
            }

            Bukkit.getLogger().info("[Tuccar] Stok güncellendi - Ürün: " + product + ", Yeni Stok: " + data.getNewStock());
        } catch (Exception e) {
            Bukkit.getLogger().warning("[Tuccar] Stok güncellenirken hata: " + e.getMessage());
        }
    }

    public static class PriceUpdateData {
        private final int id;
        private final String dataName;
        private final String category;
        private final double newPrice;
        private final double oldPrice;

        public PriceUpdateData(int id, String dataName, String category, double newPrice, double oldPrice) {
            this.id = id;
            this.dataName = dataName;
            this.category = category;
            this.newPrice = newPrice;
            this.oldPrice = oldPrice;
        }

        public int getId() { return id; }
        public String getDataName() { return dataName; }
        public String getCategory() { return category; }
        public double getNewPrice() { return newPrice; }
        public double getOldPrice() { return oldPrice; }
    }

    public static class StockUpdateData {
        private final int id;
        private final String dataName;
        private final String category;
        private final int newStock;
        private final int oldStock;
        private final double price;

        public StockUpdateData(int id, String dataName, String category, int newStock, int oldStock, double price) {
            this.id = id;
            this.dataName = dataName;
            this.category = category;
            this.newStock = newStock;
            this.oldStock = oldStock;
            this.price = price;
        }

        public int getId() { return id; }
        public String getDataName() { return dataName; }
        public String getCategory() { return category; }
        public int getNewStock() { return newStock; }
        public int getOldStock() { return oldStock; }
        public double getPrice() { return price; }
    }

    public static void publishPriceUpdate(int id, String dataName, String category, double newPrice, double oldPrice) {
        PriceUpdateData updateData = new PriceUpdateData(id, dataName, category, newPrice, oldPrice);
        publishProductUpdate(null, "PRICE_UPDATE", gson.toJson(updateData));
    }

    public static void publishStockUpdate(int id, String dataName, String category, int newStock, int oldStock, double price) {
        StockUpdateData updateData = new StockUpdateData(id, dataName, category, newStock, oldStock, price);
        publishProductUpdate(null, "STOCK_UPDATE", gson.toJson(updateData));
    }
}