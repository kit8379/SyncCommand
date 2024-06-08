package org.me.synccommand.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.me.synccommand.bungee.SyncCommandBungee;
import org.me.synccommand.shared.ConfigHelper;
import org.me.synccommand.shared.redis.RedisHandler;

public class SyncCommandSync extends Command {

    private final SyncCommandBungee plugin;
    private final ProxyServer proxy;
    private final ConfigHelper config;

    public SyncCommandSync(SyncCommandBungee plugin, ProxyServer proxy, ConfigHelper config) {
        super("syncb");
        this.plugin = plugin;
        this.proxy = proxy;
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
        String syncCommand = String.join(" ", args).substring(channel.length()).trim();

        proxy.getScheduler().runAsync(plugin, () -> RedisHandler.publish(channel, syncCommand));

        sender.sendMessage(new TextComponent(config.getCommandSyncedMessage(channel)));
    }
}
