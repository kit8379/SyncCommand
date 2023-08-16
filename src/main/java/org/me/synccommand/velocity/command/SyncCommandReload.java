package org.me.synccommand.velocity.command;

import org.me.synccommand.shared.ConfigHelper;
import org.me.synccommand.velocity.SyncCommandVelocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

public class SyncCommandReload implements SimpleCommand {

    private final SyncCommandVelocity plugin;
    private final ConfigHelper config;

    public SyncCommandReload(SyncCommandVelocity plugin, ConfigHelper config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!source.hasPermission("synccommand.admin")) {
            source.sendMessage(Component.text(config.getNoPermissionMessage()));
            return;
        }

        plugin.reload();
        source.sendMessage(Component.text(config.getReloadMessage()));
    }
}
