package net.boom3r.bungeeapi.core.managers;


import com.zaxxer.hikari.HikariDataSource;
import net.boom3r.bungeeapi.BungeeAPI;

import java.sql.Connection;

import static net.boom3r.bungeeapi.core.managers.LogManager.logger;

public class HConnection {

    private HikariDataSource hikari;
    public HikariDataSource openPool(String servNme, int servPort, String servDB, String servUser, String servPass) {
        try {
            hikari = new HikariDataSource();

            hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
            hikari.addDataSourceProperty("serverName", servNme);
            hikari.addDataSourceProperty("port", servPort);
            hikari.addDataSourceProperty("databaseName", servDB);
            hikari.addDataSourceProperty("user", servUser);
            hikari.addDataSourceProperty("password", servPass);

            // 🔑 Forcer une tentative de connexion tout de suite
            try (Connection conn = hikari.getConnection()) {
                logger.info("Hikari BDD Pool opened successfully");
            }

            return hikari;
        } catch (Exception e) {

            logger.severe("Erreur lors de l'ouverture du pool Hikari : " + e.getMessage());

            return null;
        }
    }

    public static void closePool(){

        if (BungeeAPI.dataSourcePool != null)
            BungeeAPI.dataSourcePool.close();
    }

    public HikariDataSource getHikari(){
        return hikari;
    }

}

