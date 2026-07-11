package org.me.synccommand.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.me.synccommand.bukkit.ConfigHelper;
import org.me.synccommand.bukkit.SyncCommandBukkit;
import org.me.synccommand.shared.redis.RedisHandler;

import java.util.Arrays;
import java.util.logging.Level;

public class SyncCommandSync implements CommandExecutor {

    private final SyncCommandBukkit plugin;

    public SyncCommandSync(SyncCommandBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ConfigHelper config = plugin.getConfigHelper();

        if (!sender.hasPermission("synccommand.admin")) {
            sender.sendMessage(config.getNoPermissionMessage());
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(config.getUsageMessage());
            return true;
        }

        String channel = args[0];

        String syncCommand = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        plugin.getFoliaLib().getScheduler().runAsync(task -> {
            try {
                RedisHandler.publish(channel, syncCommand);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to publish command to Redis channel '" + channel + "'.", e);
            }
        });

        sender.sendMessage(config.getCommandSyncedMessage(channel));

        return true;
    }
}