package org.me.synccommand.shared;

import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.logging.Logger;

public class RedisPubSub {

    private final Logger logger;
    private final ConfigHelper configHelper;
    private final ConsoleCommand consoleCommand;
    private JedisPubSub pubSub;
    private Thread redisListenerThread;

    public RedisPubSub(Logger logger, ConfigHelper configHelper, ConsoleCommand consoleCommand) {
        this.logger = logger;
        this.configHelper = configHelper;
        this.consoleCommand = consoleCommand;
    }

    public void init() {
        String host = configHelper.getRedisHost();
        int port = configHelper.getRedisPort();
        String password = configHelper.getRedisPassword();
        List<String> originalChannels = configHelper.getChannels();
        String[] namespacedChannels = originalChannels.stream()
                .map(channel -> "synccommand." + channel)
                .toArray(String[]::new);

        try {
            RedisHandler.connect(host, port, password);
        } catch (Exception e) {
            logger.warning("Failed to connect to Redis. Disabling plugin.");
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
                redisListenerThread.join(); // Ensure thread termination
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        RedisHandler.disconnect();
    }
}
