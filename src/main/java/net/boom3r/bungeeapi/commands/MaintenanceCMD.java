package net.boom3r.bungeeapi.commands;

import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.manager.MessengerManager;
import net.boom3r.bungeeapi.managers.MaintenanceManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.BreakIterator;

import static net.boom3r.bungeeapi.BungeeAPI.*;

public class MaintenanceCMD extends Command {
    public MaintenanceCMD() {
        super("maintenance", "bungeeAPI.admin.maintenance");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if(args.length == 1) {
            switch (args[0]) {
                case "on":
                    MaintenanceManager.enableMaintenance("global");
                    MessengerManager.sendToAdmins("La maintenance sur le serveur a été activée par " + ((ProxiedPlayer) sender).getDisplayName());
                case "off":
                    MaintenanceManager.disableMaintenance("global");
                    MessengerManager.sendToAdmins("La maintenance sur le serveur a été désactivée par " + ((ProxiedPlayer) sender).getDisplayName());
                default:
                    BungeeAPI.sendFormatedMessage(player, "Mauvaise synthaxe !");

            }
        } else {
                try (Connection sql = dataSourcePool.getConnection();
                     PreparedStatement statement = sql.prepareStatement("SELECT * FROM servers WHERE name = ?");)
                {                      statement.setString(1, args[1]);
                    ResultSet result = statement.executeQuery();
                if (result.getFetchSize() > 0 ){
                      logger.info("Il existe un serveur de ce nom");
                    switch (args[0]) {
                        case "on":
                            MaintenanceManager.enableMaintenance(args[1]);
                            MessengerManager.sendToAdmins("La maintenance sur le serveur "+args[1]+" a été activée par " + ((ProxiedPlayer) sender).getDisplayName());
                        case "off":
                            MaintenanceManager.disableMaintenance(args[1]);
                            MessengerManager.sendToAdmins("La maintenance sur le serveur "+args[1]+" a été désactivée par " + ((ProxiedPlayer) sender).getDisplayName());
                        default:
                            BungeeAPI.sendFormatedMessage(player, "Mauvaise synthaxe !");

                    }
                }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
}

