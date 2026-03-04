package net.boom3r.bungeeapi.core.objects;

import com.google.gson.Gson;
import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.managers.LogManager;
import net.boom3r.bungeeapi.core.managers.NetworkGroupManager;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static net.boom3r.bungeeapi.BungeeAPI.*;

public class NetworkGroup {
    private UUID groupUUID;
    private List<NetworkUser> playerList;
    private NetworkUser groupOwner;
    private String groupName;
    private String groupTag;
    private NetworkGroupManager networkGroupManager;

    public NetworkGroup(NetworkUser groupOwner, @Nullable String groupName, @Nullable String groupTag){
        this.groupOwner = groupOwner;
        this.groupUUID = groupOwner.getUuid();
        this.groupName = groupName;
        this.groupTag = groupTag;
        this.networkGroupManager = BungeeAPI.networkManager.networkGroupManager;
        playerList.add(groupOwner);
        BungeeAPI.redisManager.save("group:"+groupUUID,this);
    }

    public boolean joinGroup(NetworkUser user){
        playerList.add(user);
        BungeeAPI.redisManager.save("group:"+groupUUID,this);
        return true;
    }

    public boolean quitGroup(NetworkUser user){
        if (user == groupOwner) {
            bungeeLogger.Admin(user.getName()+" vient de quitter un groupe dont il était leader");
            //dissolution du groupe ou transfert lead
            BungeeAPI.redisManager.save("group:"+groupUUID,this);
        } else {
            playerList.remove(user);
            if (playerList.size() == 0){
                //destruction du groupe
                bungeeLogger.Admin("Destruction du groupe");
                this.getNetworkGroupManager().toDestroy.add(this);
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
}
