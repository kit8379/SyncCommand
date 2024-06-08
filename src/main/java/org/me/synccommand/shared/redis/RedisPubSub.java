package org.me.synccommand.shared.redis;

import org.me.synccommand.shared.ConsoleCommand;
import redis.clients.jedis.JedisPubSub;

import java.util.logging.Logger;

public class RedisPubSub {

    private final Logger logger;
    private final ConsoleCommand consoleCommand;
    private JedisPubSub pubSub;

    public RedisPubSub(Logger logger, ConsoleCommand consoleCommand) {
        this.logger = logger;
        this.consoleCommand = consoleCommand;
    }

    public void init() {
        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                consoleCommand.executeCommand(message);
            }
        };
        logger.info("PubSub instance created and ready to subscribe.");
    }

    public void shut() {
        if (pubSub != null) {
            pubSub.unsubscribe();
            logger.info("Unsubscribed from all channels.");
        }
    }

    public JedisPubSub getPubSub() {
        return pubSub;
    }
}
