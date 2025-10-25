package net.boom3r.bungeeapi.managers;


import com.zaxxer.hikari.HikariDataSource;
import net.boom3r.bungeeapi.BungeeAPI;

public class HConnection {

    private HikariDataSource hikari;
    public HikariDataSource openPool(){
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", "localhost");
        hikari.addDataSourceProperty("port", 3306);
        hikari.addDataSourceProperty("databaseName", "minecraft");
        hikari.addDataSourceProperty("user", "uhcraft");
        hikari.addDataSourceProperty("password", "uhcraftuhcraftuhcraft");
        LogManager.Info("Hikari BDD Pool opened");

        return hikari;
    }

    public static void closePool(){

        if (BungeeAPI.dataSourcePool != null)
            BungeeAPI.dataSourcePool.close();
    }

    public HikariDataSource getHikari(){
        return hikari;
    }

}

