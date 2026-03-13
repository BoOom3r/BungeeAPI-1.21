package net.boom3r.bungeeapi.core.managers;

import com.google.gson.GsonBuilder;
import net.boom3r.bungeeapi.BungeeAPI;
import org.jspecify.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import com.google.gson.Gson;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Modifier;

import static net.boom3r.bungeeapi.BungeeAPI.*;

public class RedisManager {
    private final JedisPool pool;
    //private final Gson gson = new Gson();
    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .create();

    public RedisManager(String host, int port, @Nullable String password) {
        // initialisation du pool (pense à gérer l’authentification si besoin)
        if(port == 0) port = 6379;
        pool = new JedisPool(host, port);
        pool.getResource().select(0);
    }

    public <T> void save(String key, T object) {
        if (!redisEnabled) return;
        try (Jedis jedis = pool.getResource()) {
            String json = gson.toJson(object);   // sérialisation en JSON
            jedis.set(key, json);
        }
    }

    public <T> T load(String key, Class<T> type) {
        if (!redisEnabled) return null;
        try (Jedis jedis = pool.getResource()) {
            String json = jedis.get(key);
            return json == null ? null : gson.fromJson(json, type);
        }
    }

    public boolean delete(String key) {
        if (!redisEnabled) return false;
        try (Jedis jedis = pool.getResource()) {
            jedis.del(key);
            return true;
        }
    }

    public void close() {
        pool.close();
    }
    public static class PubSubReaderTask implements Runnable {

        private final BungeeAPI plugin;

        public PubSubReaderTask(BungeeAPI plugin) {
            this.plugin = plugin;
        }

        @Override
        public void run() {
            JedisPubSub pubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    bungeeLogger.DebugV("Received message from channel " + channel + ": " + message,2);
                }
            };
        }
    }

    public void publish(String channel, String json) {
        if (!redisEnabled) return;
        try (Jedis jedis = pool.getResource()) {
            jedis.publish(channel, json);
        }
    }

    public void subscribe(String channel, JedisPubSub listener) {
        if (!redisEnabled) return;
        // exécuter sur un thread séparé car subscribe() est bloquant
        new Thread(() -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.subscribe(listener, channel);
            }
        }).start();
    }


}
