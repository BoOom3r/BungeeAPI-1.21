package net.boom3r.bungeeapi.commands.group;

import com.google.gson.Gson;
import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static net.boom3r.bungeeapi.BungeeAPI.*;

public class NetworkGroup {
    private UUID groupOwner;
    private UUID groupUUID;
    private final List<UUID> playerList = new ArrayList<>();
    private String groupName;
    private String groupTag;
    private transient NetworkGroupManager networkGroupManager;

    public NetworkGroup(UUID groupOwner, @Nullable String groupName, @Nullable String groupTag){
        this.groupOwner = groupOwner;
        this.groupUUID = groupOwner;
        this.groupName = groupName;
        this.groupTag = groupTag;
        this.networkGroupManager = networkManager.networkGroupManager;
        playerList.add(groupOwner);
        BungeeAPI.redisManager.save("group:"+groupUUID,this);

        if (groupName != null){
            if (groupTag != null){
                getNetworkUser(groupOwner).sendMessage("Le groupe "+groupName+ " vient d'être créé ! Son tag est "+groupTag);
            } else {
                getNetworkUser(groupOwner).sendMessage("Le groupe " + groupName + " vient d'être créé !");
            }
        } else {
            getNetworkUser(groupOwner).sendMessage("Le groupe vient d'être créé !");
        }
    }

    public boolean joinGroup(UUID user){
        if (!networkGroupManager.hasInvite(groupUUID, user)){
            getNetworkUser(user).sendMessage("Tu n'as pas d'invitation de groupe de la part de "+groupOwner);
            return false;
        }
        playerList.add(user);

        bungeeLogger.DebugV(getNetworkUser(user).getName()+" viens de rejoindre le groupe de "+getNetworkUser(groupOwner).getName()+". Ses paramètres de base étaient : "+getNetworkUser(user).getName()+" pour join le groupe "+groupUUID,2);
        BungeeAPI.redisManager.save("group:"+groupUUID,this);
        bungeeLogger.DebugV("Le groupe est sauvegardé en Redis",2);
        networkGroupManager.removeInvite(groupOwner);
        assert getNetworkUser(user).getActualServer() != null;
        if (!getNetworkUser(user).getActualServer().equalsIgnoreCase(getNetworkUser(groupOwner).getActualServer())) {
            bungeeLogger.DebugV("téléportation de "+user+" vers "+ProxyServer.getInstance().getPlayer(groupOwner).getServer().getInfo().getName(),2);
            ProxyServer.getInstance().getPlayer(user).connect(ProxyServer.getInstance().getPlayer(groupOwner).getServer().getInfo());
        }
        return true;
    }

    public boolean quitGroup(UUID user){
        if (groupOwner.equals(user)) {
            bungeeLogger.DebugV(user+" vient de quitter un groupe dont il était leader",2);
            if (playerList.size()-1 > 0){
                bungeeLogger.DebugV("Transfert du lead",2);
                this.transfert(user);
                //saveInRedis();
                //transfert lead
                //BungeeAPI.redisManager.save("group:"+groupUUID,this);
                networkGroupManager.removeInvite(user);
                saveInRedis();
            } else {
                bungeeLogger.DebugV("Destruction du groupe : plus assez de monde",2);
                networkGroupManager.removeInvite(user);
                this.getNetworkGroupManager().toDestroy.add(this);
                BungeeAPI.redisManager.delete("group:"+groupUUID);
            }
        } else {
            playerList.remove(user);
            saveInRedis();
            if (playerList.size() == 0){
                //destruction du groupe
                bungeeLogger.DebugV("Destruction du groupe : plus personne",2);

                this.getNetworkGroupManager().toDestroy.add(this);
                BungeeAPI.redisManager.delete("group:"+groupUUID);
            }
        }

        return true;
    }



    public boolean isInGroup(UUID user){
        return playerList.contains(user) || groupOwner.equals(user);
    }

    public boolean isGroupOwner(UUID user){
        return groupOwner.equals(user);
    }

    public List<UUID> getPlayerList() {
        return playerList;
    }

    public NetworkUser getGroupOwner() {
        return NetworkUser.getNetUserFromRedis(groupOwner);
    }

    public UUID getGroupUUID() {
        return groupUUID;
    }

    public NetworkGroupManager getNetworkGroupManager() {
        return networkGroupManager;
    }

    public void sendMessageToGroup(String msg){
        for (UUID uuid : playerList){
            NetworkUser nUser = getNetworkUser(uuid);
            nUser.sendMessage(msg);
        }
    }

    public Gson toJson(){
         Gson json = new Gson();
         json.toJson(this);
         return json;
    }

    public boolean saveInRedis(){
        if (redisEnabled){
            redisManager.save("group:"+this.getGroupUUID().toString(),this);
        }
        return false;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupTag() {
        return groupTag;
    }

    public void transfert(UUID oldOwner, UUID networkUser){
        this.groupOwner = networkUser;
        this.groupUUID = networkUser;
        networkGroupManager.networkGroupList.remove(oldOwner);
        BungeeAPI.redisManager.delete("group:"+oldOwner);
        networkGroupManager.networkGroupList.put(networkUser, this);
        BungeeAPI.redisManager.save("group:"+networkUser,this);
    }

    public void transfert(UUID oldOwner){
        if (this.playerList.size() > 1){
            List<UUID>toShuffle = playerList;
            Collections.shuffle(toShuffle);
            this.groupOwner = toShuffle.getFirst();
            this.groupUUID = toShuffle.getFirst();
        } else {
            this.groupOwner = playerList.getFirst();
            this.groupUUID = playerList.getFirst();
        }

        networkGroupManager.networkGroupList.remove(oldOwner);
        BungeeAPI.redisManager.delete("group:"+oldOwner);
        networkGroupManager.networkGroupList.put(this.groupUUID, this);
        BungeeAPI.redisManager.save("group:"+this.groupUUID,this);
        bungeeLogger.DebugV("Transfert du group vers "+this.getGroupUUID()+". Sauvegarde en Redis de"+this.toJson(),2 );
    }

    public void teleportPlayer(UUID user){
        if (!ProxyServer.getInstance().getPlayer(user).getServer().getInfo().getName().equalsIgnoreCase(ProxyServer.getInstance().getPlayer(groupOwner).getServer().getInfo().getName())) {
            ProxyServer.getInstance().getPlayer(user).connect(ProxyServer.getInstance().getPlayer(groupOwner).getServer().getInfo());
        }
    }

    public void teleportPlayers(){
        for (UUID user : playerList){
            if (!ProxyServer.getInstance().getPlayer(user).getServer().getInfo().getName().equalsIgnoreCase(ProxyServer.getInstance().getPlayer(groupOwner).getServer().getInfo().getName())) {
                ProxyServer.getInstance().getPlayer(user).connect(ProxyServer.getInstance().getPlayer(groupOwner).getServer().getInfo());
            }
        }
    }

    public NetworkUser getNetworkUser(UUID uuid){
        return NetworkUser.getNetUserFromRedis(uuid);
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) return true;
//        if (!(obj instanceof NetworkGroup other)) return false;
//        return groupUUID.equals(other.groupUUID);
//    }
//
//    @Override
//    public int hashCode() {
//        return groupUUID.hashCode();
//    }
}
