package net.boom3r.bungeeapi.commands.friends;

import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import net.boom3r.bungeeapi.core.utils.DebugUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;
import static net.boom3r.bungeeapi.commands.friends.FriendManager.*;

public class FriendCMD extends Command {
    public FriendCMD() {
        super("group", "bungeeAPI.friend");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        bungeeLogger.DebugV(DebugUtils.debugCommand("friend",args),2);
        if (sender instanceof ProxiedPlayer){
            bungeeLogger.DebugV("La commande group à été faite",2);
            NetworkUser nSender = BungeeAPI.networkManager.networkUserList.get(((ProxiedPlayer) sender).getUniqueId());
            if(args.length == 1){
                //ouverture du menu friend
            }

            if (args.length == 2) {
                    wrongUse(nSender);
            }

            if (args.length == 3) {
                String subCmd = args[0];
                String pseudo = args[1];

                switch (subCmd){
                    case "add":
                        // Envoie d'une invite à l'ami
                        // Vérification si l'ami a déjà envoyé une invitation
                        if (isFriend(nSender.getUuid(), ProxyServer.getInstance().getPlayer(pseudo).getUniqueId())) {
                            nSender.sendMessage("Tu es déjà ami avec ce joueur.");
                            return;
                        }
                        if (isPendingFriend(nSender.getUuid(), ProxyServer.getInstance().getPlayer(pseudo).getUniqueId())){
                            nSender.sendMessage("Tu as déjà ami une demande d'ami en attente pour ce joueur.");
                            return;
                        }

                        sendInvite(((ProxiedPlayer) sender).getUniqueId(), ProxyServer.getInstance().getPlayer(pseudo).getUniqueId());
                        break;
                    case "del":
                        // Suppression du lien dans la base de donnée
                        removeFromDb(ProxyServer.getInstance().getPlayer(pseudo).getUniqueId());
                        break;
                    case "list":
                        // Suppression du lien dans la base de donnée
                        getOtherFriendList(nSender, ProxyServer.getInstance().getPlayer(pseudo).getUniqueId());
                        break;

                    default:
                        wrongUse(nSender);
                }

            }
            if (args.length > 3){
                wrongUse(nSender);
            }
        }
    }

    private void wrongUse(NetworkUser nSender){
        nSender.sendMessage("Mauvaise utilisation de la commande. Ajouter un ami : /friend add <pseudo>. Pour retirer un ami : /friend del <pseudo>");
    }
}
