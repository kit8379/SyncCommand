package org.me.synccommand.bukkit;

import org.me.synccommand.shared.ConfigHelper;
import org.me.synccommand.shared.RedisHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPubSub;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class SyncCommandBukkit extends JavaPlugin {

    private Logger logger;
    private ConfigHelper configHelper;
    private JedisPubSub pubSub;
    private Thread redisListenerThread;

    @Override
    public void onEnable() {
        logger = getLogger();
        configHelper = new ConfigHelper(logger);

        String host = configHelper.getRedisHost();
        int port = configHelper.getRedisPort();
        String password = configHelper.getRedisPassword();
        List<String> originalChannels = configHelper.getChannels();
        String[] namespacedChannels = originalChannels.stream()
                .map(channel -> "synccommand." + channel)
                .toArray(String[]::new);

        try {
            RedisHandler.connect(host, port, password);
        } catch (Exception e) {
            logger.warning("Failed to connect to Redis. Disabling plugin.");
            e.printStackTrace();
            return;
        }

        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message);
            }
        };

        redisListenerThread = new Thread(() -> RedisHandler.subscribe(pubSub, namespacedChannels));
        redisListenerThread.start();

        Objects.requireNonNull(this.getCommand("sync")).setExecutor(new CommandHelper());
    }

    @Override
    public void onDisable() {
        logger.info("SyncCommand is shutting down...");
        if (pubSub != null) {
            pubSub.unsubscribe();
        }

        if (redisListenerThread != null) {
            redisListenerThread.interrupt();
            try {
                redisListenerThread.join(); // Ensure thread termination
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        RedisHandler.disconnect();
        logger.info("SyncCommand has shut down successfully!");
    }
}
