package org.me.synccommand.bukkit.command;

import org.me.synccommand.bukkit.SyncCommandBukkit;
import org.me.synccommand.bukkit.ConfigHelper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SyncCommandReload implements CommandExecutor {

    private final SyncCommandBukkit plugin;
    private final ConfigHelper config;

    public SyncCommandReload(SyncCommandBukkit plugin, ConfigHelper config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("synccommand.admin")) {
            sender.sendMessage(config.getNoPermissionMessage());
            return true;
        }
        plugin.reload();
        sender.sendMessage(config.getReloadMessage());
        return true;
    }
}
