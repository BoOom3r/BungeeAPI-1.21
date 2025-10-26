package net.boom3r.bungeeapi.managers;

import net.boom3r.bungeeapi.BungeeAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

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
            // TODO ServerObject.maintenance off
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
