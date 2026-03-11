package net.boom3r.bungeeapi.core.listeners;

import com.google.gson.Gson;
import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.objects.RedisPubSubMsg;
import net.md_5.bungee.api.ProxyServer;
import redis.clients.jedis.JedisPubSub;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;

// dans vos plugins Spigot et Bungee
public class RedisPubSubListener extends JedisPubSub {
    private final BungeeAPI plugin; // JavaPlugin ou Plugin Bungee selon le contexte

    public RedisPubSubListener(BungeeAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onMessage(String channel, String message) {
        // désérialiser votre message avec Gson
        RedisPubSubMsg msg = new Gson().fromJson(message, RedisPubSubMsg.class);

        if (plugin instanceof net.md_5.bungee.api.plugin.Plugin bungee) {
            ProxyServer.getInstance().getScheduler().runAsync(bungee, () -> {
                // traiter msg : broadcast, téléportation, etc.
                bungeeLogger.DebugV("Message reçu en PubSub : "+message, 2);
            });
        }
    }
}
