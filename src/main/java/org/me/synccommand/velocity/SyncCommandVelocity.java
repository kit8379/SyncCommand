package org.me.synccommand.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.me.synccommand.shared.redis.RedisHandler;
import org.me.synccommand.shared.redis.RedisPubSub;
import org.me.synccommand.velocity.command.SyncCommandReload;
import org.me.synccommand.velocity.command.SyncCommandSync;
import org.slf4j.Logger;
import redis.clients.jedis.JedisPubSub;

import java.nio.file.Path;

@Plugin(id = "synccommand", name = "SyncCommand", version = "1.0", description = "Sync commands across servers", authors = {"kit8379"})
public class SyncCommandVelocity {

    private final ProxyServer proxy;
    private final Logger logger;
    private final ConfigHelper configHelper;

    private RedisPubSub redisPubSub;
    private ScheduledTask subscriptionTask;

    @Inject
    public SyncCommandVelocity(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.configHelper = new ConfigHelper(dataDirectory, logger);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("SyncCommand is starting up...");

        registerCommands();

        if (initialize()) {
            logger.info("SyncCommand has started successfully!");
        } else {
            logger.error("SyncCommand failed to start. Check the errors above.");
        }
    }

    private void registerCommands() {
        CommandMeta syncCommandMeta = proxy.getCommandManager().metaBuilder("syncv").plugin(this).build();

        proxy.getCommandManager().register(syncCommandMeta, new SyncCommandSync(this));

        CommandMeta reloadCommandMeta = proxy.getCommandManager().metaBuilder("syncvreload").plugin(this).build();

        proxy.getCommandManager().register(reloadCommandMeta, new SyncCommandReload(this));
    }

    public boolean initialize() {
        if (!configHelper.loadConfiguration()) {
            return false;
        }

        try {
            RedisHandler.connect(configHelper.getRedisHost(), configHelper.getRedisPort(), configHelper.getRedisPassword());

            logger.info("Connected to Redis.");
        } catch (Exception e) {
            logger.error("Failed to connect to Redis.", e);

            cleanupAfterFailedInitialization();
            return false;
        }

        try {
            redisPubSub = new RedisPubSub(new VelocityConsoleCommand(proxy));

            redisPubSub.init();

            logger.info("Initialized Redis PubSub.");
        } catch (Exception e) {
            logger.error("Failed to initialize Redis PubSub.", e);

            cleanupAfterFailedInitialization();
            return false;
        }

        try {
            String[] channels = configHelper.getChannels().toArray(new String[0]);

            JedisPubSub jedisPubSub = redisPubSub.getPubSub();

            subscriptionTask = proxy.getScheduler().buildTask(this, () -> RedisHandler.subscribe(jedisPubSub, channels)).schedule();

            logger.info("Redis subscription task has been scheduled.");
        } catch (Exception e) {
            logger.error("Failed to schedule Redis subscription.", e);

            cleanupAfterFailedInitialization();
            return false;
        }

        return true;
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
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
                logger.warn("An error occurred while shutting down Redis PubSub.", e);
            } finally {
                redisPubSub = null;
            }
        }

        if (subscriptionTask != null) {
            try {
                subscriptionTask.cancel();
            } catch (Exception e) {
                logger.warn("An error occurred while cancelling the Redis subscription task.", e);
            } finally {
                subscriptionTask = null;
            }
        }

        try {
            RedisHandler.disconnect();

            logger.info("Disconnected from Redis.");
        } catch (Exception e) {
            logger.warn("An error occurred while disconnecting from Redis.", e);
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
            logger.error("SyncCommand failed to reload. Check the errors above.");
        }

        return success;
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public Logger getLogger() {
        return logger;
    }

    public ConfigHelper getConfigHelper() {
        return configHelper;
    }
}