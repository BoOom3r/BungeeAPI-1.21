package net.boom3r.bungeeapi;

import com.zaxxer.hikari.HikariDataSource;
import net.boom3r.bungeeapi.commands.HubCMD;
import net.boom3r.bungeeapi.commands.ServerManagerCMD;
import net.boom3r.bungeeapi.listeners.BungeeListeners;
import net.boom3r.bungeeapi.listeners.MOTDListener;
import net.boom3r.bungeeapi.managers.HConnection;
import net.boom3r.bungeeapi.managers.LogManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class BungeeAPI extends Plugin {
    public static BungeeAPI bungeeInstance;
    public static String PREFIX = ChatColor.AQUA + "[BungeeAPI] -> ";
    public static int DEBUGLVL = 2;
    public static Boolean DEBUG = true;
    public static HikariDataSource dataSourcePool;
    public static String networkPrefix = "[Network]";
    public static boolean maintenance = false;

    @Override
    public void onEnable() {
        bungeeInstance = this;

        getProxy().getPluginManager().registerListener(this, new BungeeListeners());
        getProxy().getPluginManager().registerListener(this, new MOTDListener());
        getProxy().getPluginManager().registerCommand(this, new HubCMD());
        getProxy().getPluginManager().registerCommand(this, new ServerManagerCMD());

        dataSourcePool = new HConnection().openPool();
        isNetworkMaintenance();
        getServerList();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static void getServerList() {
        List<String> listRetour = new ArrayList<>();
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT name FROM servers WHERE status=?");
        ) {
            statement.setInt(1, 1);

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                BungeeAPI.bungeeInstance.getProxy().getConsole().sendMessage(new ComponentBuilder(result.getString("name")).create());
                //listRetour.add(result.getString("name"));
            }

            result.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return listRetour;
    }

    public static boolean isNetworkMaintenance() {
        boolean listRetour = false;

        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT status FROM maintenance WHERE server_name=?");
        ) {
            statement.setString(1, "global");

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                if(result.getBoolean("status")){
                    LogManager.Info("Mode Maintenance Activé");
                    maintenance = true;
                } else {
                    maintenance = false;
                }
            }
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listRetour;
    }


    public static void sendFormatedMessage(ProxiedPlayer p, String msg){
        TextComponent returnComponent = new TextComponent(msg);
        p.sendMessage(returnComponent);
    }

    public static TextComponent getFormatedMessage(String msg){
        TextComponent returnComponent = new TextComponent(msg);
        return  returnComponent;
    }

}
