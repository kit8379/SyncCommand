package org.me.synccommand.shared;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConfigHelper {

    private final Logger logger;
    private final Path dataFolder;
    private ConfigurationNode configData;

    public ConfigHelper(Logger logger) {
        this.logger = logger;
        this.dataFolder = Path.of("plugins/SyncCommand");
    }

    public void loadConfiguration() {
        try {
            if (!Files.exists(dataFolder)) {
                Files.createDirectories(dataFolder);
            }

            Path configFile = dataFolder.resolve("config.yml");
            YamlConfigurationLoader loader =
                    YamlConfigurationLoader.builder()
                            .path(configFile)
                            .nodeStyle(NodeStyle.BLOCK)
                            .build();

            if (!Files.exists(configFile)) {
                // Copying the default config from resources
                try (InputStream defaultConfigStream = this.getClass().getResourceAsStream("/config.yml")) {
                    if (defaultConfigStream != null) {
                        Files.copy(defaultConfigStream, configFile);
                    } else {
                        throw new IOException("Could not find default config in resources!");
                    }
                }
            }
            configData = loader.load();
        } catch (IOException e) {
            logger.warning("Failed to load config.yml: " + e.getMessage());
        }
    }

    public String getRedisHost() {
        return configData.node("redis", "host").getString("localhost"); // default to "localhost" if not set
    }

    public int getRedisPort() {
        return configData.node("redis", "port").getInt(6379); // default to 6379 if not set
    }

    public String getRedisPassword() {
        return configData.node("redis", "password").getString(""); // default to empty string if not set
    }

    public List<String> getChannels() {
        try {
            return configData.node("channels").getList(String.class, new ArrayList<>());
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getReloadMessage() {
        return Utils.colorize(configData.node("messages", "reload").getString("&aSyncCommand has been reloaded."));
    }

    public String getUsageMessage() {
        return Utils.colorize(configData.node("messages", "usage").getString("&cUsage: /sync <enable|disable>"));
    }

    public String getNoPermissionMessage() {
        return Utils.colorize(configData.node("messages", "noPermission").getString("&cYou do not have permission to use this command."));
    }

    public String getCommandSyncedMessage(String channel) {
        return Utils.colorize(String.format(configData.node("messages", "commandSynced").getString("&aCommand has been synced to the %s channel."), channel));
    }

}
