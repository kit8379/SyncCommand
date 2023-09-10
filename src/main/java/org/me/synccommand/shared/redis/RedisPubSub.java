package org.me.synccommand.shared.redis;

import org.me.synccommand.shared.ConsoleCommand;
import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.logging.Logger;

public class RedisPubSub {

    private final Logger logger;
    private final ConsoleCommand consoleCommand;
    private JedisPubSub pubSub;
    private Thread redisListenerThread;
    private final String host;
    private final int port;
    private final String password;
    private final List<String> channels;

    public RedisPubSub(Logger logger, ConsoleCommand consoleCommand, String host, int port, String password, List<String> channels) {
        this.logger = logger;
        this.consoleCommand = consoleCommand;
        this.host = host;
        this.port = port;
        this.password = password;
        this.channels = channels;
    }

    public void init() {
        String[] namespacedChannels = channels.toArray(String[]::new);

        try {
            RedisHandler.connect(host, port, password);
        } catch (Exception e) {
            logger.warning("Failed to connect to Redis.");
            e.printStackTrace();
            return;
        }

        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                consoleCommand.executeCommand(message);
            }
        };

        redisListenerThread = new Thread(() -> RedisHandler.subscribe(pubSub, namespacedChannels));
        redisListenerThread.start();
    }

    public void shut() {
        if (pubSub != null) {
            pubSub.unsubscribe();
        }

        if (redisListenerThread != null) {
            redisListenerThread.interrupt();
            try {
                redisListenerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        RedisHandler.disconnect();
    }
}
