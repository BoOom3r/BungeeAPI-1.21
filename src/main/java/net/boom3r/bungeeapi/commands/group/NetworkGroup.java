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
    private UUID groupUUID;
    private List<NetworkUser> playerList = new ArrayList<>();;
    private NetworkUser groupOwner;
    private String groupName;
    private String groupTag;
    private NetworkGroupManager networkGroupManager;

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
        playerList.add(user);
        //BungeeAPI.redisManager.save("group:"+groupUUID,this);
        return true;
    }

    public boolean quitGroup(NetworkUser user){
        if (user == groupOwner) {
            bungeeLogger.DebugV(user.getName()+" vient de quitter un groupe dont il était leader",2);
            if (playerList.size()-1 > 0){
                bungeeLogger.DebugV("Transfert du lead",2);
                //transfert lead
                //BungeeAPI.redisManager.save("group:"+groupUUID,this);
            } else {
                bungeeLogger.DebugV("Destruction du groupe : plus assez de monde",2);
                networkGroupManager.removeInvite(user);
                this.getNetworkGroupManager().toDestroy.add(this);
                BungeeAPI.redisManager.delete("group:"+groupUUID);
            }
        } else {
            playerList.remove(user);
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
        if (playerList.contains(user) || groupOwner == user) {
            return true;
        }
        return false;
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
            redisManager.save(this.getGroupUUID().toString(),this);
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
}
