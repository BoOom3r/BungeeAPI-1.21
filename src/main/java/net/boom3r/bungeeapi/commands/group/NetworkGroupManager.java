package net.boom3r.bungeeapi.commands.group;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jspecify.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeInstance;
import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;

public class NetworkGroupManager {
     Map<UUID, NetworkGroup > networkGroupList;
     public List<NetworkGroup> toDestroy;

    public NetworkGroupManager(){
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
                bungeeLogger.DebugV(" Group détail : "+networkGroup.getGroupUUID()+" avec Owner "+networkGroup.getGroupOwner().getName() +" - "+ networkGroup.getGroupOwner(),3);

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

    public boolean sendInvite(NetworkUser sender, NetworkUser receiver) {
        if (isGroupOwner(bungeeInstance.getNetworkManager().networkUserList.get(sender))) {
            if(!isInExistingGroup(bungeeInstance.getNetworkManager().networkUserList.get(receiver))){
                try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
                     PreparedStatement statement = sql.prepareStatement(
                             "INSERT INTO network_request ('sender', 'receiver', 'type', 'state', 'active') VALUES (?,?,?,1,1)"
                     )
                ) {
                    statement.setString(1, sender.getUuid().toString());
                    statement.setString(2, receiver.getUuid().toString());
                    statement.setString(3, "GROUP");
                    statement.executeUpdate();
                    bungeeLogger.DebugV("Invitation de groupe envoyée !",2);
                    sender.sendMessage("Invitation de groupe envoyée à "+receiver.getName());
                    // pubsub
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                bungeeLogger.DebugV("Le joueur "+receiver+" est déjà dans un groupe", 2);
                sender.sendMessage("Le joueur "+receiver.getName()+" est déjà membre d'un groupe : tu ne peux pas l'inviter.");
                return false;
            }
        } else {
            bungeeLogger.DebugV("Le joueur "+sender+" n'est pas propriétaire du groupe", 2);
            sender.sendMessage("Tu n'es pas propriétaire d'un groupe : tu ne peux pas inviter de joueurs.");
            return false;
        }
    }

    public Map<UUID, NetworkGroup> getNetworkGroupList() {
        return networkGroupList;
    }
}
