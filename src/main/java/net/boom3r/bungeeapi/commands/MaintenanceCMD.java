package net.boom3r.bungeeapi.commands;

import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.managers.MaintenanceManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.text.BreakIterator;

public class MaintenanceCMD extends Command {
    public MaintenanceCMD() {
        super("maintenance", "bungeeAPI.admin.maintenance");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("This command can only be run by a Player !").color(ChatColor.RED).create());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if(args.length > 0){
            switch (args[0]){
                case "on":
                    MaintenanceManager.enableMaintenance("global");
                case "off":
                    MaintenanceManager.disableMaintenance("global");

                default:
                BungeeAPI.sendFormatedMessage(player, "Mauvaise synthaxe !");

            }
        }
    }
}
