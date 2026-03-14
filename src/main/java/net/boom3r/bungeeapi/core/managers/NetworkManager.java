package net.boom3r.bungeeapi.core.managers;

import net.boom3r.bungeeapi.commands.group.NetworkGroupManager;
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
            redisManager.save("network_user:"+uuid,nUser);
        }
    }

    public void removeNetworkUser(UUID uuid){
        //networkUserManager.updateNetworkUserDB(nUser);
        networkUserList.remove(uuid);
        if (redisEnabled){
            redisManager.save("network_user_list",networkUserList);
            redisManager.delete("network_user:"+uuid);
        }

    }



    public Map<UUID, NetworkUser> getNetworkUserList(){

        return networkUserList;
    }
    public Map<UUID, NetworkUser> getNetworkUserMap(){

        return networkUserList;
    }




}
