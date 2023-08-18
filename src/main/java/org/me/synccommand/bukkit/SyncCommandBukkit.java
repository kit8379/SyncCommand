package org.me.synccommand.bukkit;

import org.me.synccommand.bukkit.command.SyncCommandReload;
import org.me.synccommand.bukkit.command.SyncCommandSync;
import org.me.synccommand.shared.ConfigHelper;
import org.me.synccommand.shared.RedisPubSub;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public class SyncCommandBukkit extends JavaPlugin {

    private static SyncCommandBukkit instance;
    private Logger logger;
    private RedisPubSub redisPubSub;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        logger.info("SyncCommand is starting up...");
        initialize();
        logger.info("SyncCommand has started up successfully!");
    }

    public void initialize() {
        ConfigHelper configHelper = new ConfigHelper(logger);
        configHelper.loadConfiguration();
        redisPubSub = new RedisPubSub(logger, configHelper, new BukkitConsoleCommand());
        redisPubSub.init();
        Objects.requireNonNull(this.getCommand("sync")).setExecutor(new SyncCommandSync(configHelper));
        Objects.requireNonNull(this.getCommand("syncreload")).setExecutor(new SyncCommandReload(this, configHelper));
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

    public static SyncCommandBukkit getInstance() {
        return instance;
    }
}
