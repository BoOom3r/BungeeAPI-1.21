package net.boom3r.bungeeapi.core.objects;

import java.util.UUID;

import static net.boom3r.bungeeapi.BungeeAPI.networkManager;

public class NetworkUser {


    private UUID uuid;
    private String name;
    private String ip;
    private boolean online;
    private boolean isLinked;

    public NetworkUser(UUID uuid, String name, String ip, boolean online, boolean isLinked) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.online = online;
        this.isLinked = isLinked;

        networkManager.addNetworkUser(uuid, this);
    }
    public NetworkUser(UUID uuid, String name, String ip) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
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
}
