package org.me.synccommand.shared.redis;

import org.me.synccommand.shared.ConsoleCommand;
import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class RedisPubSub {

    private final Logger logger;
    private final ConsoleCommand consoleCommand;
    private final String host;
    private final int port;
    private final String password;
    private final List<String> channels;
    private JedisPubSub pubSub;

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
        RedisHandler.subscribe(pubSub, namespacedChannels);
    }

    public void shut() {
        if (pubSub != null) {
            pubSub.unsubscribe();
        }

        RedisHandler.disconnect();
    }
}
