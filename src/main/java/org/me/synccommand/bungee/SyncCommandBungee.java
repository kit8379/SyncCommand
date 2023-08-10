package org.me.synccommand.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
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
        logger = getLogger();
        proxy = getProxy();
        configHelper = new ConfigHelper(this);

        saveDefaultConfig();

        String host = getConfig().getString("redisHost");
        int port = getConfig().getInt("redisPort");
        String password = getConfig().getString("redisPassword");
        List<String> originalChannels = getConfig().getStringList("channels");
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
                ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), message);
            }
        };

        redisListenerThread = new Thread(() -> RedisHandler.subscribe(pubSub, namespacedChannels));
        redisListenerThread.start();

        getProxy().getPluginManager().registerCommand(this, new SyncCommand());
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

    public void saveDefaultConfig() {
        configHelper.saveDefaultConfig();
    }

    public Configuration getConfig() { // Return type is BungeeCord's Configuration
        return configHelper.getConfig();
    }
}
