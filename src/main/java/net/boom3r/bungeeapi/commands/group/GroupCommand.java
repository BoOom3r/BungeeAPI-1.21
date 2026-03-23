package net.boom3r.bungeeapi.commands.group;

import net.boom3r.bungeeapi.BungeeAPI;
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
import static net.boom3r.bungeeapi.core.utils.NickUuidTool.getPlayerNickFromDB;

public class GroupCommand implements BungeeCommand {
    @Override
    public String getName() { return "group"; }
    @Override
    public String getPermission() { return "bungeeAPI.group"; }
    @Override
    public List<String> getAliases() { return List.of("svrman"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        bungeeLogger.DebugV(DebugUtils.debugCommand("group",args),2);
        if (sender instanceof ProxiedPlayer) {
            bungeeLogger.DebugV("La commande group à été faite", 3);
            NetworkUser nSender = NetworkUser.getNetUserFromRedis(((ProxiedPlayer) sender).getUniqueId());

            if (args.length < 1) {
                sender.sendMessage(new ComponentBuilder("§cUsage: /groupe create|invite|join|transfert|delete ").create());
                return;
            }
            if (args.length == 2 && args[0].toLowerCase().equalsIgnoreCase("list")){
                // TODO List
            }
            switch (args[0].toLowerCase()) {

                case "create" -> {
                    if (networkManager.networkGroupManager.isInExistingGroup(nSender.getUuid())){
                        bungeeLogger.DebugV("Création du groupe impossible : déjà dans un groupe",3);
                        return;
                    }
                    // group create
                    if (args.length == 1){
                        bungeeLogger.DebugV("Création du groupe",3);
                        //nSender.sendMessage("Création du groupe");
                        BungeeAPI.networkManager.networkGroupManager.createGroup(nSender.getUuid(), null, null);
                        return;
                    }
                    // group create <name>
                    if (args.length == 2){
                        if (args[1].length() < 3 || args[1].length() > 12) {
                            nSender.sendMessage("Le nom de ton groupe doit faire plus de 3 caractères et moins de 12");
                            bungeeLogger.DebugV("Nom du groupe non conforme",3);

                            return;
                        } else {
                            bungeeLogger.DebugV("Création du groupe avec le nom "+args[1],3);
                            //nSender.sendMessage("Création du groupe avec le nom "+args[1]);
                            BungeeAPI.networkManager.networkGroupManager.createGroup(nSender.getUuid(), args[1], null);
                            //BungeeAPI.networkManager.networkGroupManager.createGroup(nSender, groupName, null);
                            return;
                        }
                    }
                    // group create <name> <tag>
                    if (args.length == 3){
                        if (args[1].length() < 3 || args[1].length() > 12) {
                            nSender.sendMessage("Le nom de ton groupe doit faire plus de 3 caractères et moins de 12");
                            bungeeLogger.DebugV("Nom du groupe non conforme",3);
                            return;
                        } else {
                            if (args[2].length() < 3 || args[2].length() > 5) {
                                nSender.sendMessage("Le tag de ton groupe doit faire plus de 3 caractères et moins de 5");
                                bungeeLogger.DebugV("Tag du groupe non conforme",3);
                                return;
                            } else {
                                bungeeLogger.DebugV("Création du groupe avec le nom "+args[1]+" et le tag "+args[2], 3);
                                //nSender.sendMessage("Création du groupe avec le nom "+args[1]+" et le tag "+args[2]);
                                BungeeAPI.networkManager.networkGroupManager.createGroup(nSender.getUuid(), args[1], args[2]);
                                //BungeeAPI.networkManager.networkGroupManager.createGroup(nSender, groupName, null);
                                return;
                            }
                        }
                    }
                    bungeeLogger.DebugV("Mauvaise utilisation de la commande. La bonne commande est /group create NomGroupe TagGroup (NomGroup et TagGroup étant facultatifs).",2);
                    nSender.sendMessage("Mauvaise utilisation de la commande. La bonne commande est /group create NomGroupe TagGroup (NomGroup et TagGroup étant facultatifs).");
                }
                case "destroy", "delete", "remove" -> {
                    if (BungeeAPI.networkManager.networkGroupManager.isGroupOwner(((ProxiedPlayer) sender).getUniqueId())){
                        bungeeLogger.DebugV("Destruction du groupe",3);
                        if (args.length == 2){
                            NetworkUser quitter = NetworkUser.getNetUserFromRedis(ProxyServer.getInstance().getPlayer(args[1]).getUniqueId());
                            if (quitter != null) {
                                networkManager.networkGroupManager.getUserGroup(quitter.getUuid()).quitGroup(quitter.getUuid());
                            }
                        } else {
                            // TODO Mauvais usage

                        }
                    }
                }
                case "quit" -> {
                    if (BungeeAPI.networkManager.networkGroupManager.isInExistingGroup(nSender.getUuid())){
                        bungeeLogger.DebugV("Départ du groupe - quit",3);
                        if (args.length == 1){
                            networkManager.networkGroupManager.getUserGroup(nSender.getUuid()).quitGroup(nSender.getUuid());
                            nSender.sendMessage("Tu as quitté ton groupe !");
                        } else {
                            // TODO Mauvais usage
                        }
                    } else {
                        nSender.sendMessage("Tu n'es pas dans un groupe !");
                    }
                }
                case "invite" -> {
                    if (BungeeAPI.networkManager.networkGroupManager.isGroupOwner(nSender.getUuid())){
                        if (args.length == 1){
                            bungeeLogger.DebugV("Ouverture du menu d'invitation de groupe",3);

                            return;
                        }
                        if (args.length == 2){
                            ProxiedPlayer proxiedReceiver = ProxyServer.getInstance().getPlayer(args[1]);
                            // Est-ce que le joueur existe
                            if (proxiedReceiver != null) {
                                NetworkUser toInvite = NetworkUser.getNetUserFromRedis(ProxyServer.getInstance().getPlayer(args[1]).getUniqueId());
                                // Est-ce que le NetUser existe
                                if (toInvite != null) {
                                    bungeeLogger.DebugV("Invitation du joueur avec le pseudo " + args[1], 2);
                                    BungeeAPI.networkManager.networkGroupManager.sendInvite(nSender.getUuid(), toInvite.getUuid());
                                } else {
                                    bungeeLogger.DebugV("Invitation raté du joueur avec le pseudo " + args[1], 2);
                                    nSender.sendMessage("Invitation au groupe impossible : ce joueur n'existe pas");
                                    return;
                                }
                            } else {
                                bungeeLogger.DebugV("Invitation raté du joueur avec le pseudo " + args[1], 2);
                                nSender.sendMessage("Invitation au groupe impossible : ce joueur n'existe pas");
                            }
                        }

                    } else {
                        nSender.sendMessage("Tu n'as pas encore de groupe ou tu n'es pas propriétaire du groupe dans lequel tu te trouves");
                    }
                }
                case "add" -> {
                    if (sender.hasPermission("bungeeAPI.group.add")) {
                        if (BungeeAPI.networkManager.networkGroupManager.isGroupOwner((nSender.getUuid()))) {

                            if (args.length == 1) {
                                bungeeLogger.DebugV("Ouverture du menu d'invitation de groupe forcé", 2);
                                //nSender.sendMessage("Création du groupe");
                                return;
                            }
                            if (args.length == 2) {
                                ProxiedPlayer toInvite = ProxyServer.getInstance().getPlayer(args[1]);
                                if (toInvite != null) {
                                    bungeeLogger.DebugV("Ajout forcé du joueur avec le pseudo " + args[1], 2);
                                    //nSender.sendMessage("Création du groupe avec le nom "+args[1]);

                                }
                            }

                        } else {
                            bungeeLogger.DebugV("Le joueur "+sender.getName()+" n'est pas propriétaire d'un groupe", 2);
                        }
                    } else {
                        bungeeLogger.DebugV("Le joueur " + sender.getName() + " n'a pas le droit d'ajouter de force un joueur", 2);

                    }
                }

                case "transfert" -> {
                    if (BungeeAPI.networkManager.networkGroupManager.isGroupOwner((nSender.getUuid()))
                            || sender.hasPermission("bungeeAPI.group.admin")){
                        if (args.length == 2){
                            ProxiedPlayer toTransfert = ProxyServer.getInstance().getPlayer(args[1]);
                            if (toTransfert != null) {
                                bungeeLogger.DebugV("Transfert du lead au joueur" + args[1], 2);
                                NetworkGroup oldGroup = BungeeAPI.networkManager.networkGroupManager.getUserGroup(((ProxiedPlayer) sender).getUniqueId());
                                oldGroup.transfert(nSender.getUuid(), toTransfert.getUniqueId());
                                //nSender.sendMessage("Création du groupe avec le nom "+args[1]);

                            }
                        }
                    } else {
                        bungeeLogger.DebugV("Le joueur "+sender.getName()+" n'a pas le droit d'ajouter de force un joueur", 2);

                    }
                }

                case "join" -> {
                    if (args.length == 2) {
                        NetworkUser toJoin = NetworkUser.getNetUserFromRedis(ProxyServer.getInstance().getPlayer(args[1]).getUniqueId());
                        if (toJoin != null) {
                            if (networkManager.networkGroupManager.isInExistingGroup(nSender.getUuid())) {
                                bungeeLogger.DebugV("Création du groupe impossible : déjà dans un groupe", 3);
                                return;
                            }
                            bungeeLogger.DebugV("Join du joueur " + nSender.getName() + " au groupe du joueur " + toJoin.getName(), 2);

                            // Si joueur a une demande
                            networkManager.networkGroupManager.getUserGroup(toJoin.getUuid()).joinGroup(nSender.getUuid());
                            nSender.sendMessage("Tu viens de rejoindre le groupe de "+args[1]+" !");

                        }

                    }
                }
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
            return List.of("create", "add", "join", "quit", "destroy", "remove", "delete", "transfert");
        }
        if (args.length == 2
                && (args[0].equalsIgnoreCase("join")
                || args[0].equalsIgnoreCase("invite")
                || args[0].equalsIgnoreCase("transfert")
        )) {
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
        if (args.length == 2
            && sender.hasPermission("BungeeAPI.groupadmin")
            && (args[0].equalsIgnoreCase("add")
            || args[0].equalsIgnoreCase("destroy")
            || args[0].equalsIgnoreCase("transfert")
        )) {
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
        if (args.length == 2
                && (args[0].equalsIgnoreCase("create")
        )) {
            return List.of("<nom du groupe>");
        }
        if (args.length == 3
                && (args[0].equalsIgnoreCase("create")
        )) {
            return List.of("<tag du groupe>");
        }
        // etc.
        return List.of();
    }

}