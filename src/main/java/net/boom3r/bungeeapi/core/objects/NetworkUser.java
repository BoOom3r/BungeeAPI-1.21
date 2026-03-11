package net.boom3r.bungeeapi.core.objects;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.boom3r.bungeeapi.BungeeAPI.*;

public class NetworkUser {


    private UUID uuid;
    private String name;
    private String ip;
    private boolean online;
    private boolean isLinked;
    private ServerObject actualServer;
    private ServerObject lastServer;
    private List<UUID> friendList = new ArrayList<>();

    public NetworkUser(UUID uuid, String name, String ip, boolean online, boolean isLinked) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.online = online;
        this.isLinked = isLinked;
        this.friendList = new ArrayList<>();

        networkManager.addNetworkUser(uuid, this);
    }
    public NetworkUser(UUID uuid, String name, String ip) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.online = true;
        this.isLinked = false;
        this.friendList = new ArrayList<>();

        networkManager.addNetworkUser(uuid, this);

    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public boolean isLinked() {
        return isLinked;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline() {
        this.online = true;
    }

    public void setOffline() {
        this.online = false;
    }

    public void setLinked(boolean linked) {
        isLinked = linked;
    }

    public void sendMessage(String msg) {
        ProxyServer.getInstance().getPlayer(this.uuid).sendMessage(new TextComponent("§b["+bungeeInstance.getNetworkConf().getNetworkName()+"]§c[System Chat] §f" + msg));
    }

    public NetworkUser getFromRedis(UUID uuid){
        NetworkUser nUser = null;
        nUser = redisManager.load("network_user:"+uuid, NetworkUser.class);
        return nUser;
    }
}
