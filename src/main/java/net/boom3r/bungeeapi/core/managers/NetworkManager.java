package net.boom3r.bungeeapi.core.managers;

import net.boom3r.bungeeapi.commands.group.NetworkGroupManager;
import net.boom3r.bungeeapi.core.objects.NetworkUser;

import java.util.*;

import static net.boom3r.bungeeapi.BungeeAPI.redisEnabled;
import static net.boom3r.bungeeapi.BungeeAPI.redisManager;

public class NetworkManager {


    public NetworkUserManager networkUserManager;
    public NetworkGroupManager networkGroupManager;

    public NetworkManager() {
        networkUserManager = new NetworkUserManager();
        networkGroupManager = new NetworkGroupManager();
    }




    public List<UUID> getNetworkUserList(){

        return networkUserManager.networkUserList;
    }

    public NetworkUserManager getNetworkUserManager() {
        return networkUserManager;
    }

    public NetworkGroupManager getNetworkGroupManager() {
        return networkGroupManager;
    }
}
