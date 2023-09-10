package org.me.synccommand.velocity.command;

import org.me.synccommand.shared.ConfigHelper;
import org.me.synccommand.shared.redis.RedisHandler;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

public class SyncCommandSync implements SimpleCommand {

    private final ConfigHelper config;

    public SyncCommandSync(ConfigHelper config) {
        this.config = config;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("synccommand.admin")) {
            source.sendMessage(Component.text(config.getNoPermissionMessage()));
            return;
        }

        if (args.length < 2) {
            source.sendMessage(Component.text(config.getUsageMessage()));
            return;
        }

        String channel = args[0];
        String command = String.join(" ", args).substring(channel.length()).trim();

        RedisHandler.publish(channel, command);
        source.sendMessage(Component.text(config.getCommandSyncedMessage(channel)));
    }
}
