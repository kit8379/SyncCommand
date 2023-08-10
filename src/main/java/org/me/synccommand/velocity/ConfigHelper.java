package org.me.synccommand.velocity;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import org.yaml.snakeyaml.Yaml;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

public class ConfigHelper {
    private final PluginContainer plugin;
    private final Yaml yaml;
    private Path configFile;
    private Configuration config;

    public ConfigHelper(PluginContainer plugin) {
        this.plugin = plugin;
        this.yaml = new Yaml();
    }

    public void saveDefaultConfig() {
        PluginDescription description = plugin.getDescription();
        String resourcePath = "config.yml";
        configFile = description.getSource().get().getParent().resolve(resourcePath);

        if (!Files.exists(configFile)) {
            try (InputStream in = plugin.getInstance().getClass().getResourceAsStream("/" + resourcePath)) {
                if (in != null) {
                    Files.copy(in, configFile, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Configuration getConfig() {
        if (config == null) {
            try (Reader reader = Files.newBufferedReader(configFile)) {
                Object rawConfig = yaml.load(reader);
                if (rawConfig instanceof Map) {
                    config = new Configuration((Map<String, Object>) rawConfig);
                } else {
                    throw new RuntimeException("Invalid config.yml");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    public static class Configuration {
        private final Map<String, Object> data;

        public Configuration(Map<String, Object> data) {
            this.data = data;
        }

        public String getString(String key) {
            Object value = data.get(key);
            if (value instanceof String) {
                return (String) value;
            }
            throw new RuntimeException("Invalid config.yml");
        }

        public int getInt(String key) {
            Object value = data.get(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            throw new RuntimeException("Invalid config.yml");
        }

        public List<String> getStringList(String key) {
            Object value = data.get(key);
            if (value instanceof List) {
                return (List<String>) value; // here, you assume that the list contains strings
            }
            throw new RuntimeException("Invalid config.yml");
        }
    }
}
