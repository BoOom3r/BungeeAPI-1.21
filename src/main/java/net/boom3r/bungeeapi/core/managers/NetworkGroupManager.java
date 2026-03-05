package net.boom3r.bungeeapi.core.managers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.boom3r.bungeeapi.core.objects.NetworkGroup;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static net.boom3r.bungeeapi.BungeeAPI.redisEnabled;
import static net.boom3r.bungeeapi.BungeeAPI.redisManager;

public class NetworkGroupManager {
     Map<UUID, NetworkGroup > networkGroupList;
     public List<NetworkGroup> toDestroy;

    NetworkGroupManager(){
        this.networkGroupList = new HashMap<>();
        toDestroy = new ArrayList<>();
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

    public boolean destroyGroup() {
        for (NetworkGroup ng : toDestroy) {
            networkGroupList.remove(ng.getGroupUUID());
        }
        toDestroy.clear();
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

    public void openGroupMenu(ProxiedPlayer player)
    {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        // perform a check to see if globally are no players
        if ( networkPlayers == null || networkPlayers.isEmpty() )
        {
            return;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "GroupMenuOpen" ); // the channel could be whatever you want
        out.writeUTF(player.getUniqueId().toString()); // this data could be whatever you want


        // we send the data to the server
        // using ServerInfo the packet is being queued if there are no players in the server
        // using only the server to send data the packet will be lost if no players are in it
        player.getServer().getInfo().sendData( "bungee:group", out.toByteArray() );
    }



}
