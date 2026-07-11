package org.me.synccommand.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.me.synccommand.velocity.ConfigHelper;
import org.me.synccommand.velocity.SyncCommandVelocity;

public class SyncCommandReload implements SimpleCommand {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    private final SyncCommandVelocity plugin;

    public SyncCommandReload(SyncCommandVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        ConfigHelper config = plugin.getConfigHelper();

        if (!source.hasPermission("synccommand.admin")) {
            source.sendMessage(LEGACY_SERIALIZER.deserialize(config.getNoPermissionMessage()));
            return;
        }

        if (!plugin.reload()) {
            source.sendMessage(Component.text("Failed to reload SyncCommand. " + "Check the proxy console."));
            return;
        }

        source.sendMessage(LEGACY_SERIALIZER.deserialize(plugin.getConfigHelper().getReloadMessage()));
    }
}