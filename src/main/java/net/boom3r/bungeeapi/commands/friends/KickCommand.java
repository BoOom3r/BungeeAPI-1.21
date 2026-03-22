package net.boom3r.bungeeapi.commands.friends;

import net.boom3r.bungeeapi.commands.BungeeCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;
import static net.boom3r.bungeeapi.BungeeAPI.redisManager;
import static net.md_5.bungee.api.ProxyServer.getInstance;

public class KickCommand implements BungeeCommand {
    @Override
    public String getName() { return "kick"; }
    @Override
    public String getPermission() { return "bungeeAPI.modo.kick"; }
    @Override
    public List<String> getAliases() { return List.of("svrman"); }

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

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return redisManager.load("network_user_list",List.class);
        }
        if (args.length == 2) {
            return List.of("<Raison>");
        }
        // etc.
        return List.of();
    }
}