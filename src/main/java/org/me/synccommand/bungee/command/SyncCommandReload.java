package org.me.synccommand.bungee.command;

import org.me.synccommand.bungee.SyncCommandBungee;
import org.me.synccommand.shared.ConfigHelper;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class SyncCommandReload extends Command {

    private final SyncCommandBungee plugin;
    private final ConfigHelper config;

    public SyncCommandReload(SyncCommandBungee plugin, ConfigHelper config) {
        super("syncb");
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("synccommand.admin")) {
            sender.sendMessage(new TextComponent(config.getNoPermissionMessage()));
            return;
        }

        plugin.reload();
        sender.sendMessage(new TextComponent(config.getReloadMessage()));
    }
}
