package org.me.synccommand.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.me.synccommand.shared.ConfigHelper;
import org.me.synccommand.shared.redis.RedisHandler;
import org.me.synccommand.shared.redis.RedisPubSub;
import org.me.synccommand.velocity.command.SyncCommandReload;
import org.me.synccommand.velocity.command.SyncCommandSync;

import javax.inject.Inject;
import java.util.logging.Logger;

@Plugin(id = "synccommand", name = "SyncCommand", version = "1.0", description = "Sync commands across servers", authors = {"kit8379"})
public class SyncCommandVelocity {

    private final Logger logger;
    public ProxyServer proxy;
    private RedisPubSub redisPubSub;

    @Inject
    public SyncCommandVelocity(ProxyServer server, Logger logger) {
        this.proxy = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
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
            redisPubSub = new RedisPubSub(logger, new VelocityConsoleCommand(proxy));
            redisPubSub.init();
            logger.info("Initialized Redis PubSub.");
        } catch (Exception e) {
            logger.warning("Failed to initialize Redis PubSub.");
            e.printStackTrace();
            return;
        }

        // Schedule Redis subscription
        proxy.getScheduler().buildTask(this, () -> RedisHandler.subscribe(redisPubSub.getPubSub(), configHelper.getChannels().toArray(new String[0]))).schedule();

        // Register commands
        proxy.getCommandManager().register("syncv", new SyncCommandSync(this, proxy, configHelper));
        proxy.getCommandManager().register("syncvreload", new SyncCommandReload(this, configHelper));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
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
