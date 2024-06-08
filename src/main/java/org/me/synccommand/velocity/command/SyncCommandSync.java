package org.me.synccommand.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.me.synccommand.shared.ConfigHelper;
import org.me.synccommand.shared.redis.RedisHandler;
import org.me.synccommand.velocity.SyncCommandVelocity;

public class SyncCommandSync implements SimpleCommand {

    private final SyncCommandVelocity plugin;
    private final ProxyServer proxy;
    private final ConfigHelper config;

    public SyncCommandSync(SyncCommandVelocity plugin, ProxyServer proxy, ConfigHelper config) {
        this.plugin = plugin;
        this.proxy = proxy;
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

        proxy.getScheduler().buildTask(plugin, () -> RedisHandler.publish(channel, command)).schedule();

        source.sendMessage(Component.text(config.getCommandSyncedMessage(channel)));
    }
}
