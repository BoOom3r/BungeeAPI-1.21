package net.boom3r.bungeeapi.core.managers;

import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import net.boom3r.bungeeapi.core.objects.ServerObject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.boom3r.bungeeapi.BungeeAPI.*;
import static net.boom3r.bungeeapi.BungeeAPI.redisEnabled;
import static net.boom3r.bungeeapi.BungeeAPI.redisManager;

public class NetworkUserManager {

    public List<UUID> networkUserList;

    public NetworkUserManager (){
        networkUserList = new ArrayList<>();
    }

    public void addNetworkUser(UUID uuid, NetworkUser nUser){
        updateNetworkUserDB(nUser);
        if (!networkUserList.contains(uuid)) networkUserList.add(uuid);
        redisManager.save("network_user_list",networkUserList);
        redisManager.save("network_user:"+uuid,nUser);

    }

    public void removeNetworkUser(UUID uuid){
        //networkUserManager.updateNetworkUserDB(nUser);
        if (networkUserList.contains(uuid)) networkUserList.remove(uuid);

        redisManager.save("network_user_list",networkUserList);
        redisManager.delete("network_user:"+uuid);

    }

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
                bungeeLogger.Admin("Serveur NetworkUser enregistré !");
                return true;
            } else {
                bungeeLogger.Admin("Problème dans l'ajout du NetworkUser...");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }




}
