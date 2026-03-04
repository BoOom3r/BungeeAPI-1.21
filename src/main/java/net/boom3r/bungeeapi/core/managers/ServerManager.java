package net.boom3r.bungeeapi.core.managers;

import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.objects.ServerObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeInstance;

public class ServerManager {

    private static Map<String, ServerObject> serverlist;

    public ServerManager() {
        serverlist = new HashMap<>();
    }

    public static void addServer(String name, InetSocketAddress address, String motd, boolean restricted) {
        ProxyServer.getInstance().getServers().put(name, ProxyServer.getInstance().constructServerInfo(name, address, motd, restricted));
        UUID uuid = UUID.randomUUID();
        ServerObject newServer = new ServerObject(uuid.toString(), name, address.getHostName(), address.getPort(), motd, 1, false);
        serverlist.put(name, newServer);
        setServerInDB(newServer);
    }

    public static void removeServer(String name) {
        if (ProxyServer.getInstance().getServerInfo(name) != null) {
            for (ProxiedPlayer p : ProxyServer.getInstance().getServerInfo(name).getPlayers()) {
                if (p.getServer().getInfo().getName().equalsIgnoreCase(name)) {
                    if (p.getServer().getInfo().getName().equalsIgnoreCase("lobby")) {
                        p.sendMessage(new ComponentBuilder("You are already connected to the Hub!").color(ChatColor.RED).create());
                        ServerInfo target = ProxyServer.getInstance().getServerInfo("sheepwars");
                        p.connect(target);
                    } else {
                        ServerInfo target = ProxyServer.getInstance().getServerInfo("lobby");
                        p.connect(target);
                    }
                }
            }
        }
            ProxyServer.getInstance().getServers().remove(name);
            ServerObject newServer = serverlist.get(name);
            serverlist.remove(name);
            removeServerInDB(newServer);


    }

    public static void initServerList() {

        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT network_servers.uuid, network_servers.name, network_servers.address, network_servers.port, network_servers.motd, network_servers.players, network_servers.status, network_maintenance.status AS maintenance FROM network_servers LEFT JOIN network_maintenance ON network_servers.name = network_maintenance.server_name WHERE inactive = false");
        ) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                ServerObject actualServer = new ServerObject(result.getString("uuid"),
                        result.getString("name"),
                        result.getString("address"),
                        result.getInt("port"),
                        result.getString("motd"),
                        result.getInt("status"),
                        result.getBoolean("maintenance"));

