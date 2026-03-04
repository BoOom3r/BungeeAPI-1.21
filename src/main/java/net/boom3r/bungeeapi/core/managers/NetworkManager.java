package net.boom3r.bungeeapi.core.managers;

import net.boom3r.bungeeapi.core.objects.NetworkUser;

import java.util.*;

import static net.boom3r.bungeeapi.BungeeAPI.redisEnabled;
import static net.boom3r.bungeeapi.BungeeAPI.redisManager;

public class NetworkManager {

    public Map<UUID, NetworkUser> networkUserList;
    public NetworkUserManager networkUserManager;
    public NetworkGroupManager networkGroupManager;

    public NetworkManager() {
        networkUserList = new HashMap<>();
        networkUserManager = new NetworkUserManager();
        networkGroupManager = new NetworkGroupManager();
    }
    public void addNetworkUser(UUID uuid, NetworkUser nUser){
        networkUserManager.updateNetworkUserDB(nUser);
        networkUserList.put(uuid, nUser);
        if (redisEnabled){
            redisManager.save("network_user_list",networkUserList);
        }
    }

    public void removeNetworkUser(UUID uuid){
        networkUserList.remove(uuid);
    }

    public List<NetworkUser> getNetworkUserList(){
        List<NetworkUser> list = new ArrayList<>();
        for(Map.Entry<UUID, NetworkUser> entry : networkUserList.entrySet()){
            list.add(entry.getValue());
        }
        return list;
    }

    public Map<UUID, NetworkUser> getNetworkUserMap(){
        return networkUserList;
    }


}
