package org.me.synccommand.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import org.me.synccommand.shared.RedisHandler;

public class CommandHelper implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 2) {
            source.sendMessage(Component.text("Usage: /syncv <channel> <command>"));
            return;
        }

        String channel = args[0];
        String command = String.join(" ", args).substring(channel.length()).trim();

        RedisHandler.publish(channel, command);
        source.sendMessage(Component.text("Command synchronized to the " + channel + " channel."));
    }
}
