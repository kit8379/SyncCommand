package org.me.synccommand.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.me.synccommand.shared.redis.RedisHandler;
import org.me.synccommand.velocity.ConfigHelper;
import org.me.synccommand.velocity.SyncCommandVelocity;

import java.util.Arrays;

public class SyncCommandSync implements SimpleCommand {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    private final SyncCommandVelocity plugin;

    public SyncCommandSync(SyncCommandVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        ConfigHelper config = plugin.getConfigHelper();

        if (!source.hasPermission("synccommand.admin")) {
            source.sendMessage(LEGACY_SERIALIZER.deserialize(config.getNoPermissionMessage()));
            return;
        }

        if (args.length < 2) {
            source.sendMessage(LEGACY_SERIALIZER.deserialize(config.getUsageMessage()));
            return;
        }

        String channel = args[0];

        String syncCommand = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        plugin.getProxy().getScheduler().buildTask(plugin, () -> {
            try {
                RedisHandler.publish(channel, syncCommand);
            } catch (Exception e) {
                plugin.getLogger().error("Failed to publish command to Redis channel '{}'.", channel, e);
            }
        }).schedule();

        source.sendMessage(LEGACY_SERIALIZER.deserialize(config.getCommandSyncedMessage(channel)));
    }
}