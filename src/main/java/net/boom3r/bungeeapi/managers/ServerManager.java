package net.boom3r.bungeeapi.managers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;

public class ServerManager {

    public static void addServer(String name, InetSocketAddress address, String motd, boolean restricted) {
        ProxyServer.getInstance().getServers().put(name, ProxyServer.getInstance().constructServerInfo(name, address, motd, restricted));
    }

    public static void removeServer(String name) {
        for (ProxiedPlayer p : ProxyServer.getInstance().getServerInfo(name).getPlayers()) {
            p.disconnect(new TextComponent("This server was forcefully closed.\nPlease report this error in the bug report section of the forums."));
        }
        ProxyServer.getInstance().getServers().remove(name);
    }

}
