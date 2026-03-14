package net.boom3r.bungeeapi.core.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.boom3r.bungeeapi.core.managers.RedisManager;
import net.boom3r.bungeeapi.core.objects.PubSubMessage;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Modifier;
import java.util.function.Consumer;

public class PubSubService {
    private final RedisManager redisManager;
    private final Gson gson;

    public PubSubService(RedisManager redisManager) {
        this.redisManager = redisManager;
        this.gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                //.serializeNulls()
                .create();
    }

    /** Publie un message sur un canal */
    public void publish(String channel, PubSubMessage message) {
        if (message == null) return;
        String json = gson.toJson(message);
        redisManager.publish(channel, json);
    }

    /**
     * S’abonne à un canal.
     * Le callback est exécuté sur le thread du listener ; si vous devez
     * interagir avec l’API Bukkit/Bungee, basculez sur le scheduler.
     */
    public void subscribe(String channel, Consumer<PubSubMessage> consumer) {
        JedisPubSub listener = new JedisPubSub() {
            @Override
            public void onMessage(String ch, String message) {
                try {
                    PubSubMessage msg = gson.fromJson(message, PubSubMessage.class);
                    consumer.accept(msg);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        redisManager.subscribe(channel, listener);
    }
}
