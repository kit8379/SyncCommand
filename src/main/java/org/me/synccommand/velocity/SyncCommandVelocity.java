package org.me.synccommand.velocity;

import org.me.synccommand.shared.ConfigHelper;
import org.me.synccommand.shared.redis.RedisPubSub;
import org.me.synccommand.velocity.command.SyncCommandReload;
import org.me.synccommand.velocity.command.SyncCommandSync;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import javax.inject.Inject;
import java.util.logging.Logger;

@Plugin(id = "synccommand", name = "SyncCommand", version = "1.0",
        description = "Sync commands across servers", authors = {"kit8379"})
public class SyncCommandVelocity {

    public ProxyServer proxy;
    private final Logger logger;
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
        redisPubSub = new RedisPubSub(logger, new VelocityConsoleCommand(proxy), configHelper.getRedisHost(), configHelper.getRedisPort(), configHelper.getRedisPassword(), configHelper.getChannels());
        redisPubSub.init();
        proxy.getCommandManager().register("syncv", new SyncCommandSync(configHelper));
        proxy.getCommandManager().register("syncvreload", new SyncCommandReload(this, configHelper));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("SyncCommand is shutting down...");
        shutdown();
        logger.info("SyncCommand has shut down successfully!");
    }

    public void shutdown() {
        redisPubSub.shut();
    }

    public void reload() {
        logger.info("SyncCommand is reloading...");
        shutdown();
        initialize();
        logger.info("SyncCommand has reloaded successfully!");
    }
}
