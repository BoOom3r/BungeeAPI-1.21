package net.boom3r.bungeeapi.core.managers;

import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.objects.ServerObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerManager {

    private static Map<UUID, ServerObject> serverlist;

    public ServerManager() {
        serverlist = new HashMap<>();
    }

    public static void addServer(String name, InetSocketAddress address, String motd, boolean restricted) {
        ProxyServer.getInstance().getServers().put(name, ProxyServer.getInstance().constructServerInfo(name, address, motd, restricted));
        // TODO serverlist.put()
        // TODO add to SQL
    }

    public static void removeServer(String name) {
        for (ProxiedPlayer p : ProxyServer.getInstance().getServerInfo(name).getPlayers()) {
            p.disconnect(new TextComponent("This server was forcefully closed.\nPlease report this error in the bug report section of the forums."));
        }
        ProxyServer.getInstance().getServers().remove(name);
        // TODO serverlist.remove()
        // TODO remove from SQL
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

                serverlist.put(UUID.fromString(result.getString("uuid")), actualServer);
                BungeeAPI.bungeeInstance.getProxy().getConsole().sendMessage(
                        new ComponentBuilder(
                                actualServer.getName() +
                                        " - " + actualServer.getAddress() +
                                        ":" + actualServer.getPort() +
                                        " >> " + actualServer.getMotd() +
                                        " --> " + actualServer.getMaintenance()
                        ).create()
                );
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

}
