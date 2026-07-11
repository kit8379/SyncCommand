package org.me.synccommand.shared.redis;

import org.me.synccommand.shared.ConsoleCommand;
import redis.clients.jedis.JedisPubSub;

public class RedisPubSub {

    private final ConsoleCommand consoleCommand;
    private JedisPubSub pubSub;

    public RedisPubSub(ConsoleCommand consoleCommand) {
        this.consoleCommand = consoleCommand;
    }

    public void init() {
        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                consoleCommand.executeCommand(message);
            }
        };
    }

    public void shut() {
        if (pubSub != null) {
            pubSub.unsubscribe();
            pubSub = null;
        }
    }

    public JedisPubSub getPubSub() {
        if (pubSub == null) {
            throw new IllegalStateException("Redis PubSub has not been initialized.");
        }

        return pubSub;
    }
}