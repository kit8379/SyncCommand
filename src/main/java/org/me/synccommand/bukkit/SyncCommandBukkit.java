package org.me.synccommand.bukkit;

import com.tcoded.folialib.FoliaLib;
import org.bukkit.plugin.java.JavaPlugin;
import org.me.synccommand.bukkit.command.SyncCommandReload;
import org.me.synccommand.bukkit.command.SyncCommandSync;
import org.me.synccommand.shared.redis.RedisHandler;
import org.me.synccommand.shared.redis.RedisPubSub;
import redis.clients.jedis.JedisPubSub;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncCommandBukkit extends JavaPlugin {

    private Logger logger;
    private RedisPubSub redisPubSub;
    private FoliaLib foliaLib;
    private ConfigHelper configHelper;

    private volatile boolean shuttingDown;

    @Override
    public void onEnable() {
        logger = getLogger();

        saveDefaultConfig();

        foliaLib = new FoliaLib(this);
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
        Objects.requireNonNull(getCommand("sync"), "Command 'sync' is missing from plugin.yml.").setExecutor(new SyncCommandSync(this));

        Objects.requireNonNull(getCommand("syncreload"), "Command 'syncreload' is missing from plugin.yml.").setExecutor(new SyncCommandReload(this));
    }

    public boolean initialize() {
        shuttingDown = false;

        String[] channels = configHelper.getChannels().toArray(new String[0]);

        if (channels.length == 0) {
            logger.severe("The channels list in config.yml cannot be empty.");
            return false;
        }

        int redisPort = configHelper.getRedisPort();

        if (redisPort < 1 || redisPort > 65535) {
            logger.severe("Invalid Redis port in config.yml: " + redisPort);
            return false;
        }

        try {
            RedisHandler.connect(configHelper.getRedisHost(), redisPort, configHelper.getRedisPassword());

            logger.info("Connected to Redis.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to connect to Redis.", e);

            cleanupAfterFailedInitialization();
            return false;
        }

        try {
            redisPubSub = new RedisPubSub(new BukkitConsoleCommand(this));

            redisPubSub.init();

            logger.info("Initialized Redis PubSub.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize Redis PubSub.", e);

            cleanupAfterFailedInitialization();
            return false;
        }

        try {
            JedisPubSub jedisPubSub = redisPubSub.getPubSub();

            /*
             * FoliaLib runAsync expects Consumer<WrappedTask>,
             * so the lambda must accept one parameter.
             */
            foliaLib.getScheduler().runAsync(task -> {
                try {
                    RedisHandler.subscribe(jedisPubSub, channels);
                } catch (Exception e) {
                    if (!shuttingDown) {
                        logger.log(Level.SEVERE, "Redis subscription stopped unexpectedly.", e);
                    }
                }
            });

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
        shuttingDown = true;

        /*
         * Unsubscribe before cancelling tasks so the blocking
         * Jedis subscribe operation can exit normally.
         */
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

        if (foliaLib != null) {
            try {
                foliaLib.getScheduler().cancelAllTasks();
            } catch (Exception e) {
                logger.log(Level.WARNING, "An error occurred while cancelling FoliaLib tasks.", e);
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
        reloadConfig();

        boolean success = initialize();

        if (success) {
            logger.info("SyncCommand has reloaded successfully!");
        } else {
            logger.severe("SyncCommand failed to reload. Check the errors above.");
        }

        return success;
    }

    public FoliaLib getFoliaLib() {
        return foliaLib;
    }

    public ConfigHelper getConfigHelper() {
        return configHelper;
    }
}