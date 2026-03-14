package net.boom3r.bungeeapi.commands.group;

import com.google.gson.Gson;
import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.boom3r.bungeeapi.BungeeAPI.*;

public class NetworkGroup {
    private NetworkUser groupOwner;
    private UUID groupUUID;
    private final List<NetworkUser> playerList = new ArrayList<>();
    private String groupName;
    private String groupTag;
    private transient NetworkGroupManager networkGroupManager;

    public NetworkGroup(NetworkUser groupOwner, @Nullable String groupName, @Nullable String groupTag){
        this.groupOwner = groupOwner;
        this.groupUUID = groupOwner.getUuid();
        this.groupName = groupName;
        this.groupTag = groupTag;
        this.networkGroupManager = networkManager.networkGroupManager;
        playerList.add(groupOwner);
        BungeeAPI.redisManager.save("group:"+groupUUID,this);

        if (groupName != null){
            if (groupTag != null){
                groupOwner.sendMessage("Le groupe "+groupName+ " vient d'être créé ! Son tag est "+groupTag);
            } else {
                groupOwner.sendMessage("Le groupe " + groupName + " vient d'être créé !");
            }
        } else {
            groupOwner.sendMessage("Le groupe vient d'être créé !");
        }
    }

    public boolean joinGroup(NetworkUser user){
        if (!networkGroupManager.hasInvite(groupUUID, user.getUuid())){
            user.sendMessage("Tu n'as pas d'invitation de groupe de la part de "+groupOwner.getName());
            return false;
        }
        playerList.add(user);

        bungeeLogger.DebugV(user.getName()+" viens de rejoindre le groupe de "+groupOwner.getName()+". Ses paramètres de base étaient : "+user.getName()+" pour join le groupe "+groupUUID,2);
        BungeeAPI.redisManager.save("group:"+groupUUID,this);
        bungeeLogger.DebugV("Le groupe est sauvegardé en Redis",2);
        networkGroupManager.removeInvite(groupOwner);
        return true;
    }

    public boolean quitGroup(NetworkUser user){
        if (groupOwner.equals(user)) {
            bungeeLogger.DebugV(user.getName()+" vient de quitter un groupe dont il était leader",2);
            if (playerList.size()-1 > 0){
                bungeeLogger.DebugV("Transfert du lead",2);

                saveInRedis();
                //transfert lead
                //BungeeAPI.redisManager.save("group:"+groupUUID,this);
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



    public boolean isInGroup(NetworkUser user){
        return playerList.contains(user) || groupOwner.equals(user);
    }

    public boolean isGroupOwner(NetworkUser user){
        return groupOwner.equals(user);
    }

    public List<NetworkUser> getPlayerList() {
        return playerList;
    }

    public NetworkUser getGroupOwner() {
        return groupOwner;
    }

    public UUID getGroupUUID() {
        return groupUUID;
    }

    public NetworkGroupManager getNetworkGroupManager() {
        return networkGroupManager;
    }

    public void sendMessageToGroup(String msg){
        for (NetworkUser nUser : playerList){
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

    public void transfert(NetworkUser oldOwner, NetworkUser networkUser){
        this.groupOwner = networkUser;
        this.groupUUID = networkUser.getUuid();
        networkGroupManager.networkGroupList.remove(oldOwner.getUuid());
        BungeeAPI.redisManager.delete("group:"+oldOwner.getUuid());
        networkGroupManager.networkGroupList.put(networkUser.getUuid(), this);
        BungeeAPI.redisManager.save("group:"+networkUser.getUuid(),this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NetworkGroup other)) return false;
        return groupUUID.equals(other.groupUUID);
    }

    @Override
    public int hashCode() {
        return groupUUID.hashCode();
    }
}
