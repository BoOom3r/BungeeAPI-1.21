package net.boom3r.bungeeapi.core.managers;

import net.boom3r.bungeeapi.core.objects.NetworkGroup;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.boom3r.bungeeapi.BungeeAPI.redisEnabled;
import static net.boom3r.bungeeapi.BungeeAPI.redisManager;

public class NetworkGroupManager {
     Map<UUID, NetworkGroup > networkGroupList;
     public List<NetworkGroup> toDestroy;

    NetworkGroupManager(){
        this.networkGroupList = new HashMap<>();
    }

    public boolean createGroup(NetworkUser owner, @Nullable String groupName, @Nullable String groupTag){
        if (isInExistingGroup(owner)) {
            owner.sendMessage("Tu es déjà dans un groupe ! Quitte le avant d'en créer un...");
            return false;
        }
        NetworkGroup toAdd = new NetworkGroup(owner,groupName,groupTag);
        networkGroupList.put(owner.getUuid(), toAdd);
        return true;
    }

    public boolean isInExistingGroup(NetworkUser user){
        for (NetworkGroup networkGroup : networkGroupList.values()){
            if (networkGroup.isInGroup(user)){
                return true;
            }
        }
        return false;
    }

    public boolean isGroupOwner(NetworkUser user){
        for (NetworkGroup networkGroup : networkGroupList.values()){
            if (networkGroup.getGroupOwner() == user){
                return true;
            }
        }
        return false;
    }

    public boolean destroyGroup(){
        List<NetworkGroup> cloneTmp = toDestroy;
        for (NetworkGroup networkGroup : cloneTmp){
            networkGroupList.remove(networkGroup.getGroupUUID());
            toDestroy.remove(networkGroup);
            networkGroup = null;

        }

        return true;
    }

    public NetworkGroup getUserGroup(NetworkUser user){
        for (NetworkGroup networkGroup : networkGroupList.values()){
            if (networkGroup.isInGroup(user)){
                return networkGroup;
            }
        }
        return null;
    }



}
