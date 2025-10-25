package net.boom3r.bungeeapi.core.manager;

import net.boom3r.bungeeapi.BungeeAPI;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WhiteListManager {

    public static boolean isWhiteListed(ProxiedPlayer p) {
        boolean returnBool = false;
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT COUNT(uuid) AS nb FROM whitelist WHERE uuid=?");
        ) {
            statement.setString(1, p.getUniqueId().toString());

            ResultSet result = statement.executeQuery();
            if(result.next()){
                if (result.getInt("nb") == 0) {
                    // Kick this One !
                    returnBool = false;
                } else {
                    returnBool = true;
                }
            }

            result.close();


        } catch (SQLException e) {
            returnBool = false;
            e.printStackTrace();
        }
        return returnBool;
    }
}
