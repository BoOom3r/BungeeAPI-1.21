package net.boom3r.bungeeapi.commands.friends;

import net.boom3r.bungeeapi.commands.interfaces.BungeeCommand;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import net.boom3r.bungeeapi.core.utils.DebugUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.boom3r.bungeeapi.BungeeAPI.*;
import static net.boom3r.bungeeapi.commands.friends.FriendManager.*;
import static net.boom3r.bungeeapi.core.utils.NickUuidTool.*;

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

                    UUID playerUuid =  getUuidFromNick(args[1]);
                    if (playerUuid == null) return;
                    if (isFriend(nSender.getUuid(), playerUuid)) {
                        nSender.sendMessage("Tu es déjà ami avec ce joueur.");
                        return;
                    }
                    if (isPendingFriend(nSender.getUuid(), playerUuid)) {
                        nSender.sendMessage("Tu as déjà une demande d'ami en attente pour ce joueur.");
                        return;
                    }

                    if(sendInvite(((ProxiedPlayer) sender).getUniqueId(), playerUuid)) nSender.sendMessage("Ta demande d'ami a bien été envoyée !");;
                }
                case "remove" -> {
                    UUID playerUuid =  getUuidFromNick(args[1]);
                    if (playerUuid == null) return;
                    if (!isFriend(nSender.getUuid(), playerUuid)) {
                        nSender.sendMessage("Tu n'es pas ami avec ce joueur.");
                        return;
                    }
                    removeFromDb(((ProxiedPlayer) sender).getUniqueId(), playerUuid);
                }
                case "accept" -> {
                    UUID playerUuid =  getUuidFromNick(args[1]);
                    if (playerUuid == null) return;
                    if (isFriend(nSender.getUuid(), playerUuid)) {
                        nSender.sendMessage("Tu es déjà ami avec ce joueur.");
                        return;
                    }
                    if (!isPendingFriend(nSender.getUuid(), playerUuid)) {
                        nSender.sendMessage("Tu n'as pas de demande d'ami en attente pour ce joueur.");
                        return;
                    }
                    if (acceptInvite(((ProxiedPlayer) sender).getUniqueId(), playerUuid)){
                        nSender.sendMessage("Ajout de "+args[1]+" en tant qu'ami.");
                        nSender.addFriend(playerUuid);
                    };
                }
                case "deny" -> {
                    UUID playerUuid =  getUuidFromNick(args[1]);
                    if (playerUuid == null) return;
                    if (isFriend(nSender.getUuid(), playerUuid)) {
                        nSender.sendMessage("Tu es déjà ami avec ce joueur. Utilise remove pour le supprimer.");
                        return;
                    }
                    if (!isPendingFriend(nSender.getUuid(), playerUuid)) {
                        nSender.sendMessage("Tu n'as pas de demande d'ami en attente pour ce joueur.");
                        return;
                    }
                    denyRequest(playerUuid, ((ProxiedPlayer) sender).getUniqueId());
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
        List<String> redisPlayerList = redisManager.load("network_user_list", List.class);
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
            for (String uuidPlayer : redisPlayerList){
                String nickname = getPlayerNickFromDB(UUID.fromString(uuidPlayer));
                if (nickname != null){
                    if (!playerlist.contains(nickname)){
                        playerlist.add(nickname);
                    }
                }
            }
            for (String uuidPlayer : networkManager.networkUserManager.getRegisteredPlayerList()){
                String nickname = getPlayerNickFromDB(UUID.fromString(uuidPlayer));
                if (nickname != null){
                    if (!playerlist.contains(nickname)){
                        playerlist.add(nickname);
                    }
                }
            }

            return playerlist;

        }

        // etc.
        return List.of();
    }

}