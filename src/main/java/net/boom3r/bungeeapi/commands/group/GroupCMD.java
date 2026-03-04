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
            bungeeLogger.DebugV("La commande group à été faite",2);
            NetworkUser nSender = BungeeAPI.networkManager.networkUserList.get(((ProxiedPlayer) sender).getUniqueId());
            if (args.length == 2) {
                String groupName = args[0];
                if (args[0].length() < 3 || args[0].length() > 12) {
                    nSender.sendMessage("Le nom de ton groupe doit faire plus de 3 caractères et moins de 12");
                } else {
                    BungeeAPI.networkManager.networkGroupManager.createGroup(nSender, groupName, null);
                }
            }

            if (args.length == 3) {
                String groupName = args[0];
                String groupTag = args[1];
                if (args[0].length() < 3 || args[0].length() > 12 || groupTag.length()<3 || groupTag.length() > 5) {
                    nSender.sendMessage("Le nom de ton groupe doit faire plus de 3 caractères et moins de 12. Celui de ton tag doit faire de 3 à 5 caractères.");
                } else {
                    BungeeAPI.networkManager.networkGroupManager.createGroup(nSender, groupName, groupTag);
                }
            }




        }
    }
}
