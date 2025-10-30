package net.boom3r.bungeeapi.core.listeners;

import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.managers.WhiteListManager;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Locale;

import static net.boom3r.bungeeapi.BungeeAPI.whitelistEnabled;
import static net.boom3r.bungeeapi.core.managers.NetworkSysEvent.AddEvent;

public class BungeeListeners implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        if (whitelistEnabled) {
            if (WhiteListManager.isWhiteListed(event.getPlayer())) {
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    player.sendMessage(new TextComponent(event.getPlayer().getName() + " a rejoins le network !"));
                    AddEvent("BungeeConWL", event.getPlayer().getUniqueId().toString(), "{\"ip\": " +event.getPlayer().getSocketAddress().toString()+", \"player\": "+ event.getPlayer().getName()+"}");
                }
            } else {
                event.getPlayer().disconnect(new TextComponent("Désolé mais tu n'est pas sur la whitelist !"));
                AddEvent("BungeeBlockWL", event.getPlayer().getUniqueId().toString(), "{\"ip\": " +event.getPlayer().getSocketAddress().toString()+", \"player\": "+ event.getPlayer().getName()+"}");
            }
        } else {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                player.sendMessage(new TextComponent(event.getPlayer().getName() + " a rejoins le network !"));
                AddEvent("BungeeConWL", event.getPlayer().getUniqueId().toString(), "{\"ip\": " +event.getPlayer().getSocketAddress().toString()+", \"player\": "+ event.getPlayer().getName()+"}");
            }
        }
    }

    @EventHandler
    public void onPlayerConnect(ServerConnectEvent event){
        ProxiedPlayer player = event.getPlayer();
        if(BungeeAPI.maintenance) {
            if ((player.hasPermission("bungeeAPI.maintenance." + event.getTarget().getName().toLowerCase(Locale.ROOT))) || (player.hasPermission("bungeeAPI.maintenance.global"))) {
                NetworkUser newUser = new NetworkUser(event.getPlayer().getUniqueId(),event.getPlayer().getName(),event.getPlayer().getSocketAddress().toString());

                AddEvent("BungeeCon", event.getPlayer().getUniqueId().toString(), "{\"ip\": " +event.getPlayer().getSocketAddress().toString()+", \"player\": "+ event.getPlayer().getName()+"}");
                return;
            } else {
                player.disconnect(BungeeAPI.getFormatedMessage("Désolé, tu n'es pas autorisé à rejoindre pendant la maintenance"));
            }
        }
    }

    @EventHandler
    public void onPostLogin(PreLoginEvent event) {

        BungeeAPI.bungeeInstance.getProxy().getConsole().sendMessage(new TextComponent(event.getConnection().getSocketAddress() + " a tenté la co !"));
        AddEvent("BungeePreCon", "NO_OWNER", "{\"ip\": " +event.getConnection().getSocketAddress().toString()+"}");
    }

    @EventHandler
    public void on(PlayerDisconnectEvent event) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(new TextComponent(event.getPlayer().getDisplayName() + " nous a quitté..."));
        }
    }

    @EventHandler
    public void on(ServerSwitchEvent event) {
        if(event.getFrom() != null){
            BungeeAPI.bungeeInstance.getProxy().getConsole().sendMessage(new TextComponent(event.getPlayer().getDisplayName() + " viens de " + event.getFrom().getName()));
        }
    }

    @EventHandler
    public void on(ServerKickEvent event) {
        BungeeAPI.bungeeInstance.getProxy().getConsole().sendMessage(new TextComponent(event.getPlayer().getDisplayName() + " a été kické de " + event.getKickedFrom()));
    }


}
