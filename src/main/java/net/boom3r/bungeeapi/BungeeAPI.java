package net.boom3r.bungeeapi;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;
import net.boom3r.bungeeapi.commands.GlobalKickCMD;
import net.boom3r.bungeeapi.commands.HubCMD;
import net.boom3r.bungeeapi.commands.MaintenanceCMD;
import net.boom3r.bungeeapi.commands.ServerManagerCMD;
import net.boom3r.bungeeapi.commands.group.GroupCMD;
import net.boom3r.bungeeapi.commands.group.NetworkGroup;
import net.boom3r.bungeeapi.core.utils.DebugHttpServer;
import net.boom3r.bungeeapi.core.listeners.BungeeListeners;
import net.boom3r.bungeeapi.core.listeners.MOTDListener;
import net.boom3r.bungeeapi.core.managers.*;
import net.boom3r.bungeeapi.core.objects.NetworkConf;
import net.boom3r.bungeeapi.core.services.PubSubService;
import net.boom3r.bungeeapi.runnables.ScheduledRunner;
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
import java.util.Map;
import java.util.logging.Logger;

public final class BungeeAPI extends Plugin {
    public static BungeeAPI bungeeInstance;
    public static String PREFIX = ChatColor.AQUA + "[BungeeAPI] -> "+ChatColor.RESET;
    public static int DEBUGLVL = 2;
    public static Boolean DEBUG = true;
    public static HikariDataSource dataSourcePool;
    public static boolean maintenance = false;
    public static ConfManager confManager;
    public static Logger logger;
    public static ServerManager serverManager;
    public static NetworkManager networkManager;
    private ScheduledRunner runner;
    public static boolean whitelistEnabled = false;
    public static RedisManager redisManager;
    public static boolean redisEnabled = false;
    public static LogManager bungeeLogger;
    public static NetworkConf networkConf;
    DebugHttpServer debugServer;
    public PubSubService psService;

    @Override
    public void onEnable() {
        // Démarrage du plugin
        bungeeInstance = this;
        logger = getLogger();
        bungeeLogger = new LogManager();

        // Chargement de la configuration
        confManager = new ConfManager(bungeeInstance);
        try {
            confManager.makeConfig();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        DEBUGLVL = confManager.getConfig().getInt("logging.verbose");
        bungeeLogger.Info("Verbose configuré sur "+DEBUGLVL);

        bungeeLogger.Info("Chargement des Listeners");
        // Enregistrement des listeners
        getProxy().getPluginManager().registerListener(bungeeInstance, new BungeeListeners());
        getProxy().getPluginManager().registerListener(bungeeInstance, new MOTDListener());
        getProxy().getPluginManager().registerCommand(bungeeInstance, new HubCMD());
        getProxy().getPluginManager().registerCommand(bungeeInstance, new ServerManagerCMD());
        getProxy().getPluginManager().registerCommand(bungeeInstance, new MaintenanceCMD());
        getProxy().getPluginManager().registerCommand(bungeeInstance, new GlobalKickCMD());
        getProxy().getPluginManager().registerCommand(bungeeInstance, new GroupCMD());

        // Création de la pool DB
        dataSourcePool = new HConnection().openPool(
                confManager.getConfig().getString("database.mysql.host"),
                confManager.getConfig().getInt("database.mysql.port"),
                confManager.getConfig().getString("database.mysql.database"),
                confManager.getConfig().getString("database.mysql.user"),
                confManager.getConfig().getString("database.mysql.password")
        );

        //Connection Redis
        redisManager = new RedisManager("localhost", 6379, null);
        if (redisManager != null) {
            redisEnabled = true;
        }
        if (redisEnabled) {
            //RedisPubSubListener listener = new RedisPubSubListener(this);
            //redisManager.subscribe("b3api-channel", listener);
            //ProxyServer.getInstance().getScheduler().runAsync(this, new RedisManager.PubSubReaderTask(this));
            psService = new PubSubService(redisManager);
            psService.subscribe("b3api-group", message -> {
                if ("group_create".equals(message.getType())) {
                    // Vérifie la source pour éviter les boucles
                    if (!"bungee".equals(message.getSource())) {
                        for (Map.Entry o : message.getPayload().entrySet()){
                            bungeeLogger.DebugV("Message PubSub raw : "+o.getKey()+" -> "+o.getValue(),3);
                            String groupUuid = o.getKey().toString();
                            Gson json = new Gson();
                            NetworkGroup newGroup = json.fromJson(o.getValue().toString(), NetworkGroup.class);
                            bungeeLogger.DebugV("Message PubSub reçu : group_create -> "+newGroup.getGroupName()+" -> "+newGroup.getGroupOwner().getUuid()+". group UUID : "+groupUuid,2);
                        }

                        //String payload = message.getPayload().get("groupUuid"));

                        // Reconstitue la liste des joueurs et crée ou met à jour le groupe
                        // via NetworkGroupManager
                    }
                }
            });
        }

        networkManager = new NetworkManager();
        networkConf = new NetworkConf(this);

        // Chargement du serveur Manager
        serverManager = new ServerManager();
        serverManager.initServerList();

        runner = new ScheduledRunner(this);
        runner.start();


        // Vérification de l'état de maintenance
        isNetworkMaintenance();

        // je sais pas
        getServerList();

        // Création channel plugin message
        getProxy().registerChannel( "bungee:group" );

        // Redis
        redisManager.save("server_list", serverManager.getServerlist());

        if (DEBUG){
            try {
                debugServer = new DebugHttpServer(getNetworkManager(), getNetworkManager().networkGroupManager);
                debugServer.start(8080); // ouvre http://localhost:8080/users et /groups
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (DEBUG){
            debugServer.stop();
        }

        if(redisEnabled){
            bungeeLogger.DebugV("Fermeture de la connexion Redis",2);
            redisManager.close();
        }
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
                    bungeeLogger.Info("Mode Maintenance Activé");
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

    public NetworkManager getNetworkManager(){
        return networkManager;
    }



    public HikariDataSource getDataSourcePool() {
        return dataSourcePool;
    }

    public NetworkConf getNetworkConf() {
        return networkConf;
    }

    public BungeeAPI getBungeeInstance() {
        return bungeeInstance;
    }

    public int getDEBUGLVL() {
        return DEBUGLVL;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

    public LogManager getBungeeLogger() {
        return bungeeLogger;
    }
}
