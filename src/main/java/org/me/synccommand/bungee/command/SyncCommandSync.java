package org.me.synccommand.bungee.command;

import org.me.synccommand.shared.ConfigHelper;
import org.me.synccommand.shared.RedisHandler;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class SyncCommandSync extends Command {

    private final ConfigHelper config;

    public SyncCommandSync(ConfigHelper config) {
        super("syncb");
        this.config = config;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("synccommand.admin")) {
            sender.sendMessage(new TextComponent(config.getNoPermissionMessage()));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(new TextComponent(config.getUsageMessage()));
            return;
        }

        String channel = args[0];
        String command = String.join(" ", args).substring(channel.length()).trim();

        RedisHandler.publish(channel, command);
        sender.sendMessage(new TextComponent(config.getCommandSyncedMessage(channel)));
    }
}
