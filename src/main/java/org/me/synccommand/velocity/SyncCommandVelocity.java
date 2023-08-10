package org.me.synccommand.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.me.synccommand.shared.RedisHandler;
import redis.clients.jedis.JedisPubSub;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

@Plugin(id = "synccommand", name = "SyncCommand", version = "1.0",
        description = "Sync commands across servers", authors = {"TonyPak"})
public class SyncCommandVelocity {

    private final ProxyServer proxy;
    private final Logger logger;
    private final ConfigHelper configHelper;
    private JedisPubSub pubSub;
    private Thread redisListenerThread;

    @Inject
    public SyncCommandVelocity(ProxyServer server, Logger logger) {
        this.proxy = server;
        this.logger = logger;
        this.configHelper = new ConfigHelper(proxy.getPluginManager().getPlugin("synccommand").get());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("SyncCommand is starting up...");
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
                proxy.getCommandManager().executeAsync(proxy.getConsoleCommandSource(), message);
            }
        };

        redisListenerThread = new Thread(() -> RedisHandler.subscribe(pubSub, namespacedChannels));
        redisListenerThread.start();

        proxy.getCommandManager().register("syncv", new SyncCommand());
        logger.info("SyncCommand has started successfully!");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
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

    public ConfigHelper.Configuration getConfig() {
        return configHelper.getConfig();
    }
}
