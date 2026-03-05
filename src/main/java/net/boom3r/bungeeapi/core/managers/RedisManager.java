package net.boom3r.bungeeapi.core.managers;

import org.jspecify.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import com.google.gson.Gson;

import static net.boom3r.bungeeapi.BungeeAPI.redisEnabled;

public class RedisManager {
    private final JedisPool pool;
    private final Gson gson = new Gson();

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
}