                serverlist.put(result.getString("name"), actualServer);
                bungeeInstance.getProxy().getConsole().sendMessage(
                        new ComponentBuilder(
                                actualServer.getName() +
                                        " - " + actualServer.getAddress() +
                                        ":" + actualServer.getPort() +
                                        " >> " + actualServer.getMotd() +
                                        " --> " + actualServer.getMaintenance()
                        ).create()
                );
                ProxyServer.getInstance().getServers().put(actualServer.getName(), ProxyServer.getInstance().constructServerInfo(actualServer.getName(), new InetSocketAddress(actualServer.getAddress(),actualServer.getPort()), actualServer.getMotd(), false));
                //listRetour.add(result.getString("name"));
            }

            result.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ServerObject getServer(UUID uuid) {
        return serverlist.get(uuid);
    }

    public static ServerObject getServer(String name) {
        for (ServerObject server : serverlist.values()) {
            if (server.getName().equals(name)) {
                return server;
            }
        }
        return null;
    }

    public static boolean getMaintenanceFromDB(String name){
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT * FROM network_maintenance WHERE server_name = ?");
        ) {
            boolean status = false;
            statement.setString(1, name);

            ResultSet result = statement.executeQuery();
            if(!result.next()) return false;
            BungeeAPI.logger.info("Réponse trouvée pour " +name +" : "+result.getBoolean("status"));
            status = result.getBoolean("status");
            result.close();

            return status;



        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public static boolean setServerInDB(ServerObject server) {
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("REPLACE INTO network_servers (uuid, name, address, port, motd, players, status, inactive) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        ) {
            statement.setString(1, server.getUuid().toString());
            statement.setString(2, server.getName());
            statement.setString(3, server.getAddress());
            statement.setInt(4, server.getPort());
            statement.setString(5, server.getMotd());
            statement.setInt(6, server.getNbPlayers());
            statement.setInt(7, server.getStatus());
            statement.setBoolean(8, false);

            int id = statement.executeUpdate();

            if (id != 0) {
                LogManager.Admin("Serveur enregistré");
                return true;
            } else {
                LogManager.Admin("Problème dans l'ajout du serveur");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean removeServerInDB(ServerObject server) {
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("REPLACE INTO network_servers (uuid, name, address, port, motd, players, status, inactive) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        ) {
            statement.setString(1, server.getUuid().toString());
            statement.setString(2, server.getName());
            statement.setString(3, server.getAddress());
            statement.setInt(4, server.getPort());
            statement.setString(5, server.getMotd());
            statement.setInt(6, server.getNbPlayers());
            statement.setInt(7, server.getStatus());
            statement.setBoolean(8, true);

            int id = statement.executeUpdate();

            if (id != 0) {
                LogManager.Admin("Serveur inactivé");
                return true;
            } else {
                LogManager.Admin("Problème dans l'inactivation du serveur");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> getServerListFromDB(){
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT * FROM network_servers WHERE inactive = ?");
        ) {
            List<String> serverList = new ArrayList<>();;

            statement.setBoolean(1, false);

            ResultSet result = statement.executeQuery();

            if(!result.next()) return null;

            while (result.next()) {
                serverList.add(result.getString("name"));

            }

            result.close();

            return serverList;



        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getServerList(){

            List<String> srvList = new ArrayList<>();

            for (ServerObject server : serverlist.values()) {
                srvList.add(server.getName());
            }

            return srvList;
    }

    public void refreshServerInstance() {
        LogManager.Info("Rafraîchissement de la liste des serveurs...");

        // 1️⃣ On nettoie la configuration actuelle
        ProxyServer.getInstance().getServers().clear();
        serverlist.clear();

        // 2️⃣ On recharge depuis la base SQL
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement(
                     "SELECT uuid, name, address, port, motd, players, status, inactive " +
                             "FROM network_servers WHERE inactive = false"
             )
        ) {
            ResultSet result = statement.executeQuery();

            int count = 0;

            while (result.next()) {
                String name = result.getString("name");
                String address = result.getString("address");
                int port = result.getInt("port");
                String motd = result.getString("motd");
                int status = result.getInt("status");

                InetSocketAddress socketAddress = new InetSocketAddress(address, port);
                boolean restricted = false; // tu peux changer selon ton modèle

                // 3️⃣ Construction du ServerInfo (côté proxy)
                ServerInfo info = ProxyServer.getInstance().constructServerInfo(name, socketAddress, motd, restricted);

                // 4️⃣ Enregistrement dans le proxy
                ProxyServer.getInstance().getServers().put(name, info);

                // 5️⃣ Construction du ServerObject (ton objet métier)
                ServerObject newServer = new ServerObject(
                        result.getString("uuid"),
                        name,
                        address,
                        port,
                        motd,
                        status,
                        false // maintenance ici à false par défaut, ou à charger d'une autre table
                );

                // 6️⃣ Enregistrement dans la Map interne
                serverlist.put(name, newServer);

                count++;
            }

            result.close();
            LogManager.Info( + count + " serveurs actifs rechargés depuis la base.");

        } catch (SQLException e) {
            LogManager.Warn("Erreur lors du rafraîchissement des serveurs : " + e.getMessage());
            e.printStackTrace();
        }

        // 7️⃣ (Optionnel) — si tu veux afficher dans la console
        for (ServerObject srv : serverlist.values()) {
            LogManager.Info("→ " + srv.getName() + " : " + srv.getAddress() + ":" + srv.getPort());
        }
    }

    public boolean serverListWasModified(){
        boolean modified = false;
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT * FROM network_servers_update WHERE updated = 1");
        ) {


            ResultSet result = statement.executeQuery();

            if(!result.next()) return modified;
            modified = true;
            result.close();

            return modified;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modified;
    }

    public void clearServerListUpdateFlag() {
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement(
                     "UPDATE network_servers_update SET updated = 0 WHERE updated = 1"
             )
        ) {
            int rows = statement.executeUpdate();
            BungeeAPI.logger.info("✅ Drapeau de mise à jour des serveurs réinitialisé (" + rows + " lignes).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
