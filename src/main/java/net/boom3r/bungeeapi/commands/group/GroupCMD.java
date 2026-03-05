package net.boom3r.bungeeapi.commands.group;

import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.managers.LogManager;
import net.boom3r.bungeeapi.core.objects.NetworkUser;
import net.boom3r.bungeeapi.core.utils.DebugUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;

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
            if(args.length == 0){
                //ouverture du menu groupe
                bungeeLogger.DebugV("ouverture du menu groupe ",3);
                BungeeAPI.bungeeInstance.getNetworkManager().networkGroupManager.openGroupMenu((ProxiedPlayer)sender);
                return;
            }

            bungeeLogger.DebugV("La commande group à été faite avec comme argument "+args[0],3);
            // create
            if (args[0].equalsIgnoreCase("create")){
                // create groupe
                if (args.length == 1){
                    bungeeLogger.DebugV("Création du groupe",3);
                    //nSender.sendMessage("Création du groupe");
                    BungeeAPI.networkManager.networkGroupManager.createGroup(nSender, null, null);
                    return;
                }
                if (args.length == 2){
                    if (args[1].length() < 3 || args[1].length() > 12) {
                        nSender.sendMessage("Le nom de ton groupe doit faire plus de 3 caractères et moins de 12");
                        bungeeLogger.DebugV("Nom du groupe non conforme",3);

                        return;
                    } else {
                        bungeeLogger.DebugV("Création du groupe avec le nom "+args[1],3);
                        //nSender.sendMessage("Création du groupe avec le nom "+args[1]);
                        BungeeAPI.networkManager.networkGroupManager.createGroup(nSender, args[1], null);
                        //BungeeAPI.networkManager.networkGroupManager.createGroup(nSender, groupName, null);
                        return;
                    }
                }
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
                            BungeeAPI.networkManager.networkGroupManager.createGroup(nSender, args[1], args[2]);
                            //BungeeAPI.networkManager.networkGroupManager.createGroup(nSender, groupName, null);
                            return;
                        }
                    }
                }
                bungeeLogger.DebugV("Mauvaise utilisation de la commande. La bonne commande est /group create NomGroupe TagGroup (NomGroup et TagGroup étant facultatifs).",2);
                nSender.sendMessage("Mauvaise utilisation de la commande. La bonne commande est /group create NomGroupe TagGroup (NomGroup et TagGroup étant facultatifs).");
            }
            // destroy


            // quit
        }
    }
}
