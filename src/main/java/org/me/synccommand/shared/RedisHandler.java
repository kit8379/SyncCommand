package org.me.synccommand.shared;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class RedisHandler {
    private static Jedis jedis;
    private static JedisPool pool;

    public static void connect(String host, int port, String password) {
        pool = new JedisPool(new JedisPoolConfig(), host, port);
        try {
            jedis = pool.getResource();
            if (password != null && !password.isEmpty()) {
                jedis.auth(password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void publish(String channel, String message) {
        try {
            jedis = pool.getResource();
            jedis.publish(channel, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void subscribe(JedisPubSub pubSub, String... channels) {
        try {
            jedis = pool.getResource();
            jedis.subscribe(pubSub, channels);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        pool.close();
    }
}

