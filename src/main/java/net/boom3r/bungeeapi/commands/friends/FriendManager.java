package net.boom3r.bungeeapi.commands.friends;

import net.boom3r.bungeeapi.core.networkusers.NetworkUser;
import net.md_5.bungee.api.ProxyServer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;
import static net.boom3r.bungeeapi.BungeeAPI.dataSourcePool;

public class FriendManager {

    public static boolean removeFromDb(UUID sender, UUID friend){
        boolean returnBool = false;
        try (Connection sql = dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("DELETE FROM network_friends WHERE (OWNER = ? AND friend = ?) OR (OWNER = ? AND friend = ?)");
        ) {
            statement.setString(1, sender.toString());
            statement.setString(2, friend.toString());
            statement.setString(3, friend.toString());
            statement.setString(4, sender.toString());
            int result = statement.executeUpdate();
            if (result != 0) {
                bungeeLogger.Admin("Requete d'amitié enregistrée");
                return true;
            } else {
                bungeeLogger.Admin("Problème dans l'ajout de la requete d'amitié");
                return false;
            }

        } catch (SQLException e) {
            bungeeLogger.DebugV("Echec de la suppression du friend "+friend, 2);
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendInvite(UUID sender, UUID receiver){
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
                return true;
            } else {
                bungeeLogger.Admin("Problème dans l'ajout de la requete d'amitié");
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean acceptInvite(UUID sender, UUID me){
        boolean returnBool = false;
        try (Connection sql = dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("DELETE FROM network_request WHERE ((sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)) AND type = 'FRIEND'");
        ) {
            statement.setString(1, sender.toString());
            statement.setString(2, me.toString());
            statement.setString(3, me.toString());
            statement.setString(4, sender.toString());


            int affected = statement.executeUpdate();
            if(affected > 0 ){
                bungeeLogger.DebugV("Suppression réussie du friend request"+sender, 2);
            }

        } catch (SQLException e) {
            bungeeLogger.DebugV("Echec de la suppression du friend friend request"+sender, 2);
            e.printStackTrace();
            return false;
        }

        try (Connection sql = dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("REPLACE INTO network_friends (OWNER,friend, LAST) VALUES (?, ?, ?)");
        ) {
            statement.setString(1, sender.toString());
            statement.setString(2, me.toString());
            statement.setDate(3, Date.valueOf(LocalDate.now()));

            int id = statement.executeUpdate();

            if (id != 0) {

                bungeeLogger.Admin("Amitié enregistrée");

            } else {
                bungeeLogger.Admin("Problème dans l'ajout de l'amitié");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return returnBool;
    }


    public static boolean denyRequest(UUID sender, UUID me){
        boolean returnBool = false;
        try (Connection sql = dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("DELETE FROM network_request WHERE ((sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)) AND type = 'FRIEND'");
        ) {
            statement.setString(1, sender.toString());
            statement.setString(2, me.toString());
            statement.setString(3, me.toString());
            statement.setString(4, sender.toString());


            int affected = statement.executeUpdate();
            if(affected > 0 ){
                bungeeLogger.DebugV("Suppression réussie du friend request"+sender, 2);
            }
            returnBool = true;

        } catch (SQLException e) {
            bungeeLogger.DebugV("Echec de la suppression du friend friend request"+sender, 2);
            e.printStackTrace();
            return false;
        }
        return returnBool;
    }


    public static boolean isFriend(UUID sender, UUID receiver) {
        boolean returnBool = false;
        try (Connection sql = dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT COUNT(owner) AS nb FROM network_friends WHERE (owner = ? AND friend = ?) OR (owner = ? AND friend = ?)");
        ) {
            statement.setString(1, sender.toString());
            statement.setString(2, receiver.toString());
            statement.setString(3, receiver.toString());
            statement.setString(4, sender.toString());
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
             PreparedStatement statement = sql.prepareStatement("SELECT COUNT(sender) AS nb FROM network_request WHERE ((sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)) AND type = 'FRIEND'");
        ) {
            statement.setString(1, sender.toString());
            statement.setString(2, receiver.toString());
            statement.setString(3, receiver.toString());
            statement.setString(4, sender.toString());
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
