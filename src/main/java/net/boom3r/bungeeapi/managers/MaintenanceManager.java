package net.boom3r.bungeeapi.managers;

import net.boom3r.bungeeapi.BungeeAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

public class MaintenanceManager {

    public static void enableMaintenance(String server){
        if(server.equalsIgnoreCase("global")){
            BungeeAPI.maintenance = true;
            updateDB("global",true);
        }
    }

    public static void disableMaintenance(String server){
        if(server.equalsIgnoreCase("global")){
            BungeeAPI.maintenance = false;
            updateDB("global",false);
        }
    }

    public static boolean updateDB(String server, boolean status){
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("REPLACE INTO maintenance (server_name, status) VALUES (?, ?)");
        ) {
            statement.setString(1, server);
            statement.setBoolean(2, status);

            int id = statement.executeUpdate();

            if (id != 0) {
                LogManager.Info("status enregistré");
                return true;
            } else {
                LogManager.Err("Problème dans l'ajout du status");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
