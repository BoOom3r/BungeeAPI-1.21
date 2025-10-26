package net.boom3r.bungeeapi.listeners;

import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.manager.WhiteListManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Locale;

public class BungeeListeners implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        if(WhiteListManager.isWhiteListed(event.getPlayer())) {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                player.sendMessage(new TextComponent(event.getPlayer().getName() + " a rejoins le network !"));
            }
        } else {
            event.getPlayer().disconnect(new TextComponent("Désolé mais tu n'est pas sur la whitelist !"));
        }
    }

    @EventHandler
    public void onPlayerConnect(ServerConnectEvent event){
        ProxiedPlayer player = event.getPlayer();//A simple variable to define the player
        if(BungeeAPI.maintenance) {
            if ((player.hasPermission("bungeeAPI.maintenance." + event.getTarget().getName().toLowerCase(Locale.ROOT))) || (player.hasPermission("bungeeAPI.maintenance.global"))) {//Check if the player has the right permissions to join
                return;
            } else {
                player.disconnect(BungeeAPI.getFormatedMessage("Désolé, tu n'es pas autorisé à rejoindre pendant la maintenance"));//Here would go the kick method with it's own message!
            }
        }
    }

    @EventHandler
    public void onPostLogin(PreLoginEvent event) {

        BungeeAPI.bungeeInstance.getProxy().getConsole().sendMessage(new TextComponent(event.getConnection().getSocketAddress() + " a tenté la co !"));

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
