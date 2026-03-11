package net.boom3r.bungeeapi.core.objects;

import net.boom3r.bungeeapi.BungeeAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;

public class NetworkConf {

    private String networkName;
    private BungeeAPI plugin;

    public NetworkConf(BungeeAPI plugin){
        this.plugin = plugin;
        this.networkName = loadName();
    }

    public String getNetworkName(){
        return this.networkName;
    }

    public String loadName(){
        String networkName = "";
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement(
                     "SELECT valeur FROM network_conf WHERE cle_1 = ?"
             )
        ) {
            statement.setString(1, "NETWORK_NAME");

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                networkName = result.getString("valeur");
            } else {
                networkName = "SYSTEM";
            }

            result.close();
            bungeeLogger.DebugV("Le réseau s'appelle : "+networkName,2);

            return networkName;

        } catch (SQLException e) {
            e.printStackTrace();
            return "DEFAULT_SYSTEM";
        }
    }

}
