package poyrazinan.com.tr.tuccar.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import poyrazinan.com.tr.tuccar.Tuccar;

public class ConnectionPool {
	private static HikariDataSource dataSource;

	public static void initialize() {
		String dbType = Tuccar.instance.getConfig().getString("Database.type", "SQLITE");

		if (dbType.equalsIgnoreCase("MYSQL")) {
			initializeMysql();
		} else {
			initializeSqlite();
		}
	}

	private static void initializeMysql() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s",
				Tuccar.instance.getConfig().getString("Database.host"),
				Tuccar.instance.getConfig().getInt("Database.port"),
				Tuccar.instance.getConfig().getString("Database.database")
		));
		config.setUsername(Tuccar.instance.getConfig().getString("Database.username"));
		config.setPassword(Tuccar.instance.getConfig().getString("Database.password"));
		config.setMaximumPoolSize(10);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		dataSource = new HikariDataSource(config);
	}

	private static void initializeSqlite() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:sqlite:plugins/Tuccar/Database.db");
		config.setDriverClassName("org.sqlite.JDBC");
		config.setMaximumPoolSize(1); // SQLite only supports one connection

		dataSource = new HikariDataSource(config);
	}

	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public static void closePool() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
		}
	}
}