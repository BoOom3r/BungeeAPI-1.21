package net.boom3r.bungeeapi.core.utils;

import net.boom3r.bungeeapi.BungeeAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;

public class NickUuidTool {


    public static UUID getUuidFromNick(String nickName){
        UUID playerUuid = null;
        ProxiedPlayer playerP =  ProxyServer.getInstance().getPlayer(nickName);
        if (playerP != null) {
            playerUuid = playerP.getUniqueId();
            if (playerUuid == null) {
                playerUuid = getPlayerUUIDFromDB(nickName);
                if (playerUuid == null) {
                    bungeeLogger.DebugV("Pas d'utilisateur connu", 2);
                    return playerUuid;
                }
            }
        } else {
            playerUuid = getPlayerUUIDFromDB(nickName);
            if (playerUuid == null) {
                bungeeLogger.DebugV("Pas d'utilisateur connu", 2);
                return playerUuid;
            }
        }
        return playerUuid;
    }

    public static UUID getPlayerUUIDFromDB(String nickName){
        UUID playerUuid = null;
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT * FROM network_users WHERE name = ?");
        ) {
            statement.setString(1, nickName);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                playerUuid = UUID.fromString(result.getString("uuid"));
            }

            result.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerUuid;
    }

    public static String getPlayerNickFromDB(UUID nickName){
        String playerUuid = null;
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT * FROM network_users WHERE uuid = ?");
        ) {
            statement.setString(1, nickName.toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                playerUuid = result.getString("name");
            }

            result.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerUuid;
    }
}
