package net.boom3r.bungeeapi.commands;

import net.boom3r.bungeeapi.core.managers.LogManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeInstance;
import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;
import static net.md_5.bungee.api.ProxyServer.getInstance;

public class GlobalKickCMD extends Command {
    public GlobalKickCMD() {
        super("kick", "bungeeAPI.modo.kick");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        if (args.length > 1){
            String playerName = args[0];
            String reason = "Tu as été kické du serveur";
            if (args.length > 2) {
                for (int i = 1; i < args.length; i++) {
                    reason += args[i]+" ";
                }

                bungeeLogger.Admin(reason);
            }

            ProxiedPlayer player = getInstance().getPlayer(playerName);
            if (player != null){
                bungeeLogger.Admin("Le joueur "+playerName+" a été kické par "+sender);
                player.disconnect(new TextComponent(reason));
            }


       }
    }
}
