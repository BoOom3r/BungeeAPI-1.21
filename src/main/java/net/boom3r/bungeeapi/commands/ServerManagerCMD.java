package net.boom3r.bungeeapi.commands;

import net.boom3r.bungeeapi.core.managers.LogManager;
import net.boom3r.bungeeapi.core.managers.ServerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.boom3r.bungeeapi.BungeeAPI.serverManager;

public class ServerManagerCMD extends Command implements TabExecutor {

    public ServerManagerCMD() {
        super("servermanager", "bungeeAPI.admin.servermanager");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("This command can only be run by a player!").color(ChatColor.RED).create());
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length < 1) {
            player.sendMessage(new ComponentBuilder("§cUsage: /servermanager add|remove <name> [host] [port] [motd]").create());
            return;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (args.length < 4) {
                    player.sendMessage(new ComponentBuilder("§cUsage: /servermanager add <name> <host> <port> [motd]").create());
                    return;
                }

                String name = args[1];
                String host = args[2];
                int port = Integer.parseInt(args[3]);

                String motd = (args.length > 4)
                        ? String.join(" ", Arrays.copyOfRange(args, 4, args.length))
                        : "MC Network - " + name;

                motd = motd.replaceAll("^\"|\"$", "");

                LogManager.Admin("Ajout du serveur " + name + " par " + player.getName() +
                        " via " + host + ":" + port + " avec MOTD : " + motd);

                ServerManager.addServer(name, new InetSocketAddress(host, port), motd, false);
            }

            case "remove" -> {
                if (args.length < 2) {
                    player.sendMessage(new ComponentBuilder("§cUsage: /servermanager remove <name>").create());
                    return;
                }

                ServerManager.removeServer(args[1]);
            }

            default -> player.sendMessage(new ComponentBuilder("§cUsage: /servermanager add|remove <args>").create());
        }
    }

    // 🧠 Autocomplétion ici
    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("add", "remove"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            completions.addAll(serverManager.getServerList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            completions.add("<nom_du_serveur>");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            completions.add("localhost");
        } else if (args.length == 4 && args[0].equalsIgnoreCase("add")) {
            completions.add("25565");
        } else if (args.length == 5 && args[0].equalsIgnoreCase("add")) {
            completions.add("\"Motd du serveur\"");
        }

        return completions;
    }
}
