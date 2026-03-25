package net.boom3r.bungeeapi.core.objects;

import com.google.gson.Gson;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jspecify.annotations.Nullable;

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
    private String actualServer;
    private String lastServer;
    private List<UUID> friendList = new ArrayList<>();
    private String skinValue;
    private String skinSignature;


    public NetworkUser(UUID uuid, String name, String ip, boolean online, boolean isLinked) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.online = online;
        this.isLinked = isLinked;
        this.friendList = new ArrayList<>();
        this.actualServer = null;
        this.lastServer = null;

        networkManager.networkUserManager.addNetworkUser(uuid, this);
    }
    public NetworkUser(UUID uuid, String name, String ip) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.online = true;
        this.isLinked = false;
        this.friendList = new ArrayList<>();
        this.actualServer = "hisroom";
        this.lastServer = "hisroom";

        networkManager.networkUserManager.addNetworkUser(uuid, this);

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

    public ProxiedPlayer getPlayer(){
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    public String getActualServer() {
        return actualServer;
    }

    public String getLastServer() {
        return lastServer;
    }

    public String toJson(){
        Gson json = new Gson();
        return json.toJson(this);
    }

    public void moveServer(String oldServer, String newServer){
        this.lastServer = oldServer;
        this.actualServer = newServer;
        bungeeLogger.DebugV(getClass().getName()+"le joueur "+this.getName()+" va vers "+newServer+". Il vient de "+oldServer,3);
    }

    public static NetworkUser getNetUserFromRedis(UUID uuid){
        NetworkUser nUser = null;
        nUser = bungeeInstance.getRedisManager().load("network_user:"+uuid, NetworkUser.class);
        return nUser;
    }

    public List<UUID> getFriendList() {
        return friendList;
    }

    public void addFriend(UUID friendList) {
        this.friendList.add(friendList);
    }

    //    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) return true;
//        if (!(obj instanceof NetworkUser other)) return false;
//        return uuid.equals(other.uuid);
//    }
//
//    @Override
//    public int hashCode() {
//        return uuid.hashCode();
//    }

    public void updateSkin(String value, String signature) {
        this.skinValue = value;
        this.skinSignature = signature;
    }

    public String getSkinValue() { return skinValue; }
    public String getSkinSignature() { return skinSignature; }

}
