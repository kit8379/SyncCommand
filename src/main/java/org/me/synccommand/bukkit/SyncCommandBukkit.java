package org.me.synccommand.bukkit;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.plugin.java.JavaPlugin;
import org.me.synccommand.bukkit.command.SyncCommandReload;
import org.me.synccommand.bukkit.command.SyncCommandSync;
import org.me.synccommand.shared.redis.RedisHandler;
import org.me.synccommand.shared.redis.RedisPubSub;

import java.util.Objects;
import java.util.logging.Logger;

public class SyncCommandBukkit extends JavaPlugin {

    private Logger logger;
    private RedisPubSub redisPubSub;
    private FoliaLib foliaLib;

    @Override
    public void onEnable() {
        logger = getLogger();
        logger.info("SyncCommand is starting up...");
        initialize();
        logger.info("SyncCommand has started up successfully!");
    }

    public void initialize() {
        saveDefaultConfig();
        ConfigHelper configHelper = new ConfigHelper(this);
        String[] namespacedChannels = configHelper.getChannels().toArray(new String[0]);

        foliaLib = new FoliaLib(this);

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
            redisPubSub = new RedisPubSub(logger, new BukkitConsoleCommand(this));
            redisPubSub.init();
            logger.info("Initialized Redis PubSub.");
        } catch (Exception e) {
            logger.warning("Failed to initialize Redis PubSub.");
            e.printStackTrace();
            return;
        }

        // Schedule Redis subscription
        foliaLib.getImpl().runAsync((WrappedTask task) -> RedisHandler.subscribe(redisPubSub.getPubSub(), namespacedChannels));

        // Register commands
        Objects.requireNonNull(this.getCommand("sync")).setExecutor(new SyncCommandSync(this, configHelper));
        Objects.requireNonNull(this.getCommand("syncreload")).setExecutor(new SyncCommandReload(this, configHelper));
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
        reloadConfig();
        initialize();
        logger.info("SyncCommand has reloaded successfully!");
    }

    public FoliaLib getFoliaLib() {
        return foliaLib;
    }
}
