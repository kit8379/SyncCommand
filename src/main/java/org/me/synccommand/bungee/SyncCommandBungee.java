package org.me.synccommand.bungee;

import org.me.synccommand.bungee.command.SyncCommandReload;
import org.me.synccommand.bungee.command.SyncCommandSync;
import org.me.synccommand.shared.ConfigHelper;
import org.me.synccommand.shared.redis.RedisPubSub;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

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
        redisPubSub = new RedisPubSub(logger, new BungeeConsoleCommand(proxy), configHelper.getRedisHost(), configHelper.getRedisPort(), configHelper.getRedisPassword(), configHelper.getChannels());
        redisPubSub.init();
        proxy.getPluginManager().registerCommand(this, new SyncCommandSync(configHelper));
        proxy.getPluginManager().registerCommand(this, new SyncCommandReload(this, configHelper));
    }

    @Override
    public void onDisable() {
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
