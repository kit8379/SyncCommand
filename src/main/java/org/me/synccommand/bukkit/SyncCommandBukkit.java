package org.me.synccommand.bukkit;

import org.me.synccommand.shared.RedisHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPubSub;
import java.util.List;

public class SyncCommandBukkit extends JavaPlugin {

    private JedisPubSub pubSub;
    private Thread redisListenerThread;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String host = getConfig().getString("redisHost");
        int port = getConfig().getInt("redisPort");
        String password = getConfig().getString("redisPassword");
        List<String> originalChannels = getConfig().getStringList("channels");
        String[] namespacedChannels = originalChannels.stream()
                .map(channel -> "synccommand." + channel)
                .toArray(String[]::new);

        RedisHandler.connect(host, port, password);

        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message);
            }
        };

        redisListenerThread = new Thread(() -> RedisHandler.subscribe(pubSub, namespacedChannels));
        redisListenerThread.start();

        this.getCommand("sync").setExecutor(new SyncCommand());
    }

    @Override
    public void onDisable() {
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
    }
}
