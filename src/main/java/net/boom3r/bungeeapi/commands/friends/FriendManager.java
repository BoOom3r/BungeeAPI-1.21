package net.boom3r.bungeeapi.commands.friends;

import com.mojang.brigadier.Command;
import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;
import static net.boom3r.bungeeapi.BungeeAPI.dataSourcePool;

public class FriendManager {

    public static void removeFromDb(UUID uuid){
        boolean returnBool = false;
        try (Connection sql = dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("DELETE * FROM network_friend WHERE OWNER = ? OR friend = ?");
        ) {
            statement.setString(1, uuid.toString());
            statement.setString(2, uuid.toString());

            ResultSet result = statement.executeQuery();
            if(result.next()){
                bungeeLogger.DebugV("Suppression réussie du friend "+uuid, 2);
            }

            result.close();


        } catch (SQLException e) {
            bungeeLogger.DebugV("Echec de la suppression du friend "+uuid, 2);
            e.printStackTrace();
        }

    }

    public static void sendInvite(UUID sender, UUID receiver){
        try (Connection sql = dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("REPLACE INTO network_request (sender, receiver, type, state, active) VALUES (?, ?, ?, ?, ?)");
        ) {
            statement.setString(1, sender.toString());
            statement.setString(2, receiver.toString());
            statement.setString(3, "FRIEND");
            statement.setInt(4, 1);
            statement.setInt(5, 1);

            int id = statement.executeUpdate();

            if (id != 0) {
                bungeeLogger.Admin("Requete d'amitié enregistrée");
            } else {
                bungeeLogger.Admin("Problème dans l'ajout de la requete d'amitié");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isFriend(UUID sender, UUID receiver) {
        boolean returnBool = false;
        try (Connection sql = dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT COUNT(uuid) AS nb FROM network_friend WHERE owner = ? OR friend = ?");
        ) {
            statement.setString(1, sender.toString());
            statement.setString(2, receiver.toString());

            ResultSet result = statement.executeQuery();
            if (result.next()) {
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

    public static boolean isPendingFriend(UUID sender, UUID receiver) {
        boolean returnBool = false;
        try (Connection sql = dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT COUNT(uuid) AS nb FROM network_request WHERE sender = ? OR receiver = ?");
        ) {
            statement.setString(1, sender.toString());
            statement.setString(2, receiver.toString());

            ResultSet result = statement.executeQuery();
            if (result.next()) {
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

    public static List<UUID> getOtherFriendList(NetworkUser nUser, UUID uuid){
        List<UUID> friendList = new ArrayList<>();
        if (ProxyServer.getInstance().getPlayer(nUser.getUuid()).hasPermission("bungeeAPI.friend.list_other")) {
            try (Connection conn = dataSourcePool.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM network_friends WHERE OWNER = ? OR friend = ?")) {
                ps.setString(1, uuid.toString());
                ps.setString(2, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (rs.getString("owner").equalsIgnoreCase(uuid.toString())) {
                            bungeeLogger.DebugV("Ajout de friend" + rs.getString("friend") + " en tant qu'ami", 2);
                            friendList.add(UUID.fromString(rs.getString("friend")));
                            // ajout friend
                        }
                        if (rs.getString("friend").equalsIgnoreCase(uuid.toString())) {
                            bungeeLogger.DebugV("Ajout de owner" + rs.getString("owner") + " en tant qu'ami", 2);
                            friendList.add(UUID.fromString(rs.getString("owner")));
                            // ajout friend
                        }
                    }
                }
                return friendList;
            } catch (SQLException e) {
                bungeeLogger.Err(e.getMessage());
                //Bukkit.getLogger().severe(e.getMessage());
                e.printStackTrace();
            }
        } else  {
            nUser.sendMessage("Tu n'as pas la permission d'utiliser cette commande.");
        }
        return friendList;
    }
}
