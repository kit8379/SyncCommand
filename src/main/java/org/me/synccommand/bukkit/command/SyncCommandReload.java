package org.me.synccommand.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.me.synccommand.bukkit.ConfigHelper;
import org.me.synccommand.bukkit.SyncCommandBukkit;

public class SyncCommandReload implements CommandExecutor {

    private final SyncCommandBukkit plugin;

    public SyncCommandReload(SyncCommandBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        ConfigHelper config = plugin.getConfigHelper();

        if (!sender.hasPermission("synccommand.admin")) {
            sender.sendMessage(config.getNoPermissionMessage());
            return true;
        }

        if (!plugin.reload()) {
            sender.sendMessage("§cFailed to reload SyncCommand. " + "Check the server console.");
            return true;
        }

        sender.sendMessage(plugin.getConfigHelper().getReloadMessage());

        return true;
    }
}