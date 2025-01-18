package org.me.synccommand.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.me.synccommand.bukkit.ConfigHelper;
import org.me.synccommand.bukkit.SyncCommandBukkit;
import org.me.synccommand.shared.redis.RedisHandler;

public class SyncCommandSync implements CommandExecutor {

    private final SyncCommandBukkit plugin;
    private final ConfigHelper config;

    public SyncCommandSync(SyncCommandBukkit plugin, ConfigHelper config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("synccommand.admin")) {
            sender.sendMessage(config.getNoPermissionMessage());
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(config.getUsageMessage());
            return true;
        }

        String channel = args[0];
        String syncCommand = String.join(" ", args).substring(channel.length()).trim();

        // Schedule Redis publish
        plugin.getFoliaLib().getScheduler().runAsync(task -> RedisHandler.publish(channel, syncCommand));

        sender.sendMessage(config.getCommandSyncedMessage(channel));
        return true;
    }
}
