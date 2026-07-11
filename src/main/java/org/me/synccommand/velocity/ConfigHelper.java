package org.me.synccommand.velocity;

import org.me.synccommand.shared.Utils;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ConfigHelper {

    private final Path dataDirectory;
    private final Logger logger;

    private volatile Values values = Values.defaults();

    public ConfigHelper(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    public boolean loadConfiguration() {
        try {
            Files.createDirectories(dataDirectory);

            Path configPath = dataDirectory.resolve("config.yml");
            copyDefaultConfiguration(configPath);

            YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(configPath).build();

            ConfigurationNode root = loader.load();

            String redisHost = requireString(root, "redis", "host");

            int redisPort = root.node("redis", "port").getInt(-1);

            if (redisPort < 1 || redisPort > 65535) {
                throw new IllegalStateException("Invalid redis.port in config.yml: " + redisPort);
            }

            String redisPassword = requireString(root, "redis", "password");

            List<String> channels = root.node("channels").getList(String.class);

            if (channels == null || channels.isEmpty()) {
                throw new IllegalStateException("The channels list in config.yml cannot be empty.");
            }

            values = new Values(redisHost, redisPort, redisPassword, List.copyOf(channels), Utils.colorize(requireString(root, "messages", "reload")), Utils.colorize(requireString(root, "messages", "usage")), Utils.colorize(requireString(root, "messages", "noPermission")), Utils.colorize(requireString(root, "messages", "commandSynced")));

            logger.info("Velocity configuration loaded successfully.");
            return true;
        } catch (Exception e) {
            logger.error("Failed to load Velocity configuration.", e);
            return false;
        }
    }

    private void copyDefaultConfiguration(Path configPath) throws IOException {

        if (Files.exists(configPath)) {
            return;
        }

        try (InputStream inputStream = SyncCommandVelocity.class.getClassLoader().getResourceAsStream("config.yml")) {

            if (inputStream == null) {
                throw new IOException("Default config.yml was not found inside the plugin JAR.");
            }

            Files.copy(inputStream, configPath);
        }

        logger.info("Created default Velocity config.yml.");
    }

    private static String requireString(ConfigurationNode root, String... path) {
        String value = root.node((Object[]) path).getString();

        if (value == null) {
            throw new IllegalStateException("Missing configuration value: " + String.join(".", path));
        }

        return value;
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
            return new Values("localhost", 6379, "", List.of(), Utils.colorize("&aSyncCommand configuration reloaded."), Utils.colorize("&cUsage: /syncv <channel> <command>"), Utils.colorize("&cYou do not have permission."), Utils.colorize("&aCommand synced to channel &e%s&a."));
        }
    }
}