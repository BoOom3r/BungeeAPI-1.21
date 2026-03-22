package net.boom3r.bungeeapi.commands.friends;

import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.commands.BungeeCommand;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import net.boom3r.bungeeapi.core.utils.DebugUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;
import static net.boom3r.bungeeapi.commands.friends.FriendManager.*;

public class FriendCommand  implements BungeeCommand {
    @Override
    public String getName() { return "friend"; }
    @Override
    public String getPermission() { return "bungeeAPI.friend"; }
    @Override
    public List<String> getAliases() { return List.of("svrman"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        bungeeLogger.DebugV(DebugUtils.debugCommand("friend",args),2);
        if (sender instanceof ProxiedPlayer) {
            bungeeLogger.DebugV("La commande friend à été faite", 2);
            NetworkUser nSender = NetworkUser.getNetUserFromRedis(((ProxiedPlayer) sender).getUniqueId());
            if (args.length < 1) {
                sender.sendMessage(new ComponentBuilder("§cUsage: /friend add|remove|accept|deny pseudo").create());
                return;
            }
            if (args.length == 2 && args[0].toLowerCase().equalsIgnoreCase("list")){
                // TODO List
            }
            switch (args[0].toLowerCase()) {
                case "add" -> {
                    // Envoie d'une invite à l'ami
                    // Vérification si l'ami a déjà envoyé une invitation
                    if (isFriend(nSender.getUuid(), ProxyServer.getInstance().getPlayer(args[1]).getUniqueId())) {
                        nSender.sendMessage("Tu es déjà ami avec ce joueur.");
                        return;
                    }
                    if (isPendingFriend(nSender.getUuid(), ProxyServer.getInstance().getPlayer(args[1]).getUniqueId())) {
                        nSender.sendMessage("Tu as déjà ami une demande d'ami en attente pour ce joueur.");
                        return;
                    }

                    sendInvite(((ProxiedPlayer) sender).getUniqueId(), ProxyServer.getInstance().getPlayer(args[1]).getUniqueId());
                }
                case "remove" -> {
                    removeFromDb(ProxyServer.getInstance().getPlayer(args[1]).getUniqueId());
                }
                case "accept" -> {
                    UUID owner =  ProxyServer.getInstance().getPlayer(args[1]).getUniqueId();
                    if (owner == null ){
                        owner = getPlayerUUIDFromDB(owner);
                        if (owner == null){
                            bungeeLogger.DebugV("Pas d'utilisateur connu",2);
                            return;
                        }
                    }
                    acceptInvite(((ProxiedPlayer) sender).getUniqueId(), owner);
                }
                case "deny" -> {
                    denyRequest(ProxyServer.getInstance().getPlayer(args[1]).getUniqueId(), ((ProxiedPlayer) sender).getUniqueId());
                }
                case "list" -> { /* logique de suppression */ }
                default ->
                        sender.sendMessage(new ComponentBuilder("§cUsage: /servermanager add|remove|accept|deny|list …").create());
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> playerlist = new ArrayList<>();
        if (args.length == 1) {
            return List.of("add", "remove", "accept", "deny", "list");
        }
        if (args.length == 2
                && (args[0].equalsIgnoreCase("add")
                || args[0].equalsIgnoreCase("remove")
                || args[0].equalsIgnoreCase("deny")
                || args[0].equalsIgnoreCase("accept"))) {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
                playerlist.add(player.getDisplayName());
            }
            return playerlist;

        }

        // etc.
        return List.of();
    }

    public UUID getPlayerUUIDFromDB(UUID uuid){
        UUID playerUuid = null;
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT network_servers.uuid, network_servers.name, network_servers.address, network_servers.port, network_servers.motd, network_servers.players, network_servers.status, network_maintenance.status AS maintenance FROM network_servers LEFT JOIN network_maintenance ON network_servers.name = network_maintenance.server_name WHERE inactive = false");
        ) {
            statement.setString(2, uuid.toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                playerUuid = java.util.UUID.fromString(result.getString("uuid"));
            }

            result.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerUuid;
    }
}