package org.me.synccommand.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import org.me.synccommand.shared.ConfigHelper;
import org.me.synccommand.shared.RedisHandler;
import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.logging.Logger;

public class SyncCommandBungee extends Plugin implements Listener {
    private ProxyServer proxy;
    private Logger logger;
    private ConfigHelper configHelper;
    private JedisPubSub pubSub;
    private Thread redisListenerThread;

    @Override
    public void onEnable() {
        proxy = getProxy();
        logger = getLogger();
        configHelper = new ConfigHelper(logger);

        logger.info("SyncCommand is starting up...");
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
                proxy.getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), message);
            }
        };

        redisListenerThread = new Thread(() -> RedisHandler.subscribe(pubSub, namespacedChannels));
        redisListenerThread.start();

        proxy.getPluginManager().registerCommand(this, new CommandHelper());
        logger.info("SyncCommand has started successfully!");
    }

    @Override
    public void onDisable() {
        logger.info("SyncCommand is shutting down...");
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
        logger.info("SyncCommand has shut down successfully!");
    }
}
