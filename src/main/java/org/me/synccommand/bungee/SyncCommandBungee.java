package org.me.synccommand.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.me.synccommand.bungee.command.SyncCommandReload;
import org.me.synccommand.bungee.command.SyncCommandSync;
import org.me.synccommand.shared.redis.RedisHandler;
import org.me.synccommand.shared.redis.RedisPubSub;
import redis.clients.jedis.JedisPubSub;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncCommandBungee extends Plugin implements Listener {

    private ProxyServer proxy;
    private Logger logger;
    private ConfigHelper configHelper;

    private RedisPubSub redisPubSub;
    private ScheduledTask subscriptionTask;

    @Override
    public void onEnable() {
        proxy = getProxy();
        logger = getLogger();

        configHelper = new ConfigHelper(this);

        logger.info("SyncCommand is starting up...");

        registerCommands();

        if (initialize()) {
            logger.info("SyncCommand has started successfully!");
        } else {
            logger.severe("SyncCommand failed to start. Check the errors above.");
        }
    }

    private void registerCommands() {
        proxy.getPluginManager().registerCommand(this, new SyncCommandSync(this));

        proxy.getPluginManager().registerCommand(this, new SyncCommandReload(this));
    }

    public boolean initialize() {
        if (!configHelper.loadConfiguration()) {
            return false;
        }

        try {
            RedisHandler.connect(configHelper.getRedisHost(), configHelper.getRedisPort(), configHelper.getRedisPassword());

            logger.info("Connected to Redis.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to connect to Redis.", e);

            cleanupAfterFailedInitialization();
            return false;
        }

        try {
            redisPubSub = new RedisPubSub(new BungeeConsoleCommand(proxy));

            redisPubSub.init();

            logger.info("Initialized Redis PubSub.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize Redis PubSub.", e);

            cleanupAfterFailedInitialization();
            return false;
        }

        try {
            String[] channels = configHelper.getChannels().toArray(new String[0]);

            JedisPubSub jedisPubSub = redisPubSub.getPubSub();

            subscriptionTask = proxy.getScheduler().runAsync(this, () -> RedisHandler.subscribe(jedisPubSub, channels));

            logger.info("Redis subscription task has been scheduled.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to schedule Redis subscription.", e);

            cleanupAfterFailedInitialization();
            return false;
        }

        return true;
    }

    @Override
    public void onDisable() {
        logger.info("SyncCommand is shutting down...");

        shutdown();

        logger.info("SyncCommand has shut down successfully!");
    }

    public void shutdown() {
        if (redisPubSub != null) {
            try {
                redisPubSub.shut();

                logger.info("Redis PubSub has been shut down.");
            } catch (Exception e) {
                logger.log(Level.WARNING, "An error occurred while shutting down Redis PubSub.", e);
            } finally {
                redisPubSub = null;
            }
        }

        if (subscriptionTask != null) {
            try {
                subscriptionTask.cancel();
            } catch (Exception e) {
                logger.log(Level.WARNING, "An error occurred while cancelling the Redis subscription task.", e);
            } finally {
                subscriptionTask = null;
            }
        }

        try {
            RedisHandler.disconnect();

            logger.info("Disconnected from Redis.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "An error occurred while disconnecting from Redis.", e);
        }
    }

    private void cleanupAfterFailedInitialization() {
        shutdown();
    }

    public boolean reload() {
        logger.info("SyncCommand is reloading...");

        shutdown();

        boolean success = initialize();

        if (success) {
            logger.info("SyncCommand has reloaded successfully!");
        } else {
            logger.severe("SyncCommand failed to reload. Check the errors above.");
        }

        return success;
    }

    public ConfigHelper getConfigHelper() {
        return configHelper;
    }
}