package net.boom3r.bungeeapi.core.managers;

import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import net.boom3r.bungeeapi.core.objects.ServerObject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;

public class NetworkUserManager {


    public boolean updateNetworkUserDB(NetworkUser user) {
        boolean retour = false;

        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("REPLACE INTO network_users (uuid, name, last_known_ip, last_conn) VALUES (?, ?, ?, ?)");
        ) {
            statement.setString(1, user.getUuid().toString());
            statement.setString(2, user.getName());
            statement.setString(3, user.getIp());
            statement.setDate(4, new Date(System.currentTimeMillis()));

            int id = statement.executeUpdate();

            if (id != 0) {
                bungeeLogger.Admin("Serveur enregistré");
                return true;
            } else {
                bungeeLogger.Admin("Problème dans l'ajout du serveur");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }




}
