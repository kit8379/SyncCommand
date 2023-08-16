package org.me.synccommand.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.me.synccommand.shared.RedisHandler;

public class CommandHelper implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("synccommand.sync")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /sync <channel> <command>");
            return true;
        }

        String channel = args[0];
        String syncCommand = String.join(" ", args).substring(channel.length()).trim();

        RedisHandler.publish(channel, syncCommand);
        sender.sendMessage("§aCommand synchronized to the " + channel + " channel.");
        return true;
    }
}
