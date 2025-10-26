package net.boom3r.bungeeapi.core.manager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MessengerManager {

    public static void sendToAdmins(String message) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.hasPermission("bungeeAPI.admin.messages")) {
                player.sendMessage(new TextComponent(ChatColor.stripColor("AQUA")+"[BungeeAPI]"+ChatColor.stripColor("RESET")+"§c[Admin Chat] §f" + message));
            }
        }
    }
}
