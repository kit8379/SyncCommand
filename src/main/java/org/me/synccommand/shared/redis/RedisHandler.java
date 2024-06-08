package org.me.synccommand.shared.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class RedisHandler {
    private static JedisPool pool;

    public static void connect(String host, int port, String password) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        if (password != null && !password.isEmpty()) {
            pool = new JedisPool(poolConfig, host, port, 2000, password);
        } else {
            pool = new JedisPool(poolConfig, host, port, 2000);
        }
    }

    public static void publish(String channel, String message) {
        try (Jedis jedis = pool.getResource()) {
            jedis.publish(channel, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void subscribe(JedisPubSub pubSub, String... channels) {
        try (Jedis jedis = pool.getResource()) {
            jedis.subscribe(pubSub, channels);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        if (pool != null) {
            pool.close();
        }
    }
}
