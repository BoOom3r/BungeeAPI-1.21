package net.boom3r.bungeeapi.core.managers;

import net.boom3r.bungeeapi.core.objects.NetworkUser;

import java.util.*;

public class NetworkManager {

    public Map<UUID, NetworkUser> networkUserList;
    public NetworkUserManager networkUserManager;

    public NetworkManager() {
        networkUserList = new HashMap<>();
        networkUserManager = new NetworkUserManager();
    }
    public void addNetworkUser(UUID uuid, NetworkUser nUser){
        networkUserManager.updateNetworkUserDB(nUser);
        networkUserList.put(uuid, nUser);
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
