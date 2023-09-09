package org.me.synccommand.bukkit;

import org.me.synccommand.shared.Utils;

import java.util.List;
import java.util.Objects;

public class ConfigHelper {

    private final SyncCommandBukkit plugin;

    public ConfigHelper(SyncCommandBukkit plugin) {
        this.plugin = plugin;
    }

    public String getRedisHost() {
        return plugin.getConfig().getString("redis.host");
    }

    public int getRedisPort() {
        return plugin.getConfig().getInt("redis.port");
    }

    public String getRedisPassword() {
        return plugin.getConfig().getString("redis.password");
    }

    public List<String> getChannels() {
        return plugin.getConfig().getStringList("channels");
    }

    public String getReloadMessage() {
        return Utils.colorize(plugin.getConfig().getString("messages.reload"));
    }

    public String getUsageMessage() {
        return Utils.colorize(plugin.getConfig().getString("messages.usage"));
    }

    public String getNoPermissionMessage() {
        return Utils.colorize(plugin.getConfig().getString("messages.noPermission"));
    }

    public String getCommandSyncedMessage(String channel) {
        return Utils.colorize(Objects.requireNonNull(plugin.getConfig().getString("messages.commandSynced")).replace("%s", channel));
    }
}
