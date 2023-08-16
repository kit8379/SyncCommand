package org.me.synccommand.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.me.synccommand.shared.ConfigHelper;
import org.me.synccommand.shared.RedisHandler;
import redis.clients.jedis.JedisPubSub;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

@Plugin(id = "synccommand", name = "SyncCommand", version = "${project.version}",
        description = "Sync commands across servers", authors = {"TonyPak"})
public class SyncCommandVelocity {

    public final ProxyServer proxy;
    public final Logger logger;
    private final ConfigHelper configHelper;
    private JedisPubSub pubSub;
    private Thread redisListenerThread;

    @Inject
    public SyncCommandVelocity(ProxyServer server, Logger logger) {
        this.proxy = server;
        this.logger = logger;
        configHelper = new ConfigHelper(logger);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
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
                proxy.getCommandManager().executeAsync(proxy.getConsoleCommandSource(), message);
            }
        };

        redisListenerThread = new Thread(() -> RedisHandler.subscribe(pubSub, namespacedChannels));
        redisListenerThread.start();

        proxy.getCommandManager().register("syncv", new CommandHelper());
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
}
