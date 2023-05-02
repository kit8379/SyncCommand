package org.me.synccommand;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;

public class SyncCommand extends JavaPlugin implements CommandExecutor {

    private JedisPool jedisPool;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        String redisHost = config.getString("redisHost");
        int redisPort = config.getInt("redisPort");
        String redisPassword = config.getString("redisPassword");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(poolConfig, redisHost, redisPort, 2000, redisPassword);

        Bukkit.getPluginCommand("sync").setExecutor(this);

        // Listener for Redis messages
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                JedisPubSub jedisPubSub = new JedisPubSub() {
                    @Override
                    public void onPMessage(String pattern, String channel, String message) {
                        if (config.getStringList("channels").contains(channel)) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message);
                        }
                    }
                };
                jedis.psubscribe(jedisPubSub, "*");
            }
        });
    }

    @Override
    public void onDisable() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("sync")) {
            if (args.length >= 2) {
                String channel = args[0];
                String cmd = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                // Publish the command to the Redis channel
                Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                    try (Jedis jedis = jedisPool.getResource()) {
                        jedis.publish(channel, cmd);
                    }
                });
                sender.sendMessage("Successfully sent the command.");
                return true;
            } else {
                sender.sendMessage("Usage: /sync <channel> <command>");
            }
        }
        return false;
    }
}