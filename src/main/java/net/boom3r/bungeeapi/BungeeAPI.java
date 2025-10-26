package net.boom3r.bungeeapi;

import com.zaxxer.hikari.HikariDataSource;
import net.boom3r.bungeeapi.commands.GlobalKickCMD;
import net.boom3r.bungeeapi.commands.HubCMD;
import net.boom3r.bungeeapi.commands.MaintenanceCMD;
import net.boom3r.bungeeapi.commands.ServerManagerCMD;
import net.boom3r.bungeeapi.core.listeners.BungeeListeners;
import net.boom3r.bungeeapi.core.listeners.MOTDListener;
import net.boom3r.bungeeapi.core.managers.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class BungeeAPI extends Plugin {
    public static BungeeAPI bungeeInstance;
    public static String PREFIX = ChatColor.AQUA + "[BungeeAPI] -> ";
    public static int DEBUGLVL = 2;
    public static Boolean DEBUG = true;
    public static HikariDataSource dataSourcePool;
    public static String networkPrefix = "[Network]";
    public static boolean maintenance = false;
    public static ConfManager confManager;
    public static Logger logger;
    public static ServerManager serverManager;
    public static NetworkManager networkManager;

    @Override
    public void onEnable() {
        // Démarrage du plugin
        bungeeInstance = this;
        logger = getLogger();

        // Chargement de la configuration
        confManager = new ConfManager(bungeeInstance);
        try {
            confManager.makeConfig();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }

        // Enregistrement des listeners
        getProxy().getPluginManager().registerListener(bungeeInstance, new BungeeListeners());
        getProxy().getPluginManager().registerListener(bungeeInstance, new MOTDListener());
        getProxy().getPluginManager().registerCommand(bungeeInstance, new HubCMD());
        getProxy().getPluginManager().registerCommand(bungeeInstance, new ServerManagerCMD());
        getProxy().getPluginManager().registerCommand(bungeeInstance, new MaintenanceCMD());
        getProxy().getPluginManager().registerCommand(bungeeInstance, new GlobalKickCMD());

        // Création de la pool DB
        dataSourcePool = new HConnection().openPool(
                confManager.getConfig().getString("database.mysql.host"),
                confManager.getConfig().getInt("database.mysql.port"),
                confManager.getConfig().getString("database.mysql.database"),
                confManager.getConfig().getString("database.mysql.user"),
                confManager.getConfig().getString("database.mysql.password")
        );
        networkManager = new NetworkManager();

        // Chargement du serveur Manager
        serverManager = new ServerManager();
        serverManager.initServerList();


        // Vérification de l'état de maintenance
        isNetworkMaintenance();

        // je sais pas
        getServerList();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static void getServerList() {
        List<String> listRetour = new ArrayList<>();
        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT name FROM network_servers WHERE status=? AND inactive = false");
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
             PreparedStatement statement = sql.prepareStatement("SELECT status FROM network_maintenance WHERE server_name=?");
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

    public void stopBungeeCord(){
        getLogger().severe("Arrêt du serveur BungeeCord...");
        ProxyServer.getInstance().stop();
    }

}
