package org.me.synccommand.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.me.synccommand.bukkit.command.SyncCommandReload;
import org.me.synccommand.bukkit.command.SyncCommandSync;
import org.me.synccommand.shared.redis.RedisPubSub;

import java.util.Objects;
import java.util.logging.Logger;

public class SyncCommandBukkit extends JavaPlugin {

    private static SyncCommandBukkit instance;
    private Logger logger;
    private RedisPubSub redisPubSub;

    public static SyncCommandBukkit getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        logger.info("SyncCommand is starting up...");
        initialize();
        logger.info("SyncCommand has started up successfully!");
    }

    public void initialize() {
        saveDefaultConfig();
        ConfigHelper configHelper = new ConfigHelper(this);
        redisPubSub = new RedisPubSub(logger, new BukkitConsoleCommand(), configHelper.getRedisHost(), configHelper.getRedisPort(), configHelper.getRedisPassword(), configHelper.getChannels());
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
        reloadConfig();
        initialize();
        logger.info("SyncCommand has reloaded successfully!");
    }
}
