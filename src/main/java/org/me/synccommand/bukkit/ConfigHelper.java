package org.me.synccommand.bukkit;

import org.me.synccommand.shared.Utils;

import java.util.List;

public final class ConfigHelper {

    private final SyncCommandBukkit plugin;

    public ConfigHelper(SyncCommandBukkit plugin) {
        this.plugin = plugin;
    }

    public String getRedisHost() {
        return plugin.getConfig().getString("redis.host", "localhost");
    }

    public int getRedisPort() {
        return plugin.getConfig().getInt("redis.port", 6379);
    }

    public String getRedisPassword() {
        return plugin.getConfig().getString("redis.password", "");
    }

    public List<String> getChannels() {
        return plugin.getConfig().getStringList("channels");
    }

    public String getReloadMessage() {
        return Utils.colorize(plugin.getConfig().getString("messages.reload", "&aSyncCommand configuration reloaded."));
    }

    public String getUsageMessage() {
        return Utils.colorize(plugin.getConfig().getString("messages.usage", "&cUsage: /sync <channel> <command>"));
    }

    public String getNoPermissionMessage() {
        return Utils.colorize(plugin.getConfig().getString("messages.noPermission", "&cYou do not have permission."));
    }

    public String getCommandSyncedMessage(String channel) {
        String message = plugin.getConfig().getString("messages.commandSynced", "&aCommand synced to channel &e%s&a.");

        return Utils.colorize(message.replace("%s", channel));
    }
}