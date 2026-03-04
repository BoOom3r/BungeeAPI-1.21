package net.boom3r.bungeeapi.core.managers;

import net.boom3r.bungeeapi.BungeeAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;

public class MaintenanceManager {

    public static void enableMaintenance(String server){
        if(server.equalsIgnoreCase("global")){
            BungeeAPI.maintenance = true;
            updateDB("global",true);
        } else {
            // TODO ServerObject.maintenance on
            updateDB(server,true);
        }
    }

    public static void disableMaintenance(String server){
        if(server.equalsIgnoreCase("global")){
            BungeeAPI.maintenance = false;
            updateDB("global",false);
        } else {

            updateDB(server,false);
        }
    }

    public static boolean updateDB(String server, boolean status){
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("REPLACE INTO network_maintenance (server_name, status) VALUES (?, ?)");
        ) {
            statement.setString(1, server);
            statement.setBoolean(2, status);

            int id = statement.executeUpdate();

            if (id != 0) {
                bungeeLogger.Info("status enregistré");
                return true;
            } else {
                bungeeLogger.Err("Problème dans l'ajout du status");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

}
