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
        jedis = pool.getResource();
        jedis.auth(password);
    }

    public static void publish(String channel, String message) {
        jedis.publish(channel, message);
    }

    public static void subscribe(JedisPubSub pubSub, String... channels) {
        jedis.subscribe(pubSub, channels);
    }

    public static void disconnect() {
        pool.close();
    }
}

