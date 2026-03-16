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

import static net.boom3r.bungeeapi.BungeeAPI.*;
import static net.boom3r.bungeeapi.core.managers.NetworkSysEvent.AddEvent;

public class BungeeListeners implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        bungeeLogger.DebugV("PostLogin - "+event.getPlayer().getName(),3);
        if (whitelistEnabled) {
            if (WhiteListManager.isWhiteListed(event.getPlayer())) {
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    player.sendMessage(new TextComponent(event.getPlayer().getName() + " a rejoins le network !"));
                }
                AddEvent("BungeeConWL", event.getPlayer().getUniqueId().toString(), "{\"ip\": " +event.getPlayer().getSocketAddress().toString().substring(1, event.getPlayer().getSocketAddress().toString().indexOf(':'))+", \"player\": "+ event.getPlayer().getName()+"}");
            } else {
                event.getPlayer().disconnect(new TextComponent("Désolé mais tu n'est pas sur la whitelist !"));
                AddEvent("BungeeBlockWL", event.getPlayer().getUniqueId().toString(), "{\"ip\": " +event.getPlayer().getSocketAddress().toString().substring(1, event.getPlayer().getSocketAddress().toString().indexOf(':'))+", \"player\": "+ event.getPlayer().getName()+"}");
            }
        } else {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                player.sendMessage(new TextComponent(event.getPlayer().getName() + " a rejoins le network !"));
            }

            AddEvent("BungeeConWL", event.getPlayer().getUniqueId().toString(), "{\"ip\": " +event.getPlayer().getSocketAddress().toString().substring(1, event.getPlayer().getSocketAddress().toString().indexOf(':'))+", \"player\": "+ event.getPlayer().getName()+"}");
        }

    }

    @EventHandler
    public void onPlayerConnect(ServerConnectEvent event){
        bungeeLogger.DebugV("ServerConnectEvent - "+event.getPlayer().getName(),3);
        ProxiedPlayer player = event.getPlayer();
        if(BungeeAPI.maintenance) {
            if ((player.hasPermission("bungeeAPI.maintenance." + event.getTarget().getName().toLowerCase(Locale.ROOT))) || (player.hasPermission("bungeeAPI.maintenance.global"))) {

                NetworkUser newUser = new NetworkUser(event.getPlayer().getUniqueId(),event.getPlayer().getName(),event.getPlayer().getSocketAddress().toString());
                newUser.moveServer("hisroom", "lobby");
                newUser.setOnline();
                redisManager.save("network_user:"+newUser.getUuid().toString(), newUser);
                bungeeLogger.DebugV("Enregistrement en REDIS du user "+newUser.getName(),2);
                AddEvent("BungeeConMaint",
                        event.getPlayer().getUniqueId().toString(),
                        "{\"ip\": " +event.getPlayer().getSocketAddress().toString().substring(1, event.getPlayer().getSocketAddress().toString().indexOf(':'))+", \"player\": "+ event.getPlayer().getName()+"}"
                );
                return;
            } else {
                player.disconnect(BungeeAPI.getFormatedMessage("Désolé, tu n'es pas autorisé à rejoindre pendant la maintenance"));
            }
        } else {
            NetworkUser newUser = NetworkUser.getNetUserFromRedis(event.getPlayer().getUniqueId());
            if (newUser == null) {
                newUser = new NetworkUser(event.getPlayer().getUniqueId(),event.getPlayer().getName(),event.getPlayer().getSocketAddress().toString().substring(1, event.getPlayer().getSocketAddress().toString().indexOf(':')));
                newUser.moveServer("hisroom", "lobby");
                newUser.setOnline();
                if (redisManager.save("network_user:"+newUser.getUuid().toString(), newUser)) bungeeLogger.DebugV("Enregistrement en REDIS du user"+newUser.getName()+" : "+newUser.toJson().toString(),2);
            } else {
                bungeeLogger.DebugV("Chargement à partir de REDIS du user "+newUser.getName(),2);
            }


            AddEvent("BungeeCon", event.getPlayer().getUniqueId().toString(), "{\"ip\": " +event.getPlayer().getSocketAddress().toString().substring(1, event.getPlayer().getSocketAddress().toString().indexOf(':'))+", \"player\": "+ event.getPlayer().getName()+"}");

        }
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        bungeeLogger.DebugV("PreLoginEvent - "+event.getConnection().getSocketAddress(),3);
        bungeeInstance.getProxy().getConsole().sendMessage(new TextComponent(event.getConnection().getSocketAddress() + " a tenté la co !"));
        AddEvent("BungeePreCon", "NO_OWNER", "{\"ip\": " +event.getConnection().getSocketAddress().toString().substring(1, event.getConnection().getSocketAddress().toString().indexOf(':'))+"}");
    }

    @EventHandler
    public void on(PlayerDisconnectEvent event) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(new TextComponent(event.getPlayer().getDisplayName() + " nous a quitté..."));
        }
        NetworkUser user = NetworkUser.getNetUserFromRedis(event.getPlayer().getUniqueId());
        user.setOffline();
        if (bungeeInstance.getNetworkManager().networkGroupManager.isInExistingGroup(user.getUuid())){
            bungeeLogger.DebugV("le joueur était dans un groupe : ",3);
            bungeeInstance.getNetworkManager().networkGroupManager.getUserGroup(user.getUuid()).quitGroup(user.getUuid());
            bungeeInstance.getNetworkManager().networkGroupManager.destroyGroup();
            bungeeLogger.DebugV("le joueur a quitté le groupe",3);
        } else {
            bungeeLogger.DebugV("le joueur n'était pas dans un groupe : ",3);
        }
        networkManager.networkGroupManager.saveInRedis();
        //redisManager.delete("network_user:"+user.getUuid());
        networkManager.getNetworkUserManager().removeNetworkUser(user.getUuid());
    }


    @EventHandler
    public void on(ServerSwitchEvent event) {
        bungeeLogger.DebugV("ServerSwitchEvent - "+event.getPlayer().getName(),3);
        if(event.getFrom() != null){
            bungeeLogger.DebugV(getClass().getName()+">>"+event.getPlayer().getDisplayName() + " viens de " + event.getFrom().getName()+ " pour aller vers "+event.getPlayer().getServer().getInfo().getName(),3);
            NetworkUser user = NetworkUser.getNetUserFromRedis(event.getPlayer().getUniqueId());
            user.moveServer(event.getFrom().getName(), event.getPlayer().getServer().getInfo().getName());
            bungeeLogger.DebugV(getClass().getName()+">>Sauvegarde de "+user.toJson(),3);
            redisManager.save("network_user:"+user.getUuid().toString(), user);
        }
    }

    @EventHandler
    public void on(ServerKickEvent event) {
        bungeeInstance.getProxy().getConsole().sendMessage(new TextComponent(event.getPlayer().getDisplayName() + " a été kické de " + event.getKickedFrom()));
    }

}
