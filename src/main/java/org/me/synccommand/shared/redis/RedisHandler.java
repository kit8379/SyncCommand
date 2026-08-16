package org.me.synccommand.shared.redis;

import redis.clients.jedis.Connection;
import redis.clients.jedis.ConnectionPool;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPubSub;

public class RedisHandler {
    private static ConnectionPool pool;

    public static void connect(String host, int port, String password) {
        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();

        DefaultJedisClientConfig.Builder clientConfigBuilder = DefaultJedisClientConfig.builder()
                .connectionTimeoutMillis(2000)
                .socketTimeoutMillis(2000);

        if (password != null && !password.isEmpty()) {
            clientConfigBuilder.password(password);
        }

        JedisClientConfig clientConfig = clientConfigBuilder.build();
        pool = new ConnectionPool(new HostAndPort(host, port), clientConfig, poolConfig);
    }

    public static void publish(String channel, String message) {
        try (Jedis jedis = getJedis()) {
            jedis.publish(channel, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void subscribe(JedisPubSub pubSub, String... channels) {
        try (Jedis jedis = getJedis()) {
            jedis.subscribe(pubSub, channels);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Jedis getJedis() {
        Connection connection = pool.getResource();
        return new Jedis(connection);
    }

    public static void disconnect() {
        if (pool != null) {
            pool.close();
        }
    }
}
