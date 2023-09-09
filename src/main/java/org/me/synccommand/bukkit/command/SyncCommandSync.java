package org.me.synccommand.bukkit.command;

import org.me.synccommand.bukkit.ConfigHelper;
import org.me.synccommand.shared.RedisHandler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SyncCommandSync implements CommandExecutor {

    private final ConfigHelper config;

    public SyncCommandSync(ConfigHelper config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

        RedisHandler.publish(channel, syncCommand);
        sender.sendMessage(config.getCommandSyncedMessage(channel));
        return true;
    }
}
