package org.me.synccommand.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.me.synccommand.bungee.ConfigHelper;
import org.me.synccommand.bungee.SyncCommandBungee;
import org.me.synccommand.shared.redis.RedisHandler;

import java.util.Arrays;
import java.util.logging.Level;

public class SyncCommandSync extends Command {

    private final SyncCommandBungee plugin;

    public SyncCommandSync(SyncCommandBungee plugin) {
        super("syncb");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ConfigHelper config = plugin.getConfigHelper();

        if (!sender.hasPermission("synccommand.admin")) {
            sender.sendMessage(new TextComponent(config.getNoPermissionMessage()));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(new TextComponent(config.getUsageMessage()));
            return;
        }

        String channel = args[0];

        String syncCommand = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            try {
                RedisHandler.publish(channel, syncCommand);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to publish command to Redis channel '" + channel + "'.", e);
            }
        });

        sender.sendMessage(new TextComponent(config.getCommandSyncedMessage(channel)));
    }
}