package org.me.synccommand.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import org.me.synccommand.bungee.command.SyncCommandReload;
import org.me.synccommand.bungee.command.SyncCommandSync;
import org.me.synccommand.shared.ConfigHelper;
import org.me.synccommand.shared.redis.RedisHandler;
import org.me.synccommand.shared.redis.RedisPubSub;

import java.util.logging.Logger;

public class SyncCommandBungee extends Plugin implements Listener {
    private ProxyServer proxy;
    private Logger logger;
    private RedisPubSub redisPubSub;

    @Override
    public void onEnable() {
        proxy = getProxy();
        logger = getLogger();

        logger.info("SyncCommand is starting up...");
        initialize();
        logger.info("SyncCommand has started successfully!");
    }

    public void initialize() {
        ConfigHelper configHelper = new ConfigHelper(logger);
        configHelper.loadConfiguration();

        // Connect to Redis
        try {
            RedisHandler.connect(configHelper.getRedisHost(), configHelper.getRedisPort(), configHelper.getRedisPassword());
            logger.info("Connected to Redis.");
        } catch (Exception e) {
            logger.warning("Failed to connect to Redis.");
            e.printStackTrace();
            return;
        }

        // Initialize Redis PubSub
        try {
            redisPubSub = new RedisPubSub(logger, new BungeeConsoleCommand(proxy));
            redisPubSub.init();
            logger.info("Initialized Redis PubSub.");
        } catch (Exception e) {
            logger.warning("Failed to initialize Redis PubSub.");
            e.printStackTrace();
            return;
        }

        // Schedule Redis subscription
        proxy.getScheduler().runAsync(this, () -> RedisHandler.subscribe(redisPubSub.getPubSub(), configHelper.getChannels().toArray(new String[0])));

        // Register commands
        proxy.getPluginManager().registerCommand(this, new SyncCommandSync(this, proxy, configHelper));
        proxy.getPluginManager().registerCommand(this, new SyncCommandReload(this, configHelper));
    }

    @Override
    public void onDisable() {
        logger.info("SyncCommand is shutting down...");
        shutdown();
        logger.info("SyncCommand has shut down successfully!");
    }

    public void shutdown() {
        if (redisPubSub != null) {
            redisPubSub.shut();
            logger.info("Redis PubSub has been shut down.");
        }
        RedisHandler.disconnect();
        logger.info("Disconnected from Redis.");
    }

    public void reload() {
        logger.info("SyncCommand is reloading...");
        shutdown();
        initialize();
        logger.info("SyncCommand has reloaded successfully!");
    }
}
