package net.boom3r.bungeeapi.commands.group;

import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import net.boom3r.bungeeapi.core.utils.DebugUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;
import static net.boom3r.bungeeapi.BungeeAPI.networkManager;

public class GroupCMD extends Command {
    public GroupCMD() {
        super("group", "bungeeAPI.group");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        bungeeLogger.DebugV(DebugUtils.debugCommand("group",args),2);
        if (sender instanceof ProxiedPlayer){
            bungeeLogger.DebugV("La commande group à été faite avec "+args.length+" argument(s)",2);
            NetworkUser nSender = BungeeAPI.networkManager.networkUserList.get(((ProxiedPlayer) sender).getUniqueId());
            if (nSender == null) {
                bungeeLogger.DebugV("Erreur dans la récupération du NetworkSender",3);
                return;
            }
            if(args.length == 0){
                //ouverture du menu groupe
                bungeeLogger.DebugV("ouverture du menu groupe ",3);
                BungeeAPI.bungeeInstance.getNetworkManager().networkGroupManager.openGroupMenu((ProxiedPlayer)sender);
                return;
            }

            bungeeLogger.DebugV("La commande group à été faite avec comme subcommand "+args[0],3);
            // CREATION DE GROUPE
            if (args[0].equalsIgnoreCase("create")){
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
            // DESTRUCTION D UN GROUPE
            if (args[0].equalsIgnoreCase("destroy") || args[0].equalsIgnoreCase("delete") ) {
                if (BungeeAPI.networkManager.networkGroupManager.isGroupOwner(((ProxiedPlayer) sender).getUniqueId())){
                    bungeeLogger.DebugV("Destruction du groupe",3);
                    if (args.length == 2){
                        NetworkUser quitter = NetworkUser.getNetUserFromRedis(ProxyServer.getInstance().getPlayer(args[1]).getUniqueId());
                        if (quitter != null) {
                            networkManager.networkGroupManager.getUserGroup(quitter.getUuid()).quitGroup(quitter.getUuid());
                        }
                    } else {
                        wrongUsage(nSender);
                        // TODO Mauvais usage

                    }

                }
            }

            // DEPART D UN GROUPE
            if (args[0].equalsIgnoreCase("quit")) {
                if (BungeeAPI.networkManager.networkGroupManager.isInExistingGroup(nSender.getUuid())){
                    bungeeLogger.DebugV("Départ du groupe - quit",3);
                    if (args.length == 1){
                        networkManager.networkGroupManager.getUserGroup(nSender.getUuid()).quitGroup(nSender.getUuid());
                        nSender.sendMessage("Tu as quitté ton groupe !");
                    } else {
                        wrongUsage(nSender);
                        // TODO Mauvais usage

                    }
                } else {
                    nSender.sendMessage("Tu n'es pas dans un groupe !");
                }
            }

            // INVITATION A UN GROUPE
            if (args[0].equalsIgnoreCase("invite")) {
                if (BungeeAPI.networkManager.networkGroupManager.isGroupOwner(nSender.getUuid())){
                    if (args.length == 1){
                        bungeeLogger.DebugV("Ouverture du menu d'invitation de groupe",3);

                        return;
                    }
                    if (args.length == 2){
                        ProxiedPlayer proxiedReceiver = ProxyServer.getInstance().getPlayer(args[1]);
                        // Est-ce que le joueur existe
                        if (proxiedReceiver != null) {
                            NetworkUser toInvite = networkManager.networkUserList.get(ProxyServer.getInstance().getPlayer(args[1]).getUniqueId());
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

            if (args[0].equalsIgnoreCase("add")) {
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

            if (args[0].equalsIgnoreCase("transfert")) {
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
                    } else {
                        // TODO le player n'a pas fait la bonne synthaxe
                        wrongUsage(nSender);
                    }
                } else {
                    bungeeLogger.DebugV("Le joueur "+sender.getName()+" n'a pas le droit d'ajouter de force un joueur", 2);

                }
            }

            if (args[0].equalsIgnoreCase("join")) {
                if (args.length == 2) {
                    NetworkUser toJoin = networkManager.getNetworkUserList().get(ProxyServer.getInstance().getPlayer(args[1]).getUniqueId());
                    if (toJoin != null) {
                        if (networkManager.networkGroupManager.isInExistingGroup(nSender.getUuid())) {
                            bungeeLogger.DebugV("Création du groupe impossible : déjà dans un groupe", 3);
                            return;
                        }
                        bungeeLogger.DebugV("Join du joueur " + nSender.getName() + " au groupe du joueur " + toJoin.getName(), 2);
                        // Si joueur a une demande
                        networkManager.networkGroupManager.getUserGroup(toJoin.getUuid()).joinGroup(nSender.getUuid());
                        nSender.sendMessage("Tu viens de rejoindre le groupe de "+args[1]+" !");
                        //nSender.sendMessage("Création du groupe avec le nom "+args[1]);

                    }

                } else {
                    // TODO le player n'a pas fait la bonne synthaxe
                }
            }
        }
    }

    public void wrongUsage(NetworkUser user){
        user.sendMessage("Mauvais usage de la commande");
    }
}
