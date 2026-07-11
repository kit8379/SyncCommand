package org.me.synccommand.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.me.synccommand.bungee.ConfigHelper;
import org.me.synccommand.bungee.SyncCommandBungee;

public class SyncCommandReload extends Command {

    private final SyncCommandBungee plugin;

    public SyncCommandReload(SyncCommandBungee plugin) {
        super("syncbreload");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ConfigHelper config = plugin.getConfigHelper();

        if (!sender.hasPermission("synccommand.admin")) {
            sender.sendMessage(new TextComponent(config.getNoPermissionMessage()));
            return;
        }

        if (!plugin.reload()) {
            sender.sendMessage(new TextComponent("§cFailed to reload SyncCommand. " + "Check the proxy console."));
            return;
        }

        sender.sendMessage(new TextComponent(plugin.getConfigHelper().getReloadMessage()));
    }
}