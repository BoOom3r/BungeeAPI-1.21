package net.boom3r.bungeeapi.commands;

import net.boom3r.bungeeapi.core.managers.ServerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetSocketAddress;

public class ServerManagerCMD extends Command {
    public ServerManagerCMD() {
        super("servermanager", "bungeeAPI.admin.servermanager");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("This command can only be run by a player !").color(ChatColor.RED).create());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (args.length > 2){
           switch (args[0]){
               case "add":
                   ComponentBuilder texteRetour = new ComponentBuilder("Add demandé pour ");
                   texteRetour.append(args[1]);
                   texteRetour.append(" via l adresse 127.0.0.1:");
                   texteRetour.append(args[2]);
                   texteRetour.append(" avec pour MOTD ");
                   texteRetour.append(args[3]);

                   player.sendMessage(texteRetour.create());
                   ServerManager.addServer(args[1], new InetSocketAddress("127.0.0.1",Integer.getInteger(args[2])),"MC Network - "+args[1],false);
                   break;

               case "remove":

                   ServerManager.removeServer(args[1]);
                   break;
           }
       }
    }
}
