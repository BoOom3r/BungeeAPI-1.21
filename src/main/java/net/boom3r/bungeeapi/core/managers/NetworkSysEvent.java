package net.boom3r.bungeeapi.core.managers;

import net.boom3r.bungeeapi.BungeeAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NetworkSysEvent {

    public static boolean AddEvent(String name, String owner, String info){
        boolean result = false;

        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("INSERT INTO network_sys_events (NAME, OWNER, info) VALUES (?, ?, ?)");
        ) {
            statement.setString(1, name);
            statement.setString(2, owner);
            statement.setString(3, info);

            int id = statement.executeUpdate();

            if (id != 0) {
                //LogManager.Admin("Event enregistré");
                return true;
            } else {
                LogManager.Admin("Problème dans l'ajout de l'event");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}