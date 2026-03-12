package net.boom3r.bungeeapi.core.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
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
        bungeeLogger.DebugV("Message PubSub reçu : " +message,3);
        RedisPubSubMsg msg = new Gson().fromJson(message, RedisPubSubMsg.class);
        bungeeLogger.DebugV("Message MSG reçu : " + msg.getPayload(),2);
    }
}
