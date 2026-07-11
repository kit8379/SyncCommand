package org.me.synccommand.bungee;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.me.synccommand.shared.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;

public final class ConfigHelper {

    private final SyncCommandBungee plugin;

    private volatile Values values = Values.defaults();

    public ConfigHelper(SyncCommandBungee plugin) {
        this.plugin = plugin;
    }

    public boolean loadConfiguration() {
        try {
            File dataFolder = plugin.getDataFolder();

            if (!dataFolder.exists() && !dataFolder.mkdirs()) {

                throw new IOException("Could not create plugin data directory: " + dataFolder.getAbsolutePath());
            }

            File configFile = new File(dataFolder, "config.yml");

            copyDefaultConfiguration(configFile);

            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            String redisHost = configuration.getString("redis.host", "localhost");

            int redisPort = configuration.getInt("redis.port", 6379);

            String redisPassword = configuration.getString("redis.password", "");

            List<String> channels = configuration.getStringList("channels");

            if (channels.isEmpty()) {

                throw new IllegalStateException("The channels list in config.yml cannot be empty.");
            }

            if (redisPort < 1 || redisPort > 65535) {

                throw new IllegalStateException("Invalid Redis port: " + redisPort);
            }

            String reloadMessage = configuration.getString("messages.reload");

            String usageMessage = configuration.getString("messages.usage");

            String noPermissionMessage = configuration.getString("messages.noPermission");

            String commandSyncedMessage = configuration.getString("messages.commandSynced");

            values = new Values(redisHost, redisPort, redisPassword, List.copyOf(channels), Utils.colorize(reloadMessage), Utils.colorize(usageMessage), Utils.colorize(noPermissionMessage), Utils.colorize(commandSyncedMessage));

            plugin.getLogger().info("BungeeCord configuration loaded successfully.");

            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load BungeeCord configuration.", e);

            return false;
        }
    }

    private void copyDefaultConfiguration(File configFile) throws IOException {

        if (configFile.exists()) {
            return;
        }

        try (InputStream inputStream = SyncCommandBungee.class.getResourceAsStream("/config.yml")) {

            if (inputStream == null) {
                throw new IOException("Default config.yml was not found inside the plugin JAR.");
            }

            Files.copy(inputStream, configFile.toPath());
        }

        plugin.getLogger().info("Created default BungeeCord config.yml.");
    }

    public String getRedisHost() {
        return values.redisHost();
    }

    public int getRedisPort() {
        return values.redisPort();
    }

    public String getRedisPassword() {
        return values.redisPassword();
    }

    public List<String> getChannels() {
        return values.channels();
    }

    public String getReloadMessage() {
        return values.reloadMessage();
    }

    public String getUsageMessage() {
        return values.usageMessage();
    }

    public String getNoPermissionMessage() {
        return values.noPermissionMessage();
    }

    public String getCommandSyncedMessage(String channel) {
        return values.commandSyncedMessage().replace("%s", channel);
    }

    private record Values(String redisHost, int redisPort, String redisPassword, List<String> channels,
                          String reloadMessage, String usageMessage, String noPermissionMessage,
                          String commandSyncedMessage) {

        private static Values defaults() {
            return new Values("localhost", 6379, "", List.of(), Utils.colorize("&aSyncCommand configuration reloaded."), Utils.colorize("&cUsage: /syncb <channel> <command>"), Utils.colorize("&cYou do not have permission."), Utils.colorize("&aCommand synced to channel &e%s&a."));
        }
    }
}